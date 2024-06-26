# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.manager.MessageSeqManager`类型, 管理向DataBus内核发送消息的序列号.

本文件内容为DataBus Python SDK的`databus.manager.MessageSeqManager`类实现.
`MessageSeqManager`类管理向DataBus内核发送消息的序列号,确保线程安全.
"""
import threading


class MessageSeqManager:
    # 最大消息序列号是uint32_t的
    MAX_MESSAGE_SEQ = 0xffffffff

    def __init__(self):
        self._seq = 0
        self._lock = threading.Lock()

    def get_seq(self):
        """ 获取当前消息的序列号

        :return: 当前消息序列号
        """
        with self._lock:
            self._seq = (self._seq & self.MAX_MESSAGE_SEQ) + 1
            # 确保消息序列号的范围是[1, 1 << 32]
            return self._seq
