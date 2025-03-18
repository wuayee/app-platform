/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

#ifndef DATABUS_FTOK_ARGS_GENERATOR_H
#define DATABUS_FTOK_ARGS_GENERATOR_H

#include <iostream>

#include "utils/FileUtils.h"

namespace DataBus {
namespace Resource {

const std::string FILE_PATH_PREFIX = DataBus::Common::FileUtils::GetDataBusDirectory() + "ftok/";
constexpr int MIN_PROJ_ID = 0;
constexpr int MAX_PROJ_ID = 255;

// ftok函数参数构造器
class FtokArgsGenerator {
public:
    static FtokArgsGenerator& Instance();
    FtokArgsGenerator() = default;
    ~FtokArgsGenerator() = default;

    std::string GetFilePath() const;
    int GetProjId();
    void Reset();

private:
    int32_t minProjId_{MIN_PROJ_ID};
    int32_t maxProjId_{MAX_PROJ_ID};
    int32_t nextProjId_{MIN_PROJ_ID};
    uint32_t cycleCount_{MIN_PROJ_ID};
};

}  // namespace Resource
}  // namespace DataBus

#endif // DATABUS_FTOK_ARGS_GENERATOR_H
