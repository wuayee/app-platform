# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import numpy as np


def clean_args(args):
    """
    删除字典中值为None、空字符串或空列表的键值对。

    :param input_dict: 需要清理的原始字典。
    :return: 清理后的字典。
    """
    return {k: v for k, v in args.items() if v not in [None, '', []]}


# 对numpy的数据类型进行转换
# 场景:numpy.float32类型不能写入JSON,需要转成Python的float类型
def convert_numpy_data(data):
    if isinstance(data, list):
        return _convert_list(data)
    if isinstance(data, dict):
        return _convert_dict(data)
    elif isinstance(data, np.float32):
        return _convert_float32(data)
    return data


def _convert_list(data):
    if not isinstance(data, list):
        return data

    temp = []
    for obj in data:
        temp.append(convert_numpy_data(obj))

    return temp


def _convert_dict(data):
    temp = data.copy()
    if not isinstance(data, dict):
        return temp

    for key in data.keys():
        obj = data.get(key)
        temp.__setitem__(key, convert_numpy_data(obj))

    return temp


def _convert_float32(data):
    return float(data)
