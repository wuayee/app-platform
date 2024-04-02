# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
功 能：发布者的抽象父类。
"""
from abc import abstractmethod

from fit_flowable.core.publisher import Publisher
from fit_flowable.core.subscriber import Subscriber
from fit_flowable.subscriber.empty_subscriber import EmptySubscriber


class AbstractPublisher(Publisher):
    """
    表示发布者的抽象父类。
    """

    def subscribe(self, subscriber: Subscriber):
        if subscriber is None:
            self.do_subscribe(EmptySubscriber.INSTANCE)
        else:
            self.do_subscribe(subscriber)

    @abstractmethod
    def do_subscribe(self, subscriber: Subscriber) -> None:
        """
        向发布者订阅以启动数据发送时的具体行为。

        :param subscriber: 已订阅的订阅者。
        """
        pass
