/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : Test
 * Author       : wangpanbo
 * Create       : 2021/4/14
 */

#include <memory>
#include <fit/fit_code.h>
#include "gtest/gtest.h"
#include "fit_filesystem_util.hpp"
#include "mock/system_config_mock.hpp"
#include "mock/config_value_mock.hpp"
#define private public
#include <fit/internal/runtime/base_plugins_load.h>

using namespace std;
using namespace ::testing;

class BasePluginsLoadTest : public testing::Test {
public:
    void SetUp() override
    {
        systemConfigMock_ = std::make_shared<SystemConfigMock>();
        configValueMock_ = std::make_shared<ConfigValueMock>();
        conmponentValueMock_ = std::make_shared<ConfigValueMock>();
        stringValueMock_ = std::make_shared<ConfigValueMock>();
    }

    void TearDown() override {}

    void SetExpect(const vector<string>& libraryPath, uint32_t& currentIndex)
    {
        EXPECT_CALL(*configValueMock_, IsArray()).WillRepeatedly(Return(true));
        EXPECT_CALL(*configValueMock_, Size()).WillRepeatedly(Return(libraryPath.size()));
        EXPECT_CALL(*configValueMock_, BracketOp(An<int32_t>()))
            .WillRepeatedly(Invoke([&currentIndex, this](int32_t index) -> Fit::Config::Value& {
                currentIndex = index;
                return *conmponentValueMock_;
            }));
        EXPECT_CALL(*conmponentValueMock_, IsObject()).WillRepeatedly(Return(true));
        EXPECT_CALL(*conmponentValueMock_, BracketOp(An<const char*>()))
            .WillRepeatedly(Invoke([this](const char* name) -> Fit::Config::Value& { return *stringValueMock_; }));

        EXPECT_CALL(*stringValueMock_, IsString()).WillRepeatedly(Return(true));
        EXPECT_CALL(*stringValueMock_, AsString(A<const char*>()))
            .WillRepeatedly(
            Invoke([&currentIndex, &libraryPath](const char*) -> string { return libraryPath[currentIndex]; }));

        EXPECT_CALL(*systemConfigMock_, GetValue(_)).WillRepeatedly(ReturnRef(*configValueMock_));
    }

    std::shared_ptr<SystemConfigMock> systemConfigMock_;
    std::shared_ptr<ConfigValueMock> configValueMock_;
    std::shared_ptr<ConfigValueMock> conmponentValueMock_;
    std::shared_ptr<ConfigValueMock> stringValueMock_;
};

TEST_F(BasePluginsLoadTest, should_return_ok_when_load_so_given_valid_so_path)
{
    // given
    BasePluginsLoad basePluginsLoad;
    Fit::string soPath = Fit::Util::Filesystem::GetCurrentExeDir() + "/../lib/libvalid_library.so";
    int32_t expectedResult = FIT_ERR_SUCCESS;
    int mode = RTLD_NOW | RTLD_GLOBAL;
    // when
    auto ret = basePluginsLoad.LoadPlugin(soPath, mode);

    // then
    EXPECT_EQ(ret, expectedResult);
}

TEST_F(BasePluginsLoadTest, should_return_ok_when_load_plugins_by_system_config_given_valid_library_path)
{
    // given
    Fit::vector<Fit::string> libraryPath = {
        Fit::Util::Filesystem::GetCurrentExeDir() + "/../lib/libvalid_library.so",
        Fit::Util::Filesystem::GetCurrentExeDir() + "/../lib/libvalid_library_1.so"
    };
    uint32_t currentIndex = 0;
    SetExpect(libraryPath, currentIndex);

    BasePluginsLoad basePluginsLoad;
    int32_t expectedRet = FIT_ERR_SUCCESS;
    // when
    int32_t ret = basePluginsLoad.LoadBasePlugins(systemConfigMock_.get());

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(BasePluginsLoadTest, should_return_fail_when_load_plugins_by_system_config_given_invalid_library_path)
{
    // given
    Fit::vector<Fit::string> libraryPath = {
        Fit::Util::Filesystem::GetCurrentExeDir() + "/../lib/libvalid_library.so",
        Fit::Util::Filesystem::GetCurrentExeDir() + "/../lib/libvalid_library_1.so",
        Fit::Util::Filesystem::GetCurrentExeDir() + "/../lib/XXXX.so"
    };
    uint32_t currentIndex = 0;
    SetExpect(libraryPath, currentIndex);

    BasePluginsLoad basePluginsLoad;
    int32_t expectedRet = FIT_ERR_FAIL;
    // when
    int32_t ret = basePluginsLoad.LoadBasePlugins(systemConfigMock_.get());

    // then
    EXPECT_EQ(ret, expectedRet);
}
