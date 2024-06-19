# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
from typing import Dict, Optional
from pydantic import BaseModel


class PipelineParam(BaseModel):
    task: str = None
    model: Optional[str] = None
    args: Dict = {}
