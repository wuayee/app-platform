/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : application instance service for repo ut.
 * Author       : w00561424
 * Date         : 2023/09/08
 * Notes:       :
 */
#include <registry_server/v3/fit_application_instance/include/fit_application_instance_service_for_repo.h>
#include <registry_server/registry_server_memory/fitable/fit_memory_worker_operation.h>
#include <registry_server/registry_server_memory/fitable/fit_memory_address_operation.h>
#include <fit/stl/memory.hpp>
#include <fit/fit_code.h>
#include <gtest/gtest.h>
#include <gmock/gmock.h>

using namespace ::testing;
using namespace Fit::Registry;
using namespace Fit::RegistryInfo;
class FitApplicationInstanceServiceForRepoTest : public ::testing::Test {
public:
    void SetUp()
    {
        workerRepo_ = Fit::make_shared<FitMemoryWorkerOperation>();
        addressRepo_ = Fit::make_shared<FitMemoryAddressOperation>();
        applicationInstanceRepo_ = Fit::make_shared<FitApplicationInstanceServiceForRepo>(workerRepo_, addressRepo_);

        worker_.workerId = "test_worker_id";
        worker_.application.name = "test_app_name";
        worker_.application.nameVersion = "test_app_name_version";
        worker_.expire = 30;
        worker_.environment = "test_env";

        address_.host = "test_host";
        address_.port = 66;
        address_.protocol = static_cast<Fit::fit_protocol_type>(0);
        address_.workerId = worker_.workerId;
        applicationInstance_.workers.emplace_back(worker_);
        applicationInstance_.addresses.emplace_back(address_);

        worker2_.workerId = "test_worker_id2";
        worker2_.application.name = "test_app_name2";
        worker2_.application.nameVersion = "test_app_name_version2";
        worker2_.expire = 30;
        worker2_.environment = "test_env2";

        address2_.host = "test_host2";
        address2_.port = 88;
        address2_.protocol = static_cast<Fit::fit_protocol_type>(0);
        address2_.workerId = worker2_.workerId;
        applicationInstance2_.workers.emplace_back(worker2_);
        applicationInstance2_.addresses.emplace_back(address2_);
    }
    void TearDown()
    {
    }
public:
    void CheckWorker(const Worker& actualWorker, const Worker& expectedWorker)
    {
        EXPECT_EQ(actualWorker.application.name, expectedWorker.application.name);
        EXPECT_EQ(actualWorker.application.nameVersion, expectedWorker.application.nameVersion);
        EXPECT_EQ(actualWorker.expire, expectedWorker.expire);
        EXPECT_EQ(actualWorker.environment, expectedWorker.environment);
    }

    void CheckAddress(const Address& actualAddress, const Address& expectedAddress)
    {
        EXPECT_EQ(actualAddress.host, expectedAddress.host);
        EXPECT_EQ(actualAddress.port, expectedAddress.port);
        EXPECT_EQ(actualAddress.protocol, expectedAddress.protocol);
        EXPECT_EQ(actualAddress.workerId, expectedAddress.workerId);
    }
    
    void CheckAddress()
    {
    }
public:
    Fit::shared_ptr<FitMemoryWorkerOperation> workerRepo_ {};
    Fit::shared_ptr<FitMemoryAddressOperation> addressRepo_ {};
    Fit::shared_ptr<FitApplicationInstanceServiceForRepo> applicationInstanceRepo_ {};
    Fit::RegistryInfo::ApplicationInstance applicationInstance_ {};
    Fit::RegistryInfo::Worker worker_ {};
    Fit::RegistryInfo::Address address_ {};
    Fit::RegistryInfo::ApplicationInstance applicationInstance2_ {};
    Fit::RegistryInfo::Worker worker2_ {};
    Fit::RegistryInfo::Address address2_ {};
};

TEST_F(FitApplicationInstanceServiceForRepoTest,
    should_return_application_instance_when_save_and_query_given_param)
{
    // given
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_, applicationInstance2_};
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    Fit::vector<Fit::RegistryInfo::Application> applicationsIn {worker_.application};
    Fit::vector<ApplicationInstance> expectedQueryApplicationInstances {applicationInstance_};
    // when
    int32_t saveRet = applicationInstanceRepo_->Save(applicationInstances);
    Fit::vector<ApplicationInstance> actualQueryApplicationInstances = applicationInstanceRepo_->Query(applicationsIn);
    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(actualQueryApplicationInstances.size(), expectedQueryApplicationInstances.size());
    CheckWorker(actualQueryApplicationInstances.front().workers.front(),
        expectedQueryApplicationInstances.front().workers.front());
    CheckAddress(actualQueryApplicationInstances.front().addresses.front(),
        expectedQueryApplicationInstances.front().addresses.front());
}

