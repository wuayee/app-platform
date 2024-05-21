#!/usr/bin/env python
# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.

from setuptools import setup, find_packages, Extension

raw_memory_extension = Extension("databus.memory_io", [
    "src/extensions/memory_io.c",
    "src/extensions/memory_write.cpp",
])


def main():
    setup(
        name="databus",
        version="0.0.1",
        description="Python SDK for the DataBus library, part of jade project",
        url="https://openx.huawei.com/Jade/overview",
        packages=find_packages("src"),
        package_dir={"": "src"},
        ext_modules=[raw_memory_extension],
        python_requires=">=3")


if __name__ == "__main__":
    main()
