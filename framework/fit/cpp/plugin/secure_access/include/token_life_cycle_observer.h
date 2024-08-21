/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/25
 */
#ifndef TOKEN_LIFE_CYCLE_OBSERVER_H
#define TOKEN_LIFE_CYCLE_OBSERVER_H
#include <fit/internal/secure_access/token_role_repo.h>
#include <fit/internal/registry/repository/util_by_repo.h>
#include <fit/internal/util/thread/fit_timer.h>
#include <fit/stl/memory.hpp>
namespace Fit {
class SecureAccess;
class TokenLifeCycleObserver {
public:
    TokenLifeCycleObserver(SecureAccess* secureAccess);
    ~TokenLifeCycleObserver();
    int32_t Exec();
    int32_t Init();
    int32_t Uninit();
private:
    SecureAccess* secureAccess_ {nullptr};
    bool exit_ {false};
    std::shared_ptr<Fit::timer> timer_;
    Fit::timer::timer_handle_t taskId_ {Fit::timer::INVALID_TASK_ID};
};
}
#endif