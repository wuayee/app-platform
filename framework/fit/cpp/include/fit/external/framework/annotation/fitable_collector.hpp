/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/12
 * Notes:       :
 */

#ifndef FITABLE_COLLECTOR_HPP
#define FITABLE_COLLECTOR_HPP

#include <fit/stl/vector.hpp>
#include "fitable_detail.hpp"

namespace Fit {
namespace Framework {
namespace Annotation {
class __attribute__ ((visibility ("default"))) FitableCollector {
public:
    static void Register(const FitableDetailPtrList &annotations);

    static void UnRegister(const FitableDetailPtrList &annotations);
};
}
}
}
#endif // FITABLE_COLLECTOR_HPP
