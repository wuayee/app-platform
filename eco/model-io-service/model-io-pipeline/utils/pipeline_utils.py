# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
from pipeline_factory import PipelineFactory


def create_pipeline(task: str, model: str):
    return PipelineFactory.get_pipeline(task)(task=task, model=model)
