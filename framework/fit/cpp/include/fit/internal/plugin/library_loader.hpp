/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-14 16:37:12
 */

#ifndef LIBRARYLOADER_HPP
#define LIBRARYLOADER_HPP

#include <fit/fit_code.h>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include "plugin_struct.hpp"

#include <memory>

namespace Fit {
namespace Plugin {
class LibraryLoader {
public:
    enum class LoadType {
        LOCAL,
        GLOBAL
    };

    explicit LibraryLoader() = default;
    virtual ~LibraryLoader() = default;
    LibraryLoader(const LibraryLoader&) = delete;
    LibraryLoader(LibraryLoader&&) = delete;
    LibraryLoader& operator=(const LibraryLoader&) = delete;
    LibraryLoader& operator=(LibraryLoader&&) = delete;

    virtual FitCode Load(const Fit::string& libraryName, LibraryInfo& library) = 0;
    virtual FitCode Load(const Fit::vector<Fit::string>& libraries, Fit::vector<LibraryInfo>& infos) = 0;
    virtual FitCode Unload(const LibraryInfo& library) = 0;
    virtual FitCode Unload(const Fit::vector<LibraryInfo>& infos) = 0;
};

std::unique_ptr<LibraryLoader> CreateLibraryLoader(LibraryLoader::LoadType loadType = LibraryLoader::LoadType::LOCAL);
}  // namespace Plugin
}  // namespace Fit

#endif