# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.exception.CoreError`类型, 表示DataBus内核返回的错误
"""
import logging
from databus.message import DataBusErrorCode


class CoreError(Exception):
    def __init__(self, error_message_template: str, error_type: DataBusErrorCode):
        super().__init__()
        self.error_code = error_type
        self.error_message = error_message_template.format(error_type)
        logging.error(self.error_message)

    @staticmethod
    def check_core_response(error_message_template: str, error_type: DataBusErrorCode):
        """检查error_type是否不是None_, 若不是则记录日志并抛出CoreError异常

        :param error_message_template: 错误消息的日志模板
        :param error_type: DataBus内核返回的response体中的error_type
        :raise CoreError: DataBus内核返回的错误
        """
        if error_type != DataBusErrorCode.None_:
            raise CoreError(error_message_template, error_type)


class NotConnectedError(Exception):
    def __init__(self):
        super().__init__()
        logging.error("Not connected to DataBus core")


class UnexpectedMessageTypeError(Exception):
    def __init__(self, error_msg: str):
        super().__init__()
        logging.error(error_msg)
