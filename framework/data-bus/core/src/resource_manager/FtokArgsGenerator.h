/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

#ifndef DATABUS_FTOK_ARGS_GENERATOR_H
#define DATABUS_FTOK_ARGS_GENERATOR_H

#include <iostream>

namespace DataBus {
namespace Resource {

const std::string FILE_PATH_PREFIX = "./tmp/";
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
