/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
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
