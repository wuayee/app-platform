# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：用于FFP模板中的fitable调用代理。基于Fit元数据。
    目前不支持before, after, validate, on_error, degradation, routing
"""
from typing import List

from fitframework.core.broker.configure_based_brokerimpl import ConfigureBasedBroker


class FfpFitableBroker(ConfigureBasedBroker):
    def __init__(self, return_type_override: bool, return_type: type):
        super().__init__()
        self._return_type_override = return_type_override
        self._return_type = return_type

    def on_validate(self, generic_id, func_ref, *args):
        pass

    def on_before(self, generic_id, func_ref, *args):
        pass

    def on_after(self, generic_id, func_ref, *args):
        pass

    def on_error(self, generic_id, func_ref, *args):
        pass

    def routing(self, generic_id, fitable_identifier, fit_ref, *args, route_filter=None) -> List[str]:
        return [fitable_identifier.value]

    def get_degradation(self, generic_id: str, fitable_id: str) -> str:
        pass
