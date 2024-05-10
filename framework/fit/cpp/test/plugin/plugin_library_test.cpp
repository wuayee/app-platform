/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/11/9
 * Notes:       :
 */


#include <fit/internal/plugin/plugin_library.hpp>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include "library_loader_mock.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::Fit::Plugin;
using namespace ::Fit::Framework;
using namespace ::testing;

bool operator==(const LibraryInfo &a, const LibraryInfo &b)
{
    return a.name == b.name && a.path == b.path && a.handle == b.handle;
}

class PluginLibraryTest : public ::testing::Test {
public:
    void SetUp() override
    {
        expectPluginContext = {(PluginContext *)0x01, [](PluginContext *) {}};
        pluginContextCreator = [this](const PluginArchive &pluginArchive) -> PluginContextPtr {
            return expectPluginContext;
        };
    }

    void TearDown() override {}

protected:
    LibraryInfo SetExpectCallWithLibraryLoad(const PluginArchive &pluginArchive, FitCode loadRet)
    {
        LibraryInfo libraryInfo(pluginArchive.name, pluginArchive.location, (void *)1);

        EXPECT_CALL(libraryLoaderMock_, Load(pluginArchive.location, _)).WillOnce(
            DoAll(SetArgReferee<1>(libraryInfo), Return(loadRet)));

        return libraryInfo;
    }

    void SetExpectCallWithLibraryUnload(const LibraryInfo &libraryInfo, FitCode unloadRet)
    {
        EXPECT_CALL(libraryLoaderMock_, Unload(A<const LibraryInfo &>())).WillOnce(
            Invoke([unloadRet, libraryInfo](const LibraryInfo &info) {
                EXPECT_THAT(info.name, Eq(libraryInfo.name));
                EXPECT_THAT(info.path, Eq(libraryInfo.path));
                EXPECT_THAT(info.handle, Eq(libraryInfo.handle));
                return unloadRet;
            }));
    }

    LibraryLoaderMock libraryLoaderMock_;
    PluginContextPtr expectPluginContext {};
    PluginLibrary::PluginContextCreateFunc pluginContextCreator;
};

TEST_F(PluginLibraryTest, should_return_success_when_install_given_library_load_success)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    SetExpectCallWithLibraryLoad(pluginArchive, FIT_OK);
    FitCode expectInstallRet = FIT_OK;

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    auto installRet = pluginLibrary.Install();

    EXPECT_THAT(installRet, Eq(expectInstallRet));
}

TEST_F(PluginLibraryTest, should_return_success_when_install_given_library_load_fail)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    SetExpectCallWithLibraryLoad(pluginArchive, FIT_ERR_PARAM);
    FitCode expectInstallRet = FIT_ERR_PARAM;

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    auto installRet = pluginLibrary.Install();

    EXPECT_THAT(installRet, Eq(expectInstallRet));
}

TEST_F(PluginLibraryTest, should_return_fail_when_install_given_null_library_load)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    FitCode expectInstallRet = FIT_ERR_PARAM;

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, nullptr);
    auto installRet = pluginLibrary.Install();

    EXPECT_THAT(installRet, Eq(expectInstallRet));
}

TEST_F(PluginLibraryTest, should_get_plugin_fitables_when_install_given_library_load_success_and_register_fitable)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    LibraryInfo libraryInfo(pluginArchive.name, pluginArchive.location, (void *)1);
    FitCode expectInstallRet = FIT_OK;
    FitCode expectFitableSize = 1;

    EXPECT_CALL(libraryLoaderMock_, Load(pluginArchive.location, _)).WillOnce(
        Invoke([](const Fit::string &path, LibraryInfo &retLibraryInfo) -> FitCode {
            std::function<FitCode()> testFunc = []() { return FIT_OK; };
            Fit::Framework::Annotation::Fitable(testFunc)
                .SetGenericId("g")
                .SetFitableId("f");
            return FIT_OK;
        }));

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    auto installRet = pluginLibrary.Install();

    ASSERT_THAT(installRet, Eq(expectInstallRet));
    ASSERT_THAT(pluginLibrary.GetFitables().size(), Eq(expectFitableSize));
    EXPECT_THAT(pluginLibrary.GetFitables()[0]->GetGenericId(), Eq("g"));
    EXPECT_THAT(pluginLibrary.GetFitables()[0]->GetFitableId(), Eq("f"));
}

