# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
@fit服务依赖指针记录。仅用于debug场景
"""
from fitframework.core.repo.service_repo import _set_at_fit_ref


def register_at_fit_function(generic_id: str, func):
    _set_at_fit_ref(generic_id, func)
