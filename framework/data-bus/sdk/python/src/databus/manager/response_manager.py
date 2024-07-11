# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
""" `databus.manager.ResponseManager`类型, 管理从DataBus内核接收消息.

本文件内容为DataBus Python SDK的`databus.manager.ResponseManager`类实现.
`ResponseManager`类管理从DataBus内核接收消息,确保线程安全.
"""
import logging
import threading
from typing import Dict, Optional
from dataclasses import dataclass
import socket as socket_lib
from databus.message import (
    MessageHeader, MESSAGE_HEADER_LENGTH, MESSAGE_RESPONSE_MAPPING,
    CoreMessageType, CoreMessageResponseTypeHint
)


@dataclass
class ResponseItem:
    cv: threading.Condition
    message_type: Optional[CoreMessageType] = None
    response: Optional[CoreMessageResponseTypeHint] = None


class ResponseManager(threading.Thread):
    def __init__(self, socket: socket_lib.socket, mailbox: Dict[int, ResponseItem]):
        super().__init__()
        self._socket = socket
        self._mailbox = mailbox

    def run(self):
        """处理内核返回的消息"""
        while True:
            try:
                data = self._socket.recv(2048)
                if not data:
                    raise ConnectionResetError
                # TD: 处理半包
                self._split_message(data)
            except Exception:
                self._handle_connection_error()
                break

    def _handle_connection_error(self):
        logging.error("Response manager receive from DataBus core error.")
        for mail in self._mailbox.values():
            with mail.cv:
                mail.cv.notify_all()
        if self._socket:
            try:
                self._socket.shutdown(socket_lib.SHUT_RDWR)
                self._socket.close()
            except Exception:
                # 忽略因为关闭步骤中对一个已破损socket做操作等异常，此处只期待socket关闭过程执行
                logging.debug("Response manager close socket error, this can be ignored.")
            finally:
                self._socket = None

    def _split_message(self, data: bytes):
        """分割处理内核返回的消息, 避免粘包"""
        ptr = 0
        while ptr < len(data):
            header = MessageHeader.MessageHeader.GetRootAs(data)
            seq = header.Seq()
            ptr += MESSAGE_HEADER_LENGTH
            if seq in self._mailbox:
                self._mailbox[seq].message_type = header.Type()
                if header.Type() == CoreMessageType.Hello:
                    # hello消息没有body
                    self._mailbox[seq].response = True
                else:
                    raw_body = data[ptr: ptr + header.Size()]
                    logging.debug("DataBus core response (%d-%d of %d): %s.",
                                  ptr - MESSAGE_HEADER_LENGTH, ptr + header.Size() - 1, len(data),
                                  data[ptr - MESSAGE_HEADER_LENGTH:ptr + header.Size()].hex())
                    self._mailbox[seq].response = MESSAGE_RESPONSE_MAPPING[header.Type()].GetRootAs(raw_body)
                # 通知线程
                cv = self._mailbox[seq].cv
                with cv:
                    cv.notify()
            ptr += header.Size()
