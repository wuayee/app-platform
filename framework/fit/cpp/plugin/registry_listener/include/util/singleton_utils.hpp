/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides utility for singleton.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#ifndef FIT_REGISTRY_LISTENER_SINGLETON_UTILS_HPP
#define FIT_REGISTRY_LISTENER_SINGLETON_UTILS_HPP

#include <fit/stl/mutex.hpp>

#include <functional>
#include <memory>

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为单例提供工具方法。
 */
class SingletonUtils {
public:
    /**
     * 隐藏默认构造方法，避免工具类被实例化。
     */
    SingletonUtils() = delete;

    /**
     * 获取指定对象的单例。
     *
     * @tparam T 表示单例对象的类型。
     * @param ref 表示用以存储单例对象的变量的引用。
     * @param lock 表示用以在创建单例过程中使用的互斥变量。
     * @param factory 表示用以创建对象实例方法。
     * @return 表示已经存在或新创建的单例对象的引用。
     */
    template<class T>
    static std::shared_ptr<T>& Get(std::shared_ptr<T>& ref, Fit::mutex& lock,
        const std::function<std::shared_ptr<T>()>& factory)
    {
        if (ref == nullptr) {
            lock_guard<mutex> guard {lock};
            if (ref == nullptr) {
                ref = factory();
            }
        }
        return ref;
    }
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_SINGLETON_UTILS_HPP