TEST_F(PluginLibraryTest, should_return_success_when_resolve_given_after_install_success)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    SetExpectCallWithLibraryLoad(pluginArchive, FIT_OK);
    FitCode expectResolveRet = FIT_OK;

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    pluginLibrary.Install();
    auto resolveRet = pluginLibrary.Resolve();

    EXPECT_THAT(resolveRet, Eq(expectResolveRet));
}

TEST_F(PluginLibraryTest, should_return_success_when_start_given_plugin_with_activator_and_return_ok)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    LibraryInfo libraryInfo(pluginArchive.name, pluginArchive.location, (void *)1);
    FitCode expectStartRet = FIT_OK;

    EXPECT_CALL(libraryLoaderMock_, Load(pluginArchive.location, _)).WillOnce(
        Invoke([](const Fit::string &path, LibraryInfo &retLibraryInfo) -> FitCode {
            Fit::Framework::PluginActivatorRegistrar().SetStart([](PluginContext *context) {
                return FIT_OK;
            });
            return FIT_OK;
        }));

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    pluginLibrary.Install();
    pluginLibrary.Resolve();
    auto startRet = pluginLibrary.Start();

    ASSERT_THAT(startRet, Eq(expectStartRet));
}

TEST_F(PluginLibraryTest, should_return_fail_when_start_given_plugin_with_activator_and_return_fail)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    LibraryInfo libraryInfo(pluginArchive.name, pluginArchive.location, (void *)1);
    FitCode expectStartRet = FIT_ERR_PARAM;

    EXPECT_CALL(libraryLoaderMock_, Load(pluginArchive.location, _)).WillOnce(
        Invoke([](const Fit::string &path, LibraryInfo &retLibraryInfo) -> FitCode {
            Fit::Framework::PluginActivatorRegistrar().SetStart([](PluginContext *context) {
                return FIT_ERR_PARAM;
            });
            return FIT_OK;
        }));

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    pluginLibrary.Install();
    pluginLibrary.Resolve();
    auto startRet = pluginLibrary.Start();

    ASSERT_THAT(startRet, Eq(expectStartRet));
}

TEST_F(PluginLibraryTest, should_return_fail_when_start_given_plugin_context_creator_is_null)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    LibraryInfo libraryInfo(pluginArchive.name, pluginArchive.location, (void *)1);
    FitCode expectStartRet = FIT_ERR_PARAM;

    PluginLibrary pluginLibrary(pluginArchive, nullptr, &libraryLoaderMock_);
    pluginLibrary.Install();
    pluginLibrary.Resolve();
    auto startRet = pluginLibrary.Start();

    ASSERT_THAT(startRet, Eq(expectStartRet));
}

TEST_F(PluginLibraryTest, should_return_fail_when_start_given_plugin_context_creator_return_null)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    LibraryInfo libraryInfo(pluginArchive.name, pluginArchive.location, (void *)1);
    FitCode expectStartRet = FIT_ERR_PARAM;

    PluginLibrary pluginLibrary(pluginArchive, [](const PluginArchive &pluginArchive) -> PluginContextPtr {
        return nullptr;
    }, &libraryLoaderMock_);
    pluginLibrary.Install();
    pluginLibrary.Resolve();
    auto startRet = pluginLibrary.Start();

    ASSERT_THAT(startRet, Eq(expectStartRet));
}

TEST_F(PluginLibraryTest, should_return_success_when_stop_given_plugin_with_activator_and_return_ok)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    LibraryInfo libraryInfo(pluginArchive.name, pluginArchive.location, (void *)1);
    FitCode expectStopRet = FIT_OK;

    EXPECT_CALL(libraryLoaderMock_, Load(pluginArchive.location, _)).WillOnce(
        Invoke([](const Fit::string &path, LibraryInfo &retLibraryInfo) -> FitCode {
            Fit::Framework::PluginActivatorRegistrar().SetStart({}).SetStop([]() {
                return FIT_OK;
            });
            return FIT_OK;
        }));

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    pluginLibrary.Install();
    pluginLibrary.Resolve();
    pluginLibrary.Start();
    auto stopRet = pluginLibrary.Stop();

    ASSERT_THAT(stopRet, Eq(expectStopRet));
}

