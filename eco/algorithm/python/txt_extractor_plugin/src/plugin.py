#!/user/bin/python
# -*- coding: utf-8 -*-
# Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
"""
Description: TXT文本抽取
Create: 2023/12/7 15:43
"""
import time
import logging as logger
from typing import List

from common.model import Content


class TxtExtractorPlugin:
    """txt 文档提取插件"""

    @staticmethod
    def execute(contents: List[Content]) -> List[Content]:
        for content in contents:
            start = time.time()
            try:
                # 去除文档的首尾空格，导致重复段落检验算子出现问题。经研发团队和测试团队讨论，在文本抽取时不进行首尾空格去除处理
                # 用utf-8-sig的格式进行抽取，可以避免uft-8 BOM编码格式的文件在抽取后产生隐藏字符作为前缀。
                content.text = content.data.decode(encoding='utf-8-sig').replace("\r\n", "\n")
                content.data = b""  # 将content.data置空
                logger.info("fileName: %s, method: TxtExtractorPlugin costs %.6f s",
                            content.meta.get("fileName"), time.time() - start)
            except UnicodeDecodeError as err:
                logger.error("fileName: %s, method: TxtExtractorPlugin causes decode error: %s",
                             content.meta.get("fileName"), err, exc_info=True)
                raise UnicodeDecodeError("utf-8", err.object, err.start, err.end, err.reason) from err
        return contents
