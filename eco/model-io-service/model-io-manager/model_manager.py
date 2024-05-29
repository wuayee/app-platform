# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.

import asyncio
import os
import stat
import argparse
import socket
import json
import logging
import yaml
import urllib3
import requests
import uvicorn

from fastapi import FastAPI, Response, Request
from fastapi.encoders import jsonable_encoder
from fastapi.responses import FileResponse, JSONResponse
from pydantic import BaseModel
from jinja2 import Template

from kubernetes import client, config, utils
from kubernetes.client import ApiClient, Configuration
from kubernetes.client.rest import ApiException
from fastapi.middleware.cors import CORSMiddleware
from urllib3.exceptions import MaxRetryError
from uvicorn.config import LOGGING_CONFIG

logging.basicConfig(format='%(asctime)s - %(levelname)s:  -  %(filename)s - %(lineno)s:  %(message)s',
                    level=logging.INFO)
LOGGING_CONFIG["formatters"]["default"]["fmt"] = "%(asctime)s - %(levelprefix)s %(message)s"
LOGGING_CONFIG["formatters"]["access"]["fmt"] = "%(asctime)s - %(levelprefix)s %(client_addr)s -" \
                                                "'%(request_line)s' %(status_code)s"

KUBE_CONFIG = "/root/.kube/config"
NAMESPACE_FILE = "/var/run/secrets/kubernetes.io/serviceaccount/namespace"

EMBEDDING_MODEL_TYPE = "Embedding"


def set_cross_header(response, request):
    response.headers["access-control-allow-headers"] = "content-type"
    response.headers["access-control-allow-methods"] = "GET,POST,PUT,DELETE,OPTIONS"
    response.headers["access-control-allow-credentials"] = "true"
    if request.headers.get("origin"):
        response.headers["access-control-allow-origin"] = request.headers["origin"]


def load_kube_config():
    with open(KUBE_CONFIG) as f:
        config_dict = yaml.safe_load(f)
        if os.path.exists(NAMESPACE_FILE):
            config_dict["clusters"][0]["cluster"]["server"] = "https://kubernetes.default.svc:443"
        config.load_kube_config_from_dict(config_dict)

load_kube_config()
k8s_client = client.AppsV1Api()

# Create CoreV1Api instance
api_instance = client.CoreV1Api()

MODEL_IO_NAMESPACE = "model-io"


logger = logging.getLogger('uvicorn.error')

model_weight_dir = "/mnt/models"

app = FastAPI()

app.model_io_gateways = []


class Item(BaseModel):
    name: str
    des: str
    image_name: str
    inference_accuracy: str
    replicas: int
    node_port: int
    npus: int


class Llm(BaseModel):
    name: str


class NodePortItem(BaseModel):
    node_port: int


gateways = []


def get_template():
    with open('static/template.yaml', 'r') as stream:
        data = stream.read()    
    documents = data.split('---')
    templates = [Template(doc) for doc in documents]
    return templates


@app.on_event("startup")
async def startup_event():
    asyncio.create_task(run_tasks())


def create_namespace_if_needed():
    namespace = client.V1Namespace(metadata=client.V1ObjectMeta(name=MODEL_IO_NAMESPACE))
    try:
        response = api_instance.read_namespace(MODEL_IO_NAMESPACE)
        return
    except ApiException as e:
        logger.warning(e)

    try:
        response = api_instance.create_namespace(MODEL_IO_NAMESPACE)
    except ApiException as e:
        logger.warning(e)
    return


async def run_tasks():
    _notify_model_io_gateways()
    create_namespace_if_needed()


@app.options("/v1/delete")
@app.options("/v1/start_up")
@app.options("/v1/list_supported_models")
@app.options("/v1/list_supported_models_meta")
def options_response(request : Request):
    res = {"status": "ok"}
    response = JSONResponse(content=jsonable_encoder(res))
    set_cross_header(response, request)
    return response


@app.get("/")
async def serve_index():
    return FileResponse("static/index.html")


