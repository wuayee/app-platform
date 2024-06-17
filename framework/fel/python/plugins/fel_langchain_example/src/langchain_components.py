# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. -. All rights reserved.

from eco_example.langchain_react_graph_agent import graph as graph_agent
from .langchain_registers import register_graph_agent

register_graph_agent("react", graph_agent)
