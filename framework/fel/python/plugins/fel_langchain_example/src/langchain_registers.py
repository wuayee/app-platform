# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. -. All rights reserved.

import uuid
from typing import List, Any, Dict, Union, Optional

from fitframework import fitable
from langchain.chains.base import Chain
from langchain_core.runnables import RunnableConfig, Runnable
from langgraph.graph.graph import CompiledGraph


def register_runnable(task_id: str,
                      fitable_id: str,
                      runnable: Runnable,
                      config: Optional[RunnableConfig] = None,
                      **kwargs: Any):
    @fitable(generic_id=f"langchain.runnable.{task_id}", fitable_id=f'{fitable_id}')
    def invoke(input_data: object) -> object:
        return runnable.invoke(input_data, config, **kwargs)


def register_graph_agent(fitable_id: str,
                         agent: Union[Chain, CompiledGraph],
                         config: Optional[RunnableConfig] = None,
                         **kwargs: Any):
    @fitable(generic_id=f"langchain.graph.agent", fitable_id=f'{fitable_id}')
    def invoke(input_data: List[Dict[str, object]]) -> List[Dict[str, object]]:
        config_real = config if config is not None else {"configurable": {"thread_id": f"{uuid.UUID}"}}
        input_dict = {"messages": input_data}
        return agent.invoke(input_dict, config_real, stream_mode="values", output_keys="messages", **kwargs)
