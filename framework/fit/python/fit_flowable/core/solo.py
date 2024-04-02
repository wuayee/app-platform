# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
功 能：处理单个值数据流的发布者。
"""
from fit_flowable.core.publisher import Publisher
from fit_flowable.core.subscriber import Subscriber


class Solo(Publisher):
    def subscribe(self, subscriber: Subscriber) -> None:
        pass
