/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides mock config center.
 * Author       : liangjishi 00298979
 * Date         : 2022/03/01
 */

#ifndef FIT_CONF_SERVER_MOCK_CONFIG_CENTER_HPP
#define FIT_CONF_SERVER_MOCK_CONFIG_CENTER_HPP

#include <config_center.hpp>
#include <repo/config_repo.hpp>
#include <config_node_diff.hpp>
#include <fit/stl/memory.hpp>

#include <gmock/gmock.h>

namespace Fit {
namespace Conf {
namespace Server {
MATCHER_P(UniquePtrEq, v, "") { return v == arg.get(); }

class MockConfigChangedEventHandler : public ConfigChangedEventHandler {
public:
    MOCK_CONST_METHOD3(Handle,
        bool(const ::Fit::string& environment, const ::Fit::string& topic, const ::Fit::vector<::Fit::string>& keys));

    static std::unique_ptr<MockConfigChangedEventHandler> Create()
    {
        return std::make_unique<MockConfigChangedEventHandler>();
    }
};

class MockConfigChangedPublisher : public ConfigChangedPublisher {
public:
    MOCK_CONST_METHOD1(Publish, void(ConfigChangedEventUptr));

    static std::unique_ptr<MockConfigChangedPublisher> Create()
    {
        return std::make_unique<MockConfigChangedPublisher>();
    }
};

class MockConfigChangedEvent : public ConfigChangedEvent {
public:
    MOCK_CONST_METHOD1(Handle, bool(const ConfigChangedEventHandler*));

    static std::unique_ptr<MockConfigChangedEvent> Create()
    {
        return std::make_unique<MockConfigChangedEvent>();
    }
};

class MockConfigRepo : public ConfigRepo {
public:
    MOCK_CONST_METHOD0(GetConfigCenter, ConfigCenter*());
    MOCK_CONST_METHOD3(Ensure, uint64_t(uint64_t environmentId, uint64_t parentId, const string& path));
    MOCK_CONST_METHOD2(Delete, uint32_t(uint64_t environmentId, const string& path));
    MOCK_CONST_METHOD2(GetValue, string(uint64_t environmentId, const string& path));
    MOCK_CONST_METHOD3(SetValue, void(uint64_t environmentId, const string& path, const string& value));
    MOCK_CONST_METHOD2(Save, void(uint64_t environmentId, vector<ConfigEntity*> & configs));
    MOCK_CONST_METHOD2(ListChildren, vector<ConfigEntityUptr>(uint64_t environmentId, const string& path));
};

class MockEnvironmentRepo : public EnvironmentRepo {
public:
    MOCK_CONST_METHOD0(GetConfigCenter, ConfigCenter*());
    MOCK_CONST_METHOD1(Ensure, uint64_t(const ::Fit::string& name));
};

class MockSubscriptionRepo : public SubscriptionRepo {
public:
    MOCK_CONST_METHOD0(GetConfigCenter, ConfigCenter*());
    MOCK_CONST_METHOD2(Ensure, uint64_t(uint64_t environmentId, const ::Fit::string& subscription));
    MOCK_CONST_METHOD3(Save,
        void(uint64_t environmentId, const ::Fit::string& subscription, const ::Fit::vector<::Fit::string>& paths));
    MOCK_CONST_METHOD3(Delete,
        void(uint64_t environmentId, const ::Fit::string& topic, const ::Fit::vector<::Fit::string>& paths));
    MOCK_CONST_METHOD3(Lookup,
        ::Fit::map<::Fit::string, PathVector>(uint64_t environmentId, const ::Fit::string& path, bool includeChildren));
};

class MockConfigCenterRepoFactory : public ConfigCenterRepoFactory {
public:
    MOCK_CONST_METHOD1(CreateEnvironmentRepo, std::unique_ptr<EnvironmentRepo>(ConfigCenter * configCenter));
    MOCK_CONST_METHOD1(CreateConfigRepo, std::unique_ptr<ConfigRepo>(ConfigCenter * configCenter));
    MOCK_CONST_METHOD1(CreateSubscriptionRepo, std::unique_ptr<SubscriptionRepo>(ConfigCenter * configCenter));

