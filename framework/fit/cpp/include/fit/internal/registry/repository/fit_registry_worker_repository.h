/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/8/31 22:32
 * Notes:       :
 */


#ifndef FIT_REGISTRY_WORKER_REPOSITORY_H
#define FIT_REGISTRY_WORKER_REPOSITORY_H

#include "../fit_registry_entities.h"

class fit_registry_worker_repository {
public:
    fit_registry_worker_repository()= default;
    virtual ~fit_registry_worker_repository()= default;

    virtual int32_t remove(const Fit::fit_address &address) = 0;

    virtual int32_t save(const db_worker_info_t &info) = 0;

    virtual db_worker_set get_all() = 0;

    virtual int32_t get(const Fit::fit_address &address, db_worker_info_t &result) = 0;

    virtual int32_t update_status(const Fit::fit_address &address, bool is_online) = 0;

    virtual db_worker_set get(const Fit::string &id) = 0;
};

using fit_registry_worker_repository_ptr = std::shared_ptr<fit_registry_worker_repository>;

class fit_registry_worker_repository_factory final {
public:
    static fit_registry_worker_repository_ptr create();
    static fit_registry_worker_repository_ptr create(fit_registry_worker_repository_ptr workerRepository);
};

#endif // FIT_REGISTRY_WORKER_REPOSITORY_H
