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
        self.assertFalse(consumer1._stopped_event.is_set())
        consumer2.dispose()
        self.assertTrue(consumer1._stopped_event.is_set())


if __name__ == '__main__':
    unittest.main()
