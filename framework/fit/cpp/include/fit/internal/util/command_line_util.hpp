/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/8/2
 * Notes:       :
 */

#ifndef COMMAND_LINE_UTIL_HPP
#define COMMAND_LINE_UTIL_HPP

#include <fit/stl/map.hpp>
#include <fit/stl/string.hpp>
#include <fit/external/util/string_utils.hpp>

namespace Fit {
namespace CommandLineUtil {
Fit::map<Fit::string, Fit::string> GetOpt(const Fit::vector<Fit::string> &argv);
Fit::map<Fit::string, Fit::string> GetOpt(int32_t argc, char *argv[]);
Fit::map<Fit::string, Fit::string> GetOpt(const char *optionStr);
bool IsSeparator(const char *optionStr, int32_t i);
Fit::vector<Fit::string> ConvertToArgv(const char *optionStr);
}
}

#endif // COMMAND_LINE_UTIL_HPP
