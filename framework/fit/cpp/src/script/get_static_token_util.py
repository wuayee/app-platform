#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Copyright Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
from his_decrypt import ADSKeyLoader, EnvironKeyLoader, FileKeyLoader
from his_decrypt import HisDecrypt, EncryptType
import os
import json

ADS_PCLOUD_APP_KEY = 'pcloud_app_name'
ADS_PCLOUD_SUBAPP_KEY = 'pcloud_subapp_name'
ADS_HWIT_ENV_KEY = 'docker_env'

WORK_KEY_CIPHER_KEY = 'work_key_cipher'
CIPHER_TOKEN_KEY = 'cipher_token'
CONFIG_PART1_KEY = 'CONFIG_PART1'
CONFIG_PART2_KEY = 'CONFIG_PART2'

def decrypt(cipher_token, work_key_cipher, config_part1, config_part2):
    # 2初始化KeyLoader，并注册到解码器
    his_decrypt = HisDecrypt()

    # 3文件加载器：请提前在指定路径生成1.key和2.key
    app_name = os.environ.get(ADS_PCLOUD_APP_KEY)
    sub_app_name = os.environ.get(ADS_PCLOUD_SUBAPP_KEY)
    hw_it_env = os.environ.get(ADS_HWIT_ENV_KEY)
    kms_key_path = '/opt/security/' + app_name + '/' + sub_app_name + '/' + hw_it_env + '/' + 'keys/rootkeys/'
    kl_3 = FileKeyLoader(kms_key_path)
    his_decrypt.register(kl_3)

    # 4解码
    # 通过最后一个参数encrypt_type指定J2C解密版本，支持: 'ADVANCED2.5'/'ADVANCED2.6'
    # 建议通过变量指定：EncryptType.ADV_2_5/EncryptType.ADV_2_6, 不填则为默认的2.5解密
    config_parts = [config_part1, config_part2]
    res = his_decrypt.decrypt(config_parts, work_key_cipher, cipher_token, EncryptType.ADV_2_6)
    return res


def get_static_token():
    cipher_token = os.environ.get(CIPHER_TOKEN_KEY)
    work_key_cipher = os.environ.get(WORK_KEY_CIPHER_KEY)
    config_part1 = os.environ.get(CONFIG_PART1_KEY)
    config_part2 = os.environ.get(CONFIG_PART2_KEY)
    return decrypt(cipher_token, work_key_cipher, config_part1, config_part2)