TEST_F(FitApplicationInstanceServiceForRepoTest,
    should_return_application_instance_when_save_and_query_by_id_given_param)
{
    // given
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_, applicationInstance2_};
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    
    Fit::vector<Fit::RegistryInfo::Application> applicationsIn {worker_.application};
    Fit::vector<ApplicationInstance> expectedQueryApplicationInstances {applicationInstance_};
    // when
    int32_t saveRet = applicationInstanceRepo_->Save(applicationInstances);
    Fit::vector<ApplicationInstance> actualQueryApplicationInstances
        = applicationInstanceRepo_->Query(applicationsIn, worker_.workerId);
    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(actualQueryApplicationInstances.size(), expectedQueryApplicationInstances.size());
    CheckWorker(actualQueryApplicationInstances.front().workers.front(),
        expectedQueryApplicationInstances.front().workers.front());
    CheckAddress(actualQueryApplicationInstances.front().addresses.front(),
        expectedQueryApplicationInstances.front().addresses.front());
}

TEST_F(FitApplicationInstanceServiceForRepoTest,
    should_return_empty_when_save_and_query_by_id_given_invalid_id)
{
    // given
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_, applicationInstance2_};
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    
    Fit::vector<Fit::RegistryInfo::Application> applicationsIn {worker_.application};
    // when
    int32_t saveRet = applicationInstanceRepo_->Save(applicationInstances);
    Fit::vector<ApplicationInstance> actualQueryApplicationInstances
        = applicationInstanceRepo_->Query(applicationsIn, "invalid workerId");
    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(actualQueryApplicationInstances.empty(), true);
}

TEST_F(FitApplicationInstanceServiceForRepoTest,
    should_return_empty_when_save_and_query_by_id_given_error_app)
{
    // given
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_, applicationInstance2_};
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    
    Fit::vector<Fit::RegistryInfo::Application> applicationsIn {worker2_.application};
    // when
    int32_t saveRet = applicationInstanceRepo_->Save(applicationInstances);
    Fit::vector<ApplicationInstance> actualQueryApplicationInstances
        = applicationInstanceRepo_->Query(applicationsIn, worker_.workerId);
    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(actualQueryApplicationInstances.empty(), true);
}

TEST_F(FitApplicationInstanceServiceForRepoTest,
    should_return_empty_when_save_remove_and_query_by_id_given_param)
{
    // given
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_, applicationInstance2_};
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    
    Fit::vector<Fit::RegistryInfo::Application> applicationsQuery {worker_.application};
    Fit::vector<Fit::RegistryInfo::Application> applicationsRemove {worker_.application};
    Fit::vector<ApplicationInstance> expectedQueryApplicationInstances {applicationInstance_};

    // when
    int32_t saveRet = applicationInstanceRepo_->Save(applicationInstances);
    Fit::vector<ApplicationInstance> actualQueryApplicationInstances
        = applicationInstanceRepo_->Query(applicationsQuery, worker_.workerId);
    int32_t removeRet = applicationInstanceRepo_->Remove(worker_.workerId);
    Fit::vector<ApplicationInstance> actualQueryAfterRemoveApplicationInstances
        = applicationInstanceRepo_->Query(applicationsQuery, worker_.workerId);
    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(actualQueryApplicationInstances.size(), expectedQueryApplicationInstances.size());
    CheckWorker(actualQueryApplicationInstances.front().workers.front(),
        expectedQueryApplicationInstances.front().workers.front());
    CheckAddress(actualQueryApplicationInstances.front().addresses.front(),
        expectedQueryApplicationInstances.front().addresses.front());
    EXPECT_EQ(actualQueryAfterRemoveApplicationInstances.empty(), true);
}

TEST_F(FitApplicationInstanceServiceForRepoTest,
    should_return_empty_when_save_remove_and_query_by_application_and_id_given_param)
{
    // given
    Fit::vector<ApplicationInstance> applicationInstances {applicationInstance_, applicationInstance2_};
    int32_t expectedSaveRet = FIT_ERR_SUCCESS;
    
    Fit::vector<Fit::RegistryInfo::Application> applicationsQuery {worker_.application};
    Fit::vector<Fit::RegistryInfo::Application> applicationsRemove {worker_.application};
    Fit::vector<ApplicationInstance> expectedQueryApplicationInstances {applicationInstance_};

    // when
    int32_t saveRet = applicationInstanceRepo_->Save(applicationInstances);
    Fit::vector<ApplicationInstance> actualQueryApplicationInstances
        = applicationInstanceRepo_->Query(applicationsQuery, worker_.workerId);
    int32_t removeRet = applicationInstanceRepo_->Remove(applicationsRemove, worker_.workerId);
    Fit::vector<ApplicationInstance> actualQueryAfterRemoveApplicationInstances
        = applicationInstanceRepo_->Query(applicationsQuery, worker_.workerId);
    // then
    EXPECT_EQ(saveRet, expectedSaveRet);
    EXPECT_EQ(actualQueryApplicationInstances.size(), expectedQueryApplicationInstances.size());
    CheckWorker(actualQueryApplicationInstances.front().workers.front(),
        expectedQueryApplicationInstances.front().workers.front());
    CheckAddress(actualQueryApplicationInstances.front().addresses.front(),
        expectedQueryApplicationInstances.front().addresses.front());
    EXPECT_EQ(actualQueryAfterRemoveApplicationInstances.empty(), true);
}
