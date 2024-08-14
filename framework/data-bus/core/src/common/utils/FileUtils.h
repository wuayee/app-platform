/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description: Utility method set for file system operations.
 */

#ifndef FILE_UTILS_H
#define FILE_UTILS_H

#include <sys/stat.h>
#include <cstring>
#include <dirent.h>
#include <unistd.h>
#include <pwd.h>
#include <fstream>

#include "log/Logger.h"

namespace DataBus {
namespace Common {
namespace FileUtils {

static void CreateDirectory(const std::string &directory)
{
    size_t pos = 0;
    std::string dir = directory;
    const char delimiter = '/';
    if (dir.back() != delimiter) {
        dir.push_back(delimiter);
    }
    while ((pos = dir.find_first_of(delimiter, pos + 1)) != std::string::npos) {
        dir[pos] = '\0';
        /*
         * S_IRWXU: 允许文件路径所有者阅读、编写、执行它。
         */
        if (mkdir(dir.c_str(), S_IRWXU) == -1) {
            logger.Debug("Failed to create the directory {}: {}", dir.c_str(), strerror(errno));
        }
        dir[pos] = delimiter;
    }
}

static bool RemoveDirectory(const std::string &directory)
{
    DIR *dp;
    if ((dp = opendir(directory.data())) != nullptr) {
        struct dirent *dirp;
        while ((dirp = readdir(dp)) != nullptr) {
            // 忽略 "." 和 ".." 目录。
            if (strcmp(".", dirp->d_name) == 0 || strcmp("..", dirp->d_name) == 0) {
                continue;
            }
            // 如果是目录，则递归调用; 否则，删除文件。
            if (dirp->d_type == DT_DIR) {
                const std::string subDirectories = (directory + "/" + dirp->d_name);
                RemoveDirectory(subDirectories);
            } else if (remove((directory + "/" + dirp->d_name).data()) != 0) {
                logger.Debug("Failed to remove the file {}: {}",
                             directory + "/" + dirp->d_name, strerror(errno));
                return false;
            }
        }
        closedir(dp);
    }
    if (rmdir(directory.data()) == -1) {
        logger.Debug("Failed to remove the directory {}: {}", directory, strerror(errno));
        return false;
    }
    return true;
}

inline void CreateFileIfNotExists(const std::string &filePath)
{
    // 提取目录路径
    std::string directory = filePath.substr(0, filePath.find_last_of('/'));
    // 确保目录存在
    CreateDirectory(directory);
    // 检查日志文件是否存在
    std::ifstream fileCheck(filePath);
    if (!fileCheck.good()) {
        // 如果不存在，创建新文件并关闭
        logger.Debug("{} does not exist. A new one is being created", filePath);
        std::ofstream(filePath).close();
        // 设置文件权限为 0600 (rw-------)，允许文件路径所有者阅读、编写它。
        chmod(filePath.c_str(), 0600);
        return;
    }
    fileCheck.close();
}

inline std::string GetDataBusDirectory()
{
    // 获取有效用户ID
    uid_t uid = geteuid();
    // 根据用户ID获取用户信息
    struct passwd *pw = getpwuid(uid);
    if (!pw) {
        logger.Error("[FileUtils] Failed to get the username");
        return {};
    }
    const std::string DATABUS_PATH_PREFIX = "/tmp/";
    const std::string DATABUS_PATH_SUFFIX = "/databus/";
    return DATABUS_PATH_PREFIX + pw->pw_name + DATABUS_PATH_SUFFIX;
}
} // namespace FileUtils
} // namespace Common
} // namespace DataBus
#endif // FILE_UTILS_H
