# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
Description:
读取音频文件
Create: 2024/7/8 9:26
"""
from functools import wraps
from io import BytesIO

import torch
import torchaudio

from common.model import Content
from fitframework.api.logging import plugin_logger as logger


def read_audio(func):
    """通过装饰器方式读取二进制文件，并将处理后的音频转换为bytes类型"""
    @wraps(func)
    def wrap_func(cls, content: Content, *args):
        waveform = "waveform"
        sample_rate = "sampleRate"
        content.meta[waveform], content.meta[sample_rate] = torchaudio.load(BytesIO(content.data),
                                                                               backend="soundfile")
        content = func(cls, content, *args)
        bytes_io = BytesIO()
        torchaudio.save(bytes_io, content.meta.get(waveform), content.meta.get(sample_rate),
                        format=content.meta.get("fileType"), backend="soundfile")
        content.data = bytes_io.getvalue()
        if content.meta.get(waveform, torch.Tensor()).numel():
            del content.meta[waveform]
        if content.meta.get(sample_rate, 0):
            del content.meta[sample_rate]
        return content
    return wrap_func


class ProcessAudio:
    def __init__(self):
        self._channel_list = (1, 2)  # 只支持单通道和双通道

    @staticmethod
    def set_framerate(audio_data: bytes, frame_rate: int, wav_format: str) -> bytes:
        """配置音频采样率"""
        data, sample_rate = torchaudio.load(BytesIO(audio_data), backend="soundfile")

        # 如果原音频的采样率和目标采样率相同，则不处理
        if sample_rate == frame_rate:
            return audio_data

        # 将音频的采样率更改为指定的采样率
        resample_data = torchaudio.transforms.Resample(orig_freq=sample_rate, new_freq=frame_rate).forward(data)

        # 将音频转为音频流
        audio_result = BytesIO()
        # 修改音频的采样率
        torchaudio.save(audio_result, resample_data, frame_rate, format=wav_format, backend="soundfile")

        return audio_result.getvalue()

    def set_channels(self, audio_data: bytes, channels: int, wav_format: str) -> bytes:
        """配置音频通道"""
        # 判断输入的通道数是否合适，只支持单通道和双通道
        if channels not in self._channel_list:
            logger.error("The channel is not fit! The channel can only be 1 or 2!", exc_info=True)
            return audio_data

        # 读取音频文件
        data_tensor, sample_rate = torchaudio.load(BytesIO(audio_data), backend="soundfile")

        if channels == 2:
            # 如果音频原本为双通道则不处理
            if data_tensor.shape[0] == channels:
                return audio_data
            # 将单通道改为双通道
            new_data = torch.Tensor([data_tensor[0].tolist(), data_tensor[0].tolist()])
        else:
            # 如果音频原本为单通道则不处理
            if len(data_tensor.shape) == 1 or data_tensor.shape[0] == channels:
                return audio_data
            # 将双通道改为单通道
            new_data = data_tensor[0]

        # 将音频转为音频流
        audio_result = BytesIO()
        torchaudio.save(audio_result, new_data, sample_rate, format=wav_format, backend="soundfile")

        return audio_result.getvalue()
