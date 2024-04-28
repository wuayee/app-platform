#!/usr/bin/env python
# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.

from setuptools import setup, find_packages, Extension


def main():
    setup(
        name="databus",
        version="0.0.1",
        description="Python SDK for the DataBus library, part of jade project",
        url="https://openx.huawei.com/Jade/overview",
        packages=find_packages("src"),
        package_dir={"": "src"},
        ext_modules=[Extension("databus.memory", ["src/extensions/memory.c"])],
        python_requires=">=3")


if __name__ == "__main__":
    main()
