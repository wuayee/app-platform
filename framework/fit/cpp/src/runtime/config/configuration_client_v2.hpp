/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  : 配置中心调用封装，针对接口：
 * com.huawei.matata.conf.client
 * com.huawei.matata.conf.subscription.client
 * com.huawei.matata.notification.client
 * Author       : songyongtan
 * Date         : 2021/6/25
 * Notes:       :
 */

#ifndef CONFIGURATION_CLIENT_V2_HPP
#define CONFIGURATION_CLIENT_V2_HPP

#include <memory>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/map.hpp>
#include <fit/fit_code.h>
#include <mutex>
#include "configuration_client.h"

namespace Fit {
namespace Configuration {
/**
 * 对接新版本cpp实现的引擎，新换了genericable接口
 */
class ConfigurationClientV2 : public ConfigurationClient {
public:
    ConfigurationClientV2();
    explicit ConfigurationClientV2(Fit::string environment, Fit::string groupName, Fit::string consumerName);
    ~ConfigurationClientV2() override;

    bool Start() override;
    bool Stop() override;

    int32_t Get(const Fit::string &key, Fit::string &value) override;
    int32_t Download(const Fit::string &key, ItemValueSet &out) override;
    bool IsSubscribed(const Fit::string &key) const override;
    int32_t Subscribe(const Fit::string &key, ConfigSubscribePathCallback callback) override;
    int32_t Subscribe(const Fit::string &key, ConfigSubscribeNodeCallback cb) override;
protected:
    // 首次需要去消息服务订阅 通知组
    // 其它只需要在通知组添加genericable的订阅项
    FitCode SubscribeGroup();
    /**
     * 从配置中心下载配置
     * @param key 配置项
     * @param out 配置结果
     * @return 错误码 FIT_OK成功
     */
    FitCode DownloadWithKey(const Fit::string &key, ItemValueSet &out) const;
    // 通知子节点内容更新
    void Notify(const Fit::string &key, ItemValueSet &out) const;
    // 通知当前节点值变化
    void Notify(const string &key, const string &value) const;
    std::function<void(const Fit::bytes &)> BuildChangeCallback();

    bool IsSubscribedValue(const Fit::string &key) const;
    bool IsSubscribedPath(const Fit::string &key) const;
    FitCode InvokeSubscribe(const Fit::string &key);

private:
    string environment_ {};
    string groupName_ {};
    string consumerName_ {};
    mutable std::recursive_mutex mt_;
    /**
     * 记录genericable id和订阅回调
     */
    Fit::map<Fit::string, ConfigSubscribePathCallback> genericablesNotifyGroup_ {};
    Fit::map<Fit::string, ConfigSubscribeNodeCallback> valueNotifyGroup_ {};
    bool isSubscribeSuccess_ {false};
};
}
}

#endif // CONFIGURATION_CLIENT_V2_HPP
