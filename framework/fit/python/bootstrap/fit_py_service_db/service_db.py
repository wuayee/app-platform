# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Fit运行时服务数据库，包括插件，服务管理，服务依赖管理，服务依赖注册
"""
import os
import threading
from collections import defaultdict
from functools import reduce
from itertools import groupby
from typing import List, Optional, Tuple

from fit_common_struct.registry_v1 import FitableForRegistryV1
from fit_common_struct.registry_v1 import ServiceMetaForRegistryV1
from fit_common_struct.core import FitableIds

from fitframework import const, Plugin
from fitframework.api.decorators import fit, fitable, private_fit, private_fitable, register_event, local_context
from fitframework.api.enums import PluginEvent
from fitframework.api.logging import bootstrap_logger, fit_logger
from fitframework.core.broker.broker_utils import GenericableTagEnum
from fitframework.core.exception.fit_exception import FitException

# plugin列表 - key: plugin_name, value: plugin_configuration_center_path
_plugin_conf_path = {}

# plugin和fitable映射关系 - key: plugin_name, value: List[(generic_id: str, fitable_id: str, aliases: List[str])]
_plugin_fitable_dict = defaultdict(list)

# @fit信息，记录genericable id
at_fit_set = set()


@local_context('app.name')
def app_name():
    pass


@fit(const.FIT_PLUGIN_CONFIGURATION_LOADER_GEN_ID)
def load_plugin_configuration(file_path: str, plugin_name: str) -> None:
    pass


@fit(const.ONLINE_FIT_SERVICE_GEN_ID, 'online_fit_services_v2_python')
def online_fit_services(service_meta: List[ServiceMetaForRegistryV1]) -> bool:
    pass


@private_fit
def get_configuration_item(plugin_name: str, key: str):
    pass


@fitable(const.SERVICE_DB_GET_PLUGIN_CONF_PATH_GEN_ID, const.SERVICE_DB_GET_PLUGIN_CONF_PATH_FIT_ID)
def get_plugin_configuration_path(plugin_name: str) -> str:
    return _plugin_conf_path[plugin_name] if plugin_name in _plugin_conf_path else None


@fitable(const.SERVICE_DB_REGISTER_TO_PLUGIN_GEN_ID, const.SERVICE_DB_REGISTER_TO_PLUGIN_FIT_ID)
def register_to_plugin(plugin_name: str, generic_id: str, fitable_id: str, aliases: List[str]):
    """ 本地注册fitable和对应插件名称到service_db """
    from fitframework.utils.pkg_utils import loading_so_plugin_name
    if loading_so_plugin_name is not None:
        bootstrap_logger.info(
            f"replace plugin name with so module name. [loading_so_plugin_name={loading_so_plugin_name}]")
        plugin_name = loading_so_plugin_name
    if plugin_name not in _plugin_conf_path:
        bootstrap_logger.warning("the plugin for the fitable is not registered: %s", plugin_name)
    _plugin_fitable_dict[plugin_name].append((generic_id, fitable_id, aliases))


@fitable(const.SERVICE_DB_REMOVE_PLUGIN_FITABLES_GEN_ID, const.SERVICE_DB_REMOVE_PLUGIN_FITABLES_FIT_ID)
def remove_plugin_fitables(plugin_name: str):
    if plugin_name not in _plugin_conf_path:
        bootstrap_logger.warning("the plugin for the fitable is not registered: %s", plugin_name)
    if plugin_name in _plugin_fitable_dict:
        del _plugin_fitable_dict[plugin_name]


@fitable(const.SERVICE_DB_REGISTER_PLUGIN_GEN_ID, const.SERVICE_DB_REGISTER_PLUGIN_FIT_ID)
def register_plugin(plugin_name: str, plugin_local_path: str):
    if plugin_name in _plugin_conf_path:
        bootstrap_logger.warning("plugin is already registered：%s", plugin_name)
        return

    # 加载插件配置信息，放入插件目录
    _load_plugin_config(plugin_name, plugin_local_path)
    configuration_path = get_configuration_item(plugin_name, const.PLUGIN_CONF_NODE_KEY_PROP)
    _plugin_conf_path[plugin_name] = configuration_path
    bootstrap_logger.info("plugin %s is registered locally. configuration center path: %s",
                          plugin_name, configuration_path)


@fitable(const.SERVICE_DB_UNREGISTER_PLUGIN_GEN_ID, const.SERVICE_DB_UNREGISTER_PLUGIN_FIT_ID)
def unregister_plugin(plugin_name: str, plugin_local_path: str):
    if plugin_name not in _plugin_conf_path:
        bootstrap_logger.warning("plugin not registered：%s", plugin_name)
        return

    del _plugin_conf_path[plugin_name]
    # 增加删除配置信息的功能
    bootstrap_logger.info("plugin %s is unregistered.")


def _load_plugin_config(plugin_name: str, plugin_path: str):
    configure_path = build_plugin_config_path(plugin_path)
    if os.path.isfile(configure_path):
        bootstrap_logger.info(f"loading application.yml of {plugin_name} from project root.")
        load_plugin_configuration(configure_path, plugin_name)
        return

    package_system_plugin_path = os.path.join(
        os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))), configure_path)
    if os.path.isfile(package_system_plugin_path):
        bootstrap_logger.info("loading application.yml of {plugin_name} from package root.")
        load_plugin_configuration(package_system_plugin_path, plugin_name)


def build_plugin_config_path(plugin_path):
    configure_path = os.path.join(plugin_path, 'conf', 'application.yml')
    return configure_path


@fitable(const.SERVICE_DB_IS_PLUGIN_EXISTED_GEN_ID, const.SERVICE_DB_IS_PLUGIN_EXISTED_FIT_ID)
def is_plugin_existed(plugin_name: str) -> bool:
    return plugin_name in _plugin_conf_path


@fitable(const.SERVICE_DB_REGISTER_ALL_FIT_SERVICE_GEN_ID, const.SERVICE_DB_REGISTER_ALL_FIT_SERVICE_FIT_ID)
def register_all_fit_services() -> Optional[bool]:
    try:
        bootstrap_logger.debug("registering all fitable services")
        _register_plugin_fit_services(app_name(), reduce(list.__add__, _plugin_fitable_dict.values()))
        return True
    except FitException:
        bootstrap_logger.error("register all fitable services failed.")
        return None


@fitable(const.SERVICE_DB_FITABLE_SERVICES_READY_GEN_ID,
         const.SERVICE_DB_FITABLE_SERVICES_READY_FIT_ID)
def fitable_services_ready():
    pass


@fitable(const.SERVICE_DB_GET_CONF_KEY_BY_PATH_GEN_ID, const.SERVICE_DB_GET_CONF_KEY_BY_PATH_FIT_ID)
def get_conf_key_by_path(path: str) -> Optional[str]:
    if path.startswith(const.GEN_CONF_NODE_KEY_PREFIX):
        return path
    return _get_plugin_name_by_path(path)


@private_fitable
def private_get_fitable_ids(plugin_name: str) -> Optional[List[FitableIds]]:
    if plugin_name not in _plugin_fitable_dict:
        return None
    genericable_tuples = sorted(_plugin_fitable_dict[plugin_name], key=lambda _: _[0])
    result = []
    for generic_id, tuples in groupby(genericable_tuples, key=lambda _: _[0]):
        result.append(FitableIds(generic_id, [_[1] for _ in tuples]))
    return result


@private_fitable
def get_fitables_info_by_plugin(plugin_name: str) -> List[Tuple[str, str]]:
    """
    插件未加载或不存在或其他情况时，返回None

    Args:
        plugin_name:
    Returns: 一个二元组的列表，每个二元组的含义为（generic_id，fitable_id）
    """
    return _plugin_fitable_dict.get(plugin_name)


def get_plugin_config_paths() -> List[str]:
    """
    获得所有插件配置的配置中心路径
    Returns:
        List[str]: 所有插件配置的配置中心路径
    """
    return list(filter(None, _plugin_conf_path.values()))


def dependency_subscribed(generic_id) -> bool:
    """
    @Fit依赖是否已注册

    Args:
        generic_id (str): @Fit依赖的genericable id

    Returns:
        bool: 是否已注册
    """
    return generic_id in at_fit_set


def dependency_subscribed_aware_call(generic_id, action) -> None:
    """
    根据@Fit依赖是否已注册执行action
    Args:
        generic_id (str): @Fit依赖的genericable id
        action (function): 待执行的方法
    """
    if dependency_subscribed(generic_id):
        return
    at_fit_set.add(generic_id)
    action(generic_id)
    # TDDO: deal with case when download or subscribe fails


def _get_plugin_name_by_path(path: str) -> Optional[str]:
    plugin_name_s = list(filter(lambda k: _plugin_conf_path[k] == path, _plugin_conf_path))
    if len(plugin_name_s) == 0:
        bootstrap_logger.warning('no plugin found corresponding the given path: [%s]', path)
        return None
    if len(plugin_name_s) > 1:
        bootstrap_logger.warning('multiple plugins found corresponding the given path: [%s]', path)
        return None
    return plugin_name_s[0]


def _register_plugin_fit_services(plugin_name, fitable_infos: List[Tuple[str, str, List[str]]]) -> bool:
    """ 只处理非localOnly的fitable """
    service_metas = _get_service_metas_info_by_plugin(plugin_name, fitable_infos)
    return online_fit_services(service_metas)


def _get_service_metas_info_by_plugin(plugin_name, fitable_infos):
    filtered_id_pairs = [id_pair for id_pair in fitable_infos if not _local_only_invoke(id_pair[0])]
    if not filtered_id_pairs:
        return []
    if threading.current_thread() is threading.main_thread():
        bootstrap_logger.info("registering or unregistering [%s] fitable services. Service count: %d",
                              plugin_name, len(filtered_id_pairs))
    from fit_py_service_db.service_db_configurations import get_genericable_name
    service_name = get_genericable_name(fitable_infos[0][0])

    def converter(fitable_info):
        return ServiceMetaForRegistryV1(
            FitableForRegistryV1(fitable_info[0], const.FIXED_GENERICABLE_VERSION, fitable_info[1],
                                 const.FIXED_FITABLE_VERSION), fitable_info[2], service_name, plugin_name)

    service_metas = list(map(converter, filtered_id_pairs))
    return service_metas


def _local_only_invoke(generic_id: str):
    from fit_py_service_db.service_db_configurations import get_genericable_tags
    return GenericableTagEnum.LOCAL_ONLY.value in get_genericable_tags(generic_id)
