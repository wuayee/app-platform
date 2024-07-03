/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description : Provides definitions for databus task process center
*/

#ifndef DATABUS_TASKHANDLER_H
#define DATABUS_TASKHANDLER_H

#include <thread>
#include <utility>

#include "TaskLoop.h"
#include "ApplyPermissionResponse.h"
#include "config/DataBusConfig.h"
#include "ConnectionManager.h"
#include "ResourceManager.h"
#include "fbs/apply_permission_message_generated.h"
#include "fbs/common_generated.h"
#include "fbs/message_header_generated.h"

namespace DataBus {
namespace Task {

class TaskHandler {
public:
    explicit TaskHandler(std::shared_ptr<TaskLoop>& taskLoopPtr, const Runtime::Config& config)
        : taskLoopPtr_(taskLoopPtr)
    {
        connectionMgrPtr_ = std::make_unique<DataBus::Connection::ConnectionManager>(config);
        resourceMgrPtr_ = std::make_unique<DataBus::Resource::ResourceManager>(config, taskLoopPtr);
    }

    void Init();
private:
    [[noreturn]] void Loop();
    void HandleRead(const Task&);
    void HandleMessage(const Common::MessageHeader* header, const char* buffer, int socketFd);
    void HandleWrite(const Task&);
    void HandleClose(int socketFd);
    std::function<void(const uint8_t*, size_t)> GetSender(int32_t socketFd);
    void SendApplyPermissionResponse(const Resource::ApplyPermissionResponse& applyPermissionResponse);
    void SendApplyMemoryResponse(int32_t socketFd, uint32_t seq, int32_t memoryId, uint64_t memorySize,
                                 Common::ErrorType errorType);
    void SendGetMetaDataResponse(int32_t socketFd, uint32_t seq, Common::ErrorType errorType,
                                 const Resource::MemoryMetadata& metadata);
    void SendHelloResponse(int32_t socketFd, uint32_t seq);
    void HandleMessageApplyPermission(const Common::MessageHeader* header, const char* buffer, int socketFd);
    void HandleMessageReleasePermission(const Common::MessageHeader* header, const char* buffer, int socketFd);
    void ReleasePermission(int32_t socketFd, int32_t sharedMemoryId, Common::PermissionType permissionType);
    void HandleMessageApplyMemory(const Common::MessageHeader* header, const char* buffer, int socketFd);
    void HandleMessageReleaseMemory(const Common::MessageHeader* header, const char* buffer, int socketFd);
    void HandleMessageGetMeta(const Common::MessageHeader* header, const char* buffer, int socketFd);
    void HandleMessageCleanupExpiredMemory();
    void HandleApplyZeroMemory(int32_t socketFd, uint32_t seq, const std::string& objectKey);
    void HandleApplyZeroMemoryPermission(int32_t socketFd, uint32_t seq,
                                         Common::ApplyPermissionMessage* applyPermissionMessage);

    std::shared_ptr<TaskLoop> taskLoopPtr_;
    std::unique_ptr<DataBus::Connection::ConnectionManager> connectionMgrPtr_;
    std::unique_ptr<DataBus::Resource::ResourceManager> resourceMgrPtr_;
    std::thread thread_;
};
}  // Task
}  // DataBus

#endif // DATABUS_TASKHANDLER_H
