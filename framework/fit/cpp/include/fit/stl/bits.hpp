/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/12/7
 * Notes:       :
 */

#ifndef FIT_BITS_HPP
#define FIT_BITS_HPP

#include <stdexcept>
#include <cstdint>
#include "except.hpp"

namespace Fit {
template<size_t N>
class Bits {
public:
    static constexpr uint32_t MAX_BITS = N;
    static constexpr uint32_t SEGMENT_BITS = (sizeof(uint32_t) * 8);

    explicit Bits(bool initial)
    {
        const uint32_t initValue = initial ? 0xffffffffu : 0;
        for (auto &seg : data_) {
            seg = initValue;
        }
    }

    ~Bits() = default;

    struct BitReference {
        BitReference(Bits &owner, uint32_t index) : owner(owner), index(index) {}

        operator bool() const
        {
            return owner.Get(index);
        }

        BitReference &operator=(bool valid)
        {
            owner.Set(index, valid);
            return *this;
        }

    private:
        Bits &owner;
        uint32_t index;
    };

    struct ConstBitReference {
        ConstBitReference(const Bits &owner, uint32_t index) : owner(owner), index(index) {}

        operator bool() const
        {
            return owner.Get(index);
        }

    private:
        const Bits &owner;
        uint32_t index;
    };

    BitReference operator[](uint32_t index)
    {
        return BitReference(*this, index);
    }

    ConstBitReference operator[](uint32_t index) const
    {
        return ConstBitReference(*this, index);
    }

    void Set(uint32_t index, bool value)
    {
        if (index >= MAX_BITS) {
            FIT_THROW_OUT_OF_RANGE("out of bits range");
        }
        if (value) {
            data_[IndexSegment(index)] |= IndexBitInSegment(index);
        } else {
            data_[IndexSegment(index)] &= ~IndexBitInSegment(index);
        }
    }

    bool Get(uint32_t index) const
    {
        if (index >= MAX_BITS) {
            FIT_THROW_OUT_OF_RANGE("out of bits range");
        }

        return data_[IndexSegment(index)] & IndexBitInSegment(index);
    }

    static constexpr uint32_t IndexBitInSegment(uint32_t pos)
    {
        return (0x00000001 << (pos % (SEGMENT_BITS)));
    }

    static constexpr uint32_t IndexSegment(uint32_t pos)
    {
        return pos / (SEGMENT_BITS);
    }

private:
    uint32_t data_[(N / (SEGMENT_BITS + 1)) + 1] {};
};
}

#endif // BITS_HPP
