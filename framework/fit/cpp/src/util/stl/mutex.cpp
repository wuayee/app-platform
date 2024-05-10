/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/3/4
 * Notes:       :
 */

#include <fit/stl/mutex.hpp>
#include <fit/stl/except.hpp>

namespace Fit {
void throw_bad_lock()
{
#ifdef __cpp_exceptions
    throw bad_lock();
#endif
}
}