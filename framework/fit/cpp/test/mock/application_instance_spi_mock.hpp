/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2023/09/13
 * Notes:       :
 */
#ifndef APPLICATION_INSTANCE_SPI_MOCK_HPP
#define APPLICATION_INSTANCE_SPI_MOCK_HPP
#include <registry_listener/include/support/registry_listener_spi.hpp>
#include <gmock/gmock.h>
#include <gtest/gtest.h>
class ApplicationInstanceSpiMock : public Fit::Registry::Listener::ApplicationInstanceSpi {
public:
    MOCK_METHOD1(Query, Fit::vector<Fit::Registry::Listener::ApplicationInstance>(
        const Fit::vector<Fit::Registry::Listener::ApplicationInfo>& apps));
    MOCK_METHOD1(Subscribe, Fit::vector<Fit::Registry::Listener::ApplicationInstance>(
        const Fit::vector<Fit::Registry::Listener::ApplicationInfo>& apps));
};
#endif