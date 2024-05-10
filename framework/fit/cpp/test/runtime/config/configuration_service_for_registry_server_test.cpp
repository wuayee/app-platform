/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide configuration service from registry server test
 * Author       : w00561424
 * Date         : 2023/09/01
 * Notes:       :
 */
#include <mock/configuration_service_spi_mock.hpp>
#include <mock/configuration_repo_mock.hpp>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
#include <fit/stl/memory.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/fit_code.h>
#define private public
#include <runtime/config/configuration_service_for_registry_server.h>
class ConfigurationServiceForRegistryServerTest : public ::testing::Test {
public:
    void SetUp() override
    {
        environment_ = {"debug"};
        repo_ = Fit::make_shared<ConfigurationRepoMock>();
        spi_ = Fit::make_shared<ConfigurationServiceSpiMock>();
        configurationServiceForRegistryServer_ = Fit::make_shared<ConfigurationServiceForRegistryServer>(
            repo_, environment_, spi_);
        genericableId_ = "test_gid";

        fitable_.genericableId = genericableId_;
        fitable_.genericableVersion = "1.0.0";
        fitable_.fitableId = "test_fid";
        fitable_.fitableVersion = "1.0.0";

        meta_.aliases = {"test_alias1", "test_alias2"};
        meta_.tags = {"test_tag1", "test_tag2"};
        meta_.extensions = {{"key1", "value1"}};
        meta_.fitable = &fitable_;

        genericableConf_.SetGenericId(fitable_.genericableId);
        genericableConf_.SetTags(meta_.tags);

        fitableConfig_.extensions = meta_.extensions;
        fitableConfig_.fitableId = fitable_.fitableId;
        fitableConfig_.aliases = meta_.aliases;
        ::fit::hakuna::kernel::registry::shared::Application application;
        application.name = "test_app_name";
        application.nameVersion = "test_app_name_version";
        fitableConfig_.applications.emplace_back(application);
        Fit::vector<int32_t> applicationsFormats {0, 1};
        fitableConfig_.applicationsFormats.emplace_back(applicationsFormats);
        genericableConf_.SetFitable(fitableConfig_);
    }
    void TearDown() override
    {
    }

    void CheckGetter(GenericConfigPtr actualGetter)
    {
        CheckGenericableConfig(*actualGetter);
    }
    void CheckGenericableConfig(const GenericableConfiguration& genericableConf)
    {
        EXPECT_EQ(genericableConf.GetTags()[0], meta_.tags[0]);
        EXPECT_EQ(genericableConf.GetTags()[1], meta_.tags[1]);
        auto tempExtensions = genericableConf.GetFitables()[0].extensions;
        EXPECT_EQ(tempExtensions["key1"], meta_.extensions["key1"]);
        EXPECT_EQ(genericableConf.GetFitables()[0].fitableId, fitable_.fitableId);
        EXPECT_EQ(genericableConf.GetFitables()[0].aliases[0], meta_.aliases[0]);
        EXPECT_EQ(genericableConf.GetFitables()[0].aliases[1], meta_.aliases[1]);
        EXPECT_EQ(genericableConf.GetFitables()[0].applications[0].name, fitableConfig_.applications[0].name);
        EXPECT_EQ(genericableConf.GetFitables()[0].applications[0].nameVersion,
            fitableConfig_.applications[0].nameVersion);
        EXPECT_EQ(genericableConf.GetFitables()[0].applicationsFormats[0][0], fitableConfig_.applicationsFormats[0][0]);
        EXPECT_EQ(genericableConf.GetFitables()[0].applicationsFormats[0][1], fitableConfig_.applicationsFormats[0][1]);
    }
public:
    Fit::string genericableId_ {};
    Fit::string environment_ {};
    std::shared_ptr<ConfigurationRepoMock> repo_;
    Fit::shared_ptr<ConfigurationServiceSpiMock> spi_;
    Fit::shared_ptr<ConfigurationServiceForRegistryServer> configurationServiceForRegistryServer_ {};
    ::fit::hakuna::kernel::registry::shared::FitableMeta meta_;
    ::fit::hakuna::kernel::shared::Fitable fitable_;
    GenericableConfiguration genericableConf_;
    FitableConfiguration fitableConfig_;
};

