# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Fit底座Http Server服务接口依赖的工具模块
"""
import threading
from typing import Tuple

from fit_common_struct.registry_v1 import AddressForRegistryV1

from fitframework import const
from fitframework.api.decorators import fit, value, run_once
from fitframework.core.network.enums import ProtocolEnum, SerializingStructureEnum
from fitframework.core.network.fit_response import FitResponse
from fitframework.utils.tools import b64decode_from_str, to_list, \
    to_bool, get_free_tcp_port


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


@fit(generic_id=const.SERVER_RESPONSE_GEN_ID, alias='python_default_server_response')
def server_response(metadata: bytes, data: bytes) -> FitResponse:  # TODO：修改函数签名
    pass


@fit(const.RUNTIME_GET_WORKER_ID_GEN_ID)
def get_runtime_worker_id() -> str:
    pass


def http_server_start(address: AddressForRegistryV1, event: threading.Event = None):
    if event:
        event.set()


def _convert_to_bytes(metadata_str: str, data_str: str) -> Tuple[bytes, bytes]:
    return b64decode_from_str(metadata_str), b64decode_from_str(data_str)


if __name__ == '__main__':
    http_server_start(AddressForRegistryV1('127.0.0.1', 8000, None, None, None, 'test'))
