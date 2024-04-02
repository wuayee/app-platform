# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
"""
功 能：运行时状态枚举
"""
from collections import namedtuple
from enum import Enum, auto, unique
from typing import Union


@unique
class FrameworkEvent(Enum):
    """ 框架支持的事件枚举 """
    FRAMEWORK_STARTED = auto()
    APPLICATION_STARTED = auto()
    FRAMEWORK_STOPPING = auto()
    CONFIGURATION_CHANGED = auto()


class FrameworkState(Enum):
    UNKNOWN = auto()
    BOOTING = auto()
    RUNNING = auto()
    SHUTDOWN = auto()


@unique
class FrameworkSubState(Enum):
    UNKNOWN = auto()
    BOOTING_INIT = auto()
    ALL_PLUGIN_LOADED = FrameworkEvent.APPLICATION_STARTED
    COMPONENT_LOADED = auto()
    FIT_METADATA_LOADED = auto()
    SERVER_STARTED = auto()
    CONFIGURATION_SUBSCRIBED = auto()
    FITABLE_REGISTERED = auto()
    HEART_BEAT_STARTED = FrameworkEvent.FRAMEWORK_STARTED
    RUNNING = auto()
    SHUTDOWN = auto()


@unique
class PluginEvent(Enum):
    # event_data signature:
    """ 事件可以由状态的变化触发，也可以由其他系统关键行为触发
    action -> events; current actions in FIT include：
        - state changes
        - interruption
        - ...
    def handler(starting_plugin: Plugin)
        pass
    """
    STARTING = auto()
    STARTED = auto()
    STOPPING = auto()
    STOPPED = auto()


@unique
class PluginState(Enum):
    """
    插件状态：表达了一个持续时间段内的比较稳定的状态的概念；状态的改变
        通常用“事件来表示”@PluginEvent。一般而言的逻辑：
            - 某动作完成后 -> 修改状态 -> 触发事件
            - action -> state -> event
    目前的消费场景：暂时只在插件启动调用对应的启动函数时的筛选过程中
        有用到，未来场景可按需使用
    UNINSTALLED目前暂不支持

    其他，见：@broadcast_action
    """
    UNKNOWN = auto()
    INSTALLED = auto()
    RESOLVED = auto()
    STARTING = PluginEvent.STARTING
    ACTIVE = PluginEvent.STARTED
    STOPPING = PluginEvent.STOPPING
    UNINSTALLED = PluginEvent.STOPPED


class PluginType(Enum):
    """ 模块类型
    不同类型的插件类型所允许的加载等级范围、加载默认等级不同
        - 系统插件：[-6, 0]
        - 用户插件：[1, 6]
    加载顺序为从小到大依次加载，故数字越小代表优先级越高
    加载，英文暂定词为startup
        - 包括安装（install）：找到插件对应的实际Python包，将其作为一个module类型来导入
        - 解析（resolve）
        - 启动（start）

    注意这里枚举值需和`fit_startup.yml`的plugins下面的key保持一致（详见`fit_startup.yml`）
    注：BOOTSTRAP作为特殊插件，不进行插件类型管理，故此处不设置
        BOOTSTRAP枚举值
    """
    __LoadLevel = namedtuple('LoadLevel', 'min, max, default')

    SYSTEM = __LoadLevel(-6, 0, 0)
    USER = __LoadLevel(1, 6, 3)
    VIRTUAL = __LoadLevel(6, 6, 6)


FitState = Union[FrameworkState, FrameworkSubState, PluginState]
FitEvent = Union[FrameworkEvent, PluginEvent]
