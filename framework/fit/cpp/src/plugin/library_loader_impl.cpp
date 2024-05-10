/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-14 17:11:27
 */

#include "library_loader_impl.hpp"

#include <fit/stl/memory.hpp>
#include <fit/fit_code.h>
#include <fit/internal/fit_filesystem_util.hpp>
#include <fit/fit_log.h>
#include <algorithm>
#include <dlfcn.h>
#include <memory>
#include <link.h>

namespace {
constexpr int32_t DLOPEN_LOCAL_FLAGS = RTLD_NOW | RTLD_LOCAL;
constexpr int32_t DLOPEN_GLOBAL_FLAGS = RTLD_NOW | RTLD_GLOBAL;
constexpr int32_t DLCLOSE_SUCCESS = 0;
}
namespace Fit {
namespace Plugin {
FitCode LibraryLoaderImpl::Load(const string &libraryPath, LibraryInfo &library)
{
    if (libraryPath.empty()) {
        FIT_LOG_ERROR("Failed to load library, library path is empty");
        return FIT_ERR_FAIL;
    }

    auto *handle = dlopen(libraryPath.c_str(), loadFlag_);
    if (handle == nullptr) {
        FIT_LOG_ERROR("Failed to load library %s, %s", libraryPath.c_str(), dlerror());
        return FIT_ERR_FAIL;
    }

    auto libraryName = Fit::Util::Filesystem::GetFileBasename(libraryPath);
    library = LibraryInfo {libraryName, RealPath(handle), handle};

    return FIT_ERR_SUCCESS;
}

FitCode LibraryLoaderImpl::Load(const vector<Fit::string> &libraries, vector<LibraryInfo> &infos)
{
    auto libInfoIter = infos.begin();
    for (const auto &library : libraries) {
        auto ret = Load(library, *libInfoIter);
        if (ret == FIT_ERR_FAIL) {
            break;
        }
        ++libInfoIter;
    }

    if (libInfoIter == infos.end()) {
        return FIT_ERR_SUCCESS;
    }

    std::for_each(infos.begin(), libInfoIter, [this](const LibraryInfo &info) { Unload(info); });
    return FIT_ERR_FAIL;
}

FitCode LibraryLoaderImpl::Unload(const LibraryInfo &library)
{
    if (library.handle == nullptr) {
        return FIT_ERR_FAIL;
    }

    if (dlclose(library.handle) == DLCLOSE_SUCCESS) {
        return FIT_ERR_SUCCESS;
    }

    return FIT_ERR_FAIL;
}
FitCode LibraryLoaderImpl::Unload(const Fit::vector<LibraryInfo> &infos)
{
    std::for_each(infos.begin(), infos.end(), [this](const LibraryInfo &info) { Unload(info); });
    return FIT_ERR_SUCCESS;
}

Fit::string LibraryLoaderImpl::RealPath(void *handle)
{
    struct link_map *map = nullptr;
    dlinfo(handle, RTLD_DI_LINKMAP, &map);
    if (map == nullptr) {
        FIT_LOG_ERROR("Failed to call handle, error %s.", dlerror());
        return "";
    }

    char realPath[PATH_MAX] {};
    char *real = realpath(map->l_name, realPath);
    if (real == nullptr) {
        FIT_LOG_ERROR("Failed to call realpath, path is %s, error %d.", map->l_name, errno);
        return "";
    }
    realPath[sizeof(realPath) - 1] = '\0';

    return realPath;
}

std::unique_ptr<LibraryLoader> CreateLibraryLoader(LibraryLoader::LoadType loadType)
{
    auto loadFlag = DLOPEN_LOCAL_FLAGS;
    if (loadType == LibraryLoader::LoadType::GLOBAL) {
        loadFlag = DLOPEN_GLOBAL_FLAGS;
    }

    return make_unique<LibraryLoaderImpl>(loadFlag);
}
}  // namespace Plugin
}  // namespace Fit