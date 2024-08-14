# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
Description: 调用ModelLite模型的通用接口
Create: 2024/7/12 10:26
"""
import json
from pathlib import Path
import ssl
from typing import Dict
from urllib.request import Request, urlopen

import requests

from fitframework.api.logging import plugin_logger as logger


def request_model_lite(url: str, body: dict, header: dict, context: ssl.SSLContext):
    if context:
        req = Request(url=url, data=json.dumps(body).encode(), headers=header, method="POST")
        response = json.loads(urlopen(req, context=context).read().decode("utf-8"))
    else:
        response = requests.post(url=url, data=json.dumps(body), headers=header, stream=False).json()
    return response


def load_certificate(certificate_path: Path, is_certificate: bool) -> ssl.SSLContext:
    """加载证书"""
    context = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
    context.check_hostname = False
    if is_certificate:
        context.load_verify_locations(certificate_path)
        context.verify_mode = ssl.CERT_REQUIRED
    else:
        context.verify_mode = ssl.CERT_NONE
    return context


class LLM:
    def __init__(self, url: str = None, header: Dict = None, body: Dict = None, post_params: Dict = None):
        self.url = url
        self.header = header
        self.body = body
        self.context = load_certificate(
            post_params.get("certificate_path"),
            post_params.get("is_certificate")
        ) if post_params.get("is_https") else None

    def __call__(self, input_str: str) -> Dict:
        try:
            self.body["messages"][0]["content"] = input_str
            outputs = self._call_service()
            self.body["messages"][0]["content"] = "你好"
            return outputs
        except KeyError as e:
            logger.error("The request format error: %s", e, exc_info=True)
            raise KeyError("The request format error: {}".format(e)) from e

    def _call_service(self) -> Dict:
        """调用大模型服务"""
        if not all([self.url, self.header, self.body.get("messages", [])[0].get("content")]):
            logger.error("LLM is not configured completely")
            raise ValueError("Model is not configured completely")
        try:
            response = request_model_lite(self.url, self.body, self.header, self.context)
            return response
        except Exception as e:
            logger.error("LLM response error: %s", e)
            raise Exception("LLM response error: {}".format(e)) from e


class Model:
    def __init__(self, url: str = None, header: Dict = None, body: Dict = None, post_params: Dict = None):
        self.url = url
        self.header = header
        self.body = body
        self.context = load_certificate(
            post_params.get("certificate_path"),
            post_params.get("is_certificate")
        ) if post_params.get("is_https") else None

    def __call__(self, input_str: str) -> Dict:
        outputs = self._call_service()
        return outputs

    def _call_service(self) -> Dict:
        """调用模型服务"""
        if not all([self.url, self.header, self.body]):
            logger.error("Model is not configured completely")
            raise ValueError("Model is not configured completely")
        try:
            response = request_model_lite(self.url, self.body, self.header, self.context)
            return response
        except Exception as e:
            logger.error("Model response error: %s", e)
            raise Exception("Model response error: {}".format(e)) from e
