# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：心跳服务本地代理。代表当前Runtime下的所有服务向心跳服务器发送周期性心跳。如果心跳停止并且
心跳服务器判断当前Runtime已终止，心跳服务器通知注册服务器删除Runtime对应地址的所有服务。
心跳代理和心跳服务端连接临时中断，以后恢复场景：
    心跳代理某次heartbeat请求失败或两次heartbeat时间间隔过长（可能CPU忙导致），心跳服务端连接可能中断。
    下次心跳恢复正常后，代理应通知底座重新注册服务到注册中心（BeatInfo中有回调接口，但是心跳服务代理
    必须在本地，不需要调用回调接口）
"""
import multiprocessing
import platform
import sys
import time
import traceback
from multiprocessing import Process
from queue import Empty
from threading import Thread
from typing import List

from fitframework import const
from fitframework.api.decorators import fit, fitable, value
from fitframework.api.decorators import register_event
from fitframework.api.enums import FrameworkEvent as Fit_Event
from fitframework.api.logging import sys_plugin_logger
from .heart_beat_utils import HeartBeatAddress, HeartBeatInfo

# 用于控制心跳任务退出的队列
_HEART_BEAT_FINISH_QUEUE = multiprocessing.Queue()
# 连续失败的次数
_FAIL_COUNT = 0
# 上次心跳成功的时间
_LAST_HEART_BEAT_SUCCESS_TIME = time.time()


@value('heart-beat.client.sceneType', "fit-registry")
def _scene_type():
    pass


@value('heart-beat.client.interval', 3000, converter=int)
def _interval():
    """心跳周期 (ms)"""
    pass


@value('heart-beat.client.aliveTime', 10000, converter=int)
def _alive_time():
    """最大存活时长（ms,心跳后的有效期，有效期内无心跳则认为离线）"""
    pass


@value('heart-beat.client.initDelay', 3000, converter=int)
def _init_delay():
    """首次延迟时长（ms, 用于首次增加最大存活时长，用于启动期心跳不稳定的情况）"""
    pass


@value('heart-beat.client.service_id', 3)
def _service_id():
    pass


@fit(const.RUNTIME_GET_WORKER_ID_GEN_ID)
def get_runtime_worker_id() -> str:
    pass


@fit(const.HEART_BEAT_GEN_ID)
def heartbeat(beat_info: List[HeartBeatInfo], address: HeartBeatAddress) -> bool:
    """ 可能返回 false，也可能抛出异常，也可能超时 """
    pass


def _try_heart_beat_once():
    global _FAIL_COUNT, _LAST_HEART_BEAT_SUCCESS_TIME
    try:
        heartbeat([HeartBeatInfo(_scene_type(), _alive_time(), _init_delay())],
                  HeartBeatAddress(get_runtime_worker_id()))
        heart_beat_finish_time = time.time()
        heart_beat_gap = heart_beat_finish_time - _LAST_HEART_BEAT_SUCCESS_TIME
        if heart_beat_gap > 2 * _interval() / 1000:
            sys_plugin_logger.warning(f"heart beat unstable. "
                                      f"heart_beat_gap={'{:.3f}'.format(heart_beat_gap)}s, "
                                      f"heart_beat_interval={'{:.3f}'.format(_interval() / 1000)}s]")
        if _FAIL_COUNT != 0:
            sys_plugin_logger.info(f"heart beat reconnect success. [fail_count={_FAIL_COUNT}]")
            _FAIL_COUNT = 0
        sys_plugin_logger.debug(f'heart beating success.')
        _LAST_HEART_BEAT_SUCCESS_TIME = heart_beat_finish_time
    except:
        _FAIL_COUNT += 1
        except_type, except_value, except_traceback = sys.exc_info()
        sys_plugin_logger.warning(f"heart beat failed. [fail_count={_FAIL_COUNT}]")
        sys_plugin_logger.warning(f"heart beat error type: {except_type}, value: {except_value}, "
                                  f"trace back:\n{''.join(traceback.format_tb(except_traceback))}")


def _heart_beat_task(queue: multiprocessing.Queue):
    while True:
        try:
            queue.get(timeout=_interval() / 1000)
            break
        except Empty:
            _try_heart_beat_once()
    sys_plugin_logger.info("heart beat task exit.")


@fitable(const.ONLINE_HEART_BEAT_GEN_ID, const.ONLINE_HEART_BEAT_FIT_ID)
def online() -> None:
    """ Runtime向心跳代理申请启动本地心跳服务，心跳代理周期性触发heartbeat() """
    sys_plugin_logger.info(f"start heart beating with interval {_interval()} ms, alive time {_alive_time()} ms.")
    if platform.system() == 'Windows':
        heart_beat_background_job = Thread(target=_heart_beat_task, args=(_HEART_BEAT_FINISH_QUEUE,),
                                           name='HeartBeatThread')
    else:
        heart_beat_background_job = Process(target=_heart_beat_task, args=(_HEART_BEAT_FINISH_QUEUE,),
                                            name='HeartBeatProcess')
    heart_beat_background_job.start()


@register_event(Fit_Event.FRAMEWORK_STOPPING)
@fitable(const.OFFLINE_HEART_BEAT_GEN_ID, const.OFFLINE_HEART_BEAT_FITABLE_ID)
def offline():
    """ Runtime关闭前应主动向心跳代理申请offline，心跳代理停止发送heartbeat并调用心跳服务端leave接口 """
    sys_plugin_logger.info("heart beat agent offline")
    _HEART_BEAT_FINISH_QUEUE.put(None)
