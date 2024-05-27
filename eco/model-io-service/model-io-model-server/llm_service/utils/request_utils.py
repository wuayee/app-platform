# -*- coding: utf-8 -*-
# !/usr/bin/python
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import uuid


def random_uuid() -> str:
    """
    生成uuid
    :return: uuid
    """
    return str(uuid.uuid4().hex)
