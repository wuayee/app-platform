/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

#ifndef DATABUS_CONNECTION_H
#define DATABUS_CONNECTION_H

#include "fbs/common_generated.h"

namespace DataBus {
namespace Connection {

class Connection {
public:
    // 禁用默认构造器和赋值语义
    Connection() = delete;
    Connection(const Connection&) = delete;
    Connection& operator=(const Connection&) = delete;

    explicit Connection(int fd) : socketFd_(fd) {}

    void Close();
    Common::ErrorType Send(const char* data, size_t size);
private:
    int socketFd_;
};

}  // namespace Connection
}  // namespace DataBus

#endif // DATABUS_CONNECTION_H
