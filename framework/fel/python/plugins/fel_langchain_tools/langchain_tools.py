# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. -. All rights reserved.

from langchain_community.tools import ShellTool
from langchain_experimental.tools import PythonREPLTool
from langchain_google_community import GoogleSearchRun, GoogleSearchAPIWrapper

from fel_langchain.langchain_registers import register_function_tools, register_api_tools


def _get_python_repl_tool() -> PythonREPLTool:
    import builtins
    from langchain_experimental.utilities import PythonREPL
    from RestrictedPython.Guards import safe_builtins

    def safer_import(name, globals=None, locals=None, fromlist=(), level=0):
        white_list = {'asyncio', 'json', 'numpy', 'typing'}
        if name not in white_list:
            raise NameError(f'model {name} is not valid.')
        return __import__(name, globals, locals, fromlist, level)

    builtins = {
        **safe_builtins,
        'print': getattr(builtins, 'print'),
        '__name__': getattr(builtins, '__name__'),
        'setattr': setattr,
        '__import__': safer_import
    }
    return PythonREPLTool(python_repl=PythonREPL(globals=builtins))


# function tools
function_tools = [
    _get_python_repl_tool(),
    ShellTool(),
]
register_function_tools(function_tools)

# api tools
register_api_tools(lambda dict_args: GoogleSearchRun(api_wrapper=GoogleSearchAPIWrapper(**dict_args)),
                   ["google_api_key", "google_cse_id"], "google_search")

if __name__ == "__main__":
    import time
    from fel_langchain.langchain_schema_helper import dump_schema

    current_timestamp = time.strftime('%Y%m%d%H%M%S')
    dump_schema(function_tools, f"./tool_schema-{str(current_timestamp)}.json")