    static std::unique_ptr<MockConfigCenterRepoFactory> Create()
    {
        auto repoFactory = std::make_unique<MockConfigCenterRepoFactory>();
        EXPECT_CALL(*repoFactory, CreateConfigRepo(::testing::_)).WillRepeatedly(::testing::Invoke(
            [](ConfigCenter* center) -> std::unique_ptr<ConfigRepo> {
                auto configRepo = std::make_unique<MockConfigRepo>();
                EXPECT_CALL(*configRepo, GetConfigCenter()).WillRepeatedly(::testing::Return(center));
                return std::move(configRepo);
            }));
        EXPECT_CALL(*repoFactory, CreateEnvironmentRepo(::testing::_)).WillRepeatedly(::testing::Invoke(
            [](ConfigCenter* center) -> std::unique_ptr<EnvironmentRepo> {
                auto environmentRepo = std::make_unique<MockEnvironmentRepo>();
                EXPECT_CALL(*environmentRepo, GetConfigCenter()).WillRepeatedly(::testing::Return(center));
                return std::move(environmentRepo);
            }));
        EXPECT_CALL(*repoFactory, CreateSubscriptionRepo(::testing::_)).WillRepeatedly(::testing::Invoke(
            [](ConfigCenter* center) -> std::unique_ptr<SubscriptionRepo> {
                auto subscriptionRepo = std::make_unique<MockSubscriptionRepo>();
                EXPECT_CALL(*subscriptionRepo, GetConfigCenter()).WillRepeatedly(::testing::Return(center));
                return std::move(subscriptionRepo);
            }));
        return repoFactory;
    }
};

class MockConfigCenter : public ConfigCenter {
public:
    explicit MockConfigCenter(ConfigCenterRepoFactoryUptr repoFactory, ConfigChangedPublisherUptr publisher)
        : ConfigCenter(std::move(repoFactory), std::move(publisher))
    {
    }

    MockConfigRepo& GetMockedConfigRepo() const
    {
        auto repo = (MockConfigRepo*)ConfigCenter::GetConfigRepo();
        return *repo;
    }

    MockEnvironmentRepo& GetMockedEnvironmentRepo() const
    {
        auto repo = (MockEnvironmentRepo*)ConfigCenter::GetEnvironmentRepo();
        return *repo;
    }

    MockSubscriptionRepo& GetMockedSubscriptionRepo() const
    {
        auto repo = (MockSubscriptionRepo*)ConfigCenter::GetSubscriptionRepo();
        return *repo;
    }

    static std::unique_ptr<MockConfigCenter> Create()
    {
        return std::make_unique<MockConfigCenter>(new MockConfigCenter(
            MockConfigCenterRepoFactory::Create(),
            MockConfigChangedPublisher::Create()));
    }
};

class MockConfigNode : public ConfigNode {
public:
    MOCK_CONST_METHOD0(GetParent, ConfigNode*());
    MOCK_CONST_METHOD0(GetName, const string&());
    MOCK_CONST_METHOD0(GetValue, const string&());
    MOCK_CONST_METHOD0(GetChildren, vector<ConfigNode*>());
    MOCK_CONST_METHOD1(GetChild, ConfigNode*(const string&));
    MOCK_CONST_METHOD0(CountChildren, uint32_t());
    MOCK_CONST_METHOD0(ToValues, map<string, string>());

    static std::unique_ptr<MockConfigNode> Create(const char* name, const char* value,
        const vector<ConfigNode*>& children)
    {
        auto node = new MockConfigNode();
        EXPECT_CALL(*node, GetName()).WillRepeatedly(::testing::ReturnRefOfCopy<const string>(name));
        EXPECT_CALL(*node, GetValue()).WillRepeatedly(::testing::ReturnRefOfCopy<const string>(value));
        EXPECT_CALL(*node, GetChildren()).WillRepeatedly(::testing::Return(children));
        EXPECT_CALL(*node, GetChild(::testing::_)).WillRepeatedly(::testing::Invoke(
            [node](const string& name) -> ConfigNode* {
                vector<ConfigNode*> children = node->GetChildren();
                for (auto& child: children) {
                    if (child->GetName() == name) {
                        return child;
                    }
                }
                return nullptr;
            }));
        EXPECT_CALL(*node, CountChildren()).WillRepeatedly(::testing::Return(children.size()));
        return std::unique_ptr<MockConfigNode>(node);
    }

