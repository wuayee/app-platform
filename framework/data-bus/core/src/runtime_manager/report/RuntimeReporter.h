/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: runtime report provider for databus core
 */
#ifndef DATABUS_RUNTIME_REPORTER_H
#define DATABUS_RUNTIME_REPORTER_H

#include <string>
#include <unordered_map>
#include "report/AbstractReportCollector.h"
#include "log/Logger.h"

namespace DataBus {
namespace Runtime {
class RuntimeReporter final {
public:
    RuntimeReporter(RuntimeReporter&) = delete;
    RuntimeReporter(RuntimeReporter&&) = delete;
    RuntimeReporter& operator=(RuntimeReporter&) = delete;
    RuntimeReporter& operator=(RuntimeReporter&&) = delete;

    static RuntimeReporter& Instance()
    {
        static RuntimeReporter instance;
        return instance;
    }

    void RegisterCollector(const std::string& collectorName, const AbstractReportCollector* collector)
    {
        collectors_.emplace(collectorName, collector);
    }

    void UnregisterCollector(const std::string& collectorName)
    {
        collectors_.erase(collectorName);
    }

    void Report() const
    {
        std::stringstream ss;
        ss << "{";
        for (auto iter = collectors_.cbegin(); iter != collectors_.cend(); ++iter) {
            if (iter != collectors_.begin()) {
                ss << ",";
            }
            ss << iter->second->GetReport();
        }
        ss << "}";
        logger.Info("[Runtime Report]\n================\n{}\n================", ss.str());
    }

private:
    RuntimeReporter() = default;
    ~RuntimeReporter() = default;

    // 模块注册的report collector, key:模块名称, value:对应的ReportCollector,指针生命周期对方管理
    std::unordered_map<std::string, const AbstractReportCollector*> collectors_;
};
}  // namespace Runtime
}  // namespace DataBus

#endif  // DATABUS_RUNTIME_REPORTER_H
