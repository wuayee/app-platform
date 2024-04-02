# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：http 通信服务端
"""
import heapq
import ssl
import sys
import time
import traceback
from http import HTTPStatus
import threading
from typing import Dict, List, Tuple

from flask import Flask, request
from werkzeug.serving import make_server

from fitframework import const
from fitframework.api.decorators import fit, fitable
from fitframework.api.exception import FIT_OK
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.exception.fit_exception import InternalErrorCode
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.metadata.request_metadata import RequestMetadata, GenericVersion
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.utils.tools import b64encode_to_str
from .http_utils import get_server_key_file_password, get_server_crt_path, get_server_key_path, get_server_ca_path, \
    get_server_key_file_password_scc_encrypted, get_server_verify_enabled, get_server_key_file_encrypted, \
    get_server_assert_host_name

_TASK_COUNT_LIMIT = 1000  # 后续解决通过注解读取配置的 bug 后进行优化
_RESULT_SAVE_TIME = 120

_task_dict = {}
_finished_task_queue: List[Tuple] = []
_task_dict_lock = threading.Lock()

_flask_app = Flask(__name__)
_last_success_time = time.time()


class AsyncTask:
    def __init__(self, task_id: str, thread: threading.Thread):
        self.task_id: str = task_id
        self.thread: threading.Thread = thread
        self.result: FitResponse = None


def build_meta_dict(version: int, data_format: int, degradable: bool, code: int, message: str) -> Dict:
    meta = ResponseMetadata(data_format, version, degradable, code, message, {})  # 暂时忽略 tlv 字段。
    return {"FIT-Version": f"{version}",
            "FIT-Data-Format": f"{data_format}",
            "FIT-Degradable": f"{degradable}",
            "FIT-Code": f"{code}",
            "FIT-Message": f"{message}",
            "FIT-Metadata": b64encode_to_str(meta.serialize())}


@fit(generic_id=const.SERVER_RESPONSE_GEN_ID, alias='python_default_server_response')
def server_response(metadata: RequestMetadata, data: bytes) -> FitResponse:
    pass


@fit("com.huawei.fit.security.decrypt")
def decrypt(cipher: str) -> str:
    """
    对于加密后内容进行解密。
    特别注意：
    1. 该接口的 fitable 实现需要通过本地静态插件的方式给出；
    2. 必须在 fit.yml 中指定要调用的 fitable id。

    :param cipher: 待解密的内容。
    :return: 解密后的内容。
    """
    pass


@fitable(generic_id="com.huawei.fit.get.running.async.task.count", fitable_id='local-worker')
def get_running_task_count() -> int:
    with _task_dict_lock:
        return len(_task_dict)


@fitable(generic_id="com.huawei.fit.get.last.success.time", fitable_id='local-worker')
def get_last_success_time() -> float:
    return _last_success_time


def async_serve_response(metadata: RequestMetadata, data: bytes, task_id: str):
    try:
        result: FitResponse = server_response(metadata, data)
        if result.metadata.code == FIT_OK:
            global _last_success_time
            _last_success_time = time.time()
        with _task_dict_lock:
            task: AsyncTask = _task_dict.get(task_id)
            task.result = result
            heapq.heappush(_finished_task_queue, (time.time(), task.task_id))
    except:
        sys_plugin_logger.warning(f"async serve response failed. [task_id={task_id}]")
        except_type, except_value, except_traceback = sys.exc_info()
        sys_plugin_logger.warning(f"async serve response error type: {except_type}")
        sys_plugin_logger.warning(f"async serve response error value: {except_value}")
        sys_plugin_logger.warning(
            f"async serve response error trace back:\n{''.join(traceback.format_tb(except_traceback))}")


@_flask_app.route('/fit/async/awaitResponse', methods=['GET'])
def async_await_response():
    task_id = request.args.get("tid")
    response_headers = {
        "FIT-Data-Format": request.headers.get("FIT-Data-Format"),
        "FIT-Version": request.headers.get("FIT-Version")
    }
    sys_plugin_logger.info(f"GET /fit/async/awaitResponse?tid={task_id}")
    with _task_dict_lock:
        if task_id not in _task_dict:
            response_headers["FIT-Code"] = InternalErrorCode.ASYNC_TASK_NOT_FOUND.value
            response_headers["FIT-Message"] = f"async task not found. [task_id={task_id}]"
            return "", HTTPStatus.OK, response_headers
        task: AsyncTask = _task_dict.get(task_id)
    task.thread.join(timeout=60)
    with _task_dict_lock:
        result = task.result
    if result is None:
        if not task.thread.is_alive():
            sys_plugin_logger.warning(f"thread is not alive, but result is None. [task_id={task_id}]")
        response_headers["FIT-Code"] = InternalErrorCode.ASYNC_TASK_NOT_COMPLETED.value
        response_headers["FIT-Message"] = f"async task not completed. [task_id={task_id}]"
        return "", HTTPStatus.OK, response_headers
    else:
        with _task_dict_lock:
            del _task_dict[task_id]

    meta = result.metadata
    return result.data, HTTPStatus.OK, build_meta_dict(meta.version, meta.data_format, meta.degradable, meta.code,
                                                       meta.msg)


def clear_and_check_task_dict() -> bool:
    with _task_dict_lock:
        while len(_task_dict) >= _TASK_COUNT_LIMIT and len(_finished_task_queue) > 0 \
                and _finished_task_queue[0][0] + _RESULT_SAVE_TIME < time.time():
            task_id = heapq.heappop(_finished_task_queue)[1]
            del _task_dict[task_id]
    return len(_task_dict) < _TASK_COUNT_LIMIT


@_flask_app.route('/fit/<string:genericable_id>/<string:fitable_id>', methods=['POST'])
def fit_handle(genericable_id, fitable_id):
    payload = request.get_data()
    task_id = request.headers.get("FIT-Async-Task-Id")
    sys_plugin_logger.info(
        f"{'HTTPS' if request.is_secure else 'HTTP'} POST /fit/{genericable_id}/{fitable_id}, task_id={task_id}")
    request_metadata = RequestMetadata(int(request.headers.get("FIT-Version")),
                                       int(request.headers.get("FIT-Data-Format")),
                                       GenericVersion.from_string(request.headers.get("FIT-Genericable-Version")),
                                       genericable_id,
                                       fitable_id)
    if task_id is None or len(task_id) == 0:
        result = server_response(request_metadata, payload)
        if result.metadata.code == FIT_OK:
            global _last_success_time
            _last_success_time = time.time()
        meta = result.metadata
        meta_dict = build_meta_dict(meta.version, meta.data_format, meta.degradable, meta.code, meta.msg)
        return result.data, HTTPStatus.OK, meta_dict
    if not clear_and_check_task_dict():
        meta_dict = build_meta_dict(request_metadata.version, request_metadata.data_format, True,
                                    InternalErrorCode.ASYNC_TASK_NOT_ACCEPTED.value,
                                    f"async task not accepted. [task_id={task_id}]")
        return "", HTTPStatus.ACCEPTED, meta_dict

    task_thread = threading.Thread(target=async_serve_response, args=(request_metadata, payload, task_id))
    task = AsyncTask(task_id, task_thread)
    with _task_dict_lock:
        _task_dict[task_id] = task

    task_thread.start()

    meta_dict = build_meta_dict(request_metadata.version, request_metadata.data_format, False, 0, "")
    return "", HTTPStatus.ACCEPTED, meta_dict


class FitBaseHttpServer:
    def __init__(self, host: str, port: int):
        self.host = host
        self.port = port
        self.server = None
        self.server_thread = None
        self.server_thread_name = "FitBaseHttpServer"

    def start(self):
        self.server_thread = threading.Thread(target=self.server.serve_forever, name=self.server_thread_name)
        self.server_thread.start()

    def stop(self):
        self.server.shutdown()
        self.server_thread.join()


class FitHttpServer(FitBaseHttpServer):
    def __init__(self, host: str, port: int):
        super().__init__(host, port)
        self.server_thread_name = "FitHttpServer"
        self.server = make_server(host, port, _flask_app, threaded=True)


def get_decrypted_key_file_password():
    # 对于 https 服务端，不存在不需要客户端验证自身的情况。
    if not get_server_key_file_encrypted():  # 如果私钥未被加密
        return None
    if not get_server_key_file_password_scc_encrypted():  # 如果私钥密码没有被加密
        return get_server_key_file_password()

    return decrypt(get_server_key_file_password())


class FitHttpsServer(FitBaseHttpServer):
    def __init__(self, host: str, port: int):
        super().__init__(host, port)
        self.server_thread_name = "FitHttpsServer"
        ssl_context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
        if get_server_verify_enabled():
            ssl_context.verify_mode = ssl.CERT_REQUIRED
            ssl_context.check_hostname = get_server_assert_host_name()
            ssl_context.load_verify_locations(cafile=get_server_ca_path())
        try:
            ssl_context.load_cert_chain(certfile=get_server_crt_path(),
                                        keyfile=get_server_key_path(),
                                        password=get_decrypted_key_file_password())
        except FileNotFoundError:
            sys_plugin_logger.error("file for https server is missing.")
            raise
        self.server = make_server(host, port, _flask_app, threaded=True, ssl_context=ssl_context)
