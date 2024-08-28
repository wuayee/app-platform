# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：http 通信服务端
"""
import base64
import ssl
import threading
import time
from collections import OrderedDict
from concurrent.futures import ThreadPoolExecutor
from http import HTTPStatus
from queue import Queue, Empty
from typing import Dict, Optional

import tornado.ioloop
import tornado.web
import tornado.websocket

from fitframework.api.decorators import fitable, fit
from fitframework.api.exception import FIT_OK
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.exception.fit_exception import InternalErrorCode, FitException
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.http_header import HttpHeader
from fitframework.core.network.metadata.metadata_utils import TagLengthValuesUtil
from fitframework.core.network.metadata.request_metadata import RequestMetadata, GenericVersion
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.utils.tools import AtomicInt
from .http_utils import get_context_path, get_server_thread_count, get_server_crt_path, get_server_key_path, \
    get_server_ca_path, get_server_verify_enabled, get_server_assert_host_name, get_result_save_duration, \
    AsyncExecuteResult, WorkerInfo, get_task_count_limit, get_polling_wait_time, get_runtime_instance_id, \
    get_runtime_worker_id, server_response, get_decrypted_key_file_password

_last_success_time = time.time()
_start_serve_times = OrderedDict()

_http_server: Optional[tornado.httpserver.HTTPServer] = None
_https_server: Optional[tornado.httpserver.HTTPServer] = None
_server_thread: Optional[threading.Thread] = None

# 在执行的异步任务数量
_running_task_count: AtomicInt = AtomicInt(0)

# 以 worker_id 为 key，WorkerInfo 为 value 的字典
_worker_infos: Dict[str, WorkerInfo] = {}
_worker_infos_lock = threading.Lock()

# 以 task_id 为 key，AsyncExecuteResult 为 value 的字典，并且字典中元素顺序按照插入字典顺序排列
_results: OrderedDict = OrderedDict()
_results_lock = threading.Lock()


@fitable(generic_id="com.huawei.fit.get.running.async.task.count", fitable_id='local-worker')
def get_running_task_count() -> int:
    return _running_task_count.get_value()


@fitable(generic_id="com.huawei.fit.get.last.success.time", fitable_id='local-worker')
def get_last_success_time() -> float:
    return _last_success_time


@fitable(generic_id="com.huawei.fit.get.earliest.start.time", fitable_id='local-worker')
def get_earliest_start_time() -> Optional[float]:
    start_times = list(_start_serve_times.keys())
    if len(start_times) == 0:
        return None
    return start_times[0]


def _convert_response_meta_and_task_id_to_headers(metadata: ResponseMetadata, task_id: Optional[str]) -> Dict[str, str]:
    """
    构造提交任务请求以及长轮询请求时的响应头，其中 FIT-Async-Task-Id 为可选字段。
    """
    tlvs = {
        TagLengthValuesUtil.INSTANCE_ID: get_runtime_instance_id(),
        TagLengthValuesUtil.WORKER_ID: get_runtime_worker_id()
    }
    if task_id is not None:
        tlvs[TagLengthValuesUtil.TASK_ID] = task_id
    return {
        HttpHeader.FORMAT.value: f"{metadata.data_format}",
        HttpHeader.DEGRADABLE.value: f"{metadata.degradable}",
        HttpHeader.CODE.value: f"{metadata.code}",
        HttpHeader.MESSAGE.value: metadata.msg,
        HttpHeader.TLV.value: base64.b64encode(TagLengthValuesUtil.serialize(tlvs))
    }


def _build_polling_failed_response_headers(code: int, message: str) -> Dict[str, str]:
    """
    构造长轮询请求无法正常返回任务结果时的响应头，只需要错误码和错误信息两个字段。
    """
    return {HttpHeader.CODE.value: f"{code}", HttpHeader.MESSAGE.value: f"{message}"}


def _create_and_get_result_queue(worker_id: str, instance_id: str) -> Queue:
    """
    在有异步任务提交时，根据指定的 worker_id 和 instance_id 获取结果队列，
    如果当前 worker_id 不存在或者 instance_id 与先前记录的不一致则创建新的结果队列。
    """
    with _worker_infos_lock:
        if worker_id not in _worker_infos:
            _worker_infos[worker_id] = WorkerInfo(instance_id, Queue())
        if _worker_infos.get(worker_id).instance_id != instance_id:
            sys_plugin_logger.info(f"discard old instance. [worker_id={worker_id}, instance_id={instance_id}]")
            _worker_infos[worker_id] = WorkerInfo(instance_id, Queue())
        return _worker_infos.get(worker_id).result_queue


def _get_result_queue(worker_id: str, instance_id: str) -> Optional[Queue]:
    """
    在有异步任务轮询时，根据指定的 worker_id 和 instance_id 获取结果队列，
    如果当前 worker_id 不存在或者 instance_id 与先前记录的不一致则返回 None。
    """
    with _worker_infos_lock:
        if worker_id not in _worker_infos or _worker_infos.get(worker_id).instance_id != instance_id:
            return None
        return _worker_infos.get(worker_id).result_queue


class HealthCheckHandler(tornado.web.RequestHandler):
    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    async def get(self):
        self.set_status(HTTPStatus.OK)
        self.write("OK")


@fit("com.huawei.fit.get.all.plugins.ready")
def get_all_plugins_ready() -> str:
    pass


class PluginsReadyCheckHandler(tornado.web.RequestHandler):
    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    @tornado.concurrent.run_on_executor
    def process_plugins_ready_task(self):
        try:
            return get_all_plugins_ready(), HTTPStatus.OK, {}
        except FitException:
            return "NOT OK", HTTPStatus.OK, {}

    async def get(self):
        body, status, headers = await self.process_plugins_ready_task()
        self.set_status(status)
        for key in headers:
            self.set_header(key, headers[key])
        self.write(body)


class PollingTaskHandler(tornado.web.RequestHandler):
    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    @tornado.concurrent.run_on_executor
    def process_polling_request(self):
        tlvs = TagLengthValuesUtil.deserialize(base64.b64decode(self.request.headers.get(HttpHeader.TLV.value)))
        worker_id = tlvs.get(TagLengthValuesUtil.WORKER_ID)
        instance_id = tlvs.get(TagLengthValuesUtil.INSTANCE_ID)
        is_https = self.request.protocol == "https"
        sys_plugin_logger.info(f"{'HTTPS' if is_https else 'HTTP'} GET {get_context_path()}/fit/async/await-response, "
                               f"worker_id={worker_id}, instance_id={instance_id}")
        finished_task_queue = _get_result_queue(worker_id, instance_id)
        if finished_task_queue is None:
            code = InternalErrorCode.ASYNC_TASK_NOT_FOUND.value
            message = "async task not found."
            response_headers = _build_polling_failed_response_headers(code, message)
            return "", HTTPStatus.OK, response_headers
        try:
            finished_task_id: Optional[str] = finished_task_queue.get(block=True, timeout=get_polling_wait_time())
        except Empty:
            finished_task_id = None
        if finished_task_id is None:
            code = InternalErrorCode.ASYNC_TASK_NOT_COMPLETED.value
            message = "async task not completed."
            response_headers = _build_polling_failed_response_headers(code, message)
            return "", HTTPStatus.OK, response_headers
        with _results_lock:
            result: AsyncExecuteResult = _results.pop(finished_task_id, None)
        if result is None:  # 此时表示该 task 实际上对应的 worker 已经长时间没有访问过自己，被视为废弃了。
            code = InternalErrorCode.ASYNC_TASK_NOT_FOUND.value
            message = "async task not found."
            response_headers = _build_polling_failed_response_headers(code, message)
            sys_plugin_logger.warning(f"client resurrection. [worker_id={worker_id}, instance_id={instance_id}]")
            return "", HTTPStatus.OK, response_headers
        response_headers = _convert_response_meta_and_task_id_to_headers(result.meta, finished_task_id)
        if result.meta.code == FIT_OK:
            global _last_success_time
            _last_success_time = time.time()
        return result.data, HTTPStatus.OK, response_headers

    async def get(self):
        body, status, headers = await self.process_polling_request()
        self.set_status(status)
        for key in headers:
            self.set_header(key, headers[key])
        self.write(body)


def _clear_expired_result():
    with _results_lock:
        while len(_results) != 0 and next(
                iter(_results.values())).finished_time + get_result_save_duration() < time.time():
            result: AsyncExecuteResult = _results.popitem()[1]
            sys_plugin_logger.info(f"async task result discard. [task_id={result.task_id}]")


def _async_serve_response(metadata: RequestMetadata, data: bytes, task_id: str, finished_task_queue: Queue):
    response: FitResponse = server_response(metadata, data)
    with _results_lock:
        _results[task_id] = AsyncExecuteResult(task_id, response.metadata, response.data, time.time())
    finished_task_queue.put(task_id)
    _running_task_count.decrease()


class FitHandler(tornado.web.RequestHandler):

    @classmethod
    def is_sync_request(cls, worker_id: str, instance_id: str, task_id: str):
        if worker_id is None or len(worker_id) == 0:
            return True
        if instance_id is None or len(instance_id) == 0:
            return True
        if task_id is None or len(task_id) == 0:
            return True
        return False

    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    def convert_request_to_metadata(self, genericable_id: str, fitable_id: str):
        headers = self.request.headers
        try:
            data_format = int(headers.get(HttpHeader.FORMAT.value))
        except KeyError as cause:
            raise Exception("cannot find the data format field in headers.") from cause
        try:
            genericable_version = GenericVersion.from_string(headers[HttpHeader.GENERICABLE_VERSION.value])
        except KeyError as cause:
            raise Exception("cannot find the genericable version field in headers.") from cause
        tlvs = TagLengthValuesUtil.deserialize(base64.b64decode(headers.get(HttpHeader.TLV.value, b"")))
        return RequestMetadata(data_format, genericable_version, genericable_id, fitable_id, tlvs)

    @tornado.concurrent.run_on_executor
    def process_task_submit_request(self, genericable_id: str, fitable_id: str):
        start_serve_time = time.time()
        _start_serve_times[start_serve_time] = None
        is_https = self.request.protocol == "https"
        payload = self.request.body
        request_meta = self.convert_request_to_metadata(genericable_id, fitable_id)
        worker_id = request_meta.tlv_data.get(TagLengthValuesUtil.WORKER_ID)
        instance_id = request_meta.tlv_data.get(TagLengthValuesUtil.INSTANCE_ID)
        task_id = request_meta.tlv_data.get(TagLengthValuesUtil.TASK_ID)
        sys_plugin_logger.info(f"{'HTTPS' if is_https else 'HTTP'} POST {get_context_path()}/fit/"
                               f"{genericable_id}/{fitable_id} , worker_id={worker_id}, instance_id={instance_id}, "
                               f"task_id={task_id}")
        if self.is_sync_request(worker_id, instance_id, task_id):
            result = server_response(request_meta, payload)
            response_headers = _convert_response_meta_and_task_id_to_headers(result.metadata, None)
            if result.metadata.code == FIT_OK:
                global _last_success_time
                _last_success_time = time.time()
            _start_serve_times.pop(start_serve_time)
            return result.data, HTTPStatus.OK, response_headers
        if _running_task_count.get_value() >= get_task_count_limit():
            code = InternalErrorCode.ASYNC_TASK_NOT_ACCEPTED.value
            message = f"async task not accepted. [task_count_limit={get_task_count_limit()}]"
            response_meta = ResponseMetadata(request_meta.data_format, True, code, message, {})
            response_headers = _convert_response_meta_and_task_id_to_headers(response_meta, task_id)
            _start_serve_times.pop(start_serve_time)
            return "", HTTPStatus.ACCEPTED, response_headers
        _clear_expired_result()
        finished_task_queue = _create_and_get_result_queue(worker_id, instance_id)
        _running_task_count.increase()
        _executor.submit(_async_serve_response, request_meta, payload, task_id, finished_task_queue)
        response_meta = ResponseMetadata(request_meta.data_format, False, FIT_OK, "", {})
        response_headers = _convert_response_meta_and_task_id_to_headers(response_meta, task_id)
        _start_serve_times.pop(start_serve_time)
        return "", HTTPStatus.ACCEPTED, response_headers

    async def post(self, genericable_id: str, fitable_id: str):
        body, status, headers = await self.process_task_submit_request(genericable_id, fitable_id)
        self.set_status(status)
        for key in headers:
            self.set_header(key, headers[key])
        self.write(body)


class WebSocketHandler(tornado.websocket.WebSocketHandler):
    # 仅预留待完善的 websocket 事件处理接口，后续再进行完善。
    def initialize(self, executor: ThreadPoolExecutor):
        self.executor: ThreadPoolExecutor = executor

    def open(self, genericable_id: str, fitable_id: str):
        sys_plugin_logger.info(f"[websocket] connected. [genericable_id={genericable_id}, "
                               f"fitable_id={fitable_id}]")

    async def on_message(self, message):
        sys_plugin_logger.info(f"[websocket] received message. [message={message}]")

        def process_message(message):
            return message

        result = await tornado.ioloop.IOLoop.current().run_in_executor(_executor, process_message, message)
        await self.write_message(result)

    def on_close(self):
        sys_plugin_logger.info(f"[websocket] closed [close_code={self.close_code}, close_reason={self.close_reason}]")


def init_fit_http_server(port):
    global _http_server
    _http_server = tornado.httpserver.HTTPServer(_app)
    _http_server.listen(port)


def init_fit_https_server(port):
    global _https_server
    try:
        ssl_context = ssl.SSLContext(protocol=ssl.PROTOCOL_TLSv1_2)
        if get_server_verify_enabled():
            ssl_context.verify_mode = ssl.CERT_REQUIRED
            ssl_context.check_hostname = get_server_assert_host_name()
            ssl_context.load_verify_locations(cafile=get_server_ca_path())
        ssl_context.load_cert_chain(certfile=get_server_crt_path(), keyfile=get_server_key_path(),
                                    password=get_decrypted_key_file_password())
    except FileNotFoundError:
        sys_plugin_logger.error("file for https server is missing.")
        raise
    _https_server = tornado.httpserver.HTTPServer(_app, ssl_options=ssl_context)
    _https_server.listen(port)


_executor = ThreadPoolExecutor(get_server_thread_count())
base_patterns = [
    (r"/fit/health", HealthCheckHandler),
    (r"/fit/pluginsReady", PluginsReadyCheckHandler),
    (r"/fit/async/await-response", PollingTaskHandler),
    (r"/fit/([^/]+)/([^/]+)", FitHandler),
    (r"/fit/websocket/([^/]+)/([^/]+)", WebSocketHandler)
]

# 使用列表推导式添加带有 context path 和不带 context path 的路径
patterns = [(get_context_path() + pattern[0], pattern[1], dict(executor=_executor)) for pattern in base_patterns] + [
    (pattern[0], pattern[1], dict(executor=_executor)) for pattern in base_patterns]

_app = tornado.web.Application(patterns)


def start_all_server():
    global _server_thread
    _server_thread = threading.Thread(target=tornado.ioloop.IOLoop.instance().start, name="HttpServerThread")
    _server_thread.start()


def stop_all_server():
    global _http_server
    global _https_server
    global _server_thread
    if _https_server is not None:
        _https_server.stop()
    if _http_server is not None:
        _http_server.stop()
    tornado.ioloop.IOLoop.instance().stop()
    _server_thread.join()
