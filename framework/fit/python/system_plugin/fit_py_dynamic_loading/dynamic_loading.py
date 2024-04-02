# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2019-2024. All rights reserved.
"""
功 能：Fit底座Server服务接口实现：HTTP
目前HTTP服务绑定JSON协议。
"""
import os
import shutil
import subprocess
import sys
import tarfile
import time
import threading
import traceback
import zipfile
from typing import List

from fitframework.api.enums import PluginType
from fitframework.utils.plugin_utils import load_plugin_info

from fitframework import const
from fitframework.api.decorators import fit, fitable, value
from fitframework.api.logging import sys_plugin_logger
from fitframework.domain.plugin import Plugin
from fitframework.utils.tools import get_memory_usage


@value('default_user_plugin_level', default_value=3, converter=int)
def _default_user_plugin_level() -> int:
    pass


@value('user_plugins_path')
def _user_plugins_path() -> str:
    pass


@value('cache_path')
def _cache_path() -> str:
    pass


@value('scan_period', default_value=3, converter=int)
def _scan_period() -> int:
    pass


@value('python_interpreter_alias')
def _python_interpreter_alias() -> str:
    pass


def _check_command_availability(command) -> bool:
    return subprocess.run([command, "--version"], capture_output=True, text=True).stdout.strip().startswith("Python")


@fit(const.SERVICE_DB_REGISTER_ALL_FIT_SERVICE_GEN_ID)
def register_all_fit_services() -> bool:
    pass


def _decompress_plugin_and_get_level(path: str) -> (str, str):
    file_name = os.path.basename(path)
    plugin_name = file_name.split('.')[0]
    decompressed_plugin_path = os.path.join(os.getcwd(), _cache_path(), plugin_name)
    sys.path.append(decompressed_plugin_path)
    if file_name.endswith('.tar'):
        with tarfile.open(path, "r") as tar:
            tar.extractall(decompressed_plugin_path)
    elif file_name.endswith('.zip'):
        with zipfile.ZipFile(path, 'r') as zip_ref:
            zip_ref.extractall(decompressed_plugin_path)
    plugin_level = load_plugin_info(os.path.join(os.getcwd(), decompressed_plugin_path)).get("level", None)
    if plugin_level is None:
        plugin_level = getattr(PluginType.USER.value, 'default')
        sys_plugin_logger.info(f"level of plugin is set to default. [plugin_name={plugin_name}]")
    return plugin_name, plugin_level


def _remove_plugin(path):
    file_name = os.path.basename(path)
    plugin_name = file_name.split('.')[0]
    target_path = os.path.join(_cache_path(), plugin_name)
    plugin = Plugin(name=plugin_name, location=target_path, level=None, type_=PluginType.USER)
    if not os.path.exists(target_path):
        sys_plugin_logger(f"cannot find dynamic plugin to be remove. [plugin_name={plugin_name}]")
    shutil.rmtree(target_path)
    plugin.stop()


def _run_path(path: str):
    file_list = os.listdir(path)
    for file_name in file_list:
        if file_name.endswith(".py"):
            file_path = os.path.join(path, file_name)
            _run_python_command(file_path)


def _run_python_command(file_path):
    if _python_interpreter_alias():
        subprocess.call([_python_interpreter_alias(), file_path])
    elif _check_command_availability("python3"):
        subprocess.run(['python3', file_path])
    elif _check_command_availability("python"):
        subprocess.run(['python', file_path])
    else:
        sys_plugin_logger.error(
            f"failed to install pypi plugins: cannot find any available python version.")


def _load_plugin(plugin_name):
    decompressed_plugin_path = os.path.join(os.getcwd(), _cache_path(), plugin_name)
    custom_resources_path = os.path.join(decompressed_plugin_path, "custom_plugins_resources")
    if os.path.exists(custom_resources_path):
        sys_plugin_logger.info(f"start install pypi plugins...")
        _run_path(os.path.abspath(custom_resources_path))
        sys_plugin_logger.info(f"install pypi plugins successfully.")
        shutil.rmtree(custom_resources_path)
    plugin = Plugin(name=plugin_name, location=decompressed_plugin_path, level=None, type_=PluginType.USER)
    plugin.install()


