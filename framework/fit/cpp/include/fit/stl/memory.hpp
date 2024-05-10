/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 *
 * Description  : Adapts memory operations.
 * Author       : songyongtan 00558940
 * Date         : 2022/03/15
 */

#ifndef FIT_MEMORY_HPP
#define FIT_MEMORY_HPP

#include <memory>

namespace Fit {
template <typename T, typename D = std::default_delete<T>>
using unique_ptr = std::unique_ptr<T, D>;
#if __cplusplus >= 201304L
using std::make_unique;
#else
template<typename T, typename... Args>
inline unique_ptr<T> make_unique(Args&& ... args)
{
    return unique_ptr<T>(new T(std::forward<Args>(args)...));
}
#endif

using std::move;
using std::forward;
using std::make_shared;
using std::shared_ptr;
}

#endif // FIT_MEMORY_HPP
