/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/02/23
 * Notes:       :
 */

#include <src/util/log/fit_log_inner.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::std;
using namespace ::testing;

class FitLogInnerTest : public testing::Test {
public:
    void SetUp() override
    {
        FitLogSetEnableLogLevel(FIT_LOG_LEVEL_ERROR);
    }

    void TearDown() override {}
};

TEST_F(FitLogInnerTest, should_return_error_log_level_when_show_enable_log_level_given_default_log_level)
{
    // given
    auto expectedLogLevel = FIT_LOG_LEVEL_ERROR;
    int32_t configLogLevel;

    // when
    configLogLevel = FitLogGetEnableLogLevel();

    // then
    EXPECT_THAT(configLogLevel, Eq(expectedLogLevel));
}

TEST_F(FitLogInnerTest, should_return_success_when_log_error_given_enable_error_log_level)
{
    // given
    int32_t expectedLogResult = FIT_ERR_SUCCESS;

    // when
    FitLogSetEnableLogLevel(FIT_LOG_LEVEL_ERROR);
    auto logResult = FIT_LOG_ERROR("%s", "message");

    // then
    EXPECT_THAT(logResult, Eq(expectedLogResult));
}

TEST_F(FitLogInnerTest, should_return_skip_when_log_lower_than_error_given_enable_error_log_level)
{
    // given
    int32_t expectedLogResult = FIT_ERR_SKIP;

    // when
    FitLogSetEnableLogLevel(FIT_LOG_LEVEL_ERROR);

    auto logWarningResult = FIT_LOG_DEBUG("%s", "message");
    auto logInfoResult = FIT_LOG_DEBUG("%s", "message");
    auto logDebugResult = FIT_LOG_DEBUG("%s", "message");

    // then
    EXPECT_THAT(logWarningResult, Eq(expectedLogResult));
    EXPECT_THAT(logInfoResult, Eq(expectedLogResult));
    EXPECT_THAT(logDebugResult, Eq(expectedLogResult));
}

TEST_F(FitLogInnerTest, should_return_success_when_log_higner_than_error_given_enable_error_log_level)
{
    // given
    int32_t expectedResult = FIT_ERR_SUCCESS;

    // when
    FitLogSetEnableLogLevel(FIT_LOG_LEVEL_ERROR);
    auto logFatalResult = FIT_LOG_FATAL("%s", "message");

    // then
    EXPECT_THAT(logFatalResult, Eq(expectedResult));
}

TEST_F(FitLogInnerTest, should_return_success_when_log_higner_than_debug_given_enable_debug_log_level)
{
    // given
    int32_t expectedResult = FIT_ERR_SUCCESS;

    // when
    FitLogSetEnableLogLevel(FIT_LOG_LEVEL_DEBUG);

    auto logFatalResult = FIT_LOG_DEBUG("%s", "message");
    auto logErrorResult = FIT_LOG_DEBUG("%s", "message");
    auto logWarningResult = FIT_LOG_DEBUG("%s", "message");
    auto logInfoResult = FIT_LOG_DEBUG("%s", "message");
    auto logDebugResult = FIT_LOG_DEBUG("%s", "message");

    // then
    EXPECT_THAT(logFatalResult, Eq(expectedResult));
    EXPECT_THAT(logErrorResult, Eq(expectedResult));
    EXPECT_THAT(logWarningResult, Eq(expectedResult));
    EXPECT_THAT(logInfoResult, Eq(expectedResult));
    EXPECT_THAT(logDebugResult, Eq(expectedResult));
}

TEST_F(FitLogInnerTest, should_return_skip_when_log_lower_than_info_given_enable_info_log_level)
{
    // given
    int32_t expectedLogResult = FIT_ERR_SKIP;

    // when
    FitLogSetEnableLogLevel(FIT_LOG_LEVEL_INFO);

    auto logDebugResult = FIT_LOG_DEBUG("%s", "message");

    // then
    EXPECT_THAT(logDebugResult, Eq(expectedLogResult));
}

TEST_F(FitLogInnerTest, should_return_success_when_log_higher_than_info_given_enable_info_log_level)
{
    // given
    int32_t expectedLogResult = FIT_ERR_SKIP;

    // when
    FitLogSetEnableLogLevel(FIT_LOG_LEVEL_INFO);

    auto logFatalResult = FIT_LOG_DEBUG("%s", "message");
    auto logErrorResult = FIT_LOG_DEBUG("%s", "message");
    auto logWarningResult = FIT_LOG_DEBUG("%s", "message");
    auto logInfoResult = FIT_LOG_DEBUG("%s", "message");

    // then
    EXPECT_THAT(logFatalResult, Eq(expectedLogResult));
    EXPECT_THAT(logErrorResult, Eq(expectedLogResult));
    EXPECT_THAT(logWarningResult, Eq(expectedLogResult));
    EXPECT_THAT(logInfoResult, Eq(expectedLogResult));
}

TEST_F(FitLogInnerTest, should_return_oldLogLevel_when_set_log_level_given_different_log_level)
{
    // given
    int32_t oldLogLevel = FIT_LOG_LEVEL_INFO;
    int32_t newLogLevel = FIT_LOG_LEVEL_DEBUG;
    int32_t expectedLogLevel = oldLogLevel;

    // when
    FitLogSetEnableLogLevel(oldLogLevel);
    auto setResult = FitLogSetEnableLogLevel(newLogLevel);

    // then
    EXPECT_THAT(setResult, Eq(expectedLogLevel));
}

TEST_F(FitLogInnerTest, should_return_newLogLevel_when_get_log_level_given_set_a_different_log_level)
{
    // given
    int32_t oldLogLevel = FIT_LOG_LEVEL_INFO;
    int32_t newLogLevel = FIT_LOG_LEVEL_DEBUG;
    int32_t expectedLogLevel = newLogLevel;

    // when
    FitLogSetEnableLogLevel(oldLogLevel);
    FitLogSetEnableLogLevel(newLogLevel);
    auto result = FitLogGetEnableLogLevel();

    // then
    EXPECT_THAT(result, Eq(expectedLogLevel));
}

bool logged = false;
bool logEnableCalled = false;
int32_t LogPrintTest(const FitLogInfo* logInfo)
{
    logged = true;
    return FIT_OK;
}
bool LogEnableTest(const FitLogInfo* logInfo)
{
    logEnableCalled = true;
    return true;
}
TEST_F(FitLogInnerTest, should_log_with_setted_cb_when_log)
{
    // given
    FitLogSetCallback(FitLogCallbackInfo {LogPrintTest, LogEnableTest});

    // when
    logged = false;
    FIT_LOG_ERROR("%s", "message");

    // then
    EXPECT_THAT(logged, Eq(true));
    EXPECT_THAT(logEnableCalled, Eq(true));
}
