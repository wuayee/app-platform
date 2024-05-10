/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : cli element
 * Author       : w00561424
 * Date         : 2022/7/15
 * Notes:       :
 */

#ifndef FIT_CLI_ELEMENT_HPP
#define FIT_CLI_ELEMENT_HPP

#include <fit/internal/runtime/runtime_element.hpp>
#include <fit/fit_code.h>

namespace Fit {
class CliElement : public RuntimeElementBase {
public:
    CliElement();
    ~CliElement() override;

    bool Start() override;
    bool Stop() override;
};
}

#endif // FIT_CLI_ELEMENT_HPP
