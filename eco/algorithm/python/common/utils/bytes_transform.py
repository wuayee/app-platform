# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
Description:
Create: 2023/11/7 9:26
"""
import base64
from io import BytesIO

import cv2
import numpy as np
from PIL import Image


def bytes_to_numpy(image_bytes):
    """bytes转数组"""
    image_np = np.frombuffer(image_bytes, dtype=np.uint8)
    image_np2 = cv2.imdecode(image_np, cv2.IMREAD_COLOR)
    return image_np2


def numpy_to_bytes(image_np, file_type):
    """数组转bytes"""
    if not image_np.size:
        return b""
    data = cv2.imencode(file_type, image_np)[1]
    image_bytes = data.tobytes()
    return image_bytes


def pil_to_bytes(src: Image.Image):
    """PIl.Image转bytes"""
    src = src.convert("RGB")
    img_byte = BytesIO()
    src.save(img_byte, format='png')
    im_bytes = img_byte.getvalue()
    return im_bytes


def pil_to_base64(src: Image.Image):
    """PIl.Image转base64"""
    img_buffer = BytesIO()
    src.save(img_buffer, format='png')
    byte_data = img_buffer.getvalue()
    base64_str = base64.b64encode(byte_data)
    return base64_str
