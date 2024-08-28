import {isPointInRect, line, LINEMODE} from '@fit-elsa/elsa-core';

/**
 * jade连线对象.
 *
 * @override
 */
let jadeEvent = (id, x, y, width, height, parent, drawer) => {
    let self = line(id, x, y, width, height, parent, drawer);
    self.type = "jadeEvent";
    self.serializedFields.batchAdd("runnable");
    self.borderWidth = 1;
    self.beginArrow = false;
    self.endArrow = true;
    self.lineMode = LINEMODE.AUTO_CURVE;
    self.borderColor = "#B1B1B7";
    self.mouseInBorderColor = "#B1B1B7";
    self.allowSwitchLineMode = false;
    self.runnable = true;

    /**
     * 保证曲线的位置在shape的下方
     *
     * @override
     */
    const getIndex = self.getIndex;
    self.getIndex = () => {
        let index = getIndex.call(self);
        self.index = index - 200;
        return self.index;
    };

    /**
     * 当页面加载完成之后，如果存在toShape，需要获取toShape的对象，
     * 在release的时候方便调用其offConnect方法.
     */
    self.onPageLoaded = () => {
        if (self.toShape) {
            self.currentToShape = self.page.getShapeById(self.toShape);
        }
    };

    /**
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.call(self);

        // 不展示起始连接点.
        self.fromConnector.visible = false;
        // 结束连接点透明
        self.toConnector.direction.color = "transparent";
        self.toConnector.strokeStyle = "transparent";

        /**
         * @override
         */
        const toRelease = self.toConnector.release;
        self.toConnector.release = position => {
            // 当前的线条连接的两个connector是否已经被占用了
            const isConnectorPairUsed = () => {
                return self.page.shapes.filter(s => s.isTypeof("jadeEvent") && s.id !== self.id).some(s => {
                    return s.fromShape === self.fromShape
                            && s.toShape === self.toShape
                            && s.fromShapeConnector === self.fromShapeConnector
                            && s.toShapeConnector === self.toShapeConnector;
                });
            };

            // 正式release之前，先保存release之前的toShape.
            self.isFocused = false;
            toRelease.call(self.toConnector, position);
            // 当前线条的两个connector已被占用，或者当前线条两端连接的是同一个图形，则删除线条
            if (isConnectorPairUsed() || self.fromShape === self.toShape) {
                self.remove();
            } else {
                if (self.toShape === "") {
                    self.page.onShapeOffConnect && self.page.onShapeOffConnect();
                    self.currentToShape && self.currentToShape.offConnect();
                    self.remove();
                } else {
                    // 在每一次release时，记录当前正连接的toShape.
                    const toShape = self.getToShape();
                    self.page.onShapeConnect && self.page.onShapeConnect();
                    toShape.onConnect();
                    self.currentToShape = toShape;
                }
            }
        };
        self.toConnector.radius = 4;

        /**
         * resize不再通过delta计算，否则会导致线被移出linking图形时，产生不跟手的效果.
         *
         * @override
         */
        self.toConnector.moving = (deltaX, deltaY, x, y) => {
            let value = self.page.disableReact;
            self.page.disableReact = true;
            const from = self.from();
            self.resize(x - from.x, y - from.y);
            self.shapeLinking(self.to().x, self.to().y);
            self.toMoving = true;
            self.page.disableReact = value;
            self.toConnector.afterMoving();
        };

        /**
         * @override
         */
        const afterMoving = self.toConnector.afterMoving;
        self.toConnector.afterMoving = () => {
            if (self.connectingShape && self.connectingShape.linkingConnector) {
                self.definedToConnector = self.connectingShape.linkingConnector.direction.key;
                self.toShape = self.connectingShape.id;
            } else {
                self.definedToConnector = "";
                self.toShape = "";
            }
            afterMoving.apply(self.toConnector);
        };
    };

    /**
     * @override
     */
    const remove = self.remove;
    self.remove = (source) => {
        const toShape = self.getToShape();
        const removed = remove.apply(self, [source]);
        self.page.onShapeOffConnect && self.page.onShapeOffConnect();
        toShape && toShape.offConnect();
        return removed;
    };

    /**
     * 同时连接开始和结束节点.
     *
     * @param fromShapeId 开始节点id.
     * @param fromConnector 开始节点的connector.
     * @param toShapeId 结束节点的id.
     * @param toShapeConnector 结束节点的connector.
     */
    self.connect = (fromShapeId, fromConnector, toShapeId, toShapeConnector) => {
        self.fromShape = fromShapeId;
        self.definedFromConnector = fromConnector;
        self.toShape = toShapeId;
        self.definedToConnector = toShapeConnector;
        self.follow();
    };

    /**
     * 只要鼠标进入到图形中，即视为可连接.
     *
     * @override
     */
    self.validateLinking = (shape, x, y) => {
        return isPointInRect({x, y}, shape.getBound());
    };

    /**
     * @override
     */
    self.shapeLinking = (x, y) => {
        if (!self.linkAble) {
            return;
        }
        const connectingShape = self.page.find(x, y, s => s.allowLink && self.validateLinking(s, x, y));
        self.shapeDelinking(connectingShape);
        self.connectingShape = connectingShape;
        if (self.connectingShape && self.connectingShape !== self.page) {
            self.connectingShape.linking = true;

            // 找到距离最近的可被连接的connector.
            self.connectingShape.linkingConnector = self.connectingShape.getClosestConnector(x, y, c => c.connectable && c.allowToLink);
            self.connectingShape.render();
        }
    };

    /**
     * @override
     */
    self.needGetToConnector = () => {
        return self.getToShape();
    };

    /**
     * @override
     */
    const load = self.load;
    self.load = (ignoreFilter) => {
        load.apply(self, [ignoreFilter]);
        /**
         * jadeEvent的坐标以及宽高变化不触发dirties.
         *
         * @override
         */
        const propertyChanged = self.propertyChanged;
        self.propertyChanged = (property, value, preValue) => {
            if (property === "x" || property === "y" || property === "width" || property === "height") {
                return;
            }
            propertyChanged.apply(self, [property, value, preValue]);
        };
    };

    return self;
};

export {jadeEvent};