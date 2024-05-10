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
namespace Fit {
static int FitRandom()
{
    // 安全整改，禁止使用 C 标准库随机数函数
    int r = 0;
    int fd = open("/dev/random", O_RDONLY);
    if (fd > 0) {
        read (fd, &r, sizeof(int));
    }
    close (fd);
    return r;
}
}
#endif // FIT_RANDOM_H