# when start up the app, fetch services from k8s
def get_model_services():
    services = []
    try:
        services = api_instance.list_namespaced_service(namespace=MODEL_IO_NAMESPACE)
    except MaxRetryError as e:
        logger.error("Exception: %s.", str(e))

    return services


def get_self_namespace():
    namespace = ""
    if not os.path.exists(NAMESPACE_FILE):
        return namespace
    with open(NAMESPACE_FILE) as f:
        return f.read()
    return namespace


def get_model_io_gateways():
    endpoints = []

    model_io_gateways = os.environ.get("MODEL_IO_GATEWAYS")

    if model_io_gateways:
        for model_io_gateway in model_io_gateways.split(","):
            address = model_io_gateway.split(':')
            endpoints.append(
                             {"address": address[0],
                              "port": address[1]
                             }
                            )
        return endpoints

    try:
        namespace = get_self_namespace()
        if not namespace:
            return endpoints
        k8s_endpoints = api_instance.read_namespaced_endpoints("model-io-gateway", namespace)
        if not k8s_endpoints or not k8s_endpoints.subsets:
            return endpoints
        for subset in k8s_endpoints.subsets:
            endpoints.append(
                             {"address": subset.addresses[0].ip,
                              "port": subset.ports[0].port
                             }
                            )
    except MaxRetryError as e:
        logger.error(e)
    return endpoints


@app.get("/v1/gateways")
async def list_gateways():
    endpoints = get_model_io_gateways()
    return JSONResponse(content=jsonable_encoder(endpoints))

EXTRA_CHAT_MODEL = "extra_chat_model"
EXTRA_EMBED_MODEL = "extra_embed_model"
OBJECT_KEY = "object"
MODEL_VALUE = "model"
MODEL_TYPE_KEY = "type"
CHAT_MODEL_TYPE = "chat"
EMBED_MODEL_TYPE = "embed"

extra_models = [
    {
        "id": "Qwen-72B",
        OBJECT_KEY: MODEL_VALUE,
        MODEL_TYPE_KEY: CHAT_MODEL_TYPE
    },
    {
        "id": "Qwen1.5-32B-Chat",
        OBJECT_KEY: MODEL_VALUE,
        MODEL_TYPE_KEY: CHAT_MODEL_TYPE
    },
    {
        "id": "bce-embedding-base",
        OBJECT_KEY: MODEL_VALUE,
        MODEL_TYPE_KEY: EMBED_MODEL_TYPE
    },
    {
        "id": "bge-large-zh",
        OBJECT_KEY: MODEL_VALUE,
        MODEL_TYPE_KEY: EMBED_MODEL_TYPE
    },
    {
        "id": "bge-large-en",
        OBJECT_KEY: MODEL_VALUE,
        MODEL_TYPE_KEY: EMBED_MODEL_TYPE
    },
]


def list_models(chat_model_only=False):
    data = "data"
    models = {
        "object": "list",
        data : [],
    }
    models_meta = get_models_meta()
    models_service = get_cached_model_services()
    deployed_models = set()
    for service_name in models_service:
        try:
            model_meta = models_meta["services"][service_name]
            model_name = model_meta["name"]
            model_type = model_meta.get("type_id", CHAT_MODEL_TYPE)

            if not chat_model_only or model_type == CHAT_MODEL_TYPE:
                model = {
                    "id" : model_name,
                    "object" : "model",
                    "type" : model_type
                }
                models[data].append(model)
            deployed_models.add(model_name)
        except KeyError as e:
            logging.error("KeyError:%s", e)

    extra_gateway = os.environ.get("EXTRA_GATEWAY_URL")

    if not extra_gateway:
        return JSONResponse(content=jsonable_encoder(models))

    for extra_model in extra_models:
        model_type = extra_model.get(MODEL_TYPE_KEY, CHAT_MODEL_TYPE)
        if extra_model.get("id", "") not in deployed_models:
            if not chat_model_only or model_type == CHAT_MODEL_TYPE:
                models.get(data).append(extra_model)

    return JSONResponse(content=jsonable_encoder(models))
    


