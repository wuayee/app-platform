# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
"""
功 能：提供插件工具
"""
import os.path
from typing import Dict, List
import yaml

from fitframework import const
from fitframework.api.decorators import local_context
from fitframework.api.logging import fit_logger

_PLUGIN_CONF_DIR = "conf"
_PLUGIN_CONF_FILE_NAME = "info.yml"
_PLUGIN_NAME_KEY = "name"
_PLUGIN_LOCATION_KEY = "location"
_PLUGIN_LEVEL_KEY = "level"


@local_context('system_plugin_paths')
def _get_system_plugins_paths():
    pass


@local_context('user_plugin_paths')
def _get_user_plugins_paths():
    pass


def load_plugin_info(path: str) -> Dict:
    """
    加载插件通过内置配置文件提供的插件信息
    :param path: 插件的绝对路径
    :return: 通过字典形式表示的插件信息
    """
    if path is None:
        raise Exception("load plugin failed, plugin path is None.")
    info_file_path = os.path.join(path, _PLUGIN_CONF_DIR, _PLUGIN_CONF_FILE_NAME)
    if not os.path.isfile(info_file_path):
        fit_logger.warning(f"the plugin information file info.yml is missing. [plugin_path={path}]")
        return {_PLUGIN_LEVEL_KEY: None}
    with open(info_file_path, encoding='utf-8') as f:
        return yaml.safe_load(f)


def _load_plugins_info(paths: List) -> List:
    """
    加载一组插件的信息
    :param paths: 插件的绝对路径列表
    :return: 插件信息列表
    """
    plugin_infos = []
    for path in paths:
        if not os.path.isdir(path):
            fit_logger.warning(f"path of plugins is not exist. [path={path}]")
            continue
        for dir_name in os.listdir(path):
            if not os.path.isdir(os.path.join(path, dir_name)) or dir_name == "__pycache__":
                continue
            plugin_info = {_PLUGIN_NAME_KEY: dir_name, _PLUGIN_LOCATION_KEY: os.path.join(path, dir_name)}
            plugin_info.update(load_plugin_info(os.path.join(os.getcwd(), path, dir_name)))
            plugin_infos.append(plugin_info)
    return plugin_infos


def load_all_plugins_info() -> Dict:
    """
    获取所有系统插件和静态用户插件的信息
    :return: 插件信息列表
    """
    return {const.STARTUP_PLUGINS_SYSTEM_KEY: _load_plugins_info(_get_system_plugins_paths()),
            const.STARTUP_PLUGINS_USER_KEY: _load_plugins_info(_get_user_plugins_paths())}
