/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide config info manager from configuration center.
 * Author       : w00561424
 * Date         : 2023/08/26
 * Notes:       :
 */
#ifndef CONFIGURATION_SERVICE_FOR_CONFIG_CENTER_H
#define CONFIGURATION_SERVICE_FOR_CONFIG_CENTER_H
#include <configuration_service.h>
#include <configuration_client.h>
#include <genericable_configuration_parser.hpp>
#include <configuration_repo.h>
#include <fit/stl/map.hpp>
#include <fit/stl/mutex.hpp>
namespace Fit {
namespace Configuration {
class ConfigurationServiceForConfigCenter : public ConfigurationService {
public:
    explicit ConfigurationServiceForConfigCenter(ConfigurationClientPtr client, ConfigurationRepoPtr repo);
    static Fit::string Type();
    int32_t GetGenericableConfig(const Fit::string &genericId, GenericableConfiguration &genericable) override;
    GenericConfigPtr GetGenericableConfigPtr(const Fit::string &genericId) const override;

private:
    void UpdateGenericableConfig(const Fit::string &genericId, const ItemValueSet &items) const;
    int32_t Download(const Fit::string &genericId) const;
private:
    ConfigurationClientPtr client_ {};
    mutable Fit::shared_mutex sharedMutex_ {};
    Fit::map<Fit::string, RoutineFunc> configItemRoutine_ {};
    ConfigurationRepoPtr repo_ {};
};
}
}
#endif