@app.get("/v1/models")
async def list_all_models():
    return list_models()


@app.get("/v1/chat/models")
async def list_chat_models():
    return list_models(chat_model_only=True)


def get_model_name(service_name):
    return service_name.split("_")[0]


def get_services():
    k8s_services = get_model_services()
    services = []

    if not k8s_services:
        return services

    for k8s_service in k8s_services.items:
        service_name = k8s_service.metadata.name
        services.append({
            "model_name" : get_model_name(k8s_service.metadata.labels["app"]),
            "service_name": service_name,
            "cluster_ip": k8s_service.spec.cluster_ip,
            "port": k8s_service.spec.ports[0].port,
            "node_port": k8s_service.spec.ports[0].node_port,
            "status": "healthy"
        })
    return services


@app.get("/v1/services")
async def list_services():
    services = get_services()
    return JSONResponse(content=jsonable_encoder(services))


def get_routes():
    routes = []
    services = get_services()
    for service in services:
        model_name = get_model_name_by_service_name(service["model_name"])
        url = f"http://{service['cluster_ip']}:{service['port']}"
        routes.append({
                         "id": model_name,
                         "model": model_name,
                         "url": url
                       })
    return routes


@app.get("/v1/routes")
async def list_routes():
    routes = get_routes()
    return JSONResponse(content=jsonable_encoder({"routes":routes}))


@app.post("/v1/health")
async def health(item: NodePortItem) -> Response:
    return Response(status_code=200)


def gen_error_info(code, detail):
    error_info = {
       "code": code,
       "detail": detail
    }
    return error_info


@app.delete("/v1/delete")
async def delete_model(llm: Llm, request : Request):
    model_name = llm.name.strip()
    model_services = get_cached_model_services()
    logger.info(model_services)
    if model_name.lower() in model_services:
        service_name = model_services[model_name.lower()]["service_name"]
        model_name = get_model_name(model_services[model_name.lower()]["model_name"])
        deployment_name = model_name + "-inference"
        code = "code"
        detail = "detail"
        try:
            response = api_instance.delete_namespaced_service(service_name, MODEL_IO_NAMESPACE)
            logger.info("Delete service {%s} successful", service_name)
        except ApiException as e:
            logger.warning(e)
            error_info = gen_error_info(e.status, e.reason)
            response = JSONResponse(status_code=522, content=jsonable_encoder(error_info))
            set_cross_header(response, request)
            return response
        try:
            response = k8s_client.delete_namespaced_deployment(deployment_name, MODEL_IO_NAMESPACE)
            logger.info("Delete deployment {%s} successful", deployment_name)
        except ApiException as e:
            error_info = gen_error_info(e.status, e.reason)
            logger.warning(e)
            response = JSONResponse(status_code=522, content=jsonable_encoder(error_info))
            set_cross_header(response, request)
            return response
        _notify_model_io_gateways()
        status_ok = 200
        error_info = gen_error_info(status_ok, "ok")
        response = JSONResponse(status_code=status_ok, content=jsonable_encoder(error_info))
        set_cross_header(response, request)
        return response
    else:
        error_code = 521
        error_args = [f"The Model {model_name} is not deployed"]
        detail = f"Failed to delete Model: {model_name}. Failure cause:{error_args}"
        error_info = gen_error_info(521, detail)
        response = JSONResponse(status_code=error_code, content=jsonable_encoder(error_info))
        set_cross_header(response, request)
        return response
    pass


def _notify_model_io_gateways():
    try:
        endpoints = get_model_io_gateways()
        if endpoints:
            refresh_model_services()
        for endpoint in endpoints:
            url = f"http://{endpoint['address']}:{endpoint['port']}/v1/routes"
            data = get_routes()
            routes = {"routes": data}

            response = requests.post(url, json=routes, timeout=10)
            logger.info("Notify_Model_IO_Gateways %s : %s", url, str(routes))
            logger.info(response)
    except requests.exceptions.RequestException as e:
        logger.error("Exception: %s.", str(e))


