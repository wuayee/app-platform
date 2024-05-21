# coding: utf-8
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
"""
本文件内容为转换DataBus内存分配二进制日志到可读文本日志的脚本

创建日期: 2024-05-16
"""
import struct
import argparse
import os


def convert_malloc_log(binary_log_path, readable_log_path):
    # 使用 os.open 打开二进制日志文件
    bin_log_fd = os.open(binary_log_path, os.O_RDONLY)
    bin_log = os.fdopen(bin_log_fd, 'rb')

    # 使用 os.open 打开或创建可读日志文件，并设置权限
    readable_log_fd = os.open(readable_log_path, os.O_WRONLY | os.O_CREAT, 0o644)
    txt_log = os.fdopen(readable_log_fd, 'w')

    with bin_log, txt_log:
        while True:
            shared_memory_id_data = bin_log.read(4)  # 读取4字节的sharedMemoryId
            if not shared_memory_id_data:
                break
            shared_memory_id = struct.unpack('i', shared_memory_id_data)[0]

            released_data = bin_log.read(1)  # 读取1字节的released状态
            if not released_data:
                break
            released = struct.unpack('?', released_data)[0]

            txt_log.write(f"{shared_memory_id} {released}\n")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Convert the given binary log file to human-readable format.")
    parser.add_argument('binary_log_path', type=str, help='Path to the binary log file.')
    parser.add_argument('readable_log_path', type=str, help='Path to the human-readable log file.')

    args = parser.parse_args()

    convert_malloc_log(args.binary_log_path, args.readable_log_path)
