/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: runtime report collector template that can be instantiate to collect report for RuntimeReporter
 */

#ifndef DATABUS_REPORT_COLLECTOR_H
#define DATABUS_REPORT_COLLECTOR_H

#include <iostream>
#include <sstream>

#include "report/AbstractReportCollector.h"
#include "report/RuntimeReporter.h"

namespace DataBus {
namespace Runtime {
template<typename ReportSource>
// 只有在ReportSource有void GenerateReport(std::stringstream&)的成员函数的时候才能有ReportCollector
// 但是用SFINAE限制的话看上去ReportSource会有incomplete type的问题, 只能通过约定了
class ReportCollector : public AbstractReportCollector {
public:
    explicit ReportCollector(std::string collectorName, const ReportSource& source)
        : collectorName_(std::move(collectorName)), source_(source)
    {
        RuntimeReporter::Instance().RegisterCollector(collectorName_, this);
    }

    ~ReportCollector() override
    {
        RuntimeReporter::Instance().UnregisterCollector(collectorName_);
    }

    std::string GetReport() const override
    {
        std::stringstream ss;
        ss << "\"" << collectorName_ << "\":{";
        source_.GenerateReport(ss);
        ss << "}";
        return ss.str();
    }

private:
    std::string collectorName_;
    const ReportSource& source_;
};
}  // namespace Runtime
}  // namespace DataBus

#endif  // DATABUS_REPORT_COLLECTOR_H
