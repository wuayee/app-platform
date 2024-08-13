/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/29
 */
#include <fit/internal/registry/repository/util_by_repo.h>
#include <fit/internal/fit_time_utils.h>
#include <fit/internal/util/fit_uuid.hpp>
#include <fit/stl/string.hpp>
#include <fit/fit_log.h>
namespace Fit {
class UtilByRepoTest : public UtilByRepo {
public:
FitCode GetCurrentTimeMs(uint64_t& result)
{
    result = 666;
    return FIT_OK;
}

FitCode GetUUid(Fit::string& uuid)
{
    uuid = GenerateUuid();
    return FIT_OK;
}
};
UtilByRepo& UtilByRepo::Instance()
{
    static Fit::UtilByRepoTest timeUtil;
    return timeUtil;
}
}