/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/9/3
 * Notes:       :
 */
#ifndef INIT_MODULE_H
#define INIT_MODULE_H

#include <cstdint>

namespace Fit {
class InitModule {
public:
    virtual ~InitModule() = default;
    virtual int32_t Init() = 0;
    virtual int32_t Finit() = 0;
};
}
#endif // _INIT_MODULE_H_