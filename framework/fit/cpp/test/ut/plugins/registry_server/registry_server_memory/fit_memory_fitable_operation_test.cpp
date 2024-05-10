/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <registry_server/registry_server_memory/fitable/fit_memory_fitable_operation.h>
#include <fit/fit_log.h>
#include <fit/stl/string.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using namespace Fit;
using namespace Fit::Registry;

class FitMemoryFitableOperationTest : public ::testing::Test {
public:

    void SetUp() override
    {
        application_.name = "test_app_name";
        application_.nameVersion = "test_app_version";
        fitable_.fitableId = "test_fitableId";
        fitable_.genericableId = "test_genericable_id";
        fitable_.genericableVersion = "test_genericable_version";
    }

    void TearDown() override
    {
    }
public:
    Fit::RegistryInfo::Application application_;
    Fit::RegistryInfo::Fitable fitable_;
};

TEST_F(FitMemoryFitableOperationTest, should_return_failed_when_save_fitable_meta_given_nullptr)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr = nullptr;
    int32_t expectedSaveRet = FIT_ERR_FAIL;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
}

TEST_F(FitMemoryFitableOperationTest, should_return_failed_when_save_fitable_meta_given_appname_empty)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    int32_t expectedSaveRet = FIT_ERR_FAIL;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
}

TEST_F(FitMemoryFitableOperationTest, should_return_failed_when_save_fitable_meta_given_appversion_empty)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application.name = "test";
    int32_t expectedSaveRet = FIT_ERR_FAIL;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
}

TEST_F(FitMemoryFitableOperationTest, should_return_failed_when_save_fitable_meta_given_genericable_id_empty)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    int32_t expectedSaveRet = FIT_ERR_FAIL;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
}

TEST_F(FitMemoryFitableOperationTest, should_return_failed_when_save_fitable_meta_given_fitable_id_empty)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable.genericableId = "test_genericable_id";
    int32_t expectedSaveRet = FIT_ERR_FAIL;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
}

TEST_F(FitMemoryFitableOperationTest, should_return_failed_when_save_fitable_meta_given_genericable_version_empty)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable.genericableId = "test_genericable_id";
    fitableMetaPtr->fitable.fitableId = "test_fitable_id";
    int32_t expectedSaveRet = FIT_ERR_FAIL;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
}

TEST_F(FitMemoryFitableOperationTest, should_return_success_when_save_fitable_meta_given_fitable_meta)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
}

TEST_F(FitMemoryFitableOperationTest, should_return_empty_when_save_and_query_fitable_meta_given_error_fitable)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::Fitable fitable;
    fitable.fitableId = "invalid_fid";
    fitable.genericableId = "invalid_gid";
    fitable.genericableVersion = "invalid_gversion";

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    auto queryRet = fitMemoryFitableOperation.Query(fitable);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(queryRet.empty(), true);
}

TEST_F(FitMemoryFitableOperationTest, should_return_fitable_when_save_and_query_fitable_meta_given_fitable)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::Fitable fitable = fitable_;
    FitMemoryFitableOperation::FitableMetaPtrSet expectedQueryFitableMetaPtrSet;
    expectedQueryFitableMetaPtrSet.push_back(fitableMetaPtr);

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    auto queryRet = fitMemoryFitableOperation.Query(fitable);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    ASSERT_EQ(queryRet.size(), expectedQueryFitableMetaPtrSet.size());
    EXPECT_EQ(queryRet.front()->application.name, expectedQueryFitableMetaPtrSet.front()->application.name);
    EXPECT_EQ(queryRet.front()->application.nameVersion,
              expectedQueryFitableMetaPtrSet.front()->application.nameVersion);
    EXPECT_EQ(queryRet.front()->fitable.fitableId, expectedQueryFitableMetaPtrSet.front()->fitable.fitableId);
    EXPECT_EQ(queryRet.front()->fitable.genericableId, expectedQueryFitableMetaPtrSet.front()->fitable.genericableId);
    EXPECT_EQ(queryRet.front()->fitable.genericableVersion,
              expectedQueryFitableMetaPtrSet.front()->fitable.genericableVersion);
    ASSERT_EQ(queryRet.front()->formats.size(), expectedQueryFitableMetaPtrSet.front()->formats.size());
    EXPECT_EQ(int(queryRet.front()->formats.front()), int(expectedQueryFitableMetaPtrSet.front()->formats.front()));
}

