/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-04-15 18:47:23
 */

#ifndef LIBRARY_LOADER_MOCK_HPP
#define LIBRARY_LOADER_MOCK_HPP

#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <fit/internal/plugin/library_loader.hpp>
#include <fit/internal/plugin/plugin_struct.hpp>

#include <gmock/gmock.h>

namespace Fit {
namespace Plugin {
class LibraryLoaderMock : public LibraryLoader {
public:
    MOCK_METHOD2(Load, FitCode(const Fit::string& libraryName, LibraryInfo& info));
    MOCK_METHOD2(Load, FitCode(const Fit::vector<Fit::string>& libs, vector<LibraryInfo>& infos));
    MOCK_METHOD1(Unload, FitCode(const LibraryInfo& libInfo));
    MOCK_METHOD1(Unload, FitCode(const Fit::vector<LibraryInfo>& libInfos));
};
}  // namespace Plugin
}  // namespace Fit

#endif