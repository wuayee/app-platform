/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: public exposed log provider for databus core
 */

#ifndef DATABUS_LOGGER_H
#define DATABUS_LOGGER_H

#include <memory>

#include "LoggerWrapper.h"

namespace DataBus {
using Logger = Runtime::LoggerWrapper;
static Logger& logger = Runtime::LoggerWrapper::Instance();
}

#endif  // DATABUS_LOGGER_H
