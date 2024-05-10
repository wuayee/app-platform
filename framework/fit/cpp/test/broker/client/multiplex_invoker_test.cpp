/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : l00558918
 * Date         : 2021/4/29 17:40
 */

#include <fit/external/broker/broker_client_external.hpp>
#include <mock/runtime_mock.hpp>
#include <mock/broker_fitable_discovery_mock.h>
#include <mock/configuration_service_mock.hpp>
#include "gtest/gtest.h"
#include "component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp"
#include "gmock/gmock.h"
#include "broker_client_fit_config.h"
#include "multiplex_invoker_impl.h"

using namespace ::Fit;
using namespace ::Fit::Framework;

class MultiplexInvokerTest : public testing::Test {
public:
    void SetUp() override
    {
        runtimeMock_.SetUp();
        config_ = make_shared<ConfigurationServiceMock>();
        discovery_ = make_shared<BrokerFitableDiscoveryMock>();

        address1 = {
            {
                {
                    "123",
                    "fitable_id",
                    "1.0.0",
                    "1.0.0"
                },
            },
            {
                "127.0.0.1",
                8088,
                "worker_1",
                1,
                {1, 2, 3},
                "environment"
            }
        };

        address2 = {
            {
                {
                    "123",
                    "fitable_id",
                    "1.0.0",
                    "1.0.0"
                },
            },
            {
                "127.0.0.1",
                8099,
                "worker_2",
                1,
                {1, 2, 3},
                "environment"
            }
        };
    }

    void TearDown() override
    {
        runtimeMock_.TearDown();
    }

    std::shared_ptr<ConfigurationServiceMock> config_;
    std::shared_ptr<BrokerFitableDiscoveryMock> discovery_;

    RuntimeMock runtimeMock_;
    ServiceAddress address1;
    ServiceAddress address2;
};

TEST_F(MultiplexInvokerTest, should_return_success_and_callback_0_times_when_Exec_given_empty_fitables)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {}));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {});
    auto ret = impl.Route([](const Fit::RouteFilterParam &param) -> bool {
        return true;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_FILTER_TARGET));
    EXPECT_THAT(callbackTimes, ::testing::Eq(0));
}

TEST_F(MultiplexInvokerTest,
    should_return_success_callback_0_times_when_Exec_given_not_empty_fitables_and_empty_services)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias"}},
            "degradation",
            "fitable_id"
        }
    }));
    Fit::vector<Fitable> fitables {
        {
            genericID,
            "fitable_id",
            "",
            "1.0.0"
        }
    };
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, fitables)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {}));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {1});
    auto ret = impl.Route([](const Fit::RouteFilterParam &param) -> bool {
        return true;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_FILTER_TARGET));
    EXPECT_THAT(callbackTimes, ::testing::Eq(0));
}

TEST_F(MultiplexInvokerTest,
    should_return_success_and_callback_0_times_when_Exec_given_route_fileter_0_fitable)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias"}},
            "degradation",
            "fitable_id"
        },
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {1});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        return false;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_FILTER_TARGET));
    EXPECT_THAT(callbackTimes, ::testing::Eq(0));
}

TEST_F(MultiplexInvokerTest,
    should_return_success_and_callback_0_times_when_Exec_given_route_fileter_2_fitable_and_lbfilter_0)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias"}},
            "degradation",
            "fitable_id"
        },
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, _)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {
                address1
            }
        ));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {1});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        return true;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return false;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_FILTER_TARGET));
    EXPECT_THAT(callbackTimes, ::testing::Eq(0));
}

TEST_F(MultiplexInvokerTest,
    should_return_ERR_and_callback_0_times_when_Exec_given_route_fileter_1_fitable_and_empty_local_protocol)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias"}},
            "degradation",
            "fitable_id"
        },
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, _)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {
                address1
            }
        ));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        if (param.alias == "alias") {
            return true;
        }
        return false;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_FILTER_TARGET));
    EXPECT_THAT(callbackTimes, ::testing::Eq(0));
}

