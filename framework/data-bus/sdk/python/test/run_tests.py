# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
import unittest


def run_all_test():
    cases = unittest.TestLoader().loadTestsFromNames([
        "test.manager.test_memory_manager", "test.client.test_sdk_client_impl"
    ])
    unittest.TextTestRunner(verbosity=2).run(cases)


if __name__ == '__main__':
    run_all_test()
