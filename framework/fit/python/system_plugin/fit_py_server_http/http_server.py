# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Fit底座Server服务接口实现：HTTP，目前HTTP服务绑定JSON协议。
"""

from fit_common_struct.registry_v1 import AddressForRegistryV1
from fitframework import const
from fitframework.api.decorators import fit, fitable, register_event
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.network.enums import ProtocolEnum
from fitframework.api.enums import FrameworkEvent

from .http_utils import get_http_formats, get_http_protocol, get_host, get_http_server_port, get_http_enabled, \
    get_https_enabled, worker_env, get_https_server_port, get_https_protocol, get_https_formats, get_runtime_worker_id
from .fit_http_server import FitHttpServer, FitHttpsServer

_MAX_TIMEOUT = 30

_http_server: FitHttpServer = None
_https_server: FitHttpsServer = None


@fit(const.SERVICE_DB_REGISTER_SERVER_GEN_ID)
def register_server(server_id: int, local_address: AddressForRegistryV1) -> None:
    pass


@fitable(const.FIT_SERVER_START_GEN_ID, const.FIT_SERVER_START_HTTP_FITABLE_ID)
def server_start():
    """
    接口实现应包含以下步骤：
    1. 启动一个新线程
    2. 线程中启动HTTP监听服务，服务绑定在配置IP/Port，监听配置URI上的Requests
    3. 主流程等待服务启动完成
    4. service_db注册本服务器
    """
    global _http_server
    if get_http_enabled():
        addr = get_http_server_address()
        sys_plugin_logger.info(f"http server started at 0.0.0.0:{addr.port}.")
        register_server(ProtocolEnum.HTTP.value, addr)
        _http_server = FitHttpServer('0.0.0.0', addr.port)
        _http_server.start()

    global _https_server
    if get_https_enabled():
        addr = get_https_server_address()
        sys_plugin_logger.info(f"https server started at 0.0.0.0:{addr.port}.")
        register_server(ProtocolEnum.HTTPS.value, addr)
        _https_server = FitHttpsServer('0.0.0.0', addr.port)
        _https_server.start()


@register_event(FrameworkEvent.FRAMEWORK_STOPPING)
@fitable(const.FIT_SERVER_STOP_GEN_ID, const.FIT_SERVER_STOP_HTTP_FITABLE_ID)
def server_stop():
    global _http_server
    if get_http_enabled():
        sys_plugin_logger.info("http server stopped.")
        _http_server.stop()

    global _https_server
    if get_https_enabled():
        sys_plugin_logger.info("https server stopped.")
        _https_server.stop()


def get_http_server_address() -> AddressForRegistryV1:
    """
    获取本机http server地址
    address.host: 优先读取插件配置：local.address。如果配置不存在，主动获得本机地址：utils.get_local_address()
    address.port: 读取插件配置：local.port
    address.id: <host>:<port>
    address.protocol: HTTP, 参考utils.ProtocolEnum
    address.formats: PROTOBUF, 参考utils.FormatEnum
    """
    return AddressForRegistryV1(get_host(), get_http_server_port(), get_runtime_worker_id(), get_http_protocol(),
                                get_http_formats(), worker_env())


def get_https_server_address() -> AddressForRegistryV1:
    """
    获取本机https server地址
    address.host: 优先读取插件配置：local.address。如果配置不存在，主动获得本机地址：utils.get_local_address()
    address.port: 读取插件配置：local.port
    address.id: <host>:<port>
    address.protocol: HTTP, 参考utils.ProtocolEnum
    address.formats: PROTOBUF, 参考utils.FormatEnum
    """
    return AddressForRegistryV1(get_host(), get_https_server_port(), get_runtime_worker_id(), get_https_protocol(),
                                get_https_formats(), worker_env())
