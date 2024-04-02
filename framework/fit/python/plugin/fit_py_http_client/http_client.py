# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：HTTP客户端
"""
import functools
from typing import Dict
import requests
import urllib3
from urllib3.connection import HTTPSConnection

from fit_common_struct.core import Address
from fitframework import const
from fitframework.api.decorators import fitable, fit, value
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.exception.fit_exception import NetworkException
from fitframework.core.network.enums import ProtocolEnum
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.metadata.request_metadata import RequestMetadata
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.core.network.temp_entity import RequestContext
from fitframework.utils.tools import b64decode_from_str, to_bool

_DEFAULT_HEADERS = {
    'content-type': 'application/json',
    'Accept': '*/*',
}

_KEY_FILE_DECRYPTED = False

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


@fit(const.SERVICE_DB_REGISTER_CLIENT_GEN_ID)
def register_client(client_protocol_id: int) -> None:
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


@value('client.timeout', 10 * 60)
def _get_timeout():
    pass


@value("https.client.verify_enabled", converter=to_bool)
def get_client_verify_enabled():
    pass


@value('https.client.ca_path')
def get_client_ca_path():
    pass


@value('https.client.assert_host_name')
def get_client_assert_host_name():
    pass


@value("https.client.cert_enabled", converter=to_bool)
def get_cert_enabled():
    pass


@value('https.client.crt_path')
def get_client_crt_path():
    pass


@value('https.client.key_path')
def get_client_key_path():
    pass


@value('https.client.key_file_encrypted')
def get_client_key_encrypted():
    pass


@value('https.client.key_file_password')
def get_client_key_file_password():
    pass


@value('https.client.key_file_password_scc_encrypted')
def get_client_key_file_password_scc_encrypted():
    pass


def build_request_headers(metadata: RequestMetadata) -> Dict:
    return {'FIT-Version': str(metadata.version),
            'FIT-Data-Format': str(metadata.data_format),
            'FIT-Genericable-Version': f"{metadata.generic_version.major}.{metadata.generic_version.minor}." +
                                       f"{metadata.generic_version.revision}"}


def convert_http_response_to_fit_response(response: requests.Response) -> FitResponse:
    if response.headers.get("FIT-Metadata") is not None:
        response_metadata = ResponseMetadata.deserialize(b64decode_from_str(response.headers.get("FIT-Metadata")))
    else:
        if response.status_code != requests.codes.ok:
            raise requests.HTTPError(response=response)
        version = int(response.headers.get("FIT-Version"))
        data_format = int(response.headers.get("FIT-Data-Format"))
        degradable = bool(response.headers.get("FIT-Degradable"))
        code = int(response.headers.get("FIT-Code"))
        message = ""
        if not response.headers.get("FIT-Message") is None:
            message = response.headers.get("FIT-Message")
        response_metadata = ResponseMetadata(data_format, version, degradable, code, message, {})  # 后续完善 TLV 相关逻辑
    return FitResponse(response_metadata, response.content)


@fitable(const.REQUEST_RESPONSE_GEN_ID, const.HTTP_REQUEST_RESPONSE_FITABLE_ID)
def request_response_http(remote_address: Address, context_path: str, metadata: RequestMetadata, data_bytes: bytes,
                          context: RequestContext) -> FitResponse:
    headers = build_request_headers(metadata)
    url = f"http://{remote_address.host}:{remote_address.port}{context_path}/fit/" + \
          f"{metadata.generic_id}/{metadata.fitable_id}"
    try:
        http_response = requests.post(url, headers=headers, data=data_bytes, timeout=context.timeout)
    except requests.RequestException as err:
        sys_plugin_logger.exception(err)
        raise NetworkException(err) from None
    return convert_http_response_to_fit_response(http_response)


@functools.lru_cache()
def get_decrypted_key_file_password():
    if not get_cert_enabled():  # 如果不需要服务端校验自身身份
        return None
    if not get_client_key_encrypted():  # 如果私钥未被加密
        return None
    if not get_client_key_file_password_scc_encrypted():  # 如果私钥密码未被加密
        return get_client_key_file_password()

    return decrypt(get_client_key_file_password())


@functools.lru_cache()
def get_cert():
    if not get_cert_enabled():  # 如果不需要服务端校验自身身份
        return None
    crt_file_path = get_client_crt_path()
    key_file_path = get_client_key_path()
    return crt_file_path, key_file_path


@functools.lru_cache()
def get_verify():
    if get_client_verify_enabled():
        return get_client_ca_path()
    else:
        return False


def connection_init_wrapper(func):
    def wrapper(*args, **kwargs):
        kwargs["assert_hostname"] = get_client_assert_host_name()
        kwargs["key_password"] = get_decrypted_key_file_password()
        return func(*args, **kwargs)

    return wrapper


HTTPSConnection.__init__ = connection_init_wrapper(HTTPSConnection.__init__)


@fitable(const.REQUEST_RESPONSE_GEN_ID, const.HTTPS_REQUEST_RESPONSE_FITABLE_ID)
def request_response_https(remote_address: Address, context_path: str, metadata: RequestMetadata, data_bytes: bytes,
                           context: RequestContext) -> FitResponse:
    headers = build_request_headers(metadata)
    url = f"https://{remote_address.host}:{remote_address.port}{context_path}/" + \
          f"fit/{metadata.generic_id}/{metadata.fitable_id}"
    try:
        http_response = requests.post(url, headers=headers, data=data_bytes, timeout=context.timeout, cert=get_cert(),
                                      verify=get_verify())
    except requests.RequestException as err:
        sys_plugin_logger.exception(err)
        raise NetworkException(err) from None
    return convert_http_response_to_fit_response(http_response)


register_client(ProtocolEnum.HTTP.value)
register_client(ProtocolEnum.HTTPS.value)
