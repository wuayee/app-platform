/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/4/15
 * Notes:       :
 */

#ifndef FIT_RUNTIME_HPP
#define FIT_RUNTIME_HPP

#include <fit/stl/memory.hpp>
#include <functional>
#include "runtime_element.hpp"

#define DISABLE_MOVE_AND_COPY_CONSTRUCTOR(CLASS) \
    CLASS(CLASS&&) = delete;                      \
    CLASS(const CLASS&) = delete;                  \
    CLASS& operator=(CLASS&&) = delete;                      \
    CLASS& operator=(const CLASS&) = delete;

namespace Fit {
class Runtime {
public:
    Runtime() = default;
    virtual ~Runtime() = default;

    DISABLE_MOVE_AND_COPY_CONSTRUCTOR(Runtime);

    virtual bool Start() = 0;
    virtual bool Stop() = 0;

    virtual void AddElement(unique_ptr <RuntimeElement> element) = 0;
    virtual bool GetElementAnyOf(const std::function<bool(RuntimeElement*)>& condition) = 0;

    template<class T>
    T* GetElementIs()
    {
        T* target = nullptr;
        GetElementAnyOf([&target](RuntimeElement* e) {
            target = dynamic_cast<T*>(e);
            return target != nullptr;
        });
        return target;
    }
};
}
#endif // FIT_RUNTIME_HPP
