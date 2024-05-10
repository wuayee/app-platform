/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/5/31
 * Notes:       :
 */

#ifndef REGISTRY_COMMON_CONVERTER_HPP
#define REGISTRY_COMMON_CONVERTER_HPP

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Worker/1.0.0/cplusplus/Worker.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/internal/registry/fit_registry_entities.h>

namespace Fit {
class RegistryCommonConverter {
public:
    static fitable_id Convert(const ::fit::registry::Fitable& fitable)
    {
        fitable_id result{};
        result.generic_id = fitable.genericId;
        result.fitable_id = fitable.fitId;
        result.fitable_version = fitable.fitVersion;
        result.generic_version = fitable.genericVersion;

        return result;
    }
    static ::fit::registry::Fitable Convert(const fitable_id& fitable)
    {
        ::fit::registry::Fitable result{};
        result.genericId = fitable.generic_id;
        result.fitId = fitable.fitable_id;
        result.fitVersion = fitable.fitable_version;

        return result;
    }
    static ::fit::hakuna::kernel::shared::Fitable Convert(const Fit::RegistryInfo::Fitable& fitable)
    {
        ::fit::hakuna::kernel::shared::Fitable result{};
        result.genericableId = fitable.genericableId;
        result.genericableVersion = fitable.genericableVersion;
        result.fitableId = fitable.fitableId;
        result.fitableVersion = fitable.fitableVersion;
        return result;
    }
    static ::fit::registry::Address Convert(const fit_address& address)
    {
        ::fit::registry::Address result{};
        result.id = address.id;
        result.host = address.ip;
        result.port = address.port;
        result.formats.reserve(address.formats.size());
        for (auto& item : address.formats) {
            result.formats.push_back(item);
        }
        result.environment = address.environment;
        result.protocol = static_cast<int32_t>(address.protocol);

        return result;
    }
    static fit_address Convert(const ::fit::registry::Address& address)
    {
        fit_address result{};
        result.id = address.id;
        result.ip = address.host;
        result.port = address.port;
        result.formats.reserve(address.formats.size());
        for (auto& item : address.formats) {
            result.formats.push_back(static_cast<fit_format_type>(item));
        }
        result.environment = address.environment;
        result.protocol = static_cast<fit_protocol_type>(address.protocol);

        return result;
    }
    static fitable_id Convert(const ::fit::hakuna::kernel::shared::Fitable& fitable)
    {
        fitable_id result{};
        result.generic_id = fitable.genericableId;
        result.fitable_id = fitable.fitableId;
        result.fitable_version = fitable.fitableVersion;
        result.generic_version = fitable.genericableVersion;

        return result;
    }
    static Fit::vector<fit_address> ConvertToFitAddresses(const ::fit::hakuna::kernel::registry::shared::Worker& worker)
    {
        Fit::vector<fit_address> result;
        fit_address address{};
        address.id = worker.id;
        address.extensions = worker.extensions;
        for (const auto& it : worker.addresses) {
            address.ip = it.host;
            address.environment = worker.environment;
            for (const auto& endpoint : it.endpoints) {
                address.port = endpoint.port;
                address.protocol = static_cast<fit_protocol_type>(endpoint.protocol);
                result.emplace_back(address);
            }
        }

        return result;
    }
    static void ConvertToWorker(const RegistryInfo::Worker& worker, const vector<RegistryInfo::Address>& addresses,
        ::fit::hakuna::kernel::registry::shared::Worker& result)
    {
        result.id = worker.workerId;
        result.environment = worker.environment;
        result.version = worker.version;
        result.extensions = move(worker.extensions);
        result.expire = static_cast<int32_t>(worker.expire);
        map<string, vector<const RegistryInfo::Address*>> hostGroup;
        for (auto& srcAddress : addresses) {
            hostGroup[srcAddress.host].emplace_back(&srcAddress);
        }
        result.addresses.reserve(hostGroup.size());
        for (auto& group : hostGroup) {
            result.addresses.emplace_back();
            auto& address = result.addresses.back();
            address.host = group.first;
            address.endpoints.reserve(group.second.size());
            for (auto& srcEndpoint : group.second) {
                address.endpoints.emplace_back();
                auto& endpoint = address.endpoints.back();
                endpoint.port = static_cast<int32_t>(srcEndpoint->port);
                endpoint.protocol = static_cast<int32_t>(srcEndpoint->protocol);
            }
        }
    }
    static RegistryInfo::FlatAddress ConvertToFlatAddress(
        const fit_address& address, const RegistryInfo::Application& appId)
    {
        RegistryInfo::FlatAddress result{};
        result.workerId = address.id;
        result.host = address.ip;
        result.port = address.port;
        result.formats = address.formats;
        result.environment = address.environment;
        result.protocol = address.protocol;
        result.extensions = address.extensions;

        return result;
    }
    static Context::TargetAddress ConvertToTargetAddress(const RegistryInfo::FlatAddress& src)
    {
        Context::TargetAddress targetAddress {};
        targetAddress.host = src.host;
        targetAddress.port = static_cast<int32_t>(src.port);
        targetAddress.protocol = static_cast<int32_t>(src.protocol);
        targetAddress.formats.reserve(src.formats.size());
        for (auto format : src.formats) {
            targetAddress.formats.emplace_back(format);
        }
        targetAddress.workerId = src.workerId;
        targetAddress.extensions = src.extensions;

        return targetAddress;
    }

    static ::fit::hakuna::kernel::registry::shared::Address ConvertToNewAddress(const fit_address& address)
    {
        ::fit::hakuna::kernel::registry::shared::Address result{};
        result.host = address.ip;
        ::fit::hakuna::kernel::registry::shared::Endpoint endpoint;
        endpoint.port = address.port;
        endpoint.protocol = static_cast<int32_t>(address.protocol);
        result.endpoints.emplace_back(endpoint);

        return result;
    }
    static Fit::RegistryInfo::Application Convert(const ::fit::hakuna::kernel::registry::shared::Application& src)
    {
        Fit::RegistryInfo::Application result;
        result.name = src.name;
        result.nameVersion = src.nameVersion;
        return result;
    }
    static ::fit::hakuna::kernel::registry::shared::Application Convert(const Fit::RegistryInfo::Application& src)
    {
        ::fit::hakuna::kernel::registry::shared::Application result;
        result.name = src.name;
        result.nameVersion = src.nameVersion;
        return result;
    }
    static Fit::RegistryInfo::ApplicationMeta ConvertApplicationMeta(
        const ::fit::hakuna::kernel::registry::shared::Application& src)
    {
        Fit::RegistryInfo::ApplicationMeta result;
        result.id = Convert(src);
        result.extensions = src.extensions;
        return result;
    }
    static ::fit::hakuna::kernel::registry::shared::Application ConvertApplicationMeta(
        const Fit::RegistryInfo::ApplicationMeta& src)
    {
        ::fit::hakuna::kernel::registry::shared::Application result = Convert(src.id);
        result.extensions = src.extensions;
        return result;
    }
};
}
#endif // REGISTRY_COMMON_CONVERTER_HPP
