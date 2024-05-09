/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {fieldConfiguration} from "../../fieldConfiguration.js";

/**
 * 边框颜色（borderColor）配置.
 */
export const borderColorConfiguration = (target) => {
    const self = fieldConfiguration(target, "borderColor");
    self.type = "string";
    self.title = "颜色";
    self.index = 0;
    self.group.push({type: "line", name: "线条选项", index: 3});

    /**
     * 在修改背景色时，需要同时修改 mouseInBorderColor .
     *
     * @override
     */
    const getChangedData = self.getChangedData;
    self.getChangedData = (value) => {
        const changeDataArray = getChangedData.apply(self, [value]);
        changeDataArray[0].mouseInBorderColor = value;
        return changeDataArray;
    }

    /**
     * 是否只读.
     *
     * 配置面板选项中默认为不禁用
     */
    self.isReadOnly = (focusedShapes) => {
        return false;
    }

    return self;
}