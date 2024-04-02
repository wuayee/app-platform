# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
功 能：发布者，负责产生数据流并发布给订阅者。
"""
from abc import abstractmethod

from fit_flowable.core.subscriber import Subscriber


class Publisher:
    """
    表示发布者的抽象基类，发布者是可以拥有订户并向其发布事件的对象。

    * 子类必须实现订阅方法，以将新订阅者添加到发布者的订阅者列表中。
    """

    @abstractmethod
    def subscribe(self, subscriber: Subscriber) -> None:
        """
        向发布者订阅以启动数据发送。
        * 该方法可被多次执行，每次将为其订阅者产生一个新的 Subscription；
        * 在订阅过程中发生的异常将通过 Subscriber 的 fail 方法传递。

        :param subscriber: 已订阅的 Subscriber。
        """
        pass
