/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 * Description  : plugin
 * Author       : songyongtan
 * Date         : 2021/11/10
 */

#ifndef PLUGIN_ARCHIVE_BUILDER_HPP
#define PLUGIN_ARCHIVE_BUILDER_HPP

#include <fit/internal/plugin/plugin.hpp>
#include <fit/external/runtime/config/config_value.hpp>
#include <fit/external/util/string_utils.hpp>
#include <fit/internal/fit_filesystem_util.hpp>

namespace Fit {
namespace Plugin {
class PluginArchiveBuilder {
public:
    PluginArchiveBuilder &SetConfig(Config::Value &pluginValue)
    {
        pluginValue_ = &pluginValue;
        return *this;
    }

    PluginArchiveBuilder &SetWorkDirectory(Fit::string workerDirectory)
    {
        workerDirectory_ = std::move(workerDirectory);
        return *this;
    }

    PluginArchiveBuilder &SetValidLevelRange(ValidLevelRange levelRange)
    {
        levelRange_ = levelRange;
        return *this;
    }

    PluginArchive Build() const
    {
        auto &plugin = *pluginValue_;
        PluginArchive pluginArchive {.name=plugin["name"].AsString(""),
            .location=plugin["location"].AsString(),
            .startLevel=plugin["level"].AsInt(levelRange_.defaultValue)
        };
        // 开头为/时，为绝对路径
        // 路径中不包含/时，表示通过利用默认库搜索路径查找机制，比如：设置LD_LIBRARY_PATH
        // 其它情况认为是相对路径通过workerdirectory处理
        auto pathCharPos = pluginArchive.location.find('/');
        if (pathCharPos != 0 && pathCharPos != Fit::string::npos) {
            if (!plugin["absolutely"].AsBool(false)) {
                pluginArchive.location = workerDirectory_ + "/" + pluginArchive.location;
            }
        }
        pluginArchive.configFilePath = plugin["config-file"].AsString("");
        pluginArchive.genericablesConfigFilePath = plugin["genericables-config-file"].AsString("");

        return pluginArchive;
    }

    Config::Value *pluginValue_ {};
    Fit::string workerDirectory_;
    ValidLevelRange levelRange_ {};
};
}
}

#endif // PLUGIN_ARCHIVE_BUILDER_HPP
