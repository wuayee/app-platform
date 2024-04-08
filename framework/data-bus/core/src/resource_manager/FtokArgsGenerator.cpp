/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#include "FtokArgsGenerator.h"

namespace DataBus {
namespace Resource {

FtokArgsGenerator& FtokArgsGenerator::Instance()
{
    static FtokArgsGenerator instance;
    return instance;
}

std::string FtokArgsGenerator::GetFilePath() const
{
    return FILE_PATH_PREFIX + std::to_string(cycleCount_);
}

int32_t FtokArgsGenerator::GetProjId()
{
    int projId = nextProjId_;
    if (projId == maxProjId_) {
        nextProjId_ = minProjId_;
        cycleCount_++;
    } else {
        nextProjId_++;
    }
    return projId;
}

}  // namespace Resource
}  // namespace DataBus