/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

#ifndef DATABUS_CONNECTION_MANAGER_H
#define DATABUS_CONNECTION_MANAGER_H

#include <sys/types.h>
#include <memory>
#include <unordered_map>

#include "config/DataBusConfig.h"
#include "Connection.h"
#include "fbs/common_generated.h"
#include "report/ReportCollector.h"

namespace DataBus {
namespace Connection {

class ConnectionManager {
public:
    explicit ConnectionManager(const Runtime::Config& config) : config_(config) {};
    ~ConnectionManager() = default;

    void CloseConnection(int socketFd);
    void AddNewConnection(int socketFd);
    Common::ErrorType Send(int32_t socketFd, const char* buf, size_t s);

    void GenerateReport(std::stringstream& reportStream) const;
private:
    Runtime::ReportCollector<ConnectionManager> reportCollector_{"ConnectionManager", *this};

    const Runtime::Config& config_;
    std::unordered_map<int, std::unique_ptr<Connection>> connections_;
};
}
}

#endif // DATABUS_CONNECTION_MANAGER_H
