/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/25
 */
#include <include/token_life_cycle_observer.h>
#include <include/secure_access.h>
#include <thread>
#include <chrono>
#include <fit/internal/util/thread/fit_thread_pool.h>
#include <fit/fit_log.h>
namespace Fit {
constexpr const size_t THREAD_POOL_NUM = 2;
constexpr const size_t CHECKOUT_INTERVAL = 60 * 1000; // 60秒
TokenLifeCycleObserver::TokenLifeCycleObserver(SecureAccess* secureAccess)
    : secureAccess_(secureAccess)
{
    timer_ = std::make_shared<timer>(std::make_shared<Fit::Thread::thread_pool>(THREAD_POOL_NUM));
}

TokenLifeCycleObserver::~TokenLifeCycleObserver()
{
    Uninit();
}

int32_t TokenLifeCycleObserver::Init()
{
    if (secureAccess_ == nullptr) {
        FIT_LOG_ERROR("Secure access is null.");
        return FIT_ERR_FAIL;
    }
    secureAccess_->Register(this);

    taskId_ = timer_->set_interval(CHECKOUT_INTERVAL, [this]() { Exec(); });
    return FIT_OK;
}

int32_t TokenLifeCycleObserver::Uninit()
{
    if (taskId_ != Fit::timer::INVALID_TASK_ID) {
        timer_->remove(taskId_);
    }
    return FIT_OK;
}

int32_t TokenLifeCycleObserver::Exec()
{
    if (secureAccess_->TokenRoleRepo() == nullptr || secureAccess_->TimeUtil() == nullptr) {
        FIT_LOG_ERROR("Repo is null.");
        return FIT_ERR_FAIL;
    }
    uint64_t curTimeStamp;
    FitCode ret = secureAccess_->TimeUtil()->GetCurrentTimeMs(curTimeStamp);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Get current time failed, error is %d.", ret);
        return ret;
    }

    vector<AuthTokenRole> authTokenRoles = secureAccess_->TokenRoleRepo()->QueryAll();
    vector<string> timeoutTokens;
    for (const auto& authTokenRole : authTokenRoles) {
        if (authTokenRole.IsTimeout(curTimeStamp)) {
            timeoutTokens.emplace_back(authTokenRole.token);
        }
    }
    return secureAccess_->TokenRoleRepo()->Remove(timeoutTokens);
}
}