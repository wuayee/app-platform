/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : cli element
 * Author       : w00561424
 * Date         : 2022/7/15
 * Notes:       :
 */
#include "cli_element.hpp"
#include <fit/internal/runtime/runtime.hpp>
using namespace Fit;

CliElement::CliElement() : RuntimeElementBase("CliElement")
{
}

CliElement::~CliElement()
{
}

bool CliElement::Start()
{
    return true;
}

bool CliElement::Stop()
{
    return true;
}