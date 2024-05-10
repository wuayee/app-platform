/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : c00816135
 * Date         : 2023/1/29
 * Notes:       :
 */

#ifndef FITABLE_INVOKER_FACTORY_MOCK_H
#define FITABLE_INVOKER_FACTORY_MOCK_H

#include <src/broker/client/domain/fitable_endpoint.hpp>
#include <src/broker/client/domain/fitable_coordinate.hpp>
#include <src/broker/client/domain/fitable_invoker_factory.hpp>
#include <fit/fit_code.h>
#include <gmock/gmock.h>
#include <gtest/gtest.h>

class FitableInvokerFactoryMock : public Fit::FitableInvokerFactory {
public:
    MOCK_CONST_METHOD0(GetCurrentWorkerId, const ::Fit::string&());
    MOCK_CONST_METHOD0(GetFitableDiscovery, const ::Fit::BrokerFitableDiscoveryPtr&());
    MOCK_CONST_METHOD0(GetFormatterService, const ::Fit::Framework::Formatter::FormatterServicePtr&());
    MOCK_CONST_METHOD1(GetFitableConfig, ::Fit::FitConfigPtr(
        const ::Fit::string& genericableId));
    MOCK_CONST_METHOD2(GetRawInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate, Fit::FitConfigPtr config));
    MOCK_CONST_METHOD3(GetRawInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType, Fit::FitConfigPtr config));
    MOCK_CONST_METHOD3(GetRawInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate,
        std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier, Fit::FitConfigPtr config));
    MOCK_CONST_METHOD4(GetRawInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType,
        std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier, Fit::FitConfigPtr config));
    MOCK_CONST_METHOD2(GetInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate, Fit::FitConfigPtr config));
    MOCK_CONST_METHOD3(GetInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType, Fit::FitConfigPtr config));
    MOCK_CONST_METHOD3(GetInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate,
        ::std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier, Fit::FitConfigPtr config));
    MOCK_CONST_METHOD4(GetInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType,
        ::std::unique_ptr<::Fit::FitableEndpointSupplier> endpointSupplier, Fit::FitConfigPtr config));
    MOCK_CONST_METHOD2(GetLocalInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate, Fit::FitConfigPtr config));
    MOCK_CONST_METHOD3(GetLocalInvoker, std::unique_ptr<Fit::FitableInvoker>(
        ::Fit::FitableCoordinatePtr coordinate,
        ::Fit::Framework::Annotation::FitableType fitableType, Fit::FitConfigPtr config));
};

#endif