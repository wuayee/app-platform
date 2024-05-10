/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2022/2/23
 * Notes:       :
 */

#ifndef FITABLE_DISCOVERY_MOCK_HPP
#define FITABLE_DISCOVERY_MOCK_HPP
#include <fit/internal/framework/fitable_discovery.hpp>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
using namespace Fit::Framework;
class FitableDiscoveryMock : public Fit::Framework::FitableDiscovery {
public:
    MOCK_METHOD1(RegisterLocalFitable, void(Annotation::FitableDetailPtrList));
    MOCK_METHOD1(UnRegisterLocalFitable, void(const Annotation::FitableDetailPtrList &));
    MOCK_METHOD1(GetLocalFitable, Annotation::FitableDetailPtrList(const Fit::Framework::Fitable &));
    MOCK_METHOD1(GetLocalFitableByGenericId, Annotation::FitableDetailPtrList(const Fit::string &));
    MOCK_METHOD0(GetAllLocalFitables, Annotation::FitableDetailPtrList());
    MOCK_METHOD2(DisableFitables,
                 Annotation::FitableDetailPtrList(const Fit::string &, const Fit::vector<Fit::string> &));
    MOCK_METHOD2(EnableFitables,
                 Annotation::FitableDetailPtrList(const Fit::string &, const Fit::vector<Fit::string> &));
    MOCK_METHOD0(ClearAllLocalFitables, void());
};
#endif // FITABLE_DISCOVERY_MOCK_HPP
