/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
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

void FtokArgsGenerator::Reset()
{
    nextProjId_ = MIN_PROJ_ID;
    cycleCount_ = MIN_PROJ_ID;
}

}  // namespace Resource
}  // namespace DataBus