/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/15
 * Notes:       :
 */

#ifndef FIT_CODE_HELPER_H
#define FIT_CODE_HELPER_H

#include "fit_code.h"

#ifdef __cplusplus
extern "C" {
#endif
inline bool IsNetErrorCode(FitCode code)
{
    return code >= FIT_ERR_NET_BEGIN && code <= FIT_ERR_NET_END;
}
#ifdef __cplusplus
}
#endif
#endif // FIT_CODE_HELPER_H
