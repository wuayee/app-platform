/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2020-10-26 18:50:01
 */

#ifndef FIT_FILESYSTEM_UTIL_H
#define FIT_FILESYSTEM_UTIL_H

#include <fit/stl/string.hpp>

#include <climits>
#include <libgen.h>
#include <unistd.h>
#include <linux/limits.h>

namespace Fit {
namespace Util {
namespace Filesystem {
inline Fit::string JoinPath(const Fit::string& dir_name, const Fit::string& filename)
{
    if (dir_name.back() == '/') {
        return dir_name + filename;
    }
    return dir_name + "/" + filename;
}

inline bool FileExists(const Fit::string& filename)
{
    return access(filename.c_str(), R_OK) == 0;
}

inline Fit::string GetFileDirectory(const Fit::string& file_path)
{
    char dir_path[PATH_MAX]{0};

    auto* is_valid = realpath(dirname((char*)file_path.c_str()), dir_path);
    if (is_valid != nullptr) {
        return dir_path;
    }
    return "";
}

// input: /root/lib/mylib.so
// output: mylib.so
inline Fit::string GetFileBasename(const Fit::string& filePath)
{
    auto* base = basename((char*)filePath.c_str());
    if (base == nullptr) {
        return "";
    }

    return base;
}
inline string GetCurrentExeDir()
{
    char exePath[PATH_MAX]{0};
    if (realpath("/proc/self/exe", exePath) == nullptr) {
        return "";
    }
    return dirname(exePath);
}
}  // namespace Filesystem
}  // namespace Util
}  // namespace Fit
#endif
