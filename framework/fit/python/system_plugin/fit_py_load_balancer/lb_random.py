# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：提供随机load balance服务
"""

import random
from typing import List

from fit_common_struct.core import Endpoint, FitableInfo

from fitframework import const
from fitframework.api.decorators import fitable


@fitable(const.LOAD_BALANCING_GEN_ID, const.LOAD_BALANCING_RANDOM_FIT_ID)
def load_balance(fitable_info: FitableInfo, caller_endpoints: List[Endpoint],
                 target_endpoints: List[Endpoint]) -> Endpoint:
    # 当前为同权重进行随机，后续增加按照target_endpoints的权重进行随机路由路由功能
    if not target_endpoints:
        return None
    return random.choice(target_endpoints)
