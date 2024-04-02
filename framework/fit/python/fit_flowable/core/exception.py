# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
功 能：响应式框架运行错误时抛出的异常。
"""


class FlowableException(Exception):
    """
    响应式框架运行错误时抛出的异常。
    """

    def __init__(self, message: str):
        """
        初始化 FlowableException 异常。
        Args:
            message: 错误消息。
        """
        super().__init__(message)
