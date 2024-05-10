/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  : meta data package builder
 * Author       : songyongtan
 * Create       : 2020/11/13 17:12
 * Notes:       :
 */

#ifndef FIT_META_PACKAGE_BUILDER_H
#define FIT_META_PACKAGE_BUILDER_H

#include <fit/stl/string.hpp>
#include "fit_version.h"
#include "fit_meta_data.h"

class fit_meta_package_builder final {
public:
    static Fit::string build(const fit_meta_data &meta);
};

#endif // FIT_META_PACKAGE_BUILDER_H
