/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024//
 */
#ifndef SECURE_ACCESS_PERMISSION_H
#define SECURE_ACCESS_PERMISSION_H
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
namespace Fit {
struct Permission {
public:
    Permission() = default;
    Permission(const fit::hakuna::kernel::shared::Fitable& fitableIn) : fitable(fitableIn) {}
    Permission(const Permission& permission) : fitable(permission.fitable){}
    Permission& operator=(const Permission& permission)
    {
        if (this == &permission) {
            return *this;
        }
        this->fitable = permission.fitable;
        return *this;
    }

    bool operator==(const Permission& permission) const
    {
        return (this->fitable.fitableId == permission.fitable.fitableId) &&
            (this->fitable.genericableId == permission.fitable.genericableId) &&
            (this->fitable.genericableVersion == permission.fitable.genericableVersion);
    }
public:
    fit::hakuna::kernel::shared::Fitable fitable;
};
}
#endif