/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/6/24 14:23
 */
#ifndef PARAM_JSON_FORMATTER_SERVICE_HPP
#define PARAM_JSON_FORMATTER_SERVICE_HPP

#include <fit/stl/memory.hpp>
#include <fit/internal/runtime/runtime_element.hpp>
#include "formatter_service.hpp"

namespace Fit {
namespace Framework {
namespace ParamJsonFormatter {
/**
 * 路由时构建参数信息
 */
class ParamJsonFormatterService : public RuntimeElementBase {
public:
    ParamJsonFormatterService() : RuntimeElementBase("paramRouterFormatter") {};
    ~ParamJsonFormatterService() override = default;

    /**
     * 序列化接口所有参数
     * @param ctx 调用上下文
     * @param genericID 接口id
     * @param args 类型擦除后的参数
     * @param result 序列化结果
     * @return FIT_OK-成功， 其它失败
     */
    virtual FitCode SerializeParamToJson(ContextObj ctx,
        const Fit::string& genericID,
        const Arguments& args,
        Fit::string& result) = 0;

    /**
     * 序列化指定位置的参数
     * @param ctx 调用上下文
     * @param genericID 接口id
     * @param idx 参数位置，从0开始
     * @param arg 类型擦除后对应位置的参数
     * @param result 序列化结果
     * @return FIT_OK-成功， 其它失败
     */
    virtual FitCode SerializeIndexParamToJson(ContextObj ctx,
        const Fit::string& genericID,
        int32_t idx,
        const Argument& arg,
        Fit::string& result) = 0;
};

using ParamJsonFormatterPtr = std::shared_ptr<ParamJsonFormatterService>;
unique_ptr<ParamJsonFormatterService> CreateParamJsonFormatterService();
}
}
}

#endif // PARAM_JSON_FORMATTER_SERVICE_HPP
