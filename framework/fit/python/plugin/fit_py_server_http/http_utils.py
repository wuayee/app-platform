# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Fit底座Http Server服务接口依赖的工具模块
"""
import threading
from queue import Queue
from typing import Tuple

from fit_common_struct.registry_v1 import AddressForRegistryV1

from fitframework import const
from fitframework.api.decorators import fit, value, run_once
from fitframework.core.network.enums import ProtocolEnum, SerializingStructureEnum
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.metadata.request_metadata import RequestMetadata
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.utils.tools import b64decode_from_str, to_list, \
    to_bool, get_free_tcp_port


class WorkerInfo:
    def __init__(self, instance_id: str, result_queue: Queue):
        self.instance_id = instance_id
        self.result_queue = result_queue


class AsyncExecuteResult:
    def __init__(self, task_id: str, meta: ResponseMetadata, data: bytes, finished_time):
        self.task_id = task_id
        self.meta = meta
        self.data = data
        self.finished_time = finished_time


@value('local_ip')
def get_host():
    pass


@value(key='worker-environment.env')
def get_worker_env():
    pass


@value(key='context-path', default_value="")
def get_context_path():
    pass


@value('server-thread-count', 8, converter=int)
def get_server_thread_count():
    pass


@value('http.server.enabled', False, converter=to_bool)
def get_http_enabled():
    pass


@value('http.server.address.protocol', default_value=ProtocolEnum.HTTP.value, converter=int)
def get_http_protocol():
    pass


@value('http.server.address.formats', default_value=[SerializingStructureEnum.JSON.value], converter=to_list)
def get_http_formats():
    pass


@value('http.server.address.use-random-port', converter=to_bool)
def _get_http_use_random_port():
    pass


@value('http.server.address.port', converter=int)
def _get_http_server_port():
    pass


@value('http.server.address.port-to-register', converter=int, default_value=_get_http_server_port())
def get_http_server_port_to_register():
    pass


@value('https.server.enabled', default_value=False, converter=to_bool)
def get_https_enabled():
    pass


@value('https.server.crt_path')
def get_server_crt_path():
    pass


@value('https.server.key_path')
def get_server_key_path():
    pass


@value('https.server.key_file_encrypted')
def get_server_key_file_encrypted():
    pass


@value('https.server.key_file_password')
def get_server_key_file_password():
    pass


@value('https.server.key_file_password_scc_encrypted')
def get_server_key_file_password_scc_encrypted():
    pass


@value('https.server.verify_enabled', converter=to_bool)
def get_server_verify_enabled():
    pass


@value('https.server.ca_path')
def get_server_ca_path():
    pass


@value('https.server.assert_host_name')
def get_server_assert_host_name():
    pass


@value('https.server.address.protocol', default_value=ProtocolEnum.HTTPS.value, converter=int)
def get_https_protocol():
    pass


@value('https.server.address.formats', default_value=[SerializingStructureEnum.JSON.value], converter=to_list)
def get_https_formats():
    pass


@value('https.server.address.use-random-port', converter=to_bool)
def _get_https_use_random_port():
    pass


@value('https.server.address.port', converter=int)
def _get_https_server_port():
    pass


@value('https.server.address.port-to-register', converter=int, default_value=_get_https_server_port())
def get_https_server_port_to_register():
    pass


@value('async.task-count-limit', converter=int)
def get_task_count_limit() -> int:
    pass


@value('async.result-save-duration', converter=int)
def get_result_save_duration():
    pass


@value('async.polling-wait-time', converter=int)
def get_polling_wait_time():
    pass


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


@fit(const.RUNTIME_GET_WORKER_ID_GEN_ID)
def get_runtime_worker_id() -> str:
    pass


@fit(const.RUNTIME_GET_WORKER_INSTANCE_ID_GEN_ID)
def get_runtime_instance_id() -> str:
    pass


@run_once
def get_http_server_port() -> int:
    if _get_http_use_random_port():
        return get_free_tcp_port()
    else:
        return _get_http_server_port()


@run_once
def get_https_server_port() -> int:
    if _get_https_use_random_port():
        return get_free_tcp_port()
    else:
        return _get_https_server_port()


def get_decrypted_key_file_password():
    # 对于 https 服务端，不存在不需要客户端验证自身的情况。
    if not get_server_key_file_encrypted():  # 如果私钥未被加密
        return None
    if not get_server_key_file_password_scc_encrypted():  # 如果私钥密码没有被加密
        return get_server_key_file_password()

    return decrypt(get_server_key_file_password())


def http_server_start(address: AddressForRegistryV1, event: threading.Event = None):
    if event:
        event.set()


def _convert_to_bytes(metadata_str: str, data_str: str) -> Tuple[bytes, bytes]:
    return b64decode_from_str(metadata_str), b64decode_from_str(data_str)


if __name__ == '__main__':
    http_server_start(AddressForRegistryV1('127.0.0.1', 8000, None, None, None, 'test'))
