# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Fit底座Registry客户端接口支持
"""
import hashlib
import sys
import traceback
from collections import defaultdict
from pprint import pformat
from typing import List

from fit_common_struct.registry_v1 import AddressForRegistryV1 as OldAddress
from fit_common_struct.registry_v1 import FitableForRegistryV1 as OldFitable
from fit_common_struct.registry_v1 import ServiceMetaForRegistryV1

from fitframework import const
from fitframework.api.decorators import fitable, fit, register_event, scheduled_executor, \
    private_fitable, value
from fitframework.api.enums import FrameworkEvent
from fitframework.api.logging import sys_plugin_logger
from fitframework.core.network.enums import SerializingStructureEnum
from fitframework.utils.scheduler import TimerDict
from fitframework.utils.tools import string_normalize
from .entity import AddressForRegistryV2, Application, Endpoint, FitableInstance, FitableMeta, Worker, FitableInfo, \
    FitableMetaInstance, GenericableInfo
from .entity import ServiceAddress as OldServiceAddress
from .registry_client_config import \
    get_registry_addresses_from_configuration, _registry_server_generic_ids, \
    registry_client_mode, PULL_MODE, registry_pull_frequency, registry_fitable_frequency, \
    invalid_address_ttl, clear_expired_invalid_address_frequency
from .registry_repo import get_addresses_from_cache, service_meta_cache, update_addresses_in_cache
from .server_address_util import get_address

_USING_NEW_COMMON_STRUCT = True

_GET_REGISTRY_FITABLE_ALIAS = {
    'fit': 'get_registry_address_fit_python',
    'om': 'get_registry_address_om_python',
    'default': 'get_registry_address_fit_python',
}

# 失效地址缓存
# 目前注册中心返回的地址中id为空,后续提供id后，用id以及protocol
# 添加保证清空的机制，比如间隔一天一次
invalid_address_cache = TimerDict(
    lambda address: hash(tuple([address.host, address.port, address.protocol])),
    invalid_address_ttl())
# 专用于注册中心 地址缓存
_registry_address_cache = []


@value('worker-environment.env')
def worker_env():
    pass


@value('use-registry', 'fit')
def use_registry() -> str:
    pass


@value('app.name')
def app_name():
    pass


@value("registry-center.client.registered-fitables-expire-interval",
       default_value=registry_fitable_frequency() * 3, converter=int)
def registered_fitables_expire_interval():
    pass


def _alias_of_getting_registry_fitable() -> str:
    use_registry_ = string_normalize(use_registry())
    if use_registry_ in _GET_REGISTRY_FITABLE_ALIAS:
        return _GET_REGISTRY_FITABLE_ALIAS[use_registry_]
    else:
        return _GET_REGISTRY_FITABLE_ALIAS['default']


# @fit 依赖 #
@fit(const.RUNTIME_GET_WORKER_ID_GEN_ID)
def get_runtime_worker_id() -> str:
    pass


@fit(const.SERVICE_DB_GET_REGISTER_FORMATS_GEN_ID)
def get_registered_formats() -> List[SerializingStructureEnum]:
    pass


@fit(const.GET_REGISTRY_SERVER_IP_LIST_GEN_ID, _alias_of_getting_registry_fitable())
def invoke_get_registry_address() -> List[OldAddress]:
    pass


@fit(const.SUBSCRIBE_FIT_SERVICE_GEN_ID)
def subscribe_fit_service(fitables: List[OldFitable], listener_addr: OldAddress, call_back_fit_id: str) \
        -> List[OldServiceAddress]:
    """registry client -> registry server : 想服务端注册依赖的接口"""
    pass


@fit(const.UNSUBSCRIBE_FIT_SERVICE_GEN_ID)
def unsubscribe_fit_service(fitables: List[OldFitable], listener_address: OldAddress) -> bool:
    """registry client -> registry server : 取消订阅依赖的接口"""
    pass


@fit(const.SUBSCRIBE_FIT_SERVICE_V2_GEN_ID)
def subscribe_fit_service_v2(fitables: List[FitableInfo], worker_id: str, callback_fitable_id: str) \
        -> List[FitableInstance]:
    """registry client -> registry server : 想服务端注册依赖的接口"""
    pass


@fit(const.QUERY_FIT_SERVICE_V2_GEN_ID)
def query_fitable_addresses_v2(fitables: List[FitableInfo], worker_id: str) -> List[FitableInstance]:
    pass


@fit(const.REGISTER_FIT_SERVICE_GEN_ID)
def register_fit_service(services: List[ServiceMetaForRegistryV1], address: OldAddress) -> bool:
    """client->server，注册fit服务列表"""
    pass


@fit(const.REGISTER_FIT_SERVICE_V2_GEN_ID)
def register_fitables(fitable_metas: List[FitableMeta], worker: Worker, application: Application) -> None:
    """
    :param fitable_metas
    :param worker
    :param application:
    """
    pass


@fit(const.UNREGISTER_FIT_SERVICE_GEN_ID)
def unregister_fit_service(services: List[ServiceMetaForRegistryV1], address: OldAddress) -> bool:
    """client->server，取消注册fit服务列表"""
    pass


@fit(const.QUERY_FITABLE_METAS_V2_GEN_ID)
def query_fitable_metas_v2(genericables: List[GenericableInfo]) -> List[FitableMetaInstance]:
    pass


@fit(const.SERVICE_DB_GET_ALL_ADDRESS_GEN_ID)
def get_server_addresses() -> List[OldAddress]:
    pass


@fit(const.SERVICE_DB_REGISTER_ALL_FIT_SERVICE_GEN_ID)
def register_all_fit_services() -> bool:
    """service db 提供的服务 """
    pass


# fitable 实现 #
@fitable(const.NOTIFY_FIT_SERVICE_GEN_ID, const.NOTIFY_FIT_SERVICE_FITABLE_ID)
def notify_fitable_changes(full_address_list: List[OldServiceAddress]) -> bool:
    """
    registry server 通知client依赖的fitable，client收到后更新本地缓存

    :param full_address_list:
    :return:
    """
    sys_plugin_logger.debug(
        "receive fitable changes %s" % {','.join([str(address) for address in full_address_list])})
    if full_address_list is None:
        return True
    for service_address in full_address_list:
        update_addresses_in_cache(
            service_address.serviceMeta.fitable,
            service_address.addressList)
    return True


@fitable(const.NOTIFY_FIT_SERVICE_V2_GEN_ID, const.NOTIFY_FIT_SERVICE_V2_FITABLE_ID)
def notify_fitable_changes_v2(fitable_inst_s: List[FitableInstance]) -> None:
    """
    registry server 通知client依赖的fitable，client收到后更新本地缓存

    :param full_address_list:
    :return:
    """
    sys_plugin_logger.info(
        "receive new fitable changes %s" % {','.join([str(address) for address in fitable_inst_s])})
    if fitable_inst_s:
        for fitable_inst in fitable_inst_s:
            old_service_addr = _convert_fitable_inst_to_service_address(fitable_inst)
            update_addresses_in_cache(old_service_addr.serviceMeta.fitable, old_service_addr.addressList)


@fitable(const.GET_FIT_SERVICE_ADDRESS_LIST_GEN_ID, const.GET_FIT_SERVICE_ADDRESS_LIST_FITABLE_ID)
def get_fit_service_address_list(_fitable: OldFitable) -> List[OldAddress]:
    """
    1、如果是registry_server对应的接口，address从配置中取
    2、本地缓存有，代表已经注册过
    3、本地缓存没有，进行订阅，订阅后缓存
    Args:
        _fitable (FitableInfo):

    Returns:
        该fitable的地址列表
    """
    sys_plugin_logger.debug(f"get fit service address list: gid{_fitable.genericId}")
    if is_registry_server_api(_fitable):
        sys_plugin_logger.debug(f"get fit service address list: gid{_fitable.genericId}, is registry server api")
        return get_cache_aware_registry_address()
    addresses = get_addresses_from_cache(_fitable)
    if not addresses:
        addresses = get_fitable_address_from_registry_server_and_update_cache(_fitable)
    return addresses


@fitable(const.GET_FITABLES_OF_GENERICABLE_GEN_ID, const.GET_FITABLES_OF_GENERICABLE_FIT_ID)
def get_all_fitables_from_registry(genericable_id: str) -> List[str]:
    fitable_meta_instances: List[FitableMetaInstance] = query_fitable_metas_v2(
        [GenericableInfo(genericable_id, const.FIXED_GENERICABLE_VERSION)])
    result = []
    for instance in fitable_meta_instances:
        if instance.meta.fitable.genericableId != genericable_id:
            continue
        result.append(instance.meta.fitable.fitableId)
    return result


def is_registry_server_api(_fitable: OldFitable):
    return _fitable.genericId in _registry_server_generic_ids()


def is_registry_server_api_v2(_fitable: FitableInfo):
    return _fitable.genericableId in _registry_server_generic_ids()


def get_fitable_address_from_registry_server_and_update_cache(_fitable: OldFitable):
    try:
        service_addresses = _get_fitable_address(_fitable)
    except Exception as e:
        sys_plugin_logger.error(f"subscribe_fit_service {_fitable} error: {type(e)}")
        return []
    addresses = _get_address_list(service_addresses, _fitable)
    sys_plugin_logger.debug(f"subscribe fit service {_fitable} and get: \n{pformat(addresses)}")
    update_addresses_in_cache(_fitable, addresses)
    return addresses


@fitable(const.ONLINE_FIT_SERVICE_GEN_ID, const.ONLINE_FIT_SERVICE_V2_FITABLE_ID)
def online_fitable_services_v2(service_metas: List[ServiceMetaForRegistryV1]) -> bool:
    if service_metas:
        fitable_metas = _convert_to_fitable_metas(service_metas)
        worker = Worker(
            addresses=_fetch_all_addresses(),
            id=get_runtime_worker_id(),
            environment=worker_env(),
            extensions={"expire": f"{registered_fitables_expire_interval()}"})
        register_fitables(fitable_metas, worker, _create_application(fitable_metas))
    return True


def _convert_to_fitable_metas(service_metas: List[ServiceMetaForRegistryV1]) -> List[FitableMeta]:
    local_formats = [_.value for _ in get_registered_formats()]
    return [_convert_service_meta_to_fitable_meta(_, local_formats) for _ in service_metas]


def _convert_service_meta_to_fitable_meta(service_meta: ServiceMetaForRegistryV1,
                                          support_formats: List[int]) -> FitableMeta:
    return FitableMeta(_convert_old_fitable_to_new_fitable(service_meta.fitable), service_meta.aliases, support_formats)


def _convert_old_fitable_to_new_fitable(old_fitable: OldFitable) -> FitableInfo:
    return FitableInfo(old_fitable.genericId, old_fitable.genericVersion, old_fitable.fitId, old_fitable.fitVersion)


def _fetch_all_addresses() -> List[AddressForRegistryV2]:
    # 多网卡情形: return [_get_address_per_interface_card_ip(ip) for ip in get_all_interface_card_ips()]
    addresses_dict = defaultdict(lambda: [])
    for old_address in get_server_addresses():
        addresses_dict[old_address.host].append(Endpoint(port=old_address.port, protocol=old_address.protocol))
    result: List[AddressForRegistryV2] = []
    for host in addresses_dict.keys():
        result.append(AddressForRegistryV2(host, addresses_dict[host]))
    sys_plugin_logger.debug(f"_fetch_all_addresses, result: {result}")
    return result


def _convert_old_addr_to_endpoint(old_addr: OldAddress) -> Endpoint:
    return Endpoint(old_addr.port, old_addr.protocol)


def _create_application(fitable_metas: List[FitableMeta]) -> Application:
    return Application(app_name(), _calc_app_version(fitable_metas))


def _calc_app_version(fitable_metas: List[FitableMeta]) -> str:
    """
    an "app version" is a string which is obtained by hashing the service group as a whole.

    Args:
        fitable_metas:
    """
    _sort_fitable_metas(fitable_metas)
    _summaries = map(_calc_fitable_meta_summary, fitable_metas)
    _final_str = _concat_fitable_meta_summaries(_summaries)  # 此处为了可读性，将iterable操作拆成了两行
    return _calc_hash(_final_str)


def _sort_fitable_metas(fitable_meta_s: List[FitableMeta]):
    _ = [fitable_meta.formats.sort() for fitable_meta in fitable_meta_s]
    fitable_meta_s.sort(key=_fetch_fitable_meta_info)


def _fetch_fitable_meta_info(fitable_meta: FitableMeta) -> tuple:
    fitable_info: FitableInfo = fitable_meta.fitable
    return fitable_info.genericableId, fitable_info.fitableVersion, fitable_info.fitableId, fitable_info.fitableVersion


def _calc_fitable_meta_summary(fitable_meta: FitableMeta) -> str:
    def _calc_format_str():
        return ','.join(map(str, fitable_meta.formats))

    return ':'.join(_fetch_fitable_meta_info(fitable_meta) + (_calc_format_str(),))


def _concat_fitable_meta_summaries(fitable_metas):
    return ";".join(fitable_metas)


def _calc_hash(final_str):
    return hashlib.sha256(final_str.encode()).hexdigest()


@fitable(const.MARK_ADDRESS_STATUS_GEN_ID, const.MARK_ADDRESS_STATUS_FITABLE_ID)
def set_fitable_instance_status(
        _fitable: OldFitable, _address: OldAddress, _valid: bool) -> bool:
    """
    :param _fitable:
    :param _address:
    :param _valid:
    :return:
    """
    if not _valid:
        invalid_address_cache.put(_address)
    else:
        invalid_address_cache.remove(_address)


@fitable(const.REMOVE_FIT_LISTENER_GEN_ID, const.REMOVE_FIT_LISTENER_FIT_ID)
def remove_fit_listener(fitables: List[OldFitable]) -> bool:
    pass


@fitable(const.GET_REGISTRY_SERVER_IP_LIST_GEN_ID, const.GET_REGISTRY_SERVER_IP_LIST_FIT_ID)
def get_registry_addresses() -> List[OldAddress]:
    """
    获取注册中心的地址列表

    :return
        List[OldAddress]: 注册中心地址列表
    """
    return get_registry_addresses_from_configuration()


# 此private_fitable在Address与Endpoint字段不归一的情况下使用
@private_fitable
def is_address_valid(address: OldAddress):
    """判断该地址是否是失效的

    Args:
        address ():
    Returns: True 地址有效
             False 地址无效
    """
    return address not in invalid_address_cache


def filter_invalid_address(addresses: List[OldAddress]):
    return [address for address in addresses if is_address_valid(address)]


@scheduled_executor(registry_pull_frequency())
def _flush_registry_address():
    """刷新注册中心地址的方法"""
    sys_plugin_logger.debug('flush registry address')
    addresses = invoke_get_registry_address()
    if addresses:
        global _registry_address_cache
        _registry_address_cache = addresses
        sys_plugin_logger.info(f"fetched registry address: {addresses}")


@scheduled_executor(registry_fitable_frequency())
def _registry_fitable_addresses():
    """定时向注册中心注册本地提供的服务"""
    try:
        register_all_fit_services()
        sys_plugin_logger.debug("registry all fitable address success.")
    except:
        sys_plugin_logger.warning(f"registry all fitable address failed.")
        except_type, except_value, except_traceback = sys.exc_info()
        sys_plugin_logger.warning(f"registry all fitable address error type: {except_type}")
        sys_plugin_logger.warning(f"registry all fitable address error value: {except_value}")
        sys_plugin_logger.warning(f"registry all fitable address error trace back:\n"
                                  f"{''.join(traceback.format_tb(except_traceback))}")


@scheduled_executor(clear_expired_invalid_address_frequency())
def _remove_invalid_cache():
    """
    定期清理失效的缓存
    Returns:

    """
    invalid_address_cache.clear_expired_cache()


@register_event(FrameworkEvent.FRAMEWORK_STOPPING)
def _unload_registry():
    """shutdown 时向注册中心取消注册本地提供的服务，以及取消本地订阅的服务"""
    pass


def _get_address_list(service_addresses: List[OldServiceAddress], fitable_: OldFitable) -> List[OldAddress]:
    if service_addresses is None or len(service_addresses) == 0:
        return []
    if len(service_addresses) > 1:
        sys_plugin_logger.warning(
            f"subscribe_fit_service {fitable_} get result more than one")
    return service_addresses[0].addressList


def _get_fitable_address(_fitable: OldFitable) -> List[OldServiceAddress]:
    """
        PULL模式，client主动向注册中心获取依赖的服务，并定时获取
        PUSH模式，client向注册中心订阅依赖的服务，当依赖的服务变更的时候，主持中心会向client 推送变更的内容
    """
    if _USING_NEW_COMMON_STRUCT and registry_client_mode() == PULL_MODE:
        return _get_fitable_address_v2(_fitable)

    return subscribe_fit_service([_fitable], get_address(), const.NOTIFY_FIT_SERVICE_FITABLE_ID)


def _convert_fitable_inst_to_service_address(fitable_inst: FitableInstance) -> OldServiceAddress:
    old_fitable = _convert_new_fitable_to_old_fitable(fitable_inst.fitable)
    old_addr_s = []
    for application_instance in fitable_inst.applicationInstances:
        for worker in application_instance.workers:
            for addr in worker.addresses:
                for endpoint in addr.endpoints:
                    old_addr_s.append(
                        OldAddress(addr.host, endpoint.port, worker.id, endpoint.protocol, application_instance.formats,
                                   worker.environment))
    return OldServiceAddress(ServiceMetaForRegistryV1(old_fitable, [], '', ''), old_addr_s)


def _convert_new_fitable_to_old_fitable(fitable_):
    return OldFitable(fitable_.genericableId, fitable_.genericableVersion, fitable_.fitableId, fitable_.fitableVersion)


def _get_fitable_address_v2(_fitable: OldFitable) -> List[OldServiceAddress]:
    _fitable_s = [_convert_old_fitable_to_new_fitable(_fitable)]
    if registry_client_mode() == PULL_MODE:
        fitable_inst_s = query_fitable_addresses_v2(_fitable_s, get_runtime_worker_id())
    else:
        fitable_inst_s = subscribe_fit_service_v2(_fitable_s, get_runtime_worker_id(),
                                                  const.NOTIFY_FIT_SERVICE_V2_FITABLE_ID)
    return \
        [_convert_fitable_inst_to_service_address(_) for _ in filter(lambda _: _.applicationInstances, fitable_inst_s)]


def get_cache_aware_registry_address() -> List[OldAddress]:
    if not _registry_address_cache:
        _flush_registry_address()

    return _registry_address_cache
