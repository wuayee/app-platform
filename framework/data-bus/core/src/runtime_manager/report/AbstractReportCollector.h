/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: runtime report collector interface to collect report for RuntimeReporter
 */
#ifndef DATABUS_ABSTRACT_REPORT_COLLECTOR_H
#define DATABUS_ABSTRACT_REPORT_COLLECTOR_H

#include <string>

namespace DataBus {
namespace Runtime {
class AbstractReportCollector {
public:
    AbstractReportCollector() = default;
    virtual ~AbstractReportCollector() = default;

    virtual std::string GetReport() const = 0;
};
}  // namespace Runtime
}  // namespace DataBus

#endif  // DATABUS_ABSTRACT_REPORT_COLLECTOR_H
