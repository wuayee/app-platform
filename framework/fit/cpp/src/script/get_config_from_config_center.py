#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Copyright Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
import os
import json
import sys
import base64
import http.client
import urllib.parse
import get_static_token_util


METHOD_GET = "GET"
METHOD_POST = "POST"

DEFAULT_PORT = 80

KEY_APP_ID = "appId"
KEY_STATIC_TOKEN = "credential"

ENV_KEY_AUTH_IP = "FIT_KMS_AUTH_HOST"
ENV_KEY_HIS_IP = "FIT_KMS_HIS_HOST"
ENV_KEY_APP_ID = "FIT_APP_ID"

ENV_FIT_SUB_APPLICATION_ID_KEY = "pcloud_subapp_name"
ENV_REGION_KEY = "docker_region"
ENV_ENV_KEY = "docker_env"
ENV_DOCKER_VERSION_KEY = "docker_version"
ENV_APP_CONFIG_HOST = "app_config_host"


def http_request(host, port, method, url, params, headers):
    conn = http.client.HTTPConnection(host, port)
    conn.request(method, url, params, headers)
    response = conn.getresponse()
    return response.read().decode()


def get_dynamic_token():
    static_token = get_static_token_util.get_static_token()
    static_token = base64.b64encode(static_token.encode('utf-8')).decode('utf-8')

    host = os.environ.get(ENV_KEY_AUTH_IP)
    port = DEFAULT_PORT
    method = METHOD_POST
    url = "/ApiCommonQuery/appToken/getRestAppDynamicToken"

    app_id = os.environ.get(ENV_KEY_APP_ID)
    params = json.dumps({KEY_APP_ID : app_id, KEY_STATIC_TOKEN : static_token})
    KEY_ACCEPT = "Accept"
    VALUE_APP_JSON = "application/json"
    KEY_CONTENT_TYPE = "Content-Type"
    headers = {KEY_ACCEPT : VALUE_APP_JSON, KEY_CONTENT_TYPE : VALUE_APP_JSON}

    result = http_request(host, port, method, url, params, headers)
    json_result = json.loads(result)
    return json_result["result"]


def get_config(dynamicToken):
    host = os.environ.get(ENV_APP_CONFIG_HOST) # todo add env
    port = DEFAULT_PORT
    method = METHOD_GET

    # build url
    app_id = os.environ.get(ENV_KEY_APP_ID)
    sub_application_id = os.environ.get(ENV_FIT_SUB_APPLICATION_ID_KEY)
    region = os.environ.get(ENV_REGION_KEY)
    environment = os.environ.get(ENV_ENV_KEY)
    version = os.environ.get(ENV_DOCKER_VERSION_KEY)
    url = "/ConfigCenter/services/saasConfigcenterGetConfig"
    url = (url + "?application_id=" + app_id + "&sub_application_id=" + sub_application_id + "&region=" + region
           + "&environment=" + environment + "&version=" + version)
    params = None

    # 权限填写动态token
    AUTHORIZATION_KEY = "Authorization"
    headers = {AUTHORIZATION_KEY : dynamicToken}

    json_result = json.loads(http_request(host, port, method, url, params, headers))

    # 解析每一个配置
    configs = json_result["j2c"]
    config_dict = {}
    for config in configs:
        config_dict[config["j2c_name"]] = [
            config["password"],
            config["work_key_cipher"],
            config["config_parts"][0],
            config["config_parts"][1]
        ]
    return config_dict


def decrypt_j2c_config(j2cConfigDict):
    config_dict = {}
    for key, value in j2cConfigDict.items():
        config_dict[key] = get_static_token_util.decrypt(value[0], value[1], value[2], value[3])
    return config_dict

if __name__ == "__main__":
    dynamicTokenOut = get_dynamic_token()
    j2cConfigDictOut = get_config(dynamicTokenOut)
    j2cConfigsOut = decrypt_j2c_config(j2cConfigDictOut)
    jsonConfigs = json.dumps(j2cConfigsOut)
    print(jsonConfigs, end='')