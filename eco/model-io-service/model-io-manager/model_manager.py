# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.

import os
import stat
import argparse
import logging
import requests
import uvicorn
import yaml

from fastapi import FastAPI, Response
from fastapi.responses import FileResponse
from pydantic import BaseModel
from jinja2 import Template

app = FastAPI()

logger = logging.getLogger('uvicorn.error')

model_weight_dir = "/root/models"


class Item(BaseModel):
    name: str
    des: str
    image_name: str
    inference_accuracy: str
    replicas: int
    node_port: int
    npus: int


class NodePortItem(BaseModel):
    node_port: int


def get_template():
    with open('static/template.yaml', 'r') as stream:
        data = stream.read()    
    documents = data.split('---')
    templates = [Template(doc) for doc in documents]
    return templates


@app.get("/")
async def serve_index():
    return FileResponse("static/index.html")


@app.post("/health")
async def health(item: NodePortItem) -> Response:
    return Response(status_code=200)


@app.post("/delete")
def delete_model(item: Item):
    cmd = 'kubectl delete -f ' + f'{item.name}_{item.inference_accuracy}.yaml'
    res = os.popen(cmd, 'r', 1)
    logger.info(res.read())


@app.post("/start_up")
def start_up(item: Item):
    templates = get_template()
    model_weight_path = model_weight_dir + f'/{item.name}'
    render_data = {
        "name":item.name,
        "replicas":item.replicas,
        "node_port":item.node_port,
        "image_name":item.image_name,
        "model_weight_path":model_weight_path
    }
    fill_template = [template.render(render_data) for template in templates]
    yaml_obj = [yaml.safe_load(fill) for fill in fill_template]

    flags = os.O_WRONLY | os.O_CREAT
    modes = stat.S_IRUSR

    with os.fdopen(os.open(f'{item.name}_{item.inference_accuracy}.yaml', flags, modes), 'w') as f:
        for obj in yaml_obj:
            yaml.dump(obj, f)
            f.write('---\n')

    cmd = 'kubectl apply -f ' + f'{item.name}_{item.inference_accuracy}.yaml'
    res = os.popen(cmd, 'r', 1)
    logger.info(res.read())

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
