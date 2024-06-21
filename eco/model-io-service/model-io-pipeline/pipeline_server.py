# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
import argparse
import uvicorn
from fastapi import FastAPI
from fastapi import Request
from fastapi.responses import JSONResponse, Response
from params import PipelineParam
from utils.param_utils import clean_args
from utils.pipeline_utils import create_pipeline

app = FastAPI()
hf_pipeline = None


def parse_args():
    parser = argparse.ArgumentParser(description="Pipeline")
    parser.add_argument("--model",
                        type=str,
                        default=None,
                        help="model")
    parser.add_argument("--task",
                        type=str,
                        default=None,
                        help="task")
    return parser.parse_args()


@app.post("/v1/health")
async def health():
    return JSONResponse(content="pipeline is health", status_code=200)


@app.get("/v2/health/live")
async def live():
    return Response(status_code=200)


@app.get("/v2/health/ready")
async def ready():
    return Response(status_code=200)


@app.post("/v1/huggingface/pipeline")
async def use_pipeline(request: PipelineParam, raw_request: Request):
    global hf_pipeline
    request.args = clean_args(request.args)
    try:
        response = hf_pipeline.call(request.args)
    except AttributeError:
        return JSONResponse(content={'err_msg': "call pipeline failed:{}".format(request.args)}, status_code=500)

    return JSONResponse(content=response)


if __name__ == "__main__":
    args = parse_args()
    hf_pipeline = create_pipeline(args.task, args.model)
    uvicorn.run(app,
                host="0.0.0.0",
                port=9991,
                log_level="info")
