/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-15 15:05:08
 */

#include <fit/internal/plugin/library_loader.hpp>

#include <fit/fit_code.h>
#include <fit/internal/plugin/plugin_struct.hpp>
#include <fit/stl/vector.hpp>
#include "gtest/gtest.h"

#include <dlfcn.h>
#include <memory>

using Fit::string;
using Fit::vector;
using Fit::Plugin::CreateLibraryLoader;
using Fit::Plugin::LibraryInfo;
using Fit::Plugin::LibraryLoader;

static const char* const VALID_LIBRARY = "../lib/libvalid_library.so";
static const char* const VALID_LIBRARY_1 = "../lib/libvalid_library_1.so";
static const char* const INVALID_LIBRARY = "/bin/no-exist";

class LibraryLoaderTest : public ::testing::Test {
public:
    void SetUp() override
    {
        loader = CreateLibraryLoader();
    }
    void TearDown() override {}

    std::unique_ptr<LibraryLoader> loader;
};

TEST_F(LibraryLoaderTest, Should_succeed_When_load_and_unload_library_Given_library_is_valid)
{
    ASSERT_NE(loader, nullptr);

    LibraryInfo info{};
    auto ret = loader->Load(VALID_LIBRARY, info);
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);

    ret = loader->Unload(info);
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);
}

TEST_F(LibraryLoaderTest, Should_success_When_load_library_repeatedly)
{
    ASSERT_NE(loader, nullptr);

    LibraryInfo info{};
    auto ret = loader->Load(VALID_LIBRARY, info);
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);

    ret = loader->Load(VALID_LIBRARY, info);
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);
}

TEST_F(LibraryLoaderTest, Should_fail_When_load_library_Given_library_is_invalid)
{
    ASSERT_NE(loader, nullptr);

    LibraryInfo info{};
    auto ret = loader->Load(INVALID_LIBRARY, info);
    EXPECT_EQ(ret, FIT_ERR_FAIL);
}

TEST_F(LibraryLoaderTest, Should_succeed_When_load_multiple_libraries_Given_all_valid)
{
    ASSERT_NE(loader, nullptr);

    vector<LibraryInfo> infos{2};
    auto ret = loader->Load(vector<string>{VALID_LIBRARY, VALID_LIBRARY_1}, infos);
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);

    ret = loader->Unload(infos);
    EXPECT_EQ(ret, FIT_ERR_SUCCESS);
}

TEST_F(LibraryLoaderTest, Should_fail_When_load_multiple_libraries_Given_the_first_is_invalid)
{
    vector<LibraryInfo> infos{2};
    vector<string> libs{INVALID_LIBRARY, VALID_LIBRARY};

    auto ret = loader->Load(libs, infos);
    EXPECT_EQ(ret, FIT_ERR_FAIL);
}

TEST_F(LibraryLoaderTest, Should_fail_and_rollback_When_load_multi_libs_Given_the_second_is_invalid)
{
    vector<LibraryInfo> infos{2};
    vector<string> libs{VALID_LIBRARY, INVALID_LIBRARY};

    auto ret = loader->Load(libs, infos);
    EXPECT_EQ(ret, FIT_ERR_FAIL);

    // check if the first lib is unloaded
    auto* handle = dlopen(VALID_LIBRARY, RTLD_NOLOAD);
    EXPECT_EQ(handle, nullptr);
}