# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：value方法指针注册。
"""
from fitframework.api.logging import fit_logger

from fitframework.core.repo.repo_utils import inspect_plugin_name_by_func
from fitframework.core.repo.service_repo import _set_value_ref


def register_value_ref(func, key: str):
    plugin_name = inspect_plugin_name_by_func(func)
    if plugin_name is None:
        fit_logger.error(
                    f"not able to find registered plugin for @value: {func.__name__}")
        return

    fit_logger.info(f'register @value {plugin_name}:{key}')
    _set_value_ref(plugin_name, key, func)
