/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
*/

#include "ConnectionManager.h"
#include "log/Logger.h"

using namespace std;

namespace DataBus {
namespace Connection {

void ConnectionManager::AddNewConnection(int socketFd)
{
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
}  // namespace Connection
}  // namespace DataBus