TEST_F(PluginLibraryTest, should_return_fail_when_stop_given_plugin_with_activator_and_return_fail)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    LibraryInfo libraryInfo(pluginArchive.name, pluginArchive.location, (void *)1);
    FitCode expectStopRet = FIT_ERR_PARAM;

    EXPECT_CALL(libraryLoaderMock_, Load(pluginArchive.location, _)).WillOnce(
        Invoke([](const Fit::string &path, LibraryInfo &retLibraryInfo) -> FitCode {
            Fit::Framework::PluginActivatorRegistrar().SetStart({}).SetStop([]() {
                return FIT_ERR_PARAM;
            });
            return FIT_OK;
        }));

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    pluginLibrary.Install();
    pluginLibrary.Resolve();
    pluginLibrary.Start();
    auto stopRet = pluginLibrary.Stop();

    ASSERT_THAT(stopRet, Eq(expectStopRet));
}

TEST_F(PluginLibraryTest, should_return_success_when_uninstall_given_library_unload_success)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    SetExpectCallWithLibraryUnload(SetExpectCallWithLibraryLoad(pluginArchive, FIT_OK), FIT_OK);
    FitCode expectUninstallRet = FIT_OK;

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    pluginLibrary.Install();
    auto uninstallRet = pluginLibrary.Uninstall();

    EXPECT_THAT(uninstallRet, Eq(expectUninstallRet));
}

TEST_F(PluginLibraryTest, should_return_fail_when_uninstall_given_library_unload_fail)
{
    PluginArchive pluginArchive {"filename", "lib/filename.so"};
    SetExpectCallWithLibraryUnload(SetExpectCallWithLibraryLoad(pluginArchive, FIT_OK), FIT_ERR_FAIL);
    FitCode expectUninstallRet = FIT_ERR_FAIL;

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, &libraryLoaderMock_);
    pluginLibrary.Install();
    auto uninstallRet = pluginLibrary.Uninstall();

    EXPECT_THAT(uninstallRet, Eq(expectUninstallRet));
}

TEST_F(PluginLibraryTest, should_return_fail_when_uninstall_given_null_library_load)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    FitCode expectUninstallRet = FIT_ERR_PARAM;

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, nullptr);
    pluginLibrary.Install();
    auto uninstallRet = pluginLibrary.Uninstall();

    EXPECT_THAT(uninstallRet, Eq(expectUninstallRet));
}

TEST_F(PluginLibraryTest, should_return_setted_level_and_location_when_get_level_given_plugin_archive)
{
    PluginArchive pluginArchive {"filename", "filename.so", 2};

    PluginLibrary pluginLibrary(pluginArchive, pluginContextCreator, nullptr);

    EXPECT_THAT(pluginLibrary.GetStartLevel(), Eq(pluginArchive.startLevel));
    EXPECT_THAT(pluginLibrary.GetLocation(), StrEq(pluginArchive.location.c_str()));
}

TEST_F(PluginLibraryTest,
    should_input_setted_plugin_context_when_start_given_plugin_with_activator_and_plugin_context)
{
    PluginArchive pluginArchive {"filename", "filename.so"};
    LibraryInfo libraryInfo(pluginArchive.name, pluginArchive.location, (void *)1);
    FitCode expectStartRet = FIT_OK;

    EXPECT_CALL(libraryLoaderMock_, Load(pluginArchive.location, _)).WillOnce(
        Invoke([this](const Fit::string &path, LibraryInfo &retLibraryInfo) -> FitCode {
            Fit::Framework::PluginActivatorRegistrar().SetStart([this](PluginContext *context) {
                return expectPluginContext.get() == context ? FIT_OK : FIT_ERR_FAIL;
            });
            return FIT_OK;
        }));

    PluginLibrary pluginLibrary(pluginArchive,
        pluginContextCreator, &libraryLoaderMock_);
    pluginLibrary.Install();
    pluginLibrary.Resolve();
    auto startRet = pluginLibrary.Start();

    ASSERT_THAT(startRet, Eq(expectStartRet));
}