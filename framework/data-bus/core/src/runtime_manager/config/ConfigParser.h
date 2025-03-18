/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
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
