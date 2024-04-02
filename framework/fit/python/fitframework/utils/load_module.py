# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：从package中加载所有Fitable方法
"""
from itertools import chain
from typing import List

from fitframework import const
from fitframework.api.decorators import fit, state_broadcast
from fitframework.api.enums import FrameworkEvent, PluginType, FrameworkSubState
from fitframework.api.logging import fit_logger
from fitframework.core.repo import plugin_repo
from fitframework.utils.eventing import notify
from fitframework.utils.pkg_utils import import_package
from fitframework.utils.tools import sort_and_groupby
from fitframework.domain.plugin import Plugin


@fit(const.RUNTIME_SHUTDOWN_GEN_ID)
def shutdown() -> None:
    pass


@fit(const.RUNTIME_STATE_UPDATE_GEN_ID)
def update_framework_state(new_state: str) -> None:
    pass


def load_bootstraps(bootstrap_locations: List[str]):
    """
    :param bootstrap_locations: 一种特殊的插件，不对其进行插件管理，也
        不存在插件状态等一系列生命周期特征，仅仅用作引擎最开始的初始化
        加载、元数据存储，也不对外进行服务注册，所以此处仅仅传入路径字
        符串即可
    """
    from fitframework.core.repo.fitable_register import loading_context as ctx
    ctx.is_bootstrap = True
    for location in bootstrap_locations:
        import_package(location, recursive=True)
    del ctx.is_bootstrap


@state_broadcast(update_framework_state, None, FrameworkSubState.ALL_PLUGIN_LOADED)
def load_plugins(sys_plugins_info: List[dict], user_plugins_info: List[dict]):
    """
    从roots中搜索全局配置文件（fit_startup.yml）中的插件列表，并加载。
        共包括四个步骤：install, resolve, start, install fitables；其中非用户插件
        只包含第一个步骤
        :param sys_plugins_info:
        :param user_plugins_info:
    """
    plugins_iter = chain(
        (Plugin(**kv, type_=PluginType.SYSTEM) for kv in sys_plugins_info),
        (Plugin(**kv, type_=PluginType.USER) for kv in user_plugins_info) if user_plugins_info else ())
    for cur_level, cur_plugins in sort_and_groupby(plugins_iter, Plugin.level_getter):
        fit_logger.info(f"plugins of level {cur_level} begin to load")
        try:
            if cur_level >= getattr(PluginType.USER.value, 'min'):  # user plugins
                notify(FrameworkEvent.FRAMEWORK_STARTED)
            cur_plugins = list(cur_plugins)
            if not cur_plugins:
                continue
            for plugin in cur_plugins:
                Plugin.install(plugin)
        except Exception as err:
            fit_logger.error(f"error occurred when loading plugin: {plugin}, {repr(err)}")
            fit_logger.error(f"loaded plugins begin to rollback...")
            _ = list(map(Plugin.stop, reversed(plugin_repo.get_all_plugins())))
            shutdown()
        fit_logger.info(f"plugins of level {cur_level} loaded successfully")