def get_supported_images(model_type=CHAT_MODEL_TYPE):
    if model_type == EMBED_MODEL_TYPE:
        supported_images = ["model-io-embedding:latest"]
    else:
        supported_images = ["mindie:latest"]
    return supported_images


models_meta_singleton = {}
supported_models_singleton = {"llms" : []}

MODEL_SERVICES_SINGLETON = None


def refresh_model_services():
    global MODEL_SERVICES_SINGLETON

    model_services = {}
    services = get_services()
    for service in services:
        model_services[service["model_name"]] = service

    MODEL_SERVICES_SINGLETON = model_services


def get_cached_model_services():
    global MODEL_SERVICES_SINGLETON
    model_services = MODEL_SERVICES_SINGLETON
    if model_services is None:
        refresh_model_services()
    return MODEL_SERVICES_SINGLETON


def get_model_name_by_service_name(service_name):
    models_meta = get_models_meta()
    model_name = service_name
    try:
        model_name = models_meta["services"][service_name]["name"]
    except KeyError as e:
        logger.error("KeyError:%s", e)
    return model_name


def get_models_meta():
    global models_meta_singleton
    models_meta = models_meta_singleton

    if not models_meta:
        with open('static/built-in-models.yaml', 'r') as f:
            models_meta = yaml.safe_load(f)
        models_meta["services"] = {}

    for model in models_meta.get("llms", []):
        model_name = model["name"]
        models_meta["services"][model_name.lower()] = model
        if model["type"] == EMBEDDING_MODEL_TYPE:
            model["type_id"] = "embed"
        else:
            model["type_id"] = "chat"

    return models_meta


def update_model_statistics(model, agg_statistics):
    statistics_fields = [
        "requests", "responses", "exceptions", "throughput",
        "latency", "speed", "total_input_tokens", "total_output_tokens"
    ]

    try:
        model_name = model["name"]
        for key in statistics_fields:
            if agg_statistics.get(model_name):
                model[key] = agg_statistics[model_name][key]
            else:
                model[key] = 0
    except KeyError as e:
        logger.error("KeyError:%s", e)


def get_supported_models_template(meta=False):
    global supported_models_singleton
    models = supported_models_singleton

    models_meta = get_models_meta()

    llms_key = "llms"
    if models_meta.get(llms_key):
        models[llms_key] = models_meta.get(llms_key)

    if meta:
        return models_meta.get(llms_key, {llms_key:[]})

    agg_statistics = get_models_statistics_from_gateway()

    for model in models.get("llms", []):
        model_type_id = model.get("type_id", "chat")
        model["supported_images"] = get_supported_images(model_type_id)
        name = "name"
        model_name = model[name]
        model["id"] = model_name + "-id" + "-1"

        model["replicas"] = 0
        model["port"] = 0
        model["xpu_consume"] = 0

        model_services = get_cached_model_services()
        try:
            if model_services.get(model[name].lower()):
                status = "status"
                model[status] = model_services[model[name].lower()][status]
                model["port"] = model_services[model[name].lower()]["node_port"]
                model["image"] = get_supported_images()[0]
            else:
                model["status"] = "undeployed"
                model["image"] = "unset"

            update_model_statistics(model, agg_statistics)

        except KeyError as e:
            logger.error("KeyError:%s", e)
        model["npu_flag"] = True


    return models


def merge_statistics(agg_statistics, model_statistics):
    model_name = model_statistics["model"]
    if not agg_statistics.get(model_name):
        agg_statistics[model_name] = model_statistics
    else:
        agg_statistics[model_name]["requests"] += model_statistics["requests"]
        agg_statistics[model_name]["responses"] += model_statistics["responses"]
        agg_statistics[model_name]["exceptions"] += model_statistics["exceptions"]
        agg_statistics[model_name]["throughput"] += model_statistics["throughput"]
        agg_statistics[model_name]["total_output_tokens"] += model_statistics["total_output_tokens"]
        agg_statistics[model_name]["total_input_tokens"] += model_statistics["total_input_tokens"]
        agg_statistics[model_name]["speed"] += model_statistics["speed"]
        latency = "latency"
        agg_statistics[model_name][latency] = max(model_statistics[latency], agg_statistics[model_name][latency])
    pass


