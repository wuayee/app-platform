# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：心跳工具
"""
from concurrent.futures.thread import ThreadPoolExecutor
from functools import wraps
from concurrent.futures._base import TimeoutError as future_TimeoutError
from fitframework.api.logging import sys_plugin_logger


class HeartBeatInfo:
    def __init__(self, sceneType: str, aliveTime: int, initDelay: int):
        self.sceneType: str = sceneType
        self.aliveTime: int = aliveTime
        self.initDelay: int = initDelay


class HeartBeatAddress:
    def __init__(self, id_: str):
        self.id = id_


def timeout_or_exception_retry(timeout: int = 3, a_exception=Exception, max_retry: int = 1):
    """

    :param timeout: 一次函数调用的超时时间 单位 s
    :param a_exception: 函数调用可能抛出的异常
    :param max_retry: 重试次数
    :return:
    """

    def decorator(fun):
        executor = ThreadPoolExecutor(max_workers=10)

        @wraps(fun)
        def wrapper(*args, **kwargs):
            count = 0
            while count < max_retry:
                try:
                    submit = executor.submit(fun, *args, **kwargs)
                    return submit.result(timeout=timeout)
                except future_TimeoutError as e:
                    sys_plugin_logger.error(f"time out {count}")
                    count += 1

                except a_exception as e:
                    sys_plugin_logger.error(f"exception  {count}, message:{e}")
                    count += 1
            sys_plugin_logger.error(f'after {count} retry, fail')
            raise Exception(f'after {count} retry ,{fun.__name__} fail') from None

        return wrapper

    return decorator
