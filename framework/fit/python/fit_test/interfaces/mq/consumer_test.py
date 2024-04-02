# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：消息队列消费者的测试。
"""
import unittest

from fit_test.interfaces.mq.MockConsumer import MockConsumer


def mock_processor_1():
    pass


def mock_processor_2():
    pass


class MQConsumerTest(unittest.TestCase):
    def test_start_and_dispose(self):
        consumer1 = MockConsumer('name:topic', 'name', 'topic', mock_processor_1)
        self.assertTrue(consumer1.start())
        consumer2 = MockConsumer('name:topic', 'name', 'topic', mock_processor_2)
        self.assertTrue(consumer2.start())
        self.assertEqual(consumer1, consumer2)
        consumer1.dispose()
        self.assertFalse(consumer1.stopped_event.is_set())
        consumer2.dispose()
        self.assertTrue(consumer1.stopped_event.is_set())


if __name__ == '__main__':
    unittest.main()
