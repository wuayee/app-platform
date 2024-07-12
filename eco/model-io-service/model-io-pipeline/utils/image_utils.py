# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import base64
import io

import requests
from PIL import Image


def convert_image(image: Image):
    img_byte_arr = io.BytesIO()
    image.save(img_byte_arr, format='PNG')
    img_byte_arr = img_byte_arr.getvalue()
    img_base64 = base64.b64encode(img_byte_arr).decode('utf-8')
    return img_base64


def get_image(url: str):
    try:
        response = requests.get(url, verify=False)
        img_data = io.BytesIO(response.content)
        src_image = Image.open(img_data)
    except Exception:
        src_image = Image.open(url)
    image = src_image.copy()
    src_image.close()
    return image