def get_models_statistics_from_gateway():
    statistics = {}
    endpoints = get_model_io_gateways()

    for endpoint in endpoints:
        url = f"http://{endpoint['address']}:{endpoint['port']}/v1/statistics"
        response = requests.get(url, timeout=10)

        if response.status_code == 200:
            endpoint_statistics = json.loads(response.content)
            for model_statistics in endpoint_statistics:
                merge_statistics(statistics, model_statistics)

    return statistics


@app.get("/v1/models/statistics")
async def get_models_statistics():
    statistics = get_models_statistics_from_gateway()
    return JSONResponse(content=jsonable_encoder(statistics))
    

@app.get("/v1/list_supported_models")
async def list_supported_models(request : Request):
    models = get_supported_models_template()
    response = JSONResponse(content=jsonable_encoder(models))
    set_cross_header(response, request)
    return response


@app.get("/v1/list_supported_models_meta")
async def list_supported_models(request : Request):
    models = get_supported_models_template(meta=True)
    response = JSONResponse(content=jsonable_encoder(models))
    set_cross_header(response, request)
    return response


@app.get("/v1/notify_model_io_gateways")
async def notify_model_io_gateways():
    _notify_model_io_gateways()
    return Response(status_code=200)

model_weight_model_dir = {
        #model name and it's base dir name
        "Meta-Llama-3-8B-Instruct" : "Meta-Llama-3-8B-Instruct",
        "Qwen-14B-Chat" : "Qwen-14B-Chat"
}


@app.post("/v1/start_up")
async def start_up(item: Item, request : Request):
    templates = get_template()
    model_name = item.name.strip()
    model_base_dir = model_name

    model_base_dir = model_weight_model_dir.get(model_name, model_name)

    model_weight_path = os.path.join(model_weight_dir, model_base_dir)

    model_name = item.name.strip()

    render_data = {
        "name":model_name.lower(),
        "replicas":item.replicas,
        "node_port":item.node_port,
        "image_name":item.image_name.strip(),
        "model_weight_path":model_weight_path
    }

    fill_template = [template.render(render_data) for template in templates]
    yaml_objs = [yaml.safe_load(fill) for fill in fill_template]

    service_manifest = yaml_objs[0]
    deployment_manifest = yaml_objs[1]

    flags = os.O_WRONLY | os.O_CREAT
    modes = stat.S_IRUSR

    with os.fdopen(os.open(f'{item.name}.yaml', flags, modes), 'w') as f:
        for obj in yaml_objs:
            yaml.dump(obj, f)
            f.write('---\n')

    try:
        response = api_instance.create_namespaced_service(MODEL_IO_NAMESPACE, service_manifest)
    except ApiException as e:
        logger.warning(e)
    try:
        response = k8s_client.create_namespaced_deployment(MODEL_IO_NAMESPACE, deployment_manifest)
    except ApiException as e:
        logger.warning(e)

    _notify_model_io_gateways()

    status_code = 200
    error_info = {
        "code": status_code,
        "detail": "ok"
    }

    response = JSONResponse(status_code=status_code, content=jsonable_encoder(error_info))
    set_cross_header(response, request)
    return response

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', type=int, default=8000)
    parser.add_argument('--model_weight_dir', type=str, default="/mnt/models")
    args = parser.parse_args()
    model_weight_dir = args.model_weight_dir
    logger.info("Configured model weight dir: %s", model_weight_dir)
    uvicorn.run('model_manager:app',
                host="0.0.0.0",
                port=args.port,
                log_level="info",
                reload=True)