def _remove_plugins(files: List):
    for file in files:
        file_path = os.path.join(_user_plugins_path(), file)
        try:
            _remove_plugin(file_path)
            sys_plugin_logger.info(
                f"plugin removed. [plugin_path={file_path}], memory_usage={'{:,}'.format(get_memory_usage())}")
        except:
            sys_plugin_logger.warning(f"remove plugin failed. [plugin_path={file_path}]")
            except_type, except_value, except_traceback = sys.exc_info()
            sys_plugin_logger.warning(f"remove plugin error type: {except_type}")
            sys_plugin_logger.warning(f"remove plugin error value: {except_value}")
            sys_plugin_logger.warning(
                f"remove plugin error trace back:\n{''.join(traceback.format_tb(except_traceback))}")


def _load_plugins(file_names: List):
    file_paths = [os.path.join(_user_plugins_path(), file) for file in file_names]
    plugin_levels = {}
    for file_path in file_paths:
        try:
            plugin_name, plugin_level = _decompress_plugin_and_get_level(file_path)
        except:
            except_type, except_value, except_traceback = sys.exc_info()
            sys_plugin_logger.error(
                f"plugin level info load failed. [file_path={file_path}, except_type={except_type}, "
                f"except_value={except_value}, except_traceback={except_traceback}]")
            continue
        plugin_levels[plugin_name] = plugin_level
    plugin_names = list(plugin_levels.keys())
    plugin_names.sort()
    plugin_names.sort(key=lambda name: plugin_levels.get(name))
    for plugin_name in plugin_names:
        try:
            _load_plugin(plugin_name)
            sys_plugin_logger.info(
                f"plugin loaded. [plugin_name={plugin_name}, memory_usage={'{:,}'.format(get_memory_usage())}]")
        except:
            sys_plugin_logger.warning(f"load plugin failed. [plugin_name={plugin_name}]")
            except_type, except_value, except_traceback = sys.exc_info()
            sys_plugin_logger.warning(f"load plugin error type: {except_type}")
            sys_plugin_logger.warning(f"load plugin error value: {except_value}")
            sys_plugin_logger.warning(
                f"load plugin error trace back:\n{''.join(traceback.format_tb(except_traceback))}")


def _scan_plugin_files():
    last_files_modify_time = {}
    if not os.path.exists(_user_plugins_path()):
        sys_plugin_logger.warning(f"path {_user_plugins_path()} not exist.")
    while not os.path.exists(_user_plugins_path()):
        time.sleep(1)
    sys_plugin_logger.info(f"start scan {_user_plugins_path()}.")

    while True:
        current_files_modify_time = {}
        try:
            dynamic_plugins = os.listdir(_user_plugins_path())
        except:
            except_type, except_value, _ = sys.exc_info()
            sys_plugin_logger.warning(
                f"cannot get dynamic plugins. [error_type: {except_type}, error_value: {except_value}]")
            time.sleep(_scan_period())
            continue
        for filename in dynamic_plugins:
            if filename.endswith('.tar') or filename.endswith('.zip'):
                current_files_modify_time[filename] = os.stat(os.path.join(_user_plugins_path(), filename)).st_mtime

        new_files = list(current_files_modify_time.keys() - last_files_modify_time.keys())
        deleted_files = list(last_files_modify_time.keys() - current_files_modify_time.keys())
        intersection_files = set(current_files_modify_time.keys()) & set(last_files_modify_time.keys())
        modified_files = []
        for file in intersection_files:
            if current_files_modify_time.get(file) != last_files_modify_time.get(file):
                modified_files.append(file)

        _remove_plugins(deleted_files)
        _load_plugins(new_files)
        if len(new_files) + len(deleted_files) + len(modified_files) > 0:
            # 如果有插件新增、删除或变更
            register_all_fit_services()

        last_files_modify_time = current_files_modify_time
        time.sleep(_scan_period())


@fitable(const.DYNAMIC_LOADING_START_GEN_ID, const.DYNAMIC_LOADING_START_FIT_ID)
def dynamic_loading_start():
    scan_thread = threading.Thread(target=_scan_plugin_files)
    scan_thread.daemon = True
    scan_thread.start()
