# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
def clean_args(args):
    """
    删除字典中值为None、空字符串或空列表的键值对。

    :param input_dict: 需要清理的原始字典。
    :return: 清理后的字典。
    """
    return {k: v for k, v in args.items() if v not in [None, '', []]}
