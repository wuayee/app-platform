/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
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
    unique_ptr<Connection> connection(new Connection(socketFd));
    connections_[socketFd] = std::move(connection);
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
