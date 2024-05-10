/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-14 16:47:52
 */

#ifndef LIBRARYLOADERIMPL_HPP
#define LIBRARYLOADERIMPL_HPP

#include "fit/fit_code.h"
#include "fit/internal/plugin/library_loader.hpp"
#include "fit/internal/plugin/plugin_struct.hpp"

namespace Fit {
namespace Plugin {
class LibraryLoaderImpl : public LibraryLoader {
public:
    explicit LibraryLoaderImpl(int32_t loadFlag) : loadFlag_(loadFlag) {}
    ~LibraryLoaderImpl() override = default;

    FitCode Load(const Fit::string& libraryName, LibraryInfo& library) override;
    FitCode Load(const Fit::vector<Fit::string>& libraries, Fit::vector<LibraryInfo>& infos) override;
    FitCode Unload(const LibraryInfo& library) override;
    FitCode Unload(const Fit::vector<LibraryInfo>& infos) override;

protected:
    static Fit::string RealPath(void *handle);

private:
    int32_t loadFlag_;
};
}  // namespace Plugin
}  // namespace Fit
#endif