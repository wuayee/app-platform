/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : provide random util
 * Author       : l00839724
 * Date:        : 2024/03/21
 */
#ifndef FIT_RANDOM_H
#define FIT_RANDOM_H
#include <unistd.h>
#include <fcntl.h>
#include <type_traits>
namespace Fit {
// 通用模板函数 FitRandom
template<typename T>
inline T FitRandom() {
    static_assert(std::is_integral<T>::value, "FitRandom only supports integer types");
    T r = 0;
    int fd = open("/dev/random", O_RDONLY);
    if (fd > 0) {
        read(fd, &r, sizeof(T));
        close(fd);
    }
    return r;
}
}
#endif // FIT_RANDOM_H