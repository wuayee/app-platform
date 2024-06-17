import ReactDOM from "react-dom/client";
import {rectangleDrawer} from "@fit-elsa/elsa-core";
import React, {useRef, useState} from "react";
import {NODE_STATUS} from "@";
import RunningStatusPanel from "@/components/flowRunComponent/RunningStatusPanel.jsx";
import {DefaultRoot} from "@/components/DefaultRoot.jsx";
import {HORIZONTAL_LEFT, HORIZONTAL_RIGHT, VERTICAL_DOWN, VERTICAL_UP} from "./asserts/svgIcons.jsx?react";
import ToolIcon from "./asserts/icon-tool.svg?react";
import {Header} from "@/components/Header.jsx";
import {Footer} from "@/components/Footer.jsx";
import {Tooltip} from "antd";

const WATER_DROP_DISTANCE = 500;

/**
 * jadeNode绘制器.
 *
 * @override
 */
export const jadeNodeDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.type = "jadeNodeDrawer";
    self.reactContainer = null;
    self.panelRef = null;
    self.waterDrops = [];

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.reactContainer = document.createElement("div");
        self.reactContainer.id = "react-container-" + shape.id;
        self.reactContainer.style.width = "100%";
        self.parent.appendChild(self.reactContainer);
        self.parent.style.pointerEvents = "auto";
    };

    /**
     * 写在react.
     */
    self.unmountReact = () => {
        if (!self.root) {
            return;
        }
        self.root.unmount();
        self.root = null;
    };

    /**
     * @override
     */
    self.drawStatic = () => {
        if (!shape.componentName || self.root) {
            return;
        }
        self.root = ReactDOM.createRoot(self.reactContainer);
        self.root.render(<Root/>);
    };

    const Root = () => {
        self.panelRef = useRef();
        const [runStatus, setRunStatus] = useState(shape.runStatus);
        const [disabled, setDisabled] = useState(false);

        // 设置运行状态.
        self.setRunStatus = status => {
            setRunStatus(status);
        };

        // 设置是否禁用.
        self.setDisabled = (disabled) => {
            setDisabled(disabled);
        };

        // 当展示报告时，需要重新计算area.
        const onReportShow = () => {
            shape.indexCoordinate();
        };

        return (<>
            {runStatus !== NODE_STATUS.DEFAULT &&
                    <RunningStatusPanel shape={shape} ref={self.panelRef} onReportShow={onReportShow}/>}
            <div style={{position: "relative", background: "white", padding: 16, borderRadius: shape.borderRadius}}>
                <DefaultRoot shape={shape} component={shape.getComponent()} disabled={disabled} />
            </div>
        </>);
    };

    self.getReportFrame = () => {
        if (!self.panelRef || !self.panelRef.current || !self.panelRef.current.getRunReportRect()) {
            return null;
        }
        const rect = self.panelRef.current.getRunReportRect();
        const position = shape.page.calculatePosition({clientX: rect.x, clientY: rect.y})
        return {
            x: position.x,
            y: position.y,
            width: rect.width / shape.page.scaleX,
            height: rect.height / shape.page.scaleY
        }
    };

    /**
     * 存在report时，需要判断是否坐标在report dom中.
     *
     * @override
     */
    const containsBack = self.containsBack;
    self.containsBack = (x, y) => {
        const reportFrame = self.getReportFrame();
        if (!reportFrame) {
            return containsBack.apply(self, [x, y]);
        }
        return containsBack.apply(self, [x, y]) || isPointInRect({x, y}, reportFrame);
    };

    /**
     * 不绘制focusFrame.
     */
    self.drawFocusFrame = () => {
    };

    /**
     * @override
     */
    const drawBorder = self.drawBorder;
    self.drawBorder = () => {
        drawBorder.apply(self);
        if (shape.isFocused || shape.emphasized) {
            self.parent.style.outline = shape.outlineWidth + "px" + " solid " + shape.outlineColor;
        } else {
            self.parent.style.outline = "";
        }
    };

    /**
     * 删除之前清理掉react相关的组件.
     *
     * @override
     */
    const beforeRemove = self.beforeRemove;
    self.beforeRemove = () => {
        beforeRemove.apply(self);
        self.unmountReact();
    };

    /**
     * 监听parent的变化，当发生变化时，需要修改图形的高度和宽度.
     */
    let prevHeight = 0;
    self.observe = () => {
        new ResizeObserver((entries) => {
            if (prevHeight === self.parent.offsetHeight) {
                return;
            }

            // 删除之后不需要修改图形大小.
            if (shape.container === "") {
                return;
            }

            shape.resize(shape.width, self.parent.offsetHeight);
            prevHeight = self.parent.offsetHeight;
            self.panelRef?.current?.setHeight(shape.height);
            generateWaterDrops();
        }).observe(self.parent);
    };

    const generateWaterDrops = () => {
        self.waterDrops = [];
        const width = shape.width;
        const height = shape.height;
        const emphasizedOffset = shape.emphasizedOffset;

        // 上右.
        self.waterDrops.push(waterDrop(HORIZONTAL_RIGHT,
                (x, ctl) => x + ctl.percent * width,
                (y) => y + emphasizedOffset));

        // 下左.
        self.waterDrops.push(waterDrop(HORIZONTAL_LEFT,
                (x, ctl) => x + width - ctl.percent * width,
                (y) => y + height - emphasizedOffset));
        const count = Math.floor(height / WATER_DROP_DISTANCE) + 1;
        for (let i = 0; i < count; i++) {
            // 左上
            self.waterDrops.push(waterDrop(VERTICAL_UP,
                    (x) => x + emphasizedOffset,
                    (y, ctl) => y + height / count * (count - i) - ctl.percent * height / count));

            // 右下.
            self.waterDrops.push(waterDrop(VERTICAL_DOWN, (x) => x + width - emphasizedOffset,
                    (y, ctl) => y + height / count * i + ctl.percent * height / count))
        }
    };

    /**
     * 绘制水滴流转效果.
     *
     * @override
     */
    self.drawDynamic = (context, x, y) => {
        self.waterDrops.forEach(wd => {
            if (wd.loaded) {
                wd.draw(context, x, y, shape)
            } else {
                wd.load();
            }
        })
    };

    /**
     * 有子类重写.
     */
    self.getHeaderIcon = () => {
    };

    /**
     * 本方法提供默认值-工具，具体可由子类重写.
     */
    self.getHeaderTypeIcon = () => {
        return (<>
            <Tooltip overlayClassName={"jade-node-custom-header-type-tooltip"} title="工具">
                <div className={"jade-node-custom-header-type-icon-wrapper"}>
                    <ToolIcon className="jade-node-custom-header-type-icon"/>
                </div>
            </Tooltip>
        </>);
    };

    /**
     * 获取Header组件
     *
     * @param disabled 是否禁用.
     * @return {JSX.Element}
     */
    self.getHeaderComponent = (disabled) => {
        return (<Header shape={shape} disabled={disabled}/>);
    }

    /**
     * 获取Footer组件
     *
     * @return {JSX.Element}
     */
    self.getFooterComponent = () => {
        return (<Footer shape={shape}/>);
    };

    /**
     * 默认的工具栏配置.
     *
     * @return {*} 数组.
     */
    self.getToolMenus = () => {
        return [{
            key: '1', label: "复制", action: () => {
                shape.duplicate();
            }
        }, {
            key: '2', label: "删除", action: () => {
                shape.remove();
            }
        }, {
            key: '3', label: "重命名", action: (setEdit) => {
                setEdit(true);
            }
        }];
    };

    return self;
};

