/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: public exposed log provider for databus core
 */
#ifndef DATABUS_LOGGER_H
#define DATABUS_LOGGER_H

#include <memory>

#include "LoggerWrapper.h"

namespace DataBus {
static Runtime::LoggerWrapper& logger = Runtime::LoggerWrapper::GetInstance();
}

#endif  // DATABUS_LOGGER_H
