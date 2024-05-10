/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <memory>
#include <thread>
#include <utility>
#include <registry_server/core/fit_registry_mgr.h>
#include <fit/stl/string.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using namespace Fit::Registry;

class FitRegistryWorkerStatusListenerTest : public ::testing::Test {
public:
    void SetUp() override
    {
        workerOfflineEvent_.is_online = false;
        workerOfflineEvent_.worker_id = "127.0.0.1:8888";

        workerOnlineEvent_.is_online = true;
        workerOnlineEvent_.worker_id = "127.0.0.1:9999";
    }

    void TearDown() override
    {
    }
public:
    worker_status_notify_t workerOfflineEvent_;
    worker_status_notify_t workerOnlineEvent_;
};

TEST_F(FitRegistryWorkerStatusListenerTest, should_return_void_when_add_event_given_worker_offline)
{
    // given
    // when
    worker_status_listener_ptr listenerPtr = fit_registry_mgr::instance()->get_worker_status_listener();
    // then
    listenerPtr->add(workerOfflineEvent_);
}

TEST_F(FitRegistryWorkerStatusListenerTest, should_return_void_when_add_event_given_worker_online)
{
    // given
    // when
    worker_status_listener_ptr listenerPtr = fit_registry_mgr::instance()->get_worker_status_listener();
    // then
    listenerPtr->add(workerOnlineEvent_);
}

TEST_F(FitRegistryWorkerStatusListenerTest, should_return_void_when_process_event_given_worker_offline)
{
    // given
    // when
    worker_status_listener_ptr listenerPtr = fit_registry_mgr::instance()->get_worker_status_listener();
    listenerPtr->add(workerOfflineEvent_);
    listenerPtr->process(0);
    // then
}

TEST_F(FitRegistryWorkerStatusListenerTest, should_return_void_when_process_event_given_worker_online)
{
    // given
    // when
    worker_status_listener_ptr listenerPtr = fit_registry_mgr::instance()->get_worker_status_listener();
    listenerPtr->add(workerOfflineEvent_);
    listenerPtr->process(0);
    // then
}

TEST_F(FitRegistryWorkerStatusListenerTest,
    should_return_void_when_process_event_given_worker_offline_and_delay_times)
{
    // given
    // when
    worker_status_listener_ptr listenerPtr = fit_registry_mgr::instance()->get_worker_status_listener();
    listenerPtr->add(workerOfflineEvent_);
    listenerPtr->process(1000000);
    // then
}

TEST_F(FitRegistryWorkerStatusListenerTest, should_return_void_when_process_event_given_null_fitable_service)
{
    // given
    // when
    worker_status_listener_ptr listenerPtr = std::make_shared<fit_registry_worker_status_listener>(nullptr);
    listenerPtr->add(workerOfflineEvent_);
    listenerPtr->process(0);
    // then
}