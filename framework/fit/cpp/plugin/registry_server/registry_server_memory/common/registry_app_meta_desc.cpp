/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : app meta description
 * Author       : songyongtan
 * Create       : 2023-09-25
 * Notes:       :
 */

#include "registry_app_meta_desc.h"

namespace Fit {
RegistryAppMetaDesc::RegistryAppMetaDesc(const Application& application) : application_(application) {}
const string& RegistryAppMetaDesc::GetName() const
{
    return application_.name;
}
const string& RegistryAppMetaDesc::GetVersion() const
{
    return application_.nameVersion;
}
bool RegistryAppMetaDesc::GetClusterDomain(string& result) const
{
    return GetValue("cluster.domain", result);
}
bool RegistryAppMetaDesc::GetClusterContextPath(string& result) const
{
    return GetValue("cluster.context-path", result);
}
bool RegistryAppMetaDesc::GetClusterHttpPort(int32_t& result) const
{
    return GetValue("cluster.http.port", result);
}
bool RegistryAppMetaDesc::GetClusterHttpsPort(int32_t& result) const
{
    return GetValue("cluster.https.port", result);
}
bool RegistryAppMetaDesc::GetClusterGrpcPort(int32_t& result) const
{
    return GetValue("cluster.grpc.port", result);
}
bool RegistryAppMetaDesc::GetValue(const char* key, int32_t& result) const
{
    auto iter = application_.extensions.find(key);
    if (iter == application_.extensions.end()) {
        return false;
    }
    result = atoi(iter->second.c_str());
    return true;
}
bool RegistryAppMetaDesc::GetValue(const char* key, string& result) const
{
    auto iter = application_.extensions.find(key);
    if (iter == application_.extensions.end()) {
        return false;
    }
    result = iter->second;
    return true;
}
}