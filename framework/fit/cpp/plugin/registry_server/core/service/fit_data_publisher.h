/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Create       : 2020/10/13 15:01
 * Notes:       :
 */

#ifndef FIT_DATA_PUBLISHER_H
#define FIT_DATA_PUBLISHER_H

#include "fit_publisher.h"

namespace Fit {
namespace Registry {
enum class fit_data_changed_type {
    ADD,
    REMOVE,
    MODIFY
};

template<typename __DATA_TYPE>
struct fit_changed_data_t {
    __DATA_TYPE data;
    fit_data_changed_type type;
};

template<typename __DATA_TYPE>
using fit_data_changed_publisher = fit_data_publisher<fit_changed_data_t<__DATA_TYPE>>;
}
}

#endif // FIT_DATA_PUBLISHER_H
