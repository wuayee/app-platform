/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 *
 * Description  : mock secure function
 * Author       : wangpanbo
 * Date         : 2022/08/24
 */
#include <fit/securec.h>
#include <cstdio>
#include <cstring>
#include <cstdarg>
#include <algorithm>
#include <fit/stl/vector.hpp>

/*
paramOut: dest,目的缓冲区,非空
paramIn: destMax,目的缓冲区总大小（包括'\0'）,> 0
paramIn: src,源缓冲区,非空
paramIn: count,要设置目的缓冲区的字符个数（不包括'\0'）,>= 0
description:
1. destMax等于0，返回-1
2. count大于destMax，返回-1
3. dest为空 ，返回-1
4. src为空 ，返回-1
*/
errno_t memcpy_s(void *dest, size_t destMax, const void* src, size_t count)
{
    if (destMax == 0 || count > destMax || dest == nullptr || src == nullptr) {
        return -1;
    }
    memcpy(dest, src, count);
    return 0;
}

/*
paramOut: dest,目的缓冲区,非空
paramIn: destMax,目的缓冲区总大小（包括'\0'）,>= 0
paramIn: src,要设置缓冲区字符，[0,255]，如果超出取值范围则取c中的低8位数值
paramIn: count,要设置目的缓冲区的字符个数（不包括'\0'）,>= 0
description:
1. count大于destMax，返回-1
2. dest为空 且 destMax不等于0，-1
*/
errno_t memset_s(void *dest, size_t destMax, int src, size_t count)
{
    if (count > destMax || (dest == nullptr && destMax != 0)) {
        return -1;
    }
    memset(dest, src, count);
    return 0;
}

/*
paramOut: strDest,目的缓冲区,非空
paramIn: destMax,目的缓冲区总大小（包括'\0'）,> 0
paramIn: count,要输出到目的缓冲区的格式化字符个数（不包括'\0'）,>= 0
paramIn: format,格式化控制字符串,非空
paramIn: arglist,指向参数列表(va_list)的指针,非空
description:
1. strDest为空, 返回 -1
2. format为空, 返回 -1
3. destMax等于0, 返回 -1
4. count >= destMax, 返回 -1
5. count < length, strDest[count] = '\0', 返回-1
*/
int vsnprintf_s(char *strDest, size_t destMax, size_t count, const char *format, va_list argList)
{
    if (strDest == nullptr || format == nullptr || destMax == 0 || count >= destMax) {
        return -1;
    }
    va_list copy;
    va_copy(copy, argList);
    int length = vsnprintf(nullptr, 0, format, copy);
    va_end(copy);
    // length == -1 写入失败
    // length 返回本应写入的总字节数
    if (length < 0) {
        return -1;
    }

    int ret = -1;
    // vsnprintf最多只能copy count个字符，最后一个填充\0
    ret = vsnprintf(strDest, count + 1, format, argList);
    if (static_cast<int>(count) < length) {
        ret = -1;
    }
    return ret;
}