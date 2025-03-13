# -- encoding: utf-8 --
# Copyright (c) 2024 Huawei Technologies Co., Ltd. All Rights Reserved.
# This file is a part of the ModelEngine Project.
# Licensed under the MIT License. See License.txt in the project root for license information.
# ======================================================================================================================
"""
功 能：kmc 加解密操作
"""
import os

import kmc.kmc as K

from fitframework import fitable

os.environ['KMC_DATA_USER'] = 'modelenginepublic'


@fitable("modelengine.fit.security.encrypt", "modelengine.fit.security.kmc.encrypt")
def encrypt(plain: str) -> str:
    """
    对于未加密的内容通过 kmc 方式进行加密。

    :param plain: 未加密内容。
    :return: 加密后内容。
    """
    return K.API().encrypt(0, plain)


@fitable("modelengine.fit.security.decrypt", "modelengine.fit.security.kmc.decrypt")
def decrypt(cipher: str) -> str:
    """
    对于加密后的内容通过 kmc 方式进行解密。

    :param cipher: 加密后内容。
    :return: 解密后内容。
    """
    return K.API().decrypt(0, cipher)
