/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: User metadata associated with a shared memory block.
 */

#ifndef DATABUS_USER_DATA_H
#define DATABUS_USER_DATA_H

#include <cstdint>

namespace DataBus {
namespace Resource {

// 用户自定义元数据
struct UserData {
    UserData() : userDataPtr(nullptr), dataSize(0) {};
    UserData(const int8_t* dataPtr, size_t size) : userDataPtr(nullptr), dataSize(0)
    {
        if (dataPtr != nullptr && size > 0) {
            userDataPtr = std::make_unique<int8_t[]>(size);
            dataSize = size;
            std::copy(dataPtr, dataPtr + size, userDataPtr.get());
        }
    }
    UserData(const UserData&) = delete;
    UserData& operator=(const UserData&) = delete;

    std::unique_ptr<int8_t[]> userDataPtr; // 元数据指针
    size_t dataSize; // 元数据长度
};
} // namespace Resource
} // namespace DataBus

#endif // DATABUS_USER_DATA_H
