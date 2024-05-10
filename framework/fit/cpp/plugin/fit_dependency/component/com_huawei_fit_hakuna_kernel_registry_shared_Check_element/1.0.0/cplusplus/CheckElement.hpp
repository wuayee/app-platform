/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-10-17 11:32:13
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_CHECKELEMENT_C_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_CHECKELEMENT_C_H

#include <fit/stl/bits.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace shared {
struct CheckElement {
    Fit::string type{};
    Fit::map<Fit::string, Fit::string> kvs{};
    bool HasType() const noexcept { return hasFields_[_FieldIndex::type]; }
    const Fit::string &GetType() const
    {
        if (!HasType()) {
            throw std::invalid_argument("no type setted");
        }
        return type;
    }
    void SetType(const char *val, uint32_t len)
    {
        type = Fit::string{val, len};
        hasFields_[_FieldIndex::type] = true;
    }
    void SetType(Fit::string val)
    {
        type = std::move(val);
        hasFields_[_FieldIndex::type] = true;
    }
    Fit::string *MutableType()
    {
        hasFields_[_FieldIndex::type] = true;
        return &type;
    }
    void ClearType()
    {
        hasFields_[_FieldIndex::type] = false;
        type = Fit::string{};
    }
    bool HasKvs() const noexcept { return true; }
    const Fit::map<Fit::string, Fit::string> &GetKvs() const
    {
        return kvs;
    }
    void SetKvs(Fit::map<Fit::string, Fit::string> val)
    {
        kvs = std::move(val);
    }
    Fit::map<Fit::string, Fit::string> *MutableKvs()
    {
        return &kvs;
    }
    void ClearKvs()
    {
        kvs.clear();
    }
    void Reset()
    {
        ClearType();
        ClearKvs();
    }

private:
    static constexpr uint32_t FIELD_COUNT = 2;
    ::Fit::Bits<FIELD_COUNT> hasFields_ {true};
    struct _FieldIndex {
        static constexpr uint32_t type = 0;
        static constexpr uint32_t kvs = 1;
    };
};
}
}
}
}
}

#endif
