# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. -. All rights reserved.

from langchain_community.tools import ShellTool
from langchain_experimental.tools import PythonREPLTool
from langchain_google_community import GoogleSearchRun, GoogleSearchAPIWrapper

from core.fel_langchain.langchain_registers import register_function_tools, register_api_tools

# function tools
function_tools = [
    PythonREPLTool(),
    ShellTool(),
]
register_function_tools(function_tools)

# api tools
register_api_tools(lambda dict_args: GoogleSearchRun(api_wrapper=GoogleSearchAPIWrapper(**dict_args)),
                   ["google_api_key", "google_cse_id"], "google_search")

if __name__ == "__main__":
    import time
    from plugins.fel_langchain_tools.schema_helper import dump_schema

    current_timestamp = time.strftime('%Y%m%d%H%M%S')
    dump_schema(function_tools, f"./tool_schema-{str(current_timestamp)}.json")