TEST_F(MultiplexInvokerTest,
    should_return_ERR_and_callback_0_times_when_Exec_given_route_fileter_1_fitable_and_local_protocol_not_match)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias"}},
            "degradation",
            "fitable_id"
        },
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, _)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {
                address1
            }
        ));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {2});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        if (param.alias == "alias") {
            return true;
        }
        return false;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_FILTER_TARGET));
    EXPECT_THAT(callbackTimes, ::testing::Eq(0));
}

TEST_F(MultiplexInvokerTest,
    should_return_success_and_callback_1_times_when_Exec_given_route_fileter_1_fitable)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias"}},
            "degradation",
            "fitable_id"
        },
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, _)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {
                address1
            }
        ));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {1});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        if (param.alias == "alias") {
            return true;
        }
        return false;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_SUCCESS));
    EXPECT_THAT(callbackTimes, ::testing::Eq(1));
}

TEST_F(MultiplexInvokerTest,
    should_return_success_and_callback_1_times_when_Exec_given_route_fileter_2_fitable_and_lbfiter_fileter_1)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias"}},
            "degradation",
            "fitable_id"
        },
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, _)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {
            address1,
            address2
        }));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {1});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        return true;
    }).Get([this](const Fit::LBFilterParam &param) -> bool {
        if (param.port == (uint32_t)address2.address.port) {
            return true;
        }
        return false;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_SUCCESS));
    EXPECT_THAT(callbackTimes, ::testing::Eq(1));
}

TEST_F(MultiplexInvokerTest,
    should_return_success_and_callback_2_times_when_Exec_given_fitable_in_to_different_worker)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, _)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {
            address1,
            address2
        }));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {1});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        return true;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_SUCCESS));
    EXPECT_THAT(callbackTimes, ::testing::Eq(2));
}

TEST_F(MultiplexInvokerTest, should_return_success_and_callback_1_times_when_Exec_given_fitable_in_same_worker)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    address2.address.workerId = address1.address.workerId;
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, _)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {
            address1,
            address2
        }));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {1});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        return true;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_SUCCESS));
    EXPECT_THAT(callbackTimes, ::testing::Eq(1));
}

TEST_F(MultiplexInvokerTest,
    should_return_success_and_callback_1_times_when_Exec_given_two_fitable_in_different_worker_and_one_protocol_not_sup)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    address2.address.protocol = 2;
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, _)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {
            address1,
            address2
        }));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {1});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        return true;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_SUCCESS));
    EXPECT_THAT(callbackTimes, ::testing::Eq(1));
}

TEST_F(MultiplexInvokerTest,
    should_return_success_and_callback_2_times_when_Exec_given_2_fitables_and_route_lb_filter_return_true)
{
    Fit::string genericID {"123"};
    int callbackTimes {0};
    EXPECT_CALL(*config_, GetFitables(genericID)).WillOnce(::testing::Return(Fit::Configuration::FitableSet {
        {
            {{"alias"}},
            "degradation",
            "fitable_id"
        },
        {
            {{"alias1"}},
            "degradation1",
            "fitable_id1"
        }
    }));
    EXPECT_CALL(*discovery_, GetFitablesAddresses(_, _)).WillOnce(
        ::testing::Return(Fit::vector<ServiceAddress> {
            address1,
            address2
        }));
    Fit::Framework::Arguments in;
    Fit::Framework::Arguments out;

    Fit::MultiplexInvokerImpl impl(genericID, discovery_, config_, {1});
    auto ret = impl.Route([&](const Fit::RouteFilterParam &param) -> bool {
        return true;
    }).Get([](const Fit::LBFilterParam &param) -> bool {
        return true;
    }).Exec(nullptr, in, out, [&callbackTimes](Fit::CallBackInfo info, Fit::Framework::Arguments out) {
        callbackTimes++;
    });

    EXPECT_THAT(ret, ::testing::Eq(FIT_ERR_SUCCESS));
    EXPECT_THAT(callbackTimes, ::testing::Eq(2));
}