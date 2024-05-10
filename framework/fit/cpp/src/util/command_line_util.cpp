/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : wangyanbo
 * Date         : 2022/7/6
 * Notes:       :
 */

#include <fit/internal/util/command_line_util.hpp>

namespace Fit {
namespace CommandLineUtil {
Fit::vector<Fit::string> ConvertToArgv(const char *optionStr);
Fit::map<Fit::string, Fit::string> GetOpt(const Fit::vector<Fit::string> &argv)
{
    Fit::map<Fit::string, Fit::string> options;
    Fit::string currentKey;
    for (auto &option : argv) {
        constexpr char optionKeyPrefix[] = "--";
        constexpr int32_t optionKeyPrefixLen = sizeof(optionKeyPrefix);

        if (!currentKey.empty()) {
            if (!Fit::StringUtils::StartsWith(option, optionKeyPrefix)) {
                options[std::move(currentKey)] = std::move(option);
                continue;
            }
            currentKey.clear();
        }
        if (Fit::StringUtils::StartsWith(option, optionKeyPrefix)) {
            currentKey = option.substr(optionKeyPrefixLen - 1);
        }
    }

    return options;
}

Fit::map<Fit::string, Fit::string> GetOpt(int32_t argc, char *argv[])
{
    Fit::map<Fit::string, Fit::string> options;
    Fit::vector<Fit::string> argvArr;
    argvArr.reserve(argc);
    for (int32_t i = 0; i < argc; ++i) {
        auto args = ConvertToArgv(argv[i]);
        argvArr.insert(argvArr.end(), args.begin(), args.end());
    }

    return GetOpt(argvArr);
}

Fit::map<Fit::string, Fit::string> GetOpt(const char *optionStr)
{
    return GetOpt(ConvertToArgv(optionStr));
}

bool IsSeparator(const char *optionStr, int32_t i);

Fit::vector<Fit::string> ConvertToArgv(const char *optionStr)
{
    Fit::vector<Fit::string> argv;
    Fit::string currentArg;
    bool inQuoted {false};
    for (int32_t i = 0; optionStr[i] != 0; ++i) {
        if (IsSeparator(optionStr, i) && !inQuoted) {
            if (!currentArg.empty()) {
                argv.push_back(std::move(currentArg));
            }
            continue;
        }
        if (optionStr[i] == '\"') {
            inQuoted = !inQuoted;
        }
        currentArg.push_back(optionStr[i]);
    }
    if (!currentArg.empty()) {
        argv.push_back(std::move(currentArg));
    }

    return argv;
}

bool IsSeparator(const char *optionStr, int32_t i)
{
    return (Fit::StringUtils::IsBlank(optionStr[i]) || optionStr[i] == '=');
}
}
}
