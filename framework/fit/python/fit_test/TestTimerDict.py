# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
"""
功 能：测试TimerDict
"""
import time
import unittest

from com_huawei_fit_registry_registrycommon.entity import Address

from fitframework.utils.scheduler import TimerDict


class MyTestCase(unittest.TestCase):
    def test_valid(self):
        timer_dict = TimerDict(lambda address: hash(tuple(address.id)), 1)
        address = Address('local', 8080, 'id', 1, [2, 3], 'dev')
        timer_dict.put(address)
        assert address in timer_dict
        time.sleep(1)
        assert address not in timer_dict

    def test_remote(self):
        timer_dict = TimerDict(lambda address: hash(tuple(address.id)), 1)
        address = Address('local', 8080, 'id', 1, [2, 3], 'dev')
        timer_dict.put(address)
        timer_dict.remove(address)
        assert address not in timer_dict


if __name__ == '__main__':
    unittest.main()
