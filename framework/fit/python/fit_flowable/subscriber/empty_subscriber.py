# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
功 能：仅请求数据，但不消费数据的 Subscriber。
"""
from sys import maxsize

from fit_flowable.subscriber.abstract_subscriber import AbstractSubscriber


class EmptySubscriber(AbstractSubscriber):
    """
    表示仅请求数据，但不消费数据的 Subscriber。
    """

    """ 表示 EmptySubscriber 的单例 """
    INSTANCE: AbstractSubscriber = None

    def do_on_subscribed(self):
        self.get_pre_subscription().request(maxsize)

    def do_consume(self, data):
        pass

    def do_fail(self, cause: Exception):
        pass

    def do_complete(self):
        pass


EmptySubscriber.INSTANCE = EmptySubscriber()
