/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : init disabled fitables
 * Author       : songyongtan
 * Date         : 2022/5/13
 * Notes:       :
 */

#ifndef FIT_DISABLE_FITABLES_ELE_HPP
#define FIT_DISABLE_FITABLES_ELE_HPP

#include <fit/internal/runtime/runtime_element.hpp>
#include <fit/fit_code.h>

namespace Fit {
class DisableFitablesElement : public RuntimeElementBase {
public:
    DisableFitablesElement();
    ~DisableFitablesElement() override;

    bool Start() override;
    bool Stop() override;
};
}

#endif // FIT_DISABLE_FITABLES_ELE_HPP
