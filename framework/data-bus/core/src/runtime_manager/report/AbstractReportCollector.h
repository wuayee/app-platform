/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
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
