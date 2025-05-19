/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

#include <unistd.h>
#include <cstring>
#include <cerrno>
#include <sys/socket.h>

#include "log/Logger.h"
#include "Connection.h"

using namespace std;
using namespace DataBus::Common;

namespace DataBus {
namespace Connection {

void Connection::Close()
{
    if (socketFd_ >= 0) {
        if (close(socketFd_) == -1) {
            DataBus::logger.Error("Failed to close socket {}, reason: {}", socketFd_, strerror(errno));
        }
        socketFd_ = -1;
    }
}

ErrorType Connection::Send(const char* buf, size_t size)
{
    if (socketFd_ < 0) {
        return ErrorType::DataBusDisconnected;
    }

    size_t totalSent = 0;
    size_t bytesLeft = size;
    ssize_t bytesSent = 0;

    while (totalSent < size) {
        bytesSent = send(socketFd_, buf + totalSent, bytesLeft, 0);
        if (bytesSent < 0) {
            DataBus::logger.Error("Failed to send messages to {}, reason: {}", socketFd_, strerror(errno));
            // 应该使用更明确的错误码
            return ErrorType::UnknownError;
        }

        totalSent += bytesSent;
        bytesLeft -= bytesSent;
    }

    DataBus::logger.Info("Sent {} bytes to server.", size);
    return ErrorType::None;
}

}  // namespace Connection
}  // namespace DataBus