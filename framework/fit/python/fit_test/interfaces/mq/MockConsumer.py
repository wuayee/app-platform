# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：消息队列消费者的 Mock。
"""
import threading

from fit_py_message_queue_agent.interfaces import Consumer as AbstractConsumer
from fitframework.utils.thread_utils import keyed_lock


class MockConsumer(AbstractConsumer):
    def __init__(self, key: str, name: str, topic: str, processor):
        super().__init__(key, name, topic, processor)
        self.stopped_event = threading.Event()
        self._started_event = threading.Event()
        self._mark_close = False

    def _init_arguments(self, name, topic, processor):
        super().add_processor(processor)

    def start(self):
        @keyed_lock(self.key)
        def thread_safe_start(start_func, started_event):
            if started_event.is_set():
                return True
            threading.Thread(target=start_func).start()
            return started_event.wait(10)

        if self._started_event.is_set():
            return True
        return thread_safe_start(self._start_func, self._started_event)

    def _stop(self) -> threading.Event:
        self._mark_close = True
        return self.stopped_event

    def _start_func(self):
        self._started_event.set()
        while not self._mark_close:
            import time
            time.sleep(0.1)
        self.stopped_event.set()
