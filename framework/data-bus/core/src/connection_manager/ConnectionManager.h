/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#ifndef DATABUS_CONNECTION_MANAGER_H
#define DATABUS_CONNECTION_MANAGER_H

#include <sys/types.h>
#include <memory>
#include <unordered_map>

#include "Connection.h"
#include "fbs/common_generated.h"
#include "report/ReportCollector.h"

namespace DataBus {
namespace Connection {

class ConnectionManager {
public:
    ConnectionManager() = default;
    ~ConnectionManager() = default;

    void CloseConnection(int socketFd);
    void AddNewConnection(int socketFd);
    Common::ErrorType Send(int32_t socketFd, const char* buf, size_t s);

    void GenerateReport(std::stringstream& reportStream) const;
private:
    Runtime::ReportCollector<ConnectionManager> reportCollector_{"ConnectionManager", *this};

    std::unordered_map<int, std::unique_ptr<Connection>> connections_;
};
}
}

#endif // DATABUS_CONNECTION_MANAGER_H