TEST_F(FitMemoryFitableOperationTest,
    should_return_nullptr_when_save_and_query_fitable_meta_given_empty_fitable_meta)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::FitableMeta fitableMeta;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    auto queryRet = fitMemoryFitableOperation.Query(fitableMeta);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(queryRet, nullptr);
}

TEST_F(FitMemoryFitableOperationTest, should_return_nullptr_when_save_and_query_fitable_meta_given_invalid_fitable)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::FitableMeta fitableMeta;
    fitableMeta.fitable.fitableId = "invalid_fid";
    fitableMeta.fitable.genericableId = "invalid_gid";
    fitableMeta.fitable.genericableVersion = "invalid_gversion";
    fitableMeta.application = application_;
    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    auto queryRet = fitMemoryFitableOperation.Query(fitableMeta);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(queryRet, nullptr);
}

TEST_F(FitMemoryFitableOperationTest,
    should_return_nullptr_when_save_and_query_fitable_meta_given_invalid_applicaition)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::FitableMeta fitableMeta;
    fitableMeta.fitable = fitable_;
    fitableMeta.application.name = "invalid_app_name";
    fitableMeta.application.nameVersion = "invalid_app_version";
    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    auto queryRet = fitMemoryFitableOperation.Query(fitableMeta);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(queryRet, nullptr);
}

TEST_F(FitMemoryFitableOperationTest,
    should_return_fitable_meta_when_save_and_query_fitable_meta_given_fitable_meta)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::FitableMeta fitableMeta = *fitableMetaPtr;
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> expectedFitableMetaPtr = fitableMetaPtr;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    auto queryRet = fitMemoryFitableOperation.Query(fitableMeta);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    ASSERT_NE(queryRet, nullptr);
    EXPECT_EQ(queryRet->application.name, expectedFitableMetaPtr->application.name);
    EXPECT_EQ(queryRet->application.nameVersion, expectedFitableMetaPtr->application.nameVersion);
    EXPECT_EQ(queryRet->fitable.genericableId, expectedFitableMetaPtr->fitable.genericableId);
    EXPECT_EQ(queryRet->fitable.genericableVersion, expectedFitableMetaPtr->fitable.genericableVersion);
    EXPECT_EQ(queryRet->fitable.fitableId, expectedFitableMetaPtr->fitable.fitableId);
    ASSERT_EQ(queryRet->formats.size(), expectedFitableMetaPtr->formats.size());
    EXPECT_EQ(int(queryRet->formats.front()), int(expectedFitableMetaPtr->formats.front()));
}
TEST_F(FitMemoryFitableOperationTest, should_return_empty_when_Query_given_removed_fitable)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    Fit::RegistryInfo::Fitable removedFitable = fitableMetaPtr->fitable;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    int32_t removeRet = fitMemoryFitableOperation.Remove(fitableMetaPtr->application);
    auto queryRet = fitMemoryFitableOperation.Query(removedFitable);

    // then
    ASSERT_EQ(saveRet, FIT_OK);
    ASSERT_EQ(removeRet, FIT_OK);
    ASSERT_TRUE(queryRet.empty());
}

TEST_F(FitMemoryFitableOperationTest, should_return_empty_when_save_and_query_fitable_meta_given_empty_application)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::Application application;

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    auto queryRet = fitMemoryFitableOperation.Query(application);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(queryRet.empty(), true);
}

TEST_F(FitMemoryFitableOperationTest,
    should_return_fitable_meta_set_when_save_and_query_fitable_meta_given_application)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::Application application = application_;
    FitMemoryFitableOperation::FitableMetaPtrSet expectedQueryFitableMetaPtrSet;
    expectedQueryFitableMetaPtrSet.push_back(fitableMetaPtr);

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    auto queryRet = fitMemoryFitableOperation.Query(application);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    ASSERT_EQ(queryRet.size(), expectedQueryFitableMetaPtrSet.size());
    EXPECT_EQ(queryRet.front()->application.name, expectedQueryFitableMetaPtrSet.front()->application.name);
    EXPECT_EQ(queryRet.front()->application.nameVersion,
              expectedQueryFitableMetaPtrSet.front()->application.nameVersion);
    EXPECT_EQ(queryRet.front()->fitable.fitableId, expectedQueryFitableMetaPtrSet.front()->fitable.fitableId);
    EXPECT_EQ(queryRet.front()->fitable.genericableId, expectedQueryFitableMetaPtrSet.front()->fitable.genericableId);
    EXPECT_EQ(queryRet.front()->fitable.genericableVersion,
              expectedQueryFitableMetaPtrSet.front()->fitable.genericableVersion);
    ASSERT_EQ(queryRet.front()->formats.size(), expectedQueryFitableMetaPtrSet.front()->formats.size());
    EXPECT_EQ(int(queryRet.front()->formats.front()), int(expectedQueryFitableMetaPtrSet.front()->formats.front()));
}

