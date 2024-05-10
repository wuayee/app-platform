/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/5/14 19:48
 * Notes        :
 */

#ifndef CPPCCONVERTER_HPP
#define CPPCCONVERTER_HPP

#include <fit/fit_define_c.h>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/fit_code.h>
#include <fit/external/util/context/context_api.hpp>
#include <fit/external/util/context/context_c_api.h>
#include <cstring>
#include "securec.h"

namespace Fit {
namespace Framework {
namespace CppCConverter {
template<typename CType, typename CppType>
FitCode ConvertCToCpp(ContextObj ctx, const CType &src, CppType &dst);

template<>
inline FitCode ConvertCToCpp(ContextObj ctx, const Fit_String &src, Fit::string &dst)
{
    dst = Fit::string(src.data, src.size);
    return FIT_OK;
}

template<>
inline FitCode ConvertCToCpp(ContextObj ctx, const Fit_Bytes &src, Fit::bytes &dst)
{
    if (src.size == 0 || src.data == nullptr) {
        return FIT_OK;
    }
    dst = Fit::string(src.data, src.size);
    return FIT_OK;
}

template<>
inline FitCode ConvertCToCpp(ContextObj ctx, const int32_t &src, int32_t &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCToCpp(ContextObj ctx, const uint32_t &src, uint32_t &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCToCpp(ContextObj ctx, const int64_t &src, int64_t &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCToCpp(ContextObj ctx, const uint64_t &src, uint64_t &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCToCpp(ContextObj ctx, const float &src, float &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCToCpp(ContextObj ctx, const double &src, double &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCToCpp(ContextObj ctx, const bool &src, bool &dst)
{
    dst = src;
    return FIT_OK;
}

inline FitCode ConvertCToCpp(ContextObj ctx, const bool& src, Fit::vector<bool>::reference dst)
{
    dst = src;
    return FIT_OK;
}

template<typename CRepeated, typename CppRepeated>
inline FitCode ConvertCToCpp(ContextObj ctx, const CRepeated &src, Fit::vector<CppRepeated> &dst)
{
    dst.resize(src.size);
    for (uint32_t i = 0; i < src.size; i++) {
        ConvertCToCpp(ctx, src.data[i], dst[i]);
    }
    return FIT_OK;
}

// =============================================================================

template<typename CType, typename CppType>
FitCode ConvertCppToC(ContextObj ctx, const CppType &src, CType &dst);

template<>
inline FitCode ConvertCppToC(ContextObj ctx, const Fit::string &src, Fit_String &dst)
{
    if (src.empty()) {
        FIT_STRING_INIT(dst);
    } else {
        dst.size = src.size();
        dst.data = static_cast<char *>(ContextMalloc(ctx, src.size() + 1));
        if (dst.data == nullptr) {
            FIT_LOG_ERROR("Dst data is nullptr.");
            return FIT_ERR_FAIL;
        }
        (void)memcpy_s(dst.data, dst.size, src.data(), src.size());
        dst.data[dst.size] = '\0';
    }
    return FIT_OK;
}

template<>
inline FitCode ConvertCppToC(ContextObj ctx, const Fit::bytes &src, Fit_Bytes &dst)
{
    if (src.empty()) {
        FIT_BYTES_INIT(dst);
    } else {
        dst.size = src.size();
        dst.data = static_cast<char *>(ContextMalloc(ctx, src.size()));
        if (dst.data == nullptr) {
            FIT_LOG_ERROR("Dst data is nullptr.");
            return FIT_ERR_FAIL;
        }
        (void)memcpy_s(dst.data, dst.size, src.data(), src.size());
    }
    return FIT_OK;
}

template<>
inline FitCode ConvertCppToC(ContextObj ctx, const int32_t &src, int32_t &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCppToC(ContextObj ctx, const uint32_t &src, uint32_t &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCppToC(ContextObj ctx, const int64_t &src, int64_t &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCppToC(ContextObj ctx, const uint64_t &src, uint64_t &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCppToC(ContextObj ctx, const float &src, float &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCppToC(ContextObj ctx, const double &src, double &dst)
{
    dst = src;
    return FIT_OK;
}

template<>
inline FitCode ConvertCppToC(ContextObj ctx, const bool &src, bool &dst)
{
    dst = src;
    return FIT_OK;
}

template<typename CppRepeated, typename CRepeated>
inline FitCode ConvertCppToC(ContextObj ctx, const Fit::vector<CppRepeated> &src, CRepeated &dst)
{
    if (src.empty()) {
        FIT_ARRAY_INIT(dst);
        return FIT_OK;
    }

    dst.size = src.size();
    using T = typename std::remove_pointer<decltype(dst.data)>::type;
    dst.data = (T*)ContextMalloc(ctx, dst.size * sizeof(T));
    for (size_t i = 0; i < src.size(); i++) {
        ConvertCppToC(ctx, src[i], dst.data[i]);
    }
    return FIT_OK;
}
}
}
}

#endif // JSONCONVERTER_HPP
