/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/4/1
 * Notes:       :
 */

#ifndef FIT_NUMBER_UTILS_HPP
#define FIT_NUMBER_UTILS_HPP

#include <sstream>

namespace Fit {
/**
 * 数字相关操作工具类
 */
class NumberUtils final {
public:
    NumberUtils() = delete;
    ~NumberUtils() = delete;

    /**
     * 尝试将字符串转换为类型T
     *
     * @param data 待转换的字符串
     * @param result 存放转换结果
     * @return true成功，false失败
     */
    template<typename T>
    static bool TryParse(const char *data, T &result)
    {
        return ((std::istringstream(data) >> result >> std::ws).eof());
    }

    template<typename T>
    static bool TryParse(const Fit::string &data, T &result)
    {
        return TryParse(data.c_str(), result);
    }
};
}
#endif // FIT_NUMBER_UTILS_HPP