TEST_F(ConfigurationServiceForRegistryServerTest, should_query_from_registry_server_when_get_config_given_gid)
{
    // given
    int32_t expectedGetRet = FIT_ERR_SUCCESS;
    GenericableConfiguration queryGenericableConfig {};
    EXPECT_CALL(*repo_, Get(_, _)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id, GenericableConfiguration &out) -> int32_t {
            if (genericableId_ == queryGenericableConfig.GetGenericId()) {
                out = queryGenericableConfig;
                return FIT_ERR_SUCCESS;
            }
            return FIT_ERR_NOT_FOUND;
        }));
    EXPECT_CALL(*repo_, Set(_)).WillRepeatedly(
        Invoke([&](GenericConfigPtr val) -> int32_t {
            queryGenericableConfig = *val;
            return FIT_ERR_SUCCESS;
        }));
    
    EXPECT_CALL(*spi_, GetRunningFitables(::testing::_, ::testing::_, ::testing::_))
        .Times(::testing::AtLeast(1))
        .WillOnce(Invoke([&](const Fit::vector<Fit::string>& genericIds,
        const Fit::string& environment, Fit::vector<GenericConfigPtr>& genericableConfigs) {
            genericableConfigs.emplace_back(Fit::make_shared<GenericableConfiguration>(genericableConf_));
            return FIT_ERR_SUCCESS;
        }));
    // when
    GenericableConfiguration genericableConf;
    int32_t actualGetRet
        = configurationServiceForRegistryServer_->GetGenericableConfig(genericableId_, genericableConf);
    // then
    EXPECT_EQ(expectedGetRet, actualGetRet);
    CheckGenericableConfig(genericableConf);
}

TEST_F(ConfigurationServiceForRegistryServerTest,
    should_return_not_found_when_get_config_error_and_get_2nd_given_gid)
{
    // given
    int32_t expectedGetRet = FIT_ERR_NOT_FOUND;

    EXPECT_CALL(*repo_, Get(_, _)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id, GenericableConfiguration &out) -> int32_t {
            return FIT_ERR_NOT_FOUND;
        }));
    EXPECT_CALL(*spi_, GetRunningFitables(::testing::_, ::testing::_, ::testing::_))
        .Times(::testing::AtLeast(1))
        .WillOnce(Invoke([&](const Fit::vector<Fit::string>& genericIds,
        const Fit::string& environment, Fit::vector<GenericConfigPtr>& genericableConfigs) {
            return FIT_ERR_FAIL;
        }));

    // when
    GenericableConfiguration genericableConf;
    int32_t actualGetRet
        = configurationServiceForRegistryServer_->GetGenericableConfig(genericableId_, genericableConf);
    // not call get running fitables
    int32_t actualGetRet2
        = configurationServiceForRegistryServer_->GetGenericableConfig(genericableId_, genericableConf);
    // then
    EXPECT_EQ(expectedGetRet, actualGetRet);
    EXPECT_EQ(expectedGetRet, actualGetRet2);
}

TEST_F(ConfigurationServiceForRegistryServerTest, should_return_success_when_get_given_gid)
{
    // given
    int32_t expectedGetRet = FIT_ERR_SUCCESS;

    EXPECT_CALL(*repo_, Get(_, _)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id, GenericableConfiguration &out) -> int32_t {
            out = genericableConf_;
            return FIT_ERR_SUCCESS;
        }));
    // when
    GenericableConfiguration genericableConf;
    int32_t actualGetRet
        = configurationServiceForRegistryServer_->GetGenericableConfig(genericableId_, genericableConf);
    // then
    EXPECT_EQ(expectedGetRet, actualGetRet);
    CheckGenericableConfig(genericableConf);
}

