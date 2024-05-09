/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

/**
 * shape操作器.
 *
 * @param page 页面对象.
 */
export const eventOperator = (page) => {
    if (page === null || page === undefined) {
        throw new Error("page is null or undefined.");
    }
    let touchAction = {
        0 : page.interactDrawer.getInteract().directtouchstart,
        1 : page.interactDrawer.getInteract().directtouchend,
        2 : page.interactDrawer.getInteract().directtouchmove,
        100 : page.interactDrawer.getInteract().directtouchcancel
    }

    const self = {};

    /**
     * 响应外部的touch事件，暂不支持多指 todo@xiafei
     *
     * @param touches 事件列表.
     */
    self.touch = (touches) => {
        touches.forEach(touch => {
            touchAction[touch.type]({
                changedTouches : [
                    {
                        clientX : touch.x,
                        clientY : touch.y
                    }
                ],
                button : ''
            });
        })
    }

    return self;
}