    static void Sort(vector<ConfigNode*>& nodes)
    {
        std::sort(nodes.begin(), nodes.end(), [](ConfigNode* node1, ConfigNode* node2) -> bool {
            return node1->GetName() < node2->GetName();
        });
    }
};

class MockConfigNodeDiff : public ConfigNodeDiff {
public:
    MOCK_CONST_METHOD0(GetName, const ::Fit::string&());
    MOCK_CONST_METHOD0(GetValue, const ::Fit::string&());
    MOCK_CONST_METHOD0(GetState, ConfigStatus());
    MOCK_CONST_METHOD0(GetParent, const ConfigNodeDiff*());
    MOCK_CONST_METHOD0(GetChildren, ::Fit::vector<ConfigNodeDiff*>());
    MOCK_CONST_METHOD1(GetChild, ConfigNodeDiff*(const ::Fit::string&));

    static std::unique_ptr<MockConfigNodeDiff> Create()
    {
        return std::make_unique<MockConfigNodeDiff>();
    }

    static void Sort(vector<ConfigNodeDiff*>& diffs)
    {
        std::sort(diffs.begin(), diffs.end(), [](ConfigNodeDiff* diff1, ConfigNodeDiff* diff2) -> bool {
            return diff1->GetName() < diff2->GetName();
        });
    }

    static std::unique_ptr<MockConfigNodeDiff> Create(const char* name, const char* value, ConfigStatus state,
        ::Fit::vector<ConfigNodeDiff*> children)
    {
        auto diff = new MockConfigNodeDiff();
        EXPECT_CALL(*diff, GetName()).WillRepeatedly(::testing::ReturnRefOfCopy<const ::Fit::string>(name));
        EXPECT_CALL(*diff, GetValue()).WillRepeatedly(::testing::ReturnRefOfCopy<const ::Fit::string>(value));
        EXPECT_CALL(*diff, GetState()).WillRepeatedly(::testing::Return(state));
        EXPECT_CALL(*diff, GetChildren()).WillRepeatedly(::testing::Return(std::move(children)));
        EXPECT_CALL(*diff, GetChild(::testing::_)).WillRepeatedly(
            ::testing::Invoke([diff](const ::Fit::string& name) -> ConfigNodeDiff* {
                auto children = diff->GetChildren();
                for (auto& child: children) {
                    if (child->GetName() == name) {
                        return child;
                    }
                }
                return nullptr;
            }));
        return std::make_unique<MockConfigNodeDiff>(diff);
    }
};

class MockConfigEntity : public ConfigEntity {
public:
    MOCK_CONST_METHOD0(GetId, uint64_t());
    MOCK_METHOD1(SetId, void(uint64_t));
    MOCK_CONST_METHOD0(GetParentId, uint64_t());
    MOCK_METHOD1(SetParentId, void(uint64_t));
    MOCK_CONST_METHOD0(GetName, const ::Fit::string&());
    MOCK_METHOD1(SetName, void(::Fit::string));
    MOCK_CONST_METHOD0(GetValue, const ::Fit::string&());
    MOCK_METHOD1(SetValue, void(::Fit::string));

    static ConfigEntityUptr Create(uint64_t id, uint64_t parentId, const char* name, const char* value)
    {
        auto entity = std::make_unique<MockConfigEntity>();
        EXPECT_CALL(*entity, GetId()).WillRepeatedly(::testing::Return(id));
        EXPECT_CALL(*entity, GetParentId()).WillRepeatedly(::testing::Return(parentId));
        EXPECT_CALL(*entity, GetName()).WillRepeatedly(::testing::ReturnRefOfCopy<const string>(name));
        EXPECT_CALL(*entity, GetValue()).WillRepeatedly(::testing::ReturnRefOfCopy<const string>(value));
        return entity;
    }
};
}
}
}

#endif // FIT_CONF_SERVER_MOCK_CONFIG_CENTER_HPP
