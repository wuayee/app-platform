/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <registry_server/registry_server_memory/fitable/fit_memory_worker_operation.h>
#include <fit/fit_log.h>
#include <fit/stl/string.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using namespace Fit::Registry;

class FitMemoryWorkerOperationTest : public ::testing::Test {
public:

    void SetUp() override
    {
        application_.name = "test_app_name";
        application_.nameVersion = "test_app_version";
        workerId_ = "test_worker_id";

        workerMeta_ = std::make_shared<WorkerWithMeta>();
        workerMeta_->worker.application = application_;
        workerMeta_->worker.workerId = workerId_;
        workerMeta_->worker.expire = 60;
    }

    void TearDown() override
    {
    }
public:
    Fit::RegistryInfo::Application application_;
    Fit::string workerId_;
    std::shared_ptr<WorkerWithMeta> workerMeta_;
};

static void CheckWorkerMeta(const Fit::RegistryInfo::Worker& l, const Fit::RegistryInfo::Worker& r)
{
    EXPECT_EQ(l.workerId, r.workerId);
    EXPECT_EQ(l.expire, r.expire);
    EXPECT_EQ(l.extensions, r.extensions);
    EXPECT_EQ(l.application.name, r.application.name);
    EXPECT_EQ(l.application.nameVersion, r.application.nameVersion);
}

TEST_F(FitMemoryWorkerOperationTest, should_return_failed_when_save_worker_given_nullptr)
{
    // given
    std::shared_ptr<WorkerWithMeta> worker = nullptr;
    int32_t expectedSaveRet = FIT_ERR_FAIL;

    // when
    FitMemoryWorkerOperation fitMemoryWorkerOperation;
    int32_t saveRet = fitMemoryWorkerOperation.Save(worker);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
}

TEST_F(FitMemoryWorkerOperationTest, should_return_success_when_save_worker_given_worker)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    // when
    FitMemoryWorkerOperation fitMemoryWorkerOperation;
    int32_t saveRet = fitMemoryWorkerOperation.Save(workerMeta_);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
}

TEST_F(FitMemoryWorkerOperationTest, should_return_worker_set_when_save_and_query_worker_given_application)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::RegistryInfo::Application applicationIn = application_;

    Fit::vector<std::shared_ptr<WorkerWithMeta>> expectedWorkerSet;
    expectedWorkerSet.emplace_back(workerMeta_);
    // when
    FitMemoryWorkerOperation fitMemoryWorkerOperation;
    int32_t saveRet = fitMemoryWorkerOperation.Save(workerMeta_);
    Fit::vector<std::shared_ptr<WorkerWithMeta>> workerSet = fitMemoryWorkerOperation.Query(applicationIn);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    ASSERT_EQ(workerSet.size(), expectedWorkerSet.size());
    CheckWorkerMeta(workerSet.front()->worker, expectedWorkerSet.front()->worker);
}

TEST_F(FitMemoryWorkerOperationTest, should_return_worker_when_save_and_query_worker_given_worker_id)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::string workerInput = workerId_;

    std::shared_ptr<WorkerWithMeta> expectedWorker = workerMeta_;
    // when
    FitMemoryWorkerOperation fitMemoryWorkerOperation;
    int32_t saveRet = fitMemoryWorkerOperation.Save(workerMeta_);
    std::shared_ptr<WorkerWithMeta> actualWorker = fitMemoryWorkerOperation.Query(workerInput);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    ASSERT_NE(actualWorker, nullptr);
    CheckWorkerMeta(actualWorker->worker, expectedWorker->worker);
}

TEST_F(FitMemoryWorkerOperationTest, should_return_worker_set_when_save_and_query_all_given_empty)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::vector<std::shared_ptr<WorkerWithMeta>> expectedWorkerSet;
    expectedWorkerSet.emplace_back(workerMeta_);
    // when
    FitMemoryWorkerOperation fitMemoryWorkerOperation;
    int32_t saveRet = fitMemoryWorkerOperation.Save(workerMeta_);
    Fit::vector<std::shared_ptr<WorkerWithMeta>> workerSet = fitMemoryWorkerOperation.QueryAll();

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    ASSERT_EQ(workerSet.size(), expectedWorkerSet.size());
    CheckWorkerMeta(workerSet.front()->worker, expectedWorkerSet.front()->worker);
}

TEST_F(FitMemoryWorkerOperationTest,
    should_return_worker_when_save_remove_and_query_worker_given_invalid_worker_id)
{
    // given
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::string queryWorkerId = workerId_;
    Fit::string removeWorkerId = "invalid_worker_id";
    std::shared_ptr<WorkerWithMeta> expectedWorker = workerMeta_;
    // when
    FitMemoryWorkerOperation fitMemoryWorkerOperation;
    int32_t saveRet = fitMemoryWorkerOperation.Save(workerMeta_);
    fitMemoryWorkerOperation.Remove(removeWorkerId);
    std::shared_ptr<WorkerWithMeta> actualWorker = fitMemoryWorkerOperation.Query(queryWorkerId);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    ASSERT_NE(actualWorker, nullptr);
    CheckWorkerMeta(actualWorker->worker, expectedWorker->worker);
}

TEST_F(FitMemoryWorkerOperationTest, should_return_worker_when_save_remove_and_query_worker_given_worker_id)
{
    // given
    std::shared_ptr<WorkerWithMeta> worker2 = std::make_shared<WorkerWithMeta>();
    worker2->worker.application = application_;
    worker2->worker.workerId = "test_worker_id2";
    worker2->worker.expire = 60;
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::string queryWorkerId = workerId_;
    Fit::string removeWorkerId = "test_worker_id2";
    std::shared_ptr<WorkerWithMeta> expectedWorker = workerMeta_;
    // when
    FitMemoryWorkerOperation fitMemoryWorkerOperation;
    int32_t saveRet = fitMemoryWorkerOperation.Save(workerMeta_);
    int32_t saveRet2 = fitMemoryWorkerOperation.Save(worker2);
    fitMemoryWorkerOperation.Remove(removeWorkerId);
    std::shared_ptr<WorkerWithMeta> actualWorker = fitMemoryWorkerOperation.Query(queryWorkerId);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(saveRet2, expectedSaveRet);
    ASSERT_NE(actualWorker, nullptr);
    CheckWorkerMeta(actualWorker->worker, expectedWorker->worker);
}

TEST_F(FitMemoryWorkerOperationTest, should_return_nullptr_when_save_remove_and_query_worker_given_worker_id)
{
    // given
    std::shared_ptr<WorkerWithMeta> worker2 = std::make_shared<WorkerWithMeta>();
    worker2->worker.application = application_;
    worker2->worker.workerId = "test_worker_id2";
    worker2->worker.expire = 60;
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;

    Fit::string queryWorkerId = workerId_;
    Fit::string removeWorkerId = workerId_;
    std::shared_ptr<WorkerWithMeta> expectedWorker = workerMeta_;
    // when
    FitMemoryWorkerOperation fitMemoryWorkerOperation;
    int32_t saveRet = fitMemoryWorkerOperation.Save(workerMeta_);
    int32_t saveRet2 = fitMemoryWorkerOperation.Save(worker2);
    fitMemoryWorkerOperation.Remove(removeWorkerId);
    std::shared_ptr<WorkerWithMeta> actualWorker = fitMemoryWorkerOperation.Query(queryWorkerId);

    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(saveRet2, expectedSaveRet);
    ASSERT_EQ(actualWorker, nullptr);
}