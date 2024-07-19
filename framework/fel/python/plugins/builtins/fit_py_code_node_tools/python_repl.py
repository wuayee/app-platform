# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. -. All rights reserved.
import asyncio
import builtins
import inspect
import multiprocessing
import platform
from typing import Any, Dict

import json

from fitframework.api.decorators import fitable
from .safe_global import safe_builtins


_PYTHON_REPL_HEADER = '''
import asyncio
import json
from typing import Any

Output = Any


'''

_HEADER_LEN = len(_PYTHON_REPL_HEADER.split('\n'))

_USER_FUNCTION_ENTRYPOINT = 'main'

_IMPORT_WHITELIST = {'asyncio', 'json', 'numpy', 'typing'}


class Args:
    def __init__(self, fields):
        setattr(self, 'params', fields)


class Result:
    def __init__(self, ok: bool, value: Any):
        self._ok = ok
        self._value = value

    @staticmethod
    def ok(value: Any) -> 'Result':
        return Result(True, value)

    @staticmethod
    def err(value: Any) -> 'Result':
        return Result(False, value)

    @staticmethod
    def from_dict(data: Dict[str, Any]) -> 'Result':
        return Result(data['isOk'], data['value'])

    @staticmethod
    def from_json(json_str: str) -> 'Result':
        data = json.loads(json_str)
        return Result.from_dict(data)

    def to_dict(self) -> Dict[str, Any]:
        result_dict = {'isOk': self._ok}
        if self._ok:
            result_dict['value'] = self._value
        else:
            result_dict['msg'] = self._value
        return result_dict

    def to_json(self) -> str:
        return json.dumps(self.to_dict())

    def is_ok(self):
        return self._ok

    def value(self):
        return self._value


# 创建一个安全的执行环境
def _create_restricted_exec_env():
    def safer_import(name, my_globals=None, my_locals=None, fromlist=(), level=0):
        if name not in _IMPORT_WHITELIST:
            raise NameError(f'model {name} is not valid.')
        return __import__(name, my_globals, my_locals, fromlist, level)

    safe_globals = {
        '__builtins__': {
            **safe_builtins,
            'print': getattr(builtins, 'print'),
            '__name__': getattr(builtins, '__name__'),
            'setattr': setattr,
            '__import__': safer_import,
            'Args': Args
        }
    }
    return safe_globals


# 执行受限代码
def _execute_code_with_restricted_python(args: Dict[str, object], code: str, result_queue: multiprocessing.Queue):
    try:
        full_python_code = (f'{_PYTHON_REPL_HEADER}'
                            f'{code}\n\n')

        local_vars = {}
        safer_globals = _create_restricted_exec_env()
        exec(full_python_code, safer_globals, local_vars)
        if (_USER_FUNCTION_ENTRYPOINT not in local_vars or
                not inspect.isfunction(local_vars.get(_USER_FUNCTION_ENTRYPOINT))):
            raise NameError("main function not defined")
        entrypoint = local_vars.get(_USER_FUNCTION_ENTRYPOINT)

        if inspect.iscoroutinefunction(entrypoint):
            ret = asyncio.run(entrypoint(Args(args)))
        else:
            ret = entrypoint(Args(args))
        result_queue.put(Result.ok(ret))
    except Exception as e:
        result_queue.put(Result.err(e))


def _get_except_msg(error: Any) -> str:
    if isinstance(error, SyntaxError):
        error_msg = f"{error.msg} at line {error.lineno - _HEADER_LEN}, column {error.offset}: {error.text}"
    elif isinstance(error, KeyError):
        error_msg = f"key {str(error)} do not exist"
    else:
        error_msg = str(error)
    return f"[{error.__class__.__name__}] {error_msg}"


def _execute_code_in_process(args: Dict[str, object],
                            code: str,
                            result_queue: multiprocessing.Queue,
                            timeout: int = 1):
    process = multiprocessing.Process(target=_execute_code_with_restricted_python, args=(args, code, result_queue))
    process.start()
    process.join(timeout)

    if process.is_alive():
        process.terminate()
        process.join()

        return Result.err(TimeoutError("Execution timed out"))

    return Result.ok(None)


def _execute_node_impl(args: Dict[str, object], code: str, timeout: int = 1, multiprocess: bool = False):
    result_queue = multiprocessing.Queue()
    if platform.system() == 'Windows':
        # windows平台下，创建process存在错误，创建协程/线程没有办法超时终止
        _execute_code_with_restricted_python(args, code, result_queue)
    else:
        if multiprocess:
            ret = _execute_code_in_process(args, code, result_queue, timeout)
            if not ret.is_ok():
                return Result.err(_get_except_msg(ret.value()))
        else:
            _execute_code_with_restricted_python(args, code, result_queue)

    result = result_queue.get()
    if not result.is_ok():
        return Result.err(_get_except_msg(result.value()))

    return result


@fitable("CodeNode.tool", "Python_REPL")
def execute_code(args: Dict[str, object], code: str) -> str:
    execute_result = _execute_node_impl(args, code)
    return execute_result.to_json()
