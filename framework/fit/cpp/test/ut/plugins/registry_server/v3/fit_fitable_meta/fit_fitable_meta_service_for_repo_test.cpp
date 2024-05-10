/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : fitable meta service for repo ut.
 * Author       : w00561424
 * Date         : 2023/09/08
 * Notes:       :
 */
#include <registry_server/v3/fit_fitable_meta/include/fit_fitable_meta_service_for_repo.h>
#include <registry_server/registry_server_memory/fitable/fit_memory_fitable_operation.h>
#include <fit/stl/memory.hpp>
#include <fit/fit_code.h>
#include <gtest/gtest.h>
#include <gmock/gmock.h>

using namespace ::testing;
using namespace Fit::RegistryInfo;
using namespace Fit::Registry;
class FitFitableMetaServiceForRepoTest : public ::testing::Test {
public:
    void SetUp() override
    {
        fitMemoryFitableOperation_ = Fit::make_shared<FitMemoryFitableOperation>();
        fitableMetaServiceRepo_ = Fit::make_shared<FitFitableMetaServiceForRepo>(fitMemoryFitableOperation_);

        fitableMeta_.fitable.fitableId = "test_fid";
        fitableMeta_.fitable.fitableVersion = "test_f_version";
        fitableMeta_.fitable.genericableId = "test_gid";
        fitableMeta_.fitable.genericableVersion = "test_g_version";
        fitableMeta_.formats.emplace_back(static_cast<Fit::fit_format_type>(0));
        fitableMeta_.application.name = "test_name";
        fitableMeta_.application.nameVersion = "test_name_version";
        fitableMeta_.aliases = {"test_alias1", "test_alias2"};
        fitableMeta_.tags = {"test_tag1", "test_tag2"};
        fitableMeta_.extensions = {{"key1", "value1"}, {"key2", "value2"}};
        fitableMeta_.environment = "test_environment";
    }
    void TearDown() override
    {
    }
public:
    void CheckFitableMeta(FitableMeta& actualFitableMeta, FitableMeta& expectedFitableMeta)
    {
        EXPECT_EQ(actualFitableMeta.fitable.fitableId, expectedFitableMeta.fitable.fitableId);
        EXPECT_EQ(actualFitableMeta.fitable.fitableVersion, expectedFitableMeta.fitable.fitableVersion);
        EXPECT_EQ(actualFitableMeta.fitable.genericableId, expectedFitableMeta.fitable.genericableId);
        EXPECT_EQ(actualFitableMeta.fitable.genericableVersion, expectedFitableMeta.fitable.genericableVersion);
        EXPECT_EQ(static_cast<int32_t>(actualFitableMeta.formats.front()),
            static_cast<int32_t>(expectedFitableMeta.formats.front()));
        EXPECT_EQ(actualFitableMeta.application.name, expectedFitableMeta.application.name);
        EXPECT_EQ(actualFitableMeta.application.nameVersion, expectedFitableMeta.application.nameVersion);
        EXPECT_EQ(actualFitableMeta.aliases[0], expectedFitableMeta.aliases[0]);
        EXPECT_EQ(actualFitableMeta.aliases[1], expectedFitableMeta.aliases[1]);
        EXPECT_EQ(actualFitableMeta.tags[0], expectedFitableMeta.tags[0]);
        EXPECT_EQ(actualFitableMeta.tags[1], expectedFitableMeta.tags[1]);
        EXPECT_EQ(actualFitableMeta.extensions["key1"], expectedFitableMeta.extensions["key1"]);
        EXPECT_EQ(actualFitableMeta.extensions["key2"], expectedFitableMeta.extensions["key2"]);
        EXPECT_EQ(actualFitableMeta.environment, expectedFitableMeta.environment);
    }
public:
    Fit::shared_ptr<FitMemoryFitableOperation> fitMemoryFitableOperation_ {};
    Fit::shared_ptr<FitFitableMetaServiceForRepo> fitableMetaServiceRepo_ {};
    FitableMeta fitableMeta_ {};
};

TEST_F(FitFitableMetaServiceForRepoTest, show_return_fitable_meta_when_save_and_query_given_param)
{
    // given
    Fit::vector<FitableMeta> fitableMetasIn {fitableMeta_};
    Fit::vector<FitableMeta> expectedFitableMetas {fitableMeta_};
    Fit::vector<Fit::string> genericableIdsQuery {fitableMeta_.fitable.genericableId};
    Fit::string environment {fitableMeta_.environment};
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    // when
    int32_t actualSaveRet = fitableMetaServiceRepo_->Save(fitableMetasIn);
    Fit::vector<FitableMeta> actualQueryFitableMetas = fitableMetaServiceRepo_->Query(genericableIdsQuery, environment);

    // then
    EXPECT_EQ(actualSaveRet, expectedSaveRet);
    EXPECT_EQ(actualQueryFitableMetas.size(), expectedFitableMetas.size());
    CheckFitableMeta(actualQueryFitableMetas.front(), expectedFitableMetas.front());
}

TEST_F(FitFitableMetaServiceForRepoTest, show_return_fitable_meta_when_save_query_and_remove_given_param)
{
    // given
    Fit::vector<FitableMeta> fitableMetasIn {fitableMeta_};
    Fit::string environment {fitableMeta_.environment};
    Fit::vector<FitableMeta> expectedFitableMetas {fitableMeta_};
    Fitable fitable = fitableMeta_.fitable;
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    Fit::vector<Application> applicationsRemove {fitableMeta_.application};
    int32_t expectedRemoveRet = FIT_ERR_SUCCESS;

    // when
    int32_t actualSaveRet = fitableMetaServiceRepo_->Save(fitableMetasIn);
    Fit::vector<FitableMeta> actualQueryFitableMetas = fitableMetaServiceRepo_->Query(fitable, environment);
    int32_t actualRemoveRet = fitableMetaServiceRepo_->Remove(applicationsRemove, environment);
    Fit::vector<FitableMeta> actualQueryFitableMetasAfterRemove = fitableMetaServiceRepo_->Query(fitable, environment);

    // then
    EXPECT_EQ(actualSaveRet, expectedSaveRet);
    EXPECT_EQ(actualQueryFitableMetas.size(), expectedFitableMetas.size());
    CheckFitableMeta(actualQueryFitableMetas.front(), expectedFitableMetas.front());
    EXPECT_EQ(actualRemoveRet, expectedRemoveRet);
    EXPECT_EQ(actualQueryFitableMetasAfterRemove.empty(), true);
}