const STEP = 0.009

/**
 * 水滴绘制器.
 *
 * @param rawSvg 原始svg字符串.
 * @param getX 获取x的值.
 * @param getY 获取y的值.
 * @return {{}} 对象.
 */
const waterDrop = (rawSvg, getX, getY) => {
    const self = {};
    self.img = null;
    self.control = { percent: 0, times: 0 };

    /**
     * 绘制.
     *
     * @param context canvas2dContext对象.
     * @param x 横坐标.
     * @param y 纵坐标.
     * @param shape 图形对象.
     */
    self.draw = (context, x, y, shape) => {
        self.control.percent += STEP;
        if (self.control.percent >= 1) {
            self.control.times = 100;
            self.control.percent = 0;
            return;
        }
        x -= shape.width / 2;
        y -= shape.height / 2;
        x = getX(x, self.control);
        y = getY(y, self.control);
        context.drawImage(self.img, x, y);
    };

    /**
     * 加载svg为image对象.
     */
    self.load = () => {
        const svg = new Blob([rawSvg], {type:"image/svg+xml;charset=utf-8"});
        const url = URL.createObjectURL(svg);
        const img = new Image();
        img.src = url;
        img.onload = function () {
            URL.revokeObjectURL(url);
            self.img = img;
            self.loaded = true;
        };
    }

    return self;
};