# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：插件仓库工具类
"""
import os.path
from typing import Optional

from fitframework.api.decorators import run_once
from fitframework.utils.plugin_utils import get_all_plugins_info


def inspect_plugin_name_by_func(func) -> Optional[str]:
    """
    根据方法指针获得方法所在的插件名称
    原为：service_db公共服务

    Args:
        func (function): 方法指针

    Returns:
        str: 所在的插件名
    """
    top_module = func.__module__.partition('.')[0]
    if top_module in query_all_plugin_names():
        return top_module
    return None


@run_once
def query_all_plugin_names():
    return set(info.get('name', resolve_plugin_name(info['location'])) for info in get_all_plugins_info())


def resolve_plugin_name(plugin_location: str):
    return os.path.basename(plugin_location)
