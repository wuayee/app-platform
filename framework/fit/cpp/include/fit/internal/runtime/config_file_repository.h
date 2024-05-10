/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/06/03
 * Notes:       :
 */

#ifndef CONFIG_FILE_REPOSITORY_H
#define CONFIG_FILE_REPOSITORY_H
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/unordered_map.hpp>

constexpr char* WORK_CONFIG_KEY = "worker_configuration_key";
constexpr char* BROKER_CONFIG_KEY = "broker_configuration_key";

class ConfigFileRepository {
public:
    ConfigFileRepository(const Fit::string& mainConfigPath);
    ~ConfigFileRepository() = default;
    Fit::string GetConfigPath(const Fit::string& configPathKey);
    void SetConfigFilePath(const Fit::string& configPathKey, const Fit::string& configPathVaue);
private:
    int ParseMainConfigFile(const Fit::string& configPath);
private:
    Fit::unordered_map<Fit::string, Fit::string> configFilePath_ {};
};
#endif