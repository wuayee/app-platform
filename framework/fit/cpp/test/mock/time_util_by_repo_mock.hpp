/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/27
 */
#ifndef TIME_UTIL_BY_REPO_MOCK_HPP
#define TIME_UTIL_BY_REPO_MOCK_HPP
#include <gmock/gmock.h>
#include <fit/internal/registry/repository/util_by_repo.h>
namespace Fit {
class TimeUtilByRepoMock : public UtilByRepo {
public:
    MOCK_METHOD1(GetCurrentTimeMs, FitCode(uint64_t& result));
    MOCK_METHOD1(GetUUid, FitCode(Fit::string& uuid));
};
}
#endif