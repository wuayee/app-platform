/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#ifndef DATABUS_CONNECTION_MANAGER_H
#define DATABUS_CONNECTION_MANAGER_H

#include <sys/types.h>
#include <unordered_map>
#include <memory>

#include "Connection.h"
#include "fbs/message_header_generated.h"

namespace DataBus {
namespace Connection {

class ConnectionManager {
public:
    ConnectionManager() = default;

    ~ConnectionManager() = default;

    void Handle(const char buffer[], ssize_t len, int socketFd);

    void AddNewConnection(int socketFd);

private:
    void HandleMessageApplyPermission(const Common::MessageHeader* header, const char* buffer, int socketFd);
    void HandleMessageApplyMemory(const Common::MessageHeader* header, const char* buffer, int socketFd);

    std::unordered_map<int, std::unique_ptr<Connection>> connections_;
};
}
}

#endif // DATABUS_CONNECTION_MANAGER_H