TEST_F(FitMemoryFitableOperationTest,
    should_return_fitable_meta_set_when_save_remove_and_query_fitable_meta_given_empty_application)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::Application application = application_;
    Fit::RegistryInfo::Application removeApplicationInput;
    FitMemoryFitableOperation::FitableMetaPtrSet expectedQueryFitableMetaPtrSet;
    expectedQueryFitableMetaPtrSet.push_back(fitableMetaPtr);

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    int32_t removeRet = fitMemoryFitableOperation.Remove(removeApplicationInput);
    auto queryRet = fitMemoryFitableOperation.Query(application);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(removeRet, FIT_ERR_SUCCESS);
    ASSERT_EQ(queryRet.size(), expectedQueryFitableMetaPtrSet.size());
    EXPECT_EQ(queryRet.front()->application.name, expectedQueryFitableMetaPtrSet.front()->application.name);
    EXPECT_EQ(queryRet.front()->application.nameVersion,
              expectedQueryFitableMetaPtrSet.front()->application.nameVersion);
    EXPECT_EQ(queryRet.front()->fitable.fitableId, expectedQueryFitableMetaPtrSet.front()->fitable.fitableId);
    EXPECT_EQ(queryRet.front()->fitable.genericableId, expectedQueryFitableMetaPtrSet.front()->fitable.genericableId);
    EXPECT_EQ(queryRet.front()->fitable.genericableVersion,
              expectedQueryFitableMetaPtrSet.front()->fitable.genericableVersion);
    ASSERT_EQ(queryRet.front()->formats.size(), expectedQueryFitableMetaPtrSet.front()->formats.size());
    EXPECT_EQ(int(queryRet.front()->formats.front()), int(expectedQueryFitableMetaPtrSet.front()->formats.front()));
}

TEST_F(FitMemoryFitableOperationTest,
    should_return_empty_when_save_remove_and_query_fitable_meta_given_application)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMetaPtr->application = application_;
    fitableMetaPtr->fitable = fitable_;
    fitableMetaPtr->formats.emplace_back(Fit::fit_format_type::PROTOBUF);
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::Application application = application_;
    Fit::RegistryInfo::Application removeApplicationInput = application_;
    FitMemoryFitableOperation::FitableMetaPtrSet expectedQueryFitableMetaPtrSet;
    expectedQueryFitableMetaPtrSet.push_back(fitableMetaPtr);

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMetaPtr);
    int32_t removeRet = fitMemoryFitableOperation.Remove(removeApplicationInput);
    auto queryRet = fitMemoryFitableOperation.Query(application);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(removeRet, FIT_ERR_SUCCESS);
    EXPECT_EQ(queryRet.empty(), true);
}

TEST_F(FitMemoryFitableOperationTest, should_return_fitable_meta_set_when_GetFitables_given_exist_generic_id)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMeta
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMeta->application = application_;
    fitableMeta->fitable = fitable_;
    fitableMeta->formats.emplace_back(Fit::fit_format_type::PROTOBUF);

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMeta);
    auto queryRet = fitMemoryFitableOperation.Query(fitableMeta->fitable.genericableId);

    // then
    ASSERT_EQ(saveRet, FIT_OK);
    ASSERT_EQ(queryRet.size(), 1);
    EXPECT_TRUE(queryRet.front().get() == fitableMeta.get());
}

TEST_F(FitMemoryFitableOperationTest, should_return_empty_when_GetFitables_given_removed_generic_id)
{
    // given
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMeta
        = std::make_shared<Fit::RegistryInfo::FitableMeta>();
    fitableMeta->application = application_;
    fitableMeta->fitable = fitable_;
    fitableMeta->formats.emplace_back(Fit::fit_format_type::PROTOBUF);

    // when
    FitMemoryFitableOperation fitMemoryFitableOperation;
    int32_t saveRet = fitMemoryFitableOperation.Save(fitableMeta);
    int32_t removeRet = fitMemoryFitableOperation.Remove(fitableMeta->application);
    auto queryRet = fitMemoryFitableOperation.Query(fitableMeta->fitable.genericableId);

    // then
    ASSERT_EQ(saveRet, FIT_OK);
    ASSERT_EQ(removeRet, FIT_OK);
    ASSERT_TRUE(queryRet.empty());
}

