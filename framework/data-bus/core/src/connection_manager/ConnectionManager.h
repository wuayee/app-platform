/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#ifndef DATABUS_CONNECTION_MANAGER_H
#define DATABUS_CONNECTION_MANAGER_H

#include <sys/types.h>
#include <unordered_map>
#include <memory>

#include "Connection.h"
#include "ResourceManager.h"
#include "fbs/message_header_generated.h"
#include "fbs/common_generated.h"

namespace DataBus {
namespace Connection {

class ConnectionManager {
public:
    ConnectionManager() = default;

    ~ConnectionManager() = default;

    void Handle(const char buffer[], ssize_t len, int socketFd,
                const std::unique_ptr<Resource::ResourceManager>& resourceMgrPtr);

    void AddNewConnection(int socketFd);

private:
    void HandleMessageApplyPermission(const Common::MessageHeader* header, const char* buffer, int socketFd,
                                      const std::unique_ptr<Resource::ResourceManager>& resourceMgrPtr);
    void HandleMessageApplyMemory(const Common::MessageHeader* header, const char* buffer, int socketFd,
                                  const std::unique_ptr<Resource::ResourceManager>& resourceMgrPtr);
    void SendApplyPermissionResponse(int32_t socketFd, bool granted, uint64_t memorySize, Common::ErrorType errorType);
    void SendApplyMemoryResponse(int32_t socketFd, int32_t memoryId, uint64_t memorySize, Common::ErrorType errorType);
    std::function<void(const uint8_t*, size_t)> GetSender(int32_t socketFd);
    std::unordered_map<int, std::unique_ptr<Connection>> connections_;
};
}
}

#endif // DATABUS_CONNECTION_MANAGER_H
