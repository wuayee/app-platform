/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2020-09-08
 * Notes:       :
 */
#ifndef FIT_REGISTRY_REPOSITORY_DECORATOR_H
#define FIT_REGISTRY_REPOSITORY_DECORATOR_H
#include "fit_registry_repository.h"

class FitRegistryRepositoryDecorator : public FitRegistryServiceRepository {
public:
    FitRegistryRepositoryDecorator(FitRegistryServiceRepositoryPtr serviceRepo)
        : serviceRepo_(serviceRepo)
    {}
    virtual ~FitRegistryRepositoryDecorator() = default;
    bool Save(const db_service_info_t &service) override
    {
        if (serviceRepo_ != nullptr) {
            return serviceRepo_->Save(service);
        }
        return false;
    }
    bool Save(const db_service_set &services) override
    {
        if (serviceRepo_ != nullptr) {
            return serviceRepo_->Save(services);
        }
        return false;
    }
    db_service_set Query(const fit_fitable_key_t &key) override
    {
        if (serviceRepo_ != nullptr) {
            return serviceRepo_->Query(key);
        }
        return db_service_set();
    }
    bool Remove(const Fit::fitable_id &fitable, const Fit::fit_address &address) override
    {
        if (serviceRepo_ != nullptr) {
            return serviceRepo_->Remove(fitable, address);
        }
        return false;
    }
    bool Remove(const db_service_set &services) override
    {
        if (serviceRepo_ != nullptr) {
            return serviceRepo_->Remove(services);
        }
        return false;
    }
    db_service_set GetAllServices() override
    {
        if (serviceRepo_ != nullptr) {
            return serviceRepo_->GetAllServices();
        }
        return db_service_set();
    }
    db_service_set Remove(const Fit::fit_address &address) override
    {
        if (serviceRepo_ != nullptr) {
            return serviceRepo_->Remove(address);
        }
        return db_service_set();
    }
private:
    FitRegistryServiceRepositoryPtr serviceRepo_ {nullptr};
};
using FitRegistryRepositoryDecoratorPtr = std::shared_ptr<FitRegistryRepositoryDecorator>;
class FitRegistryRepositoryFactoryWithServiceRepository final {
public:
    static FitRegistryRepositoryDecoratorPtr Create(FitRegistryServiceRepositoryPtr serviceRepository);
};
#endif
