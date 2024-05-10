/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable coordinate.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/29
 */

#include "fitable_coordinate.hpp"

#include <sstream>

using namespace Fit;

namespace {
class DefaultFitableCoordinate : public FitableCoordinateBase {
public:
    explicit DefaultFitableCoordinate(string genericableId, string genericableVersion, string fitableId,
        string fitableVersion);
    ~DefaultFitableCoordinate() override = default;
    const ::Fit::string& GetGenericableId() const override;
    const ::Fit::string& GetGenericableVersion() const override;
    const ::Fit::string& GetFitableId() const override;
    const ::Fit::string& GetFitableVersion() const override;
private:
    ::Fit::string genericableId_ {};
    ::Fit::string genericableVersion_ {};
    ::Fit::string fitableId_ {};
    ::Fit::string fitableVersion_ {};
};
}

DefaultFitableCoordinate::DefaultFitableCoordinate(string genericableId, string genericableVersion,
    string fitableId, string fitableVersion) : genericableId_(std::move(genericableId)),
    genericableVersion_(std::move(genericableVersion)), fitableId_(std::move(fitableId)),
    fitableVersion_(std::move(fitableVersion))
{
}

const ::Fit::string& DefaultFitableCoordinate::GetGenericableId() const
{
    return genericableId_;
}

const ::Fit::string& DefaultFitableCoordinate::GetGenericableVersion() const
{
    return genericableVersion_;
}

const ::Fit::string& DefaultFitableCoordinate::GetFitableId() const
{
    return fitableId_;
}

const ::Fit::string& DefaultFitableCoordinate::GetFitableVersion() const
{
    return fitableVersion_;
}

int32_t FitableCoordinateBase::Compare(const FitableCoordinate& another) const
{
    int32_t ret = GetGenericableId().compare(another.GetGenericableId());
    if (ret == 0) {
        ret = GetGenericableVersion().compare(another.GetGenericableVersion());
    }
    if (ret == 0) {
        ret = GetFitableId().compare(another.GetFitableId());
    }
    if (ret == 0) {
        ret = GetFitableVersion().compare(another.GetFitableVersion());
    }
    return ret;
}

bool FitableCoordinateBase::Equals(const FitableCoordinate& another) const
{
    return Compare(another) == 0;
}

size_t FitableCoordinateBase::ComputeHash() const
{
    return std::hash<Fit::string>()(ToString());
}

Fit::string FitableCoordinateBase::ToString() const
{
    std::stringstream ss;
    ss << GetGenericableId() << ":" << GetGenericableVersion() << ":" << GetFitableId() << ":" << GetFitableVersion();
    return Fit::to_fit_string(ss.str());
}

FitableCoordinateBuilder FitableCoordinate::Custom()
{
    return {};
}

FitableCoordinateBuilder& FitableCoordinateBuilder::SetGenericableId(::Fit::string genericableId)
{
    genericableId_ = std::move(genericableId);
    return *this;
}

FitableCoordinateBuilder& FitableCoordinateBuilder::SetGenericableVersion(::Fit::string genericableVersion)
{
    genericableVersion_ = std::move(genericableVersion);
    return *this;
}

FitableCoordinateBuilder& FitableCoordinateBuilder::SetFitableId(::Fit::string fitableId)
{
    fitableId_ = std::move(fitableId);
    return *this;
}

FitableCoordinateBuilder& FitableCoordinateBuilder::SetFitableVersion(::Fit::string fitableVersion)
{
    fitableVersion_ = std::move(fitableVersion);
    return *this;
}

FitableCoordinatePtr FitableCoordinateBuilder::Build()
{
    return std::make_shared<DefaultFitableCoordinate>(
        std::move(genericableId_), std::move(genericableVersion_),
        std::move(fitableId_), std::move(fitableVersion_));
}
