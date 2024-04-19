# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：远程调用模块
"""
from pprint import pformat
from typing import List

from fit_common_struct.core import Address, Endpoint
from fit_common_struct.registry_v1 import AddressForRegistryV1 as RegistryAddress
from fit_common_struct.registry_v1 import FitableForRegistryV1

from fitframework import const
from fitframework.api.logging import fit_logger, FileHandler
from fitframework.api.decorators import fit, local_context
from fitframework.api.exception import FIT_OK
from fitframework.utils.context import call_context
from fitframework.core.broker.broker_utils import get_priority_index
from fitframework.utils.context.call_context import ROUTING_PROTOCOL
from fitframework.core.exception.fit_exception import FitException, NetworkException
from fitframework.core.network.enums import ProtocolEnum, SerializingStructureEnum
from fitframework.core.network.fit_method_serializer import FitMethodSerializer
from fitframework.core.network.fit_response import FitResponse
from fitframework.core.network.metadata.request_metadata import RequestMetadata
from fitframework.core.network.metadata.response_metadata import ResponseMetadata
from fitframework.core.network.temp_entity import RequestContext
from fitframework.utils.tools import to_list

default_priority = [
    f'{ProtocolEnum.GRPC.name}:{SerializingStructureEnum.PROTOBUF.name}',
    f'{ProtocolEnum.HTTPS.name}:{SerializingStructureEnum.PROTOBUF.name}',
    f'{ProtocolEnum.HTTP.name}:{SerializingStructureEnum.PROTOBUF.name}',
    f'{ProtocolEnum.GRPC.name}:{SerializingStructureEnum.CBOR.name}',
    f'{ProtocolEnum.HTTPS.name}:{SerializingStructureEnum.CBOR.name}',
    f'{ProtocolEnum.HTTP.name}:{SerializingStructureEnum.CBOR.name}',
    f'{ProtocolEnum.GRPC.name}:{SerializingStructureEnum.JSON.name}',
    f'{ProtocolEnum.HTTPS.name}:{SerializingStructureEnum.JSON.name}',
    f'{ProtocolEnum.HTTP.name}:{SerializingStructureEnum.JSON.name}'
]


@local_context(key='worker.protocol-priorities', default_value=default_priority, converter=to_list)
def protocol_priors():
    pass


@fit(const.REQUEST_RESPONSE_GEN_ID)
def request_response(address: Address, metadata: RequestMetadata, data: bytes,
                     request_context: RequestContext) -> FitResponse:
    pass


@fit(const.SERVICE_DB_GET_REGISTER_FORMATS_GEN_ID)
def get_registered_formats() -> List[int]:
    pass


@fit(const.MARK_ADDRESS_STATUS_GEN_ID, alias='set_fitable_status_python')
def set_fitable_instance_status(_fitable: FitableForRegistryV1, _address: RegistryAddress, _valid: bool) -> bool:
    pass


def get_supported_formats(formats: list) -> list:
    return list(set(get_registered_formats()).intersection(set(formats)))


def call(remote_endpoint: Endpoint, fit_invoke_info: tuple, args, timeout: int):
    generic_id, fitable_id, fit_ref = fit_invoke_info
    supported_format = _get_supported_format(remote_endpoint)
    try:
        params = fit_invoke_info, args, remote_endpoint, supported_format, timeout
        fit_response = request_response(*_assemble_parameters(*params))
        _validate_result(fit_response.metadata)
        return _process_result(fit_ref, fit_response.data, supported_format)
    except NetworkException:
        info = f"error occurred in remote invoke towards: {remote_endpoint} with generic id: {generic_id}"
        fit_logger.error(info + f" and args: {pformat(args, depth=1)}")
        fit_logger.error(
            info + f"\n\tfit_ref: {fit_ref}\n\tfitable_id: {fitable_id}\n\tformat: {supported_format}"
                   f"\n\ttimeout: {timeout}\n\targs: {pformat(args)}", dests=FileHandler)
        _set_fit_address_invalid(remote_endpoint)
        raise


def _get_supported_format(remote_endpoint: Endpoint) -> int:
    supported_formats = get_supported_formats(remote_endpoint.serializeFormats)
    if len(supported_formats) == 1:
        return supported_formats[0]

    def sort_rule(_format):
        return get_priority_index(remote_endpoint.protocol, [_format], protocol_priors())

    supported_formats.sort(key=sort_rule)
    return supported_formats[0]


def _assemble_parameters(fit_invoke_info, args, remote_endpoint, supported_format, timeout):
    generic_id, fitable_id, fit_ref = fit_invoke_info
    call_context.put_context_value(ROUTING_PROTOCOL, remote_endpoint.protocol)
    address = remote_endpoint.address
    metadata = RequestMetadata.default(generic_id, fitable_id, supported_format)
    data_bytes = FitMethodSerializer(fit_ref).from_request(supported_format, list(args))
    request_context = RequestContext(timeout)
    return address, metadata, data_bytes, request_context


def _validate_result(resp_metadata: ResponseMetadata):
    if resp_metadata.code != FIT_OK:
        raise FitException(resp_metadata.code, resp_metadata.msg,
                           resp_metadata.is_err_degradable)


def _process_result(fit_ref, return_data, supported_format):
    return FitMethodSerializer(fit_ref).to_return_value(supported_format, return_data)


def _set_fit_address_invalid(remote_address: Endpoint):
    address = _build_address_from_endpoint(remote_address)
    set_fitable_instance_status(None, address, False)


def _build_address_from_endpoint(endpoint: Endpoint) -> RegistryAddress:
    return RegistryAddress(host=endpoint.address.host, port=endpoint.address.port, id=endpoint.address.workerId,
                           protocol=endpoint.protocol, formats=endpoint.serializeFormats,
                           environment=endpoint.environment, context_path=endpoint.context_path)
