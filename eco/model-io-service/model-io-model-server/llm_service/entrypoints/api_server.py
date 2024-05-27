# -*- coding: utf-8 -*-
# !/usr/bin/python
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.

import argparse
import asyncio
from datetime import timedelta
import uvicorn
import fastapi
from fastapi import Request
from fastapi.responses import JSONResponse, StreamingResponse, Response
from llm_service.utils.protocol import ChatCompletionRequest, ChatMessage
from llm_service.engine.vllm_engine import VllmEngine
from llm_service.engine.engine_config import EngineConfig
from llm_service.utils.env_utils import engine_select

engine = None

app = fastapi.FastAPI()


def parse_args():
    parser = argparse.ArgumentParser(
        description="OpenAI-Compatible RESTful API server.")
    parser.add_argument("--host", type=str, default=None, help="host name")
    parser.add_argument("--port", type=int, default=8000, help="port number")
    parser.add_argument("--model",
                        type=str,
                        default=None,
                        help="The path of model")
    parser.add_argument("--response_role",
                        type=str,
                        default="assistant",
                        help="The role name to return")
    # VLLM Engine Args
    parser.add_argument("--gpu_memory_utilization", type=float, default=0.95)
    parser.add_argument("--tensor_parallel_size", type=int, default=1)
    parser.add_argument("--enforce_eager", type=bool, default=False, help="Eager mode to save memory")
    parser.add_argument("--max_model_len", type=int, default=8192)
    parser.add_argument("--dtype", type=str, default="float16")

    return parser.parse_args()


@app.get("/v1/health")
async def health(raw_request: Request) -> Response:
    request = ChatCompletionRequest(model='', messages=[ChatMessage(role='user', content='Hi')], max_tokens=5)
    try:
        await asyncio.wait_for(engine.generate(request, raw_request), timeout=5)
        return Response(status_code=200)
    except asyncio.TimeoutError:
        return Response(status_code=408)


@app.get("/v1/models")
async def show_available_models():
    return Response(status_code=200)


@app.post("/v1/chat/completions")
async def create_chat_completion(request: ChatCompletionRequest, raw_request: Request):
    generator = await engine.generate(request, raw_request)
    if request.stream:
        return StreamingResponse(content=generator,
                                 media_type="text/event-stream")
    return JSONResponse(content=generator.model_dump())


if __name__ == "__main__":
    args = parse_args()
    engine_type = engine_select()
    engine_config = EngineConfig(model=args.model,
                                 gpu_memory_utilization=args.gpu_memory_utilization,
                                 tensor_parallel_size=args.tensor_parallel_size,
                                 enforce_eager=args.enforce_eager,
                                 max_model_len=args.max_model_len,
                                 dtype=args.dtype)
    if engine_type == "GPU":
        engine = VllmEngine(engine_config, args.response_role)
    elif engine_type == "NPU":
        raise ValueError(f"Invalid input parameter not support NPU currently")
    elif not engine_type:
        raise ValueError(f"Invalid input parameter without suitable device")
    uvicorn.run(app,
                host=args.host,
                port=args.port,
                log_level="info")
