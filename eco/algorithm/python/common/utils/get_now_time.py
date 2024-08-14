# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
Description:
根据时间格式，设置时区，返回当前时间戳
Create: 2024/6/5 9:26
"""
from datetime import datetime
import pytz

from fitframework.api.logging import plugin_logger as logger


class GetNowTime:
    @staticmethod
    def get_now_time(timezone, time_format, file_name, method):
        timestamp = ""
        try:
            china_tz = pytz.timezone(timezone)  # 设置时区
            china_time = datetime.now(tz=china_tz)  # 获取当前时间并转换为对应的时区
            timestamp = china_time.strftime(time_format)  # 格式化输出时间
        except ValueError as e:
            logger.error("fileName: %s, method: %s, formatting time failed: %s", file_name, method, e, exc_info=True)
        return timestamp
