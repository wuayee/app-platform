# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
功 能：大数据缓存接口对外提供的装饰器
"""
from typing import Callable
from functools import wraps

from fit_common_struct.core import Endpoint
from fitframework.api.decorators import local_context, fit
from fitframework.utils.json_path_utlis import convert_field_by_json_path


@local_context('worker.id')
def _worker_id():
    pass


class CacheValueMetadata:
    def __init__(self, type: str, length: int):
        self.type = type
        self.length = length


def _validate_content_type(content) -> None:
    if not isinstance(content, bytes) and not isinstance(content, str):
        raise Exception(f"only content of the bytes or string type can be converted.")


def _assemble_global_index(index: str, worker_id: str, content_type: str):
    if content_type == "str":
        return f"{index}@{worker_id}"
    else:
        return f"{index}@{worker_id}".encode("utf-8")


def _disassemble_global_index(global_index) -> (str, str):
    if isinstance(global_index, str):
        global_index_as_str = global_index
    else:
        global_index_as_str = global_index.decode("utf-8")
    index, worker_id = global_index_as_str.split("@")
    return index, worker_id


def _get_cached_value_by_global_index(index: str, worker_id: str, content_type: str):
    def address_filter_by_worker_id(endpoint: Endpoint):
        return endpoint.address.workerId == worker_id

    @fit("com.huawei.fit.bigdata.cache.read.meta", address_filter=address_filter_by_worker_id)
    def read_meta_from_cache(_: str) -> CacheValueMetadata:
        pass

    @fit("com.huawei.fit.bigdata.cache.delete", address_filter=address_filter_by_worker_id)
    def delete_value_from_cache(_: str) -> None:
        pass

    meta: CacheValueMetadata = read_meta_from_cache(index)
    if meta.type is None:
        raise Exception(f"cannot read meta from cache. [worker_id={worker_id}, index={index}]")
    if meta.type != content_type:
        raise Exception(f"type of data is incorrect. "
                        f"[worker_id={worker_id}, index={index} ,data_type={meta.type}, content_type={content_type}]")
    if worker_id != _worker_id():
        def address_filter_by_current_worker_id(endpoint: Endpoint):
            return endpoint.address.workerId == _worker_id()

        @fit("com.huawei.fit.bigdata.cache.validate.capacity", address_filter=address_filter_by_current_worker_id)
        def validate_capacity(to_allocate_memory: int) -> None:
            pass

        validate_capacity(meta.length)
    if content_type == "str":
        @fit("com.huawei.fit.bigdata.cache.read.str", address_filter=address_filter_by_worker_id)
        def read_str_from_cache(_: str) -> str:
            pass

        result = read_str_from_cache(index)
    else:
        @fit("com.huawei.fit.bigdata.cache.read.bytes", address_filter=address_filter_by_worker_id)
        def read_bytes_from_cache(_: str) -> bytes:
            pass

        result = read_bytes_from_cache(index)
    delete_value_from_cache(index)
    return result


def _replace_global_index_with_content(global_index):
    """
    将表示全局索引的值替换为缓存中 str 或 bytes 类型的内容
    :param global_index: 表示全局索引
    :return: 表示该索引值所对应的缓存中内容
    """
    if global_index is None:
        return None
    _validate_content_type(global_index)

    index, worker_id = _disassemble_global_index(global_index)
    content_type = "bytes" if isinstance(global_index, bytes) else "str"
    result = _get_cached_value_by_global_index(index, worker_id, content_type)
    if result is None:
        raise Exception(f"cannot get value from cache. [index={index}, worker_id={worker_id}]")
    return result


def _replace_content_with_global_index(content):
    """
    将 str 或 bytes 类型的值保存到缓存并替换为索引
    :param content: 表示待替换的 str 或 bytes 类型的内容
    :return: 表示该值在缓存中的索引
    """
    if content is None:
        return None
    _validate_content_type(content)

    def address_filter_by_current_worker_id(endpoint: Endpoint):
        return endpoint.address.workerId == _worker_id()

    if isinstance(content, str):
        @fit("com.huawei.fit.bigdata.cache.create.str", address_filter=address_filter_by_current_worker_id)
        def create_str_in_cache(_: str) -> str:
            pass

        return _assemble_global_index(create_str_in_cache(content), _worker_id(), "str")
    elif isinstance(content, bytes):
        @fit("com.huawei.fit.bigdata.cache.create.bytes", address_filter=address_filter_by_current_worker_id)
        def create_bytes_in_cache(_: bytes) -> str:
            pass

        return _assemble_global_index(create_bytes_in_cache(content), _worker_id(), "bytes")


def _convert_arguments_with_json_paths_and_method(args, arguments_json_path, method):
    converted_args = []
    index = 0
    for arg in args:
        if index not in arguments_json_path:
            converted_args.append(arg)
            continue
        json_paths = arguments_json_path[index] if isinstance(arguments_json_path[index], list) else [
            arguments_json_path[index]]
        converted_arg = arg
        for json_path in json_paths:
            converted_arg = convert_field_by_json_path(converted_arg, json_path, method)
        converted_args.append(converted_arg)
        index += 1
    return converted_args


def data_injection(arguments_json_path: dict, return_json_paths) -> Callable:
    if arguments_json_path is not None and not isinstance(arguments_json_path, dict):
        raise Exception(f"type of parameters_json_path must be a dict.")

    def decorator(func):
        @wraps(func)
        def wrapper(*args):
            if arguments_json_path is not None:
                converted_args = _convert_arguments_with_json_paths_and_method(args, arguments_json_path,
                                                                               _replace_global_index_with_content)
            else:
                converted_args = args
            result = func(*converted_args)
            if return_json_paths is not None:
                json_paths = return_json_paths if isinstance(return_json_paths, list) else [return_json_paths]
                for json_path in json_paths:
                    result = convert_field_by_json_path(result, json_path, _replace_content_with_global_index)
            return result

        return wrapper

    return decorator
