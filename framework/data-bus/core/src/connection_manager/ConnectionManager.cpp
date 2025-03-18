/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

#include <netinet/in.h>
#include <netinet/tcp.h>

#include "log/Logger.h"
#include "ConnectionManager.h"

using namespace std;

namespace DataBus {
namespace Connection {

void ConnectionManager::AddNewConnection(int socketFd)
{
    // 设置TCP_NODELAY，禁用Nagle算法，以防止粘包问题
    int flag = 1;
    if (setsockopt(socketFd, IPPROTO_TCP, TCP_NODELAY, &flag, sizeof(int)) == -1) {
        DataBus::logger.Error("Failed to disable TCP Nagle Algorithm, reason: {}", strerror(errno));
    }
    std::unique_ptr<Connection> connection = std::make_unique<Connection>(socketFd);
    connections_[socketFd] = std::move(connection);
    DataBus::logger.Info("Connection {} has been created.", socketFd);
}

void ConnectionManager::CloseConnection(int socketFd)
{
    auto it = connections_.find(socketFd);
    if (it != connections_.end()) {
        it->second->Close();
        connections_.erase(it);
    }
    DataBus::logger.Info("Connection {} has been closed.", socketFd);
}

Common::ErrorType ConnectionManager::Send(int32_t socketFd, const char* buf, size_t s)
{
    auto it = connections_.find(socketFd);
    if (it == connections_.end()) {
        return Common::ErrorType::UnknownError;
    }
    return connections_[socketFd]->Send(buf, s);
}

void ConnectionManager::GenerateReport(std::stringstream& reportStream) const
{
    reportStream << "\"ConnectionCount\":" << connections_.size();
}
}  // namespace Connection
}  // namespace DataBus