TEST_F(ConfigurationServiceForRegistryServerTest,
    should_query_from_registry_server_when_get_config_getter_given_gid)
{
    // given
    int32_t expectedGetRet = FIT_ERR_SUCCESS;

    GenericConfigPtr getterStore {nullptr};
    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(Invoke([&](const Fit::string &genericId) {
            return getterStore;
        }));
    EXPECT_CALL(*repo_, Set(_)).WillRepeatedly(
        Invoke([&](GenericConfigPtr val) -> int32_t {
            getterStore = std::move(val);
            return FIT_ERR_SUCCESS;
        }));
    EXPECT_CALL(*spi_, GetRunningFitables(::testing::_, ::testing::_, ::testing::_))
        .Times(::testing::AtLeast(1))
        .WillOnce(Invoke([&](const Fit::vector<Fit::string>& genericIds,
        const Fit::string& environment, Fit::vector<GenericConfigPtr>& genericableConfigs) {
            genericableConfigs.emplace_back(Fit::make_shared<GenericableConfiguration>(genericableConf_));
            return FIT_ERR_SUCCESS;
        }));
    // when
    GenericConfigPtr actualGetter
        = configurationServiceForRegistryServer_->GetGenericableConfigPtr(genericableId_);
    // then
    CheckGetter(actualGetter);
}

TEST_F(ConfigurationServiceForRegistryServerTest, should_return_success_when_get_getter_given_gid)
{
    // given
    int32_t expectedGetRet = FIT_ERR_SUCCESS;
    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(Invoke([&](const Fit::string &genericId) {
            return std::make_shared<GenericableConfiguration>(genericableConf_);
        }));
    // when
    GenericConfigPtr actualGetter
        = configurationServiceForRegistryServer_->GetGenericableConfigPtr(genericableId_);
    // then
    CheckGetter(actualGetter);
}

TEST_F(ConfigurationServiceForRegistryServerTest,
    should_return_null_when_get_conf_error_and_get_getter_2nd_given_gid)
{
    // given
    int32_t expectedGetRet = FIT_ERR_NOT_FOUND;

    GenericConfigPtr getterStore {nullptr};
    EXPECT_CALL(*repo_, Getter(_)).WillRepeatedly(
        Invoke([&](const Fit::string &generic_id) {
            return getterStore;
        }));
    EXPECT_CALL(*spi_, GetRunningFitables(::testing::_, ::testing::_, ::testing::_))
        .Times(::testing::AtLeast(1))
        .WillOnce(Invoke([&](const Fit::vector<Fit::string>& genericIds,
        const Fit::string& environment, Fit::vector<GenericConfigPtr>& genericableConfigs) {
            return FIT_ERR_FAIL;
        }));
    // when
    GenericConfigPtr actualGetter
        = configurationServiceForRegistryServer_->GetGenericableConfigPtr(genericableId_);
    // not call get running fitables
    GenericConfigPtr actualGetter2
        = configurationServiceForRegistryServer_->GetGenericableConfigPtr(genericableId_);
    // then
    EXPECT_EQ(actualGetter, nullptr);
    EXPECT_EQ(actualGetter2, nullptr);
}

TEST_F(ConfigurationServiceForRegistryServerTest, should_return_void_when_update_conf_given_mock_get_config_failed)
{
    // given
    EXPECT_CALL(*spi_, GetRunningFitables(::testing::_, ::testing::_, ::testing::_))
        .Times(::testing::AtLeast(1))
        .WillOnce(Invoke([&](const Fit::vector<Fit::string>& genericIds,
        const Fit::string& environment, Fit::vector<GenericConfigPtr>& genericableConfigs) {
            return FIT_ERR_FAIL;
        }));
    // when
    configurationServiceForRegistryServer_->UpdateConfig();
    // then
}