# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：提供轮询load balance服务
"""
import secrets
from typing import List

from fit_common_struct.core import Endpoint, FitableInfo
from fitframework import const
from fitframework.api.decorators import fitable

_fitable_pos_dict = dict()


@fitable(const.LOAD_BALANCING_GEN_ID, const.LOAD_BALANCING_ROUND_ROBIN_FIT_ID)
def load_balance(fitable_info: FitableInfo, caller_endpoints: List[Endpoint],
                 target_endpoints: List[Endpoint]) -> Endpoint:
    if len(target_endpoints) == 0:
        return None
    size = len(target_endpoints)

    if fitable_info not in _fitable_pos_dict:
        _fitable_pos_dict[fitable_info] = secrets.randbelow(size)
    else:
        _fitable_pos_dict[fitable_info] = (_fitable_pos_dict[fitable_info] + 1) % size
    return target_endpoints[_fitable_pos_dict[fitable_info]]
