/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/14
 * Notes:       :
 */

#ifndef FITABLECOLLECTORINNER_HPP
#define FITABLECOLLECTORINNER_HPP

#include <functional>
#include <fit/external/framework/annotation/fitable_detail.hpp>

namespace Fit {
namespace Framework {
namespace Annotation {
class FitableDetailReceiver {
public:
    std::function<void(const FitableDetailPtrList &)> Register;
    std::function<void(const FitableDetailPtrList &)> UnRegister;
};

__attribute__ ((visibility ("default"))) FitableDetailPtrList PopFitableDetailCache();
// @return old receiver
__attribute__ ((visibility ("default"))) FitableDetailReceiver *FitableDetailFlowTo(FitableDetailReceiver *target);
}
}
}
#endif // FITABLECOLLECTORINNER_HPP
