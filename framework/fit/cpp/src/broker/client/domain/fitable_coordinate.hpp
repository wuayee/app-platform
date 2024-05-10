/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides coordinate for fitables.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/29
 */

#ifndef FIT_FITABLE_COORDINATE_HPP
#define FIT_FITABLE_COORDINATE_HPP

#include <fit/stl/string.hpp>

#include <memory>

namespace Fit {
class FitableCoordinateBuilder;

/**
 * 为服务实现提供坐标。
 */
class FitableCoordinate {
public:
    FitableCoordinate() = default;
    virtual ~FitableCoordinate() = default;

    FitableCoordinate(const FitableCoordinate&) = delete;
    FitableCoordinate(FitableCoordinate&&) = delete;
    FitableCoordinate& operator=(const FitableCoordinate&) = delete;
    FitableCoordinate& operator=(FitableCoordinate&&) = delete;

    /**
     * 获取所实现的泛化服务的唯一标识。
     *
     * @return 表示泛化服务为标识的字符串的引用。
     */
    virtual const ::Fit::string& GetGenericableId() const = 0;

    /**
     * 获取所实现的泛化服务的版本。
     *
     * @return 表示泛化服务版本的字符串的引用。
     */
    virtual const ::Fit::string& GetGenericableVersion() const = 0;

    /**
     * 获取服务实现的唯一标识。
     *
     * @return 表示服务实现唯一标识的字符串的引用。
     */
    virtual const ::Fit::string& GetFitableId() const = 0;

    /**
     * 获取服务实现的版本。
     *
     * @return 表示服务实现的版本的字符串的引用。
     */
    virtual const ::Fit::string& GetFitableVersion() const = 0;

    /**
     * 将指定的坐标与当前坐标进行比较。
     *
     * @param another 表示待与当前坐标比较的另一个坐标实例的引用。
     * @return 若当前坐标大于另一个坐标，则是一个正数；若小于另一个坐标，则是一个负数；否则为 0。
     */
    virtual int32_t Compare(const FitableCoordinate& another) const = 0;

    /**
     * 检查指定的坐标与当前坐标是否包含相同的数据。
     *
     * @param another 表示待与当前坐标比较的另一个坐标的引用。
     * @return 若与当前坐标包含相同的数据，则为 true；否则为 false。
     */
    virtual bool Equals(const FitableCoordinate& another) const = 0;

    /**
     * 为当前坐标实例计算散列值。
     *
     * @return 表示当前坐标的散列值。
     */
    virtual size_t ComputeHash() const = 0;

    /**
     * 返回一个字符串，用以表示当前的坐标实例。
     *
     * @return 表示当前坐标的字符串。
     */
    virtual ::Fit::string ToString() const = 0;

    /**
     * 返回一个构建程序，用以创建服务实现的坐标的新实例。
     *
     * @return 表示用以构建坐标新实例的构建程序。
     */
    static FitableCoordinateBuilder Custom();
};

/**
 * 为服务实现的坐标信息提供共享指针定义。
 */
using FitableCoordinatePtr = std::shared_ptr<FitableCoordinate>;

/**
 * 为服务实现的坐标提供基类。
 */
class FitableCoordinateBase : public FitableCoordinate {
public:
    FitableCoordinateBase() = default;
    ~FitableCoordinateBase() override = default;
    int32_t Compare(const FitableCoordinate& another) const override;
    bool Equals(const FitableCoordinate& another) const override;
    size_t ComputeHash() const override;
    ::Fit::string ToString() const override;
};

/**
 * 为服务实现的坐标提供构建程序。
 */
class FitableCoordinateBuilder {
public:
    FitableCoordinateBuilder() = default;
    ~FitableCoordinateBuilder() = default;

    /**
     * 设置所实现的泛化服务的唯一标识。
     *
     * @param genericableId 表示泛化服务唯一标识的字符串。
     * @return 表示当前构建程序的引用。
     */
    FitableCoordinateBuilder& SetGenericableId(::Fit::string genericableId);

    /**
     * 设置所实现的泛化服务的版本。
     *
     * @param genericableVersion 表示泛化服务的版本的字符串。
     * @return 表示当前构建程序的引用。
     */
    FitableCoordinateBuilder& SetGenericableVersion(::Fit::string genericableVersion);

    /**
     * 设置服务实现的唯一标识。
     *
     * @param fitableId 表示服务实现唯一标识的字符串。
     * @return 表示当前构建程序的引用。
     */
    FitableCoordinateBuilder& SetFitableId(::Fit::string fitableId);

    /**
     * 设置服务实现的版本。
     *
     * @param fitableVersion 表示服务实现的版本的字符串。
     * @return 表示当前构建程序的引用。
     */
    FitableCoordinateBuilder& SetFitableVersion(::Fit::string fitableVersion);

    /**
     * 构建服务实现坐标的新实例。
     *
     * @return 表示指向新构建的服务坐标的指针。
     */
    FitableCoordinatePtr Build();
private:
    ::Fit::string genericableId_;
    ::Fit::string genericableVersion_;
    ::Fit::string fitableId_;
    ::Fit::string fitableVersion_;
};
}

#endif // FIT_FITABLE_COORDINATE_HPP
