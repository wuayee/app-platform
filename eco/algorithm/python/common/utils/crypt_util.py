# -*- coding: utf-8 -*-
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
since: 2024/3/12 11:00
"""
from common.utils.crypto_scc_api import CryptoApi, SCC_LIB_PATH

ENCRYPT_PREFIX = 'SCC@'
SCC_CONF_PATH = '/scc/conf/scc.conf'


class Crypt:
    """scc加解密类"""

    def __init__(self, scc_conf_path=SCC_CONF_PATH, scc_lib_path=SCC_LIB_PATH):
        """
        加解密组件采用实现跟配置分离,禁止直接测试例子的exampleCfgFile，务必使用调用方自己目录下的配置文件，
        同时确保scc/logger.conf中的配置参数的路径为调用方目录
        """
        self.api = CryptoApi(scc_lib_path=scc_lib_path)
        self.api.initialize(scc_conf_path)

    def __del__(self):
        self.api.finalize()

    def encrypt(self, content, domain_id=None):
        if domain_id is None:
            return self.api.encrypt(content)
        return self.api.encrypt(content, domain_id)

    def decrypt(self, content, domain_id=None):
        if ENCRYPT_PREFIX in content:
            content = content[len(ENCRYPT_PREFIX):]
            if domain_id is None:
                return self.api.decrypt(content)
            return self.api.decrypt(content, domain_id)
        return content
