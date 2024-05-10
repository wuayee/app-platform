/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide query configuration service from registry server.
 * Author       : w00561424
 * Date         : 2023/08/31
 * Notes:       :
 */
#ifndef CONFIGURATION_SERVICE_FOR_REGISTRY_SERVER_H
#define CONFIGURATION_SERVICE_FOR_REGISTRY_SERVER_H
#include <configuration_service.h>
#include <configuration_repo.h>
#include <fit/stl/mutex.hpp>
#include <fit/stl/unordered_set.hpp>
#include <fit/internal/util/thread/fit_timer.h>
#include <configuration_service_spi.h>
namespace Fit {
namespace Configuration {
class ConfigurationServiceForRegistryServer : public ConfigurationService {
public:
    explicit ConfigurationServiceForRegistryServer(ConfigurationRepoPtr repo,
        const Fit::string& environment, ConfigurationServiceSpiPtr spi);
    ~ConfigurationServiceForRegistryServer();
    static Fit::string Type();
    int32_t GetGenericableConfig(const Fit::string &genericId, GenericableConfiguration &genericable) override;
    GenericConfigPtr GetGenericableConfigPtr(const Fit::string &genericId) const override;
private:
    int32_t UpdateConfig(const Fit::vector<Fit::string> &genericId) const;
    void UpdateConfig();
    int32_t InsertGenericableId(const Fit::string& genericableId) const;
private:
    mutable Fit::shared_mutex sharedMutex_ {};
    ConfigurationRepoPtr repo_ {};
    mutable Fit::unordered_set<Fit::string> queriedGenericableId_ {};
    std::thread updateConfigExec_ {};
    std::shared_ptr<Fit::timer> timer_ {};
    Fit::timer::timer_handle_t updateConfigIntervalHandle_ {};
    Fit::string environment_ {};
    ConfigurationServiceSpiPtr spi_ {};
};
}
}
#endif