TEST_F(FitMemoryFitableOperationTest,
    should_return_two_fitable_metas_when_GetFitables_given_generic_id_have_two_fitables_in_same_app)
{
    std::unordered_set<RegistryInfo::FitableMetaPtr, RegistryInfo::FitableMetaSharedPtrHash,
        RegistryInfo::FitableMetaSharedPtrEq> expectMetas;
    std::shared_ptr<RegistryInfo::FitableMeta> fitableMeta1 = std::make_shared<RegistryInfo::FitableMeta>();
    fitableMeta1->application = application_;
    fitableMeta1->fitable = fitable_;
    fitableMeta1->formats.emplace_back(Fit::fit_format_type::PROTOBUF);

    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMeta2
        = std::make_shared<Fit::RegistryInfo::FitableMeta>(*fitableMeta1);
    fitableMeta2->fitable.fitableId = "fitable2";

    expectMetas.insert(fitableMeta1);
    expectMetas.insert(fitableMeta2);

    FitMemoryFitableOperation fitMemoryFitableOperation;
    fitMemoryFitableOperation.Save(fitableMeta1);
    fitMemoryFitableOperation.Save(fitableMeta2);
    auto result = fitMemoryFitableOperation.Query(fitable_.genericableId);

    ASSERT_EQ(result.size(), expectMetas.size());
    for (auto& meta : result) {
        EXPECT_TRUE(expectMetas.find(meta) != expectMetas.end());
    }
}

TEST_F(FitMemoryFitableOperationTest,
    should_return_two_fitable_metas_when_GetFitables_given_generic_id_have_two_fitables_in_different_app)
{
    std::unordered_set<RegistryInfo::FitableMetaPtr, RegistryInfo::FitableMetaSharedPtrHash,
        RegistryInfo::FitableMetaSharedPtrEq> expectMetas;
    std::shared_ptr<RegistryInfo::FitableMeta> fitableMeta1 = std::make_shared<RegistryInfo::FitableMeta>();
    fitableMeta1->application = application_;
    fitableMeta1->fitable = fitable_;
    fitableMeta1->formats.emplace_back(Fit::fit_format_type::PROTOBUF);

    std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMeta2
        = std::make_shared<Fit::RegistryInfo::FitableMeta>(*fitableMeta1);
    fitableMeta2->fitable.fitableId = "fitable2";
    fitableMeta2->application.name = "app2";

    expectMetas.insert(fitableMeta1);
    expectMetas.insert(fitableMeta2);

    FitMemoryFitableOperation fitMemoryFitableOperation;
    fitMemoryFitableOperation.Save(fitableMeta2);
    fitMemoryFitableOperation.Save(fitableMeta1);
    auto result = fitMemoryFitableOperation.Query(fitable_.genericableId);

    ASSERT_EQ(result.size(), expectMetas.size());
    for (auto& meta : result) {
        EXPECT_TRUE(expectMetas.find(meta) != expectMetas.end());
    }
}
TEST_F(FitMemoryFitableOperationTest,
    should_return_a_fitable_metas_when_GetFitables_given_two_same_fitable_in_same_app)
{
    std::unordered_set<RegistryInfo::FitableMetaPtr, RegistryInfo::FitableMetaSharedPtrHash,
        RegistryInfo::FitableMetaSharedPtrEq> expectMetas;
    std::shared_ptr<RegistryInfo::FitableMeta> fitableMeta1 = std::make_shared<RegistryInfo::FitableMeta>();
    fitableMeta1->application = application_;
    fitableMeta1->fitable = fitable_;

    std::shared_ptr<RegistryInfo::FitableMeta> fitableMeta2 =
        std::make_shared<RegistryInfo::FitableMeta>(*fitableMeta1);

    expectMetas.insert(fitableMeta2);

    FitMemoryFitableOperation fitMemoryFitableOperation;
    fitMemoryFitableOperation.Save(fitableMeta1);
    fitMemoryFitableOperation.Save(fitableMeta2);
    auto result = fitMemoryFitableOperation.Query(fitable_.genericableId);

    ASSERT_EQ(result.size(), expectMetas.size());
    for (auto& meta : result) {
        EXPECT_TRUE(expectMetas.find(meta) != expectMetas.end());
    }
}