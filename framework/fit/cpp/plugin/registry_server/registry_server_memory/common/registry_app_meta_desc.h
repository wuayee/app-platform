/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : app meta description
 * Author       : songyongtan
 * Create       : 2023-09-25
 * Notes:       :
 */

#ifndef FIT_REGISTRY_APP_META_DESC_H
#define FIT_REGISTRY_APP_META_DESC_H

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>

namespace Fit {
class RegistryAppMetaDesc {
public:
    using Application = fit::hakuna::kernel::registry::shared::Application;
    explicit RegistryAppMetaDesc(const Application& application);
    const string& GetName() const;
    const string& GetVersion() const;
    bool GetClusterDomain(string& result) const;
    bool GetClusterContextPath(string& result) const;
    bool GetClusterHttpPort(int32_t& result) const;
    bool GetClusterHttpsPort(int32_t& result) const;
    bool GetClusterGrpcPort(int32_t& result) const;

private:
    bool GetValue(const char* key, int32_t& result) const;
    bool GetValue(const char* key, string& result) const;
    const Application& application_;
};
}

#endif