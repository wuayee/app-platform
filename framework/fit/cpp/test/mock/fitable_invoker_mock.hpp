/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : c00816135
 * Date         : 2023/1/29
 * Notes:       :
 */
#ifndef FITABLE_INVOKER_MOCK_H
#define FITABLE_INVOKER_MOCK_H

#include <src/broker/client/domain/fitable_invoker.hpp>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

class FitableInvokerMock : public Fit::FitableInvoker {
public:
    MOCK_CONST_METHOD0(GetFactory, const ::Fit::FitableInvokerFactory*());
    MOCK_CONST_METHOD0(GetConfig, const ::Fit::FitConfigPtr&());
    MOCK_CONST_METHOD0(GetCoordinate, const ::Fit::FitableCoordinatePtr&());
    MOCK_CONST_METHOD0(GetFitableType, ::Fit::Framework::Annotation::FitableType());
    MOCK_CONST_METHOD3(Invoke, FitCode(ContextObj context, ::Fit::Framework::Arguments &in,
                                       ::Fit::Framework::Arguments &out));
};
#endif
