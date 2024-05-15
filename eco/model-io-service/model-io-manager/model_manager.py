# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.

import os
import stat
import argparse
import logging
import requests
import socket
import uvicorn
import yaml

from fastapi import FastAPI, Response
from fastapi.encoders import jsonable_encoder
from fastapi.responses import FileResponse, JSONResponse
from pydantic import BaseModel
from jinja2 import Template

from kubernetes import client, config
from kubernetes.client import ApiClient, Configuration

client_configuration = None
kube_config_path = "/root/.kube/config"

def load_kube_config():
    with open(kube_config_path) as f:
        config_dict = yaml.safe_load(f)
        config_dict["clusters"][0]["cluster"]["server"] = "https://kubernetes:443"
        config.load_kube_config_from_dict(config_dict)

load_kube_config()

# Create CoreV1Api instance
api_instance = client.CoreV1Api()

namespace = "model-io"


logger = logging.getLogger('uvicorn.error')

model_weight_dir = "/root/models"


model_io_gateways = []

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


class GateWayAddr(BaseModel):
    addr: str

gateways = []


def get_template():
    with open('static/template.yaml', 'r') as stream:
        data = stream.read()    
    documents = data.split('---')
    templates = [Template(doc) for doc in documents]
    return templates


@app.get("/")
async def serve_index():
    return FileResponse("static/index.html")


@app.get("/vl/gateways")
async def list_gateways():
    return "Hello"


models = [
    {
        "name": "Qwen",
        "urls": "http.url"
    }
]


# when start up the app, fetch services from k8s
def get_model_services():
    services = api_instance.list_namespaced_service(namespace = "model-io")
    return services

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

    k8s_endpoints = api_instance.read_namespaced_endpoints("model-io-gateway", "jade-first-blood")
    for subset in k8s_endpoints.subsets:
        endpoints.append(
                         {"address": subset.addresses[0].ip,
                          "port": subset.ports[0].port
                         }
                        )
    return endpoints

@app.get("/v1/gateways")
async def list_gateways():
    endpoints = get_model_io_gateways()
    return JSONResponse(content = jsonable_encoder(endpoints))


@app.get("/v1/models")
async def list_models():
    return JSONResponse(content = jsonable_encoder(models))

def get_model_name(service_name):
    return service_name.split("_")[0]

def get_services():
    k8s_services = get_model_services()
    services = []

    for k8s_service in k8s_services.items:
        service_name = k8s_service.metadata.name
        services.append({ "model_name" : get_model_name(k8s_service.metadata.labels["app"]),
                          "service_name": service_name,
                          "cluster_ip": k8s_service.spec.cluster_ip,
                          "port": k8s_service.spec.ports[0].port,
                          "node_port": k8s_service.spec.ports[0].node_port,
                         })
    return services

@app.get("/v1/services")
async def list_services():
    services = get_services()
    return JSONResponse(content = jsonable_encoder(services))

def get_routes():
    routes = []
    services = get_services()
    for service in services:
        url = f"http://{service["cluster_ip"]}:{service["port"]}"
        routes.append({
                         "id": service["model_name"],
                         "model": service["model_name"],
                         "url": url
                       })
    return routes


@app.get("/v1/routes")
async def list_routes():
    routes = get_routes()
    return JSONResponse(content = jsonable_encoder({"routes":routes}))

@app.post("/health")
async def health(item: NodePortItem) -> Response:
    return Response(status_code=200)


@app.post("/delete")
async def delete_model(llm: Llm):
    cmd = 'kubectl delete -f ' + f'{llm.name.strip()}.yaml'
    res = os.popen(cmd, 'r', 1)
    logger.info(res.read())

def notify_model_io_gateways():
    endpoints = get_model_io_gateways()
    for endpoint in endpoints:
        url = f"http://{endpoint['address']}:{endpoint['port']}/v1/routes"
        data = get_routes()
        routes = {"routes": data}

        response = requests.post(url, json=routes)
        logger.info(f"Notify_Model_IO_Gateways {url} :" + str(routes))
        logger.info(response)

@app.post("/start_up")
async def start_up(item: Item):
    templates = get_template()
    model_weight_path = model_weight_dir + f'/{item.name}'

    render_data = {
        "name":item.name.strip(),
        "replicas":item.replicas,
        "node_port":item.node_port,
        "image_name":item.image_name.strip(),
        "model_weight_path":model_weight_path
    }

    fill_template = [template.render(render_data) for template in templates]
    yaml_obj = [yaml.safe_load(fill) for fill in fill_template]

    flags = os.O_WRONLY | os.O_CREAT
    modes = stat.S_IRUSR

    with os.fdopen(os.open(f'{item.name}.yaml', flags, modes), 'w') as f:
        for obj in yaml_obj:
            yaml.dump(obj, f)
            f.write('---\n')

    cmd = 'kubectl apply -f ' + f'{item.name}.yaml'
    res = os.popen(cmd, 'r', 1)
    logger.info(res.read())
    notify_model_io_gateways()

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', type=int, default=8000)
    parser.add_argument('--model_weight_dir', type=str, default="/root/models")
    args = parser.parse_args()
    model_weight_dir = args.model_weight_dir
    uvicorn.run('model_manager:app',
                host="0.0.0.0",
                port=args.port,
                log_level="info",
                reload=True)
