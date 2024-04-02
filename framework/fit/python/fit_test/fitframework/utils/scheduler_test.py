import time
import unittest
from unittest.mock import Mock

from fitframework.utils import scheduler


class SchedulerTest(unittest.TestCase):
    def test_task(self):
        function_a = Mock()
        function_b = Mock()
        scheduler._task_defs[function_a] = 0.1
        scheduler._task_defs[function_b] = 0.1
        scheduler._start()
        time.sleep(2)
        scheduler._stop()
        function_a.assert_called()
        function_b.assert_called()


if __name__ == '__main__':
    unittest.main()
