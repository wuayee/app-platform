/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: config parser for databus core
 */
#ifndef DATABUS_CONFIG_PARSER_H
#define DATABUS_CONFIG_PARSER_H

#include <string>

#include "DataBusConfig.h"

namespace DataBus {
namespace Runtime {
namespace ConfigParser {
void Parse();
DataBus::Runtime::Config Parse(const std::string& configFilePath);
}  // namespace ConfigParser
}  // namespace Runtime
}  // namespace DataBus

#endif  // DATABUS_CONFIG_PARSER_H
