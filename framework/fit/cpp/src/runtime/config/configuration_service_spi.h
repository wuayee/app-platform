/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide configuration service spi.
 * Author       : w00561424
 * Date         : 2023/09/01
 * Notes:       :
 */
#ifndef CONFIGURATION_SERVICE_SPI_H
#define CONFIGURATION_SERVICE_SPI_H
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/unordered_map.hpp>
#include <fit/stl/memory.hpp>
#include <fit/fit_log.h>
#include <configuration_entities.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_query_fitable_metas/1.0.0/cplusplus/query_fitable_metas.hpp>
namespace Fit {
namespace Configuration {
class ConfigurationServiceSpi {
public:
    virtual ~ConfigurationServiceSpi() = default;
    virtual int32_t GetRunningFitables(const Fit::vector<Fit::string>& genericIds,
        const Fit::string& environment, Fit::vector<GenericConfigPtr>& genericableConfigs) = 0;
};

using ConfigurationServiceSpiPtr = Fit::shared_ptr<ConfigurationServiceSpi>;

class ConfigurationServiceSpiImpl : public ConfigurationServiceSpi {
public:
    int32_t GetRunningFitables(const Fit::vector<Fit::string>& genericIds,
        const Fit::string& environment, Fit::vector<GenericConfigPtr>& genericableConfigs) override
    {
        using namespace ::fit::hakuna::kernel::registry::shared;
        Fit::vector<Fit::string> genericableIds;
        Fit::vector<FitableMeta> *fitableMetas {nullptr};

        queryFitableMetas queryFitableMetasInvoker;
        auto ret = queryFitableMetasInvoker(&genericIds, &environment, &fitableMetas);
        if (ret != FIT_ERR_SUCCESS || fitableMetas == nullptr) {
            FIT_LOG_ERROR("Query fitable metas error : %d.", ret);
            return ret;
        }

        Fit::unordered_map<Fit::string, GenericConfigPtr> genericableConfigSet;
        for (auto& fitableMeta : *fitableMetas) {
            auto& genericableConfig = genericableConfigSet[fitableMeta.fitable->genericableId];
            if (genericableConfig == nullptr) {
                genericableConfig = std::make_shared<GenericableConfiguration>();
                genericableConfig->SetGenericId(fitableMeta.fitable->genericableId);
                genericableConfig->SetTags(fitableMeta.tags);
            }
            FitableConfiguration fitableConfig;
            if (genericableConfig->GetFitable(fitableMeta.fitable->fitableId, fitableConfig) == FIT_ERR_NOT_FOUND) {
                fitableConfig.fitableId = std::move(fitableMeta.fitable->fitableId);
                fitableConfig.aliases = std::move(fitableMeta.aliases);
                fitableConfig.extensions = std::move(fitableMeta.extensions);
            }

            fitableConfig.applications.emplace_back(std::move(*(fitableMeta.application)));
            fitableConfig.applicationsFormats.emplace_back(std::move(fitableMeta.formats));

            genericableConfig->SetFitable(fitableConfig);
        }
        for (const auto& genericableConfig : genericableConfigSet) {
            genericableConfigs.emplace_back(std::move(genericableConfig.second));
        }
        return FIT_OK;
    }
};
}
}
#endif