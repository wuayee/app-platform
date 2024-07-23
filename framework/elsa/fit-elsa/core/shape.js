import {Atom} from './atom.js';
import {convertPositionWithParents, getDistance, isNumeric, isPointInRect, isRectInRect, uuid} from '../common/util.js';
import {
    CURSORS,
    DEFAULT_FOLLOW_BAR_OFFSET,
    DOCK_MODE,
    EDITOR_NAME,
    EVENT_TYPE,
    MIN_WIDTH,
    PARENT_DOCK_MODE,
    SELECTION_STRATEGY,
    Z_INDEX_OFFSET
} from '../common/const.js';
import {configurationFactory} from "./configuration/configurationFactory.js";
import {layoutCommand, shapeIndexChangedCommand} from './commands.js';
import {lockRegion} from './hitRegion.js';
import {imageSaver} from "./thumb.js";
import {inPolygon} from "../common/graphics.js";
import {copyIcon, deleteIcon} from "./svg/icons.js";

/**
 * @class
 * 形状基类
 * 所有形状的最终基类：最最重要的类
 * 背景，线条，颜色，文字。。。。。出自该形状
 * 该形状为抽象基类，绘画实现为空
 * 绘画实现是可替换方案：可以是canvas绘制，也可以是svg绘制
 * 辉子 2020-01-15
 */
// let shape = (id, x, y, width, height, parent, shapeType, drawer) => {
let shape = (id, x, y, width, height, parent, drawer) => {
    let detections = [];
    let self = new Atom(detections);


    //----------need to be serialized---------------------
    self.type = "shape";

    if (parent !== undefined && parent !== null) {
        if (parent.newPageMode) {
            self.page = self;
            self.mode = parent.newPageMode;
        } else {
            self.graph = parent.graph;
            self.page = parent.page;
            self.pageId = parent.page.id;
            self.page.shapes.push(self);
            self.container = parent.id;
        }
    }

    /** 每个图形都有一个唯一的id，不指定时自动生成uuid */
    self.id = id ? id : uuid();
    // self.type = shapeType;

    self.typeChain = {
        parent: null, type: self.type
    }

    self.isType = type => {
        return self.typeChain.type === type;
    };

    self.isTypeof = type => {
        let chain = self.typeChain;
        while (chain !== null) {
            if (chain.type === type) {
                return true;
            }
            chain = chain.parent;
        }
        return false;
    };

    self.getPadLeft = () => {
        const padLeft = self.padLeft;
        return padLeft !== null && padLeft !== undefined ? padLeft : self.pad;
    };
    self.getPadRight = () => {
        const padRight = self.padRight;
        return padRight ? padRight : self.pad;
    };
    self.getPadTop = () => {
        const padTop = self.padTop;
        return padTop ? padTop : self.pad;
    };
    self.getPadBottom = () => {
        const padBottom = self.padBottom;
        return padBottom ? padBottom : self.pad;
    };

    self.text = "";
    //----------------static--------------
    self.namespace = "elsa";
    // self.rotateAble = true;
    // self.editable = true;
    // self.selectable = true;
    //self.scrollLock = { x: false, y: false };//是否跟随父容器滚动栏滚动
    // self.moveable = true;
    // self.dragable = true;
    // self.visible = true;
    // self.deletable = true;
    // self.allowLink = true;//允许line链接
    // self.shared = false;//是否被不同的page引用
    // self.resizeable = true;
    self.serializable = true;
    self.defaultWidth = 100;
    self.defaultHeight = 100;
    self.shareAble = false;//是否可以被不同的page引用
    /**
     * @maliya 2023.6.9 临时方案，为鸿蒙演示
     * 文档中批注功能，要求：批注的笔记可以跟随文字自适应变化，先只实现一根直线，不考虑圆
     */
    self.needLevitation = true;
    if (!parent.newPageMode) {
        self.drawer = drawer(self, self.page.div);
    }
    self.sharedParent = () => {
        if (self.container === "") {
            return self.savedSharedParent ? self.savedSharedParent : {id: ""};
        }//if it is deleted, get the previous shared parent
        let parent = self;//.getContainer();
        while (!parent.isTypeof("page")) {
            parent = parent.getContainer();
            if (parent.shared) {
                return parent;
            }
        }
        return {id: ""};
    };
    //self.enableAnimation = false;
    /**
     * 绝对横坐标
     * 即使该shape处于某个container里，依然按照全局绝对坐标设置，也就是container移动时，该shape的坐标也发生移动，并不是相对container的相对不变坐标
     * 辉子
     */
    self.x = x;
    self.y = y;
    self.width = width;
    self.height = height;
    self.listeners = [];

    //----------dynamic------------------
    self.selectedX = 0;
    self.selectedY = 0;//记录shape被选中后的原始位置，方便shape拖动时计算
    self.isFocused = false;
    self.linking = false;//是否正在被line连接
    self.linkingConnector = null;
    self.cursor = CURSORS.MOVE;
    self.allowTraced = true;//允许使用ctrl+z
    self.isAutoSize = true;
    self.allowClickRun = false;
    self.allowMultiLineEdit = false;
    self.mousedownConnector = null;
    self.isEditing = () => {
        if (!self.drawer.text) {
            return;
        }
        return self.isEdit;
    };

    self.setProperty = (property, value) => {
        self[property] = value;
        if (property === "local") {
            self.setLocal(value);
        }
    };
    self.setLocal = localAction => {
    };

    //---------------methods-----------------
    self.containerAllowed = parent => true;
    self.getVisibility = () => {
        let v = self.inScreen() && self.visible && self.globalAlpha !== 0;
        if (self.getContainer() === self.page) {
            return v;
        } else {
            return self.getContainer().getVisibility() && v;
        }
    }
    self.getSelectable = () => {
        // return self.get("selectable");// && self.page.mode === PAGE_MODE.CONFIGURATION;
        return self.selectable;
    }
    self.getIndex = () => {
        // self.index = (self.isFocused ? self.page.shapes.length : self.page.shapes.indexOf(self)) + Z_INDEX_OFFSET;
        self.index = self.page.indexOf(self) + Z_INDEX_OFFSET;
        return self.index;//被暂时存储的index是为history恢复使用
    };
    self.cachedContainer = {id: undefined, shape: undefined};

    /**
     * 撤销删除图形修改为 新建图形（服务端需处理：若图形已存在，则覆盖）
     * 场景：1、删除图形 -> 撤销删除；2、删除图形 -> 删除图形所在页面 -> 撤销删除页面 -> 撤销删除图形;
     */
    self.undoRemove = (page) => {
        self.page = page;

        // container的detection中，会设置preContainer.
        self.container = self.preContainer;
        page.shapes.remove(s => s.id === self.id);
        page.shapes.insert(self, self.index - Z_INDEX_OFFSET);
        self.invalidateAlone();
    };

    self.getContainer = () => {
        if (self.cachedContainer.id === self.container) {
            return self.cachedContainer.shape;
        }
        let p = self.page.id === self.container ? self.page : self.page.shapes.find(s => s.id === self.container && s.childAllowed && s.childAllowed(self));
        if (p === undefined) {
            p = self.page.find((self.x + self.width / 2, self.y + self.height / 2, s => s.childAllowed));
        }
        self.cachedContainer.id = p.id;
        self.cachedContainer.shape = p;
        return p;
    };
    self.center = () => {
        return {x: (self.x + self.width / 2), y: (self.y + self.height / 2)};
    };
    self.getFontString = () => {
        return self.fontStyle + " " + self.fontWeight + " " + self.fontSize + "px " + self.fontFace;
    }
    self.getBorderColor = () => {
        let color = self.borderColor;
        if (self.page.mouseInShape === self) {
            color = self.mouseInBorderColor;
        }
        if (self.isFocused) {
            color = self.focusBorderColor;
        }
        return color;
    };

    /**
     * 回去shadow值.
     *
     * @return {*}
     */
    self.getShadow = () => {
        return self.isFocused ? self.focusShadow : self.shadow;
    };

    self.getFontColor = () => {
        let color = self.fontColor;
        if (self.page.mouseInShape === self) {
            color = self.mouseInFontColor;
        }
        if (self.isFocused) {
            color = self.focusFontColor;
        }
        return color;
    };

    self.getBackColor = () => {
        let color = self.backColor;
        if (self.isFocused) {
            color = self.focusBackColor;
        }
        if (self.isLocked()) {
            color = "rgba(255,215,0,0.2)";
        }
        return color;
    };

    self.addGraphEventListener = (type, handler) => {
        self.listeners.push({type: type, handler: handler});
        self.page.graph.addEventListener(type, handler);
    }

    self.addPageEventListener = (type, handler) => {
        self.listeners.push({type: type, handler: handler});
        self.page.addEventListener(type, handler);
    }

    self.removeEventListeners = () => {
        self.listeners.forEach(listener => {
            self.page.graph.removeEventListener(listener.type, listener.handler);
            self.page.removeEventListener(listener.type, listener.handler);
        });
        // 删除当前shape持有的监听器
        self.listeners = [];
    }

    self.remove = source => {
        if (!self.page.disableReact && !self.beforeRemove()) {
            return [];
        }

        self.removeEventListeners();
        const removed = [];
        self.page.shapes.filter((s) => s.fromShape === self.id).forEach((s) => s.fromShape = "");
        self.page.shapes.filter((s) => s.toShape === self.id).forEach((s) => s.toShape = "");
        self.page.shapes.remove(s => s.id === self.id);
        self.page.animations && self.page.animations.clean && self.page.animations.clean();
        self.savedSharedParent = self.sharedParent();
        delete self.deleteFromShare;
        (source && source !== self && source.shared) && (self.deleteFromShare = true);

        // 这里不确定在container已为空字符串时是否需要走下面的流程，暂时先不走，看有无问题.
        if (self.container !== "") {
            self.container = "";
            if (!self.page.disableReact) {
                self.afterRemoved();
                self.getContainer().shapeRemoved(self);
            }
            removed.push(self);
        }

        // 删除时需要触发FOCUSED_SHAPE_CHANGE事件.
        if (self.isFocused) {
            self.page.ignoreReact(() => {
                self.isFocused = false;
            });
            self.page.triggerEvent({type: EVENT_TYPE.FOCUSED_SHAPE_CHANGE, value: [self]});
        }

        /*
         * 这里修改的原因，是为了保证幂等，当container为空字符串时，也需要走一遍流程（container为空字符串不代表图形已被删除
         * 否则，会出现问题，问题场景如下：
         * 1、协同1删除图形
         * 2、协同2收到message
         *     a、在graph的subscriptions[page_shape_data_changed]中将图形对应的_data的container设置为""
         *     b、调用page.onMessage，此时将调用图形的remove()方法，但此时由于_data中的container已经为""，所以并不会
         *        执行删除dom的操作，并且也不会将图形对象从page.shapes数组中删除
         * 3、协同1撤销
         * 4、协同2收到shape_added的message
         *     a、在graph的subscriptions[shape_added]中，将message中的value推入到graph.pages.shapes中
         *     b、调用page.onMessage，由于图形对象未被删除可以获取到，因此不做任何操作.
         * 5、协同2收到"page_shape_data_changed"消息
         *     a、在graph的subscriptions[page_shape_data_changed]中将图形对应的_data的container设置为非空字符串
         *     b、调用page.onMessage，由于图形还在，设置_data中的container为非空字符串
         *     注意： 这里图形对象中的_data和graph.pages.shapes中的_data已不是同一个对象，一个是message中的value
         * 6、协同1重做
         * 7、协同2收到"page_shape_data_changed"消息
         *     a、在graph的subscriptions[page_shape_data_changed]中将图形对应的_data的container设置为""
         *     b、调用page.onMessage，调用图形的remove()方法，由于data对象已不一样，上一步并不会影响对象中的_data，
         *     此时图形的container还是非空字符串，所以可以删除成功
         * 8、协同1撤销
         * 9、协同2收到shape_added的message
         *     a、在graph的subscriptions[shape_added]中，将message中的value推入到graph.pages.shapes中
         *     b、调用page.onMessage，创建图形，并且_data就是message.value.
         *     注意，此时data是同一个对象了
         * 10、协同1重做
         *     a、在graph的subscriptions[page_shape_data_changed]中将图形对应的_data的container设置为""
         *     b、调用page.onMessage，此时将调用图形的remove()方法，但此时由于_data中的container已经为""，所以并不会
         *     执行删除dom的操作，并且也不会将图形对象从page.shapes数组中删除
         * 到这里，以上的步骤，就会随着重做和撤销而不断的循环，导致结果不一致。
         * 因此，会存在调用remove时，container已为空字符串的情况，因此，这里需要进行保护.
         */
        self.invalidate();
        return removed;
    };

    self.getBound = () => {
        const OFFSET = 10;
        const pos = {};
        pos.minx = self.x - self.margin + (self.width < 0 ? self.width : 0) - OFFSET;
        pos.miny = self.y - self.margin + (self.height < 0 ? self.height : 0) - OFFSET;
        pos.maxx = self.x + self.margin + (self.width > 0 ? self.width : 0) + 2 * OFFSET;
        pos.maxy = self.y + self.margin + (self.height > 0 ? self.height : 0) + 2 * OFFSET;

        const clipParent = (child, scrollx, scrolly) => {
            const parent = child.getContainer();
            if (parent === self.page) {
                return;
            }
            if (parent.ifMaskItems) {
                scrollx += parent.itemScroll.x;
                scrolly += parent.itemScroll.y;
                const pminx = parent.x + Math.max(parent.borderWidth, parent.itemPad[0]) - scrollx;
                const pminy = parent.y + Math.max(parent.borderWidth, parent.itemPad[2]) - scrolly;
                if (pminx > pos.minx) {
                    pos.minx = pminx;
                }
                if (pminy > pos.miny) {
                    pos.miny = pminy;
                }

                const pmaxx = parent.x + parent.width - Math.max(2 * parent.borderWidth, parent.itemPad[1]) - scrollx;
                const pmaxy = parent.y + parent.height - Math.max(2 * parent.borderWidth, parent.itemPad[3]) - scrolly;
                if (pmaxx < pos.maxx) {
                    pos.maxx = pmaxx;
                }
                if (pmaxy < pos.maxy) {
                    pos.maxy = pmaxy;
                }
            }
            clipParent(parent, scrollx, scrolly);
        }
        clipParent(self, 0, 0);
        const result = {x: pos.minx, y: pos.miny, width: pos.maxx - pos.minx, height: pos.maxy - pos.miny};
        result.visible = result.width > 0 && result.height > 0;
        return result;
    };

    self.getFrame = () => {
        return {x: self.x, y: self.y, width: self.width, height: self.height};
    };

    setContains(self);
    setConnector(self);
    setRegion(self);
    setMouseActions(self);
    setKeyAction(self);
    setCoordinateIndex(self)

    self.isMyBlood = item => item === self;

    /**
     * 拖动该形状
     * 触发moveTo
     * 但用于手动拖拽形状
     * 逻辑比moveTo复杂很多，因为拖动时shape.container可能在旋转状态
     * 辉子 2021
     */
    self.dragTo = (position) => {
        //记录上下文，为创建positionCommand准备
        position.context.command = "position";
        let dirty = position.context.shapes.find(s => s.shape === self);
        if (!dirty) {
            dirty = {shape: self, x: {}, y: {}, container: {}};
            dirty.x.preValue = self.x;
            dirty.y.preValue = self.y;
            dirty.container.preValue = self.container;
            position.context.shapes.push(dirty);
        }
        dirty.x.value = self.x + position.deltaX;
        dirty.y.value = self.y + position.deltaY;
        dirty.container.value = self.container;
        return dirty;
    };

    /**
     * 移动该形状:无所谓是代码移的还是手动移的
     */
    self.moveTo = (x, y, after) => {
        if (x === undefined || y === undefined) {
            return false;
        }
        if (x === self.x && y === self.y) {
            return false;
        }

        // 不能移出画布.
        // todo@zhangyue 测试presentationFrame，暂时去掉.
        // if (x < 0 || y < 0 || x > (self.page.width - self.width) || y > (self.page.height - self.height)) {
        //     return;
        // }

        self.x = x;
        self.y = y;
        after ? after() : self.moved();
        return true;
    };

    /**
     * 判断形状是否在屏幕可见范围
     * 如果不在可见范围，为提高性能，动画不绘制
     * 辉子 2020
     */
    self.inScreen = () => {
        return true;
        //todo
    };
    /**
     * 留着为esc键后的动作
     */
    self.restore = () => {
    };

    /**
     * 确定编辑区域
     * 双击编辑框在这个区域显示
     */
    self.getEditRect = () => {
        return {
            x: self.x + self.getPadLeft(),
            y: self.y + self.getPadTop(),
            width: self.width - self.getPadLeft() - self.getPadRight() - 2 * self.borderWidth,
            height: self.height - self.getPadTop() - self.getPadBottom() - 2 * self.borderWidth
        };
    };

    //---------------------------------绘制相关----------------------------------------------

    /**
     * 重新绘制自己
     * 辉子 2020
     */
    self.render = () => {
        self.drawer && self.drawer.draw();
    }

    /**
     * 在页shape上，表现跟invalidate基本一致
     */
    self.invalidateAlone = () => {
        if (self.page === undefined) {
            return;
        }
        if (self.page.disableInvalidate) {
            return;
        }
        if (self.container === "") {
            delete self.enableAnimation;
            self.drawer.remove();
            return;
        }

        if (!self.getVisibility()) {
            self.drawer.hide();
        } else {
            self.manageConnectors();
            self.drawer.move();
            self.drawer.transform();
            self.render();
        }
    };

    /**
     * 将自己失效
     * 绘制，移动，变形，表现跟refresh一致
     * 继承类会做子形状适配
     * 辉子 2020
     */
    self.invalidate = () => self.invalidateAlone();

    self.delayInvalidate = (action, ignore) => {
        let invalidate = self.invalidate;
        let invalidateAlone = self.invalidateAlone;
        let render = self.render;
        self.invalidate = () => {
        }
        self.invalidateAlone = () => {
        }
        self.render = () => {
        }
        action();
        self.invalidate = invalidate;
        self.invalidateAlone = invalidateAlone;
        self.render = render;
        if (ignore) {
            return;
        }
        self.invalidate();
    }

    /**
     * 复位
     * 辉子 2020
     */
    self.reset = () => {//鼠标放开后位置状态回归原始
        if (self.width === MIN_WIDTH && self.height === MIN_WIDTH) {
            self.resize(self.defaultWidth, self.defaultHeight);
        }
        self.invalidateAlone();
    };

    /**
     * 去除文本的所有html格式
     * 辉子 2021
     */
    self.removeTextFormat = () => {
        let edit = document.getElementById(EDITOR_NAME);
        edit.innerHTML = self.text;
        self.text = edit.innerText;
    };

    /**
     * 是否被选中框选中
     * 辉子 2022
     */
    self.inSelection = rect => {
        return isRectInRect(self, rect);
    };

    /**
     * 选中该shape，选中后shape.isFocused = true;
     * 辉子 2020
     */
    self.select = (x, y) => {
        if (!self.getSelectable() || !self.getVisibility() || self.isFocused) {
            return;
        }
        self.selectX = x;
        self.selectY = y;
        self.isFocused = true;
        self.selected && self.selected();
    };

    /**
     * 去除该shape的选中状态
     * huiz 2020
     */
    self.unSelect = () => {
        if (!self.getSelectable() || !self.getVisibility() || !self.isFocused) {
            return;
        }
        self.isFocused = false;
        if (self.autoWidth || self.autoHeight) {
            self.indexCoordinate();
        }
        self.unselected && self.unselected();
    };

    /**
     * 得到focus相关所有形状
     */
    self.getRelated = () => [self];

    self.freeLineSelect = (points) => {
        let bound = self.getBound();
        let isPointsInBound = points.filter(point => isPointInRect({x: point[0], y: point[1]}, bound)).length > 0;
        if (isPointsInBound) {
            return true;
        }
        return inPolygon(points, self.x, self.y) || inPolygon(points, self.x + self.width, self.y) || inPolygon(points, self.x, self.y + self.height) || inPolygon(points, self.x + self.width, self.y + self.height);
    }

    self.resize = (width, height) => {
        isNumeric(self.minWidth) && width < self.minWidth && (width = self.minWidth);
        isNumeric(self.minHeight) && height < self.minHeight && (height = self.minHeight);
        if (self.width === width && self.height === height) {
            return;
        }
        const preWidth = self.width;
        const preHeight = self.height;
        if (self.width !== width) {
            self.width = width;
        }
        if (self.height !== height) {
            self.height = height;
        }
        self.resized && self.resized(preWidth, preHeight, width, height);
    };

    /**
     * container的itemscroll
     * 子shape在container里可以有offset移动
     */
    self.getOffset = () => {
        const container = self.getContainer();
        return {
            x: container.getOffset().x + container.itemScroll.x,
            y: container.getOffset().y + container.itemScroll.y
        };
    }

    /**
     * 返回图形默认的followBar位置和偏移量,这里可以根据图形在画布中的位置,动态调整其location
     *
     * @returns { location: string}
     */
    self.getFollowBarLocation = () => {
        return "bottom";
    }

    /**
     * 获取followBar的偏移量,这里可以根据图形的宽高和位置,动态调整偏移量
     * @returns {number}
     */
    self.getFollowBarOffset = () => {
        return DEFAULT_FOLLOW_BAR_OFFSET;
    }

    /**
     * 得到画布中图形的默认上下文工具栏菜单
     * 说明：为了支持不同应用不同默认菜单功能，故在对应应用的graph上也能获取对应的图形默认上下文工具栏菜单
     * @maliya
     */
    self.getContextMenuScript = () => {
        return {
            menus: [
                {
                    type: "icon",
                    name: "copy",
                    icon: copyIcon,
                    text: "复制",
                    group: "base",
                    onClick: function (target) {
                        const event = new KeyboardEvent('keydown', {
                            ctrlKey: true,
                            keyCode: 68,
                            code: "KeyD"
                        });
                        document.dispatchEvent(event);
                    }
                },
                {
                    type: "icon",
                    name: "delete",
                    icon: deleteIcon,
                    text: "删除",
                    group: "base",
                    onClick: function (target) {
                        // 将删除按钮映射到delete按键
                        const event = new KeyboardEvent('keydown', {
                            code: "Delete"
                        });
                        document.dispatchEvent(event);
                    }
                }
            ]
        };
    };

    /**
     * 得到該形狀的快捷菜單
     * 輝子 2022
     */
    self.getMenuScript = () => {
        //{text,action,draw,img}
        if (!self.getSelectable()) {
            return;
        }
        const setEraser = (shape, size) => {
            if (shape.isTypeof("freeLine")) {
                shape.eraser = size;
            } else {
                shape.page.shapes.filter(s => s.isTypeof("freeLine")).forEach(s => s.eraser = size);
            }
        }

        //手写
        const menus = [];
        menus.push({
            text: (self.page.inHandDrawing ? "取消" : "") + "手写", action: (shape, x, y) => {
                self.page.inHandDrawing = !self.page.inHandDrawing;
            }, draw: (context) => {
                // context.rotate(Math.PI/4);
                context.strokeStyle = "dimgray";
                context.strokeRect(-2, -2, 4, 6);
                context.strokeStyle = context.fillStyle = "red";
                context.moveTo(-2, -2);
                context.lineTo(0, -5);
                context.lineTo(2, -2);
                context.stroke();
                context.fill();

            }
        });

        const erasers = {
            text: "擦除", menus: [], width: 30, draw: (context) => {
                context.strokeStyle = "dimgray";
                context.strokeRect(-3, -2, 6, 5);
                context.strokeStyle = "red";
                context.strokeRect(-3, -3, 6, 1);

            }
        };
        erasers.menus.push({
            text: "小", action: shape => {
                setEraser(shape, 3);
                // shape.page.eraser = 3;
                // shape.page.erasePrecise = shape.isTypeof("freeLine");
            }, draw: (context) => {
                context.fillStyle = "red";
                context.fillRect(-2, -2, 4, 4);
            }
        });
        erasers.menus.push({
            text: "大", action: shape => {
                setEraser(shape, 6);
                // shape.page.eraser = 6
                // shape.page.erasePrecise = shape.isTypeof("freeLine");
            }, draw: (context) => {
                context.fillStyle = "dimgray";
                context.fillRect(-3, -3, 6, 6);
            }
        });
        // erasers.menus.push({ text: "▇ 大", action: shape => shape.page.eraser = 6 });
        menus.push(erasers);

        if (self === self.page) {
            return menus;
        }

        //层级
        const moveLayer = (shapes, move) => {
            const args = [];
            shapes.forEach(shape => {
                const arg = {shape};
                arg.preIndex = shape.getIndex();
                move(shape);
                arg.index = shape.getIndex();
                if (arg.preIndex !== arg.index) {
                    args.push(arg);
                }
            })
            // @maliya 图形层级未发生变化，则不触发命令
            if (args.length === 0) {
                return;
            }
            shapeIndexChangedCommand(self.page, args);
        };
        const focused = self.page.getFocusedShapes();

        const layer = {
            text: "层级", menus: [], draw: (context) => {
                context.fillStyle = "silver";
                context.fillRect(0, -3, 6, 6);
                context.strokeStyle = "white";
                context.strokeRect(-1.5, -1.5, 6, 6);
                context.fillStyle = "red";
                context.fillRect(-1.5, -1.5, 6, 6);
                context.strokeStyle = "white";
                context.strokeRect(-3, 0, 6, 6);
                context.fillStyle = "gray";
                context.fillRect(-3, 0, 6, 6);
            }
        };
        layer.menus.push({
            text: "向上一层",
            action: shape => moveLayer(focused, s => s.page.moveIndexAfter(s, s.getIndex() + 1)),
            draw: (context) => {
                context.fillStyle = "silver";
                context.fillRect(0, -3, 6, 6);
                context.strokeStyle = "white";
                context.strokeRect(-1.5, -1.5, 6, 6);
                context.fillStyle = "steelblue";
                context.fillRect(-1.5, -1.5, 6, 6);
                context.strokeStyle = "white";
                context.strokeRect(-3, 0, 6, 6);
                context.fillStyle = "red";
                context.fillRect(-3, 0, 6, 6);
            }
        });
        layer.menus.push({
            text: "向下一层",
            action: shape => moveLayer(focused, s => s.page.moveIndexBefore(s, s.getIndex() - 1)),
            draw: (context) => {
                context.fillStyle = "red";
                context.fillRect(0, -3, 6, 6);
                context.strokeStyle = "white";
                context.strokeRect(-1.5, -1.5, 6, 6);
                context.fillStyle = "steelblue";
                context.fillRect(-1.5, -1.5, 6, 6);
                context.strokeStyle = "white";
                context.strokeRect(-3, 0, 6, 6);
                context.fillStyle = "gray";
                context.fillRect(-3, 0, 6, 6);
            }
        });
        layer.menus.push({
            text: "到最顶层", action: shape => moveLayer(focused, s => s.page.moveIndexTop(s)), draw: (context) => {
                context.fillStyle = "silver";
                context.fillRect(0, -3, 6, 6);
                context.strokeStyle = "white";
                context.strokeRect(-1.5, -1.5, 6, 6);
                context.fillStyle = "gray";
                context.fillRect(-1.5, -1.5, 6, 6);
                context.strokeStyle = "white";
                context.fillStyle = "red";
                context.strokeRect(-3, 0, 6, 6);
                context.fillRect(-3, 0, 6, 6);
            }
        });
        layer.menus.push({
            text: "到最底层", action: shape => moveLayer(focused, s => s.page.moveIndexBottom(s)), draw: (context) => {
                context.fillStyle = "red";
                context.fillRect(0, -3, 6, 6);
                context.strokeStyle = "white";
                context.strokeRect(-1.5, -1.5, 6, 6);
                context.fillStyle = "silver";
                context.fillRect(-1.5, -1.5, 6, 6);
                context.strokeStyle = "white";
                context.fillStyle = "gray";
                context.strokeRect(-3, 0, 6, 6);
                context.fillRect(-3, 0, 6, 6);
            }
        });
        menus.push(layer);
        return menus;
    };

    /**
     * 将该形状保存为png文件
     */
    self.downloadImage = () => imageSaver(self.page, self.drawer.parent).download();

    /**
     * 将该文件转换成图形格式，没有下载保存
     */
    self.toImage = handle => {
        imageSaver(self, self.drawer.parent).toPng(handle);
    };
    self.getSnapshot = () => self.drawer.getSnapshot();

    /**
     * 鼠标拖动图形离开窗口后返回时调用，用于拖动图形到新的位置
     *
     * @param x 新位置的鼠标x坐标
     * @param mouseOffsetX x轴上鼠标位置距离图形(0,0)坐标偏移量
     * @param y 新位置的鼠标y坐标
     * @param mouseOffsetY y轴上鼠标位置距离图形(0,0)坐标偏移量
     * @param after 有值则不调用self.moved()
     */
    self.onReturnDrag = (x, mouseOffsetX, y, mouseOffsetY, after) => {
        self.moveTo(x - mouseOffsetX, y - mouseOffsetY, after);
    }

    //-----------------text operation---------------------------
    self.setSelectedTextForeColor = color => document.execCommand("foreColor", "false", color);
    self.setSelectedTextFontSize = size => document.execCommand("fontSize", "false", size);
    self.setSelectedTextBackColor = color => document.execCommand("hiliteColor", "false", color);
    self.createUnorderList = () => document.execCommand("insertUnorderedList");
    self.createOrderList = () => document.execCommand("insertOrderedList");

    //------------events---------------------

    self.editing = (edit, e) => self.runCode("editingCode");
    self.edited = editor => {
        self.runCode("editedCode");
    };

    self.onCut = (shapes) => {
    };

    /**
     * 调用select方法才会触发selected
     * 与focused不同，focsued是isFocused==true才会触发是时间
     * selected一定伴随focused，focused不一定有selected
     */
    self.selected = () => {
        self.runCode("selectedCode");
        if (!self.hideText) {
            self.beginEdit();
        }
    };

    /**
     * 调用unselect方法才会触发unselected
     * 与unfocused不同，unfocsued是isFocused==false才会触发是时间
     * unselected一定伴随unfocused，unfocused不一定有unselected
     */
    self.unselected = () => {
        self.runCode("unSelectedCode");
        if (!self.hideText) {
            self.endEdit();
        }
    };

    self.getFocused = () => {
        return self;
    };

    self.beforeRemove = source => {
        // 调用drawer的beforeRemove方法.
        self.drawer.beforeRemove(source);
        let result = self.runCode("beforeRemoveCode");
        return result === undefined ? true : result;
    };

    self.afterRemoved = source => {
        self.runCode("afterRemovedCode");
        // 销毁上下文菜单
        // 先销毁画布上的的上下文菜单
        self.page.contextToolbar && self.page.contextToolbar.destroy();
    }

    self.effectLines = () => {
        _effectLines(self);
    };
    self.effectGroup = () => {
        const parent = self.getContainer();
        if (parent.autoAlign) {
            parent.invalidate();
        }
    };

    self.resized = () => {
        if (self.page.disableReact) {
            return;
        }
        self.effectLines();
        self.effectGroup();
        self.invalidateAlone();
        self.runCode("resizedCode");
    };

    self.animate = () => {
        self.runCode("animateCode");
    };

    self.moved = () => {
        self.drawer.move();
        self.effectLines();
        if (self.parentMoving) {
            return;
        }
        if (self.page.disableReact) {
            return;
        }
        self.effectGroup();
        self.runCode("movedCode");
    };

    /**
     * trigger when page.mode changed: configuration or runtime;
     */
    self.modeChanged = mode => {
    };

    /**
     * 当isFocused==true时触发
     */
    self.focused = () => {
        //todo: self.page.communication.invoke("lock_shape", { shape: self.id });
        self.runCode("focusedCode");
    };

    /**
     * 当isFocused==false时触发
     */
    self.unfocused = () => {
        //todo: self.page.communication.invoke("unlock_shape", { shape: self.id });
        self.runCode("unfocusedCode");
    };

    //self.zoomed = (scale) => { };

    self.containerChanged = (preValue, value) => self.runCode("containerChangedCode");

    self.textChanged = (value, preValue) => self.runCode("textChangedCode");

    self.getRotateAble = () => self.rotateAble && self.getContainer().dockMode === DOCK_MODE.NONE && self.pDock === PARENT_DOCK_MODE.NONE;

    /**
     * 用于界面配置时自动生成配置项
     * 这段代码可以放到配置portal里
     * 辉子 2021
     */
    self.configFactory = configurationFactory();
    self.getConfigurations = () => {
        return Array.from(self.serializedFields).map(f => self.configFactory.get(f, self)).filter(c => !!c);
    }

    setSerialize(self);

    setDetection(self, detections);

    self.ifInConfig = () => {
        return false;
    }

    self.isInConfig = () => {
        return ((self.page !== undefined && self.ifInConfig()) || self.ignorePageMode);
    };
    self.isLocked = () => {
        const editBy = (self.editBy && self.editBy.length > 0) ? self.editBy[0] : undefined;
        return self.page.graph.allowLock === true && (editBy !== undefined) && (editBy.id !== self.page.graph.session.id && self.isInConfig());
    };

    /**
     * 获取属性
     * 先取得自身该属性，如果没有 则集成page该属性，如果page也没有该属性，则集成graph该属性
     * 辉子 2021
     */
    self.get = field => {
        let value = self._data[field];
        // if (value === undefined) {
        //     value = self.page === undefined ? undefined : self.page.setting[field];
        // }
        if (value === undefined) {
            value = self.graph === undefined ? undefined : self.graph.setting[field];
        }
        switch (field) {
            // case "selectable":
            case "deletable":
            case "resizeable":
            case "moveable":
            case "editable":
                return value && self.isInConfig();
            case "selectable":
                return value && !self.isLocked() && self.isInConfig();
            // case "borderWidth":
            //     if (self.page.mode === PAGE_MODE.CONFIGURATION && value === 0 && self.container === self.page.id && self.dashWidth !== 0) return 1;
            //     else return value;
            // case "dashWidth":
            //     if (self.page.mode === PAGE_MODE.CONFIGURATION && self.borderWidth === 0) return 5;
            //     else return value;
            default:
                return value;
        }
    };

    /**
     * 获取边框宽度.
     *
     * @returns {*} 边框宽度.
     */
    self.getBorderWidth = () => {
        return self.isFocused ? self.focusBorderWidth : self.borderWidth;
    };

    //--------------load code---------------
    self.runCode = code => {
        _runCode(self, code)
    };
    self.isLoaded = () => detections.length > 0;

    self.load = (ignoreFilter = ((property) => {
    })) => {
        setCoEdit(self, ignoreFilter);
        self.runCode("loadCode");
    };

    //-------------initialization---------------------------
    self.initialize = args => {
    };
    self.initialized = args => {
    };

    /**
     * 当图形被创建之后的声明周期函数.
     * deserialize时，只会调用page.createShape方法，其中不会调用图形的initialize方法，因此有些逻辑不能只放在initialize中.
     * {@see page#createShape}
     */
    self.created = () => {
    };

    setTextOperation(self);
    self.ifDrawFocusFrame = () => {
        return false;
    };

    /**
     * 获取选中时的优先级.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     * @return {*|number} 优先级数值.
     */
    self.getSelectPriority = (x, y) => {
        if (self.page.selectionStrategy === SELECTION_STRATEGY.BRING_TO_FRONT) {
            if (self.isFocused) {
                return 10000;
            }
            // page的index是undefined，所以这里要做特殊处理.
            return self.index ? self.index : 0;
        } else {
            if (self.isFocused && self.priorityContains(x, y)) {
                return 10000;
            }
            // page的index是undefined，所以这里要做特殊处理.
            return self.index ? self.index : 0;
        }
    };

    setLocalCollaboration(self);
    return self;
};

const setLocalCollaboration = (shape) => {
    /**
     * 接收到本地协同消息，发生了数据变化(例如，ppt场景下的主画布和缩略图之间的本地协同).
     *
     * @param data 变化的数据.
     */
    shape.onMessageDataChange = (data) => {
        shape.invalidateAlone();
    };
};

const _getString = (node) => {
    if (node.children) {
        return node.children.map(_getString).join("");
    }
    return node.data ? node.data : "";
}

/**
 * 设置文本相关操作.
 *
 * @param shape 图形对象.
 */
const setTextOperation = (shape) => {
    shape.bold = false;
    shape.italic = false;
    // shape.strikethrough = false;
    // shape.underline = false;
    // shape.numberedList = false;
    // shape.bulletedList = false;

    shape.format = (key, value) => {
        if (shape.hideText) {
            return;
        }
        const editor = shape.drawer.getEditor();
        editor.format(key, value);
    };

    shape.getFormatValue = (key) => {
        if (shape.hideText) {
            return null;
        }
        const editor = shape.drawer.getEditor();
        return editor.isFocused() ? editor.getFormatValue(key) : shape.get(key);
    };

    /**
     * 当文本属性发生变化时调用.
     * attributes的格式为:
     * {
     *     bold: true,
     *     italic: false
     *     ...
     * }
     *
     * @param attributes 属性集合.
     */
    shape.onTextAttributeChange = (attributes) => {
        const changedAttributes = {};
        Object.keys(attributes).forEach(key => {
            if (!shape.serializedFields.has(key)) {
                return;
            }

            if (shape[key] !== attributes[key]) {
                changedAttributes[key] = attributes[key];
            }
        });
        const layoutData = {shape, ...changedAttributes};
        layoutCommand(shape.page, [layoutData]).execute(shape.page);
    };

    /**
     * 判断位置是否在文本中：
     *
     * @param position 位置信息.
     */
    shape.isOnText = (position) => {
        return !shape.hideText
            && !shape.drawer.isTextPointerEventsDisabled()
            && shape.drawer.containsText(position.x, position.y);
    };

    /**
     * shape处于编辑状态下，当enter键被按下后的事件
     * huiz 2020
     */
    shape.editEnterPressed = () => {
    };

    /**
     * 开始编辑.
     * * 注意 * 一个图形在开始编辑之前，默认都是通过innerHtml进行渲染（提升性能）.只有第一次编辑过后，才通过编辑器渲染文本.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     * @param autoFocus 是否自动聚焦.
     * @return {*}
     */
    shape.beginEdit = function () {
        let first = false;
        return (x, y, autoFocus = false) => {
            if (shape.hideText || shape.isEdit) {
                return;
            }

            if (!first) {
                shape.drawer.renderTextByEditor(shape.text, autoFocus);
                first = true;
            }
            shape.isEdit = true;
            shape.enableTextPointerEvents();
        };
    }();

    /**
     * 结束编辑
     * 将编辑后value赋值shape
     * huiz 2020
     */
    shape.endEdit = () => {
        if (shape.hideText || !shape.isEdit) {
            return;
        }
        shape.isEdit = false;
        shape.edited(shape.drawer.text);
        shape.disableTextPointerEvents();
    };

    shape.enableTextPointerEvents = () => {
        shape.drawer.enableTextPointerEvents();
    };

    shape.disableTextPointerEvents = () => {
        shape.drawer.disableTextPointerEvents();
    };

    /**
     * 是否启用使用html来绘制文本.默认为true.
     * 这里使用方法的原因是为了兼容老数据，如果加入新的字段来进行控制，那么老数据中是没有该字段的。在不进行数据修正的情况下，通过增加方法来控制。
     *
     * @returns {boolean} true/false
     */
    shape.isEnableHtmlText = () => {
        return true;
    };

    /**
     * 在图形编辑态的时候手动修改text。
     * @param text
     */
    shape.setTextManually = (text) => {
        shape.text = text;
        if (shape.isEditing()) {
            shape.drawer.renderText();
        }
    }

    /**
     * 获取图形文本数据(纯字符串).
     *
     * @returns {*|string|string} 字符串.
     */
    shape.getShapeText = () => {
        if (typeof shape.text === 'string') {
            return shape.text;
        } else {
            return _getString({children: shape.text})
        }
    }
};

const cachePool = (maxSize, createNew, resume, resumeFilter, ignoreInvalidate) => {
    const cacheWrapper = (self, resume) => {
        self.idle = () => !self.visible;
        self.remove = () => {
            return self.visible = false;
        };
        self.resume = function () {
            const args = Array.from(arguments);
            args.unshift(self);
            resume.apply(this, args);
            self.visible = true;
        }
    }
    return {
        values: [], max: maxSize, show: function () {
            let self = this;
            let args = [];
            if (resumeFilter) {
                args = Array.from(arguments);
                args.unshift(null);
            }
            const idle = self.values.find(s => {
                if (!s.idle()) {
                    return false;
                }
                if (!resumeFilter) {
                    return true;
                }
                args[0] = s;
                return resumeFilter.apply(this, args);
            });
            if (idle) {
                idle.delayInvalidate(() => {
                    idle.resume.apply(this, arguments);
                }, ignoreInvalidate)
                return idle;
            }
            if (self.values.length < self.max) {
                let obj = createNew.apply(this, arguments);
                cacheWrapper(obj, resume);
                self.values.push(obj);
                return obj;
            }
        }
    }
}

let _runCode = (shape, code) => {
    if (shape.get(code) === undefined) {
        return;
    }
    try {
        eval("(async " + shape.get(code) + ")(shape.page, shape);");
    } catch (e) {
        console.warn("user input code execute error:\n" + e);
    }
};

let _effectLines = (shape) => {
    shape.manageConnectors();
    shape.page.shapes
            .filter(s => s.fromShape === shape.id || s.toShape === shape.id)
            .forEach(s => s.onEffect && s.onEffect());
};

const convertScrollWithParents = (s, pos) => {
    const parent = s.getContainer();
    if (parent === s.page) {
        return;
    }
    pos.x -= parent.itemScroll.x;
    pos.y -= parent.itemScroll.y;
    convertScrollWithParents(parent, pos);
};

const setContains = shape => {
    /**
     * 为鼠标命中某个形状
     * 判定某个坐标点是否可以捕获该形状
     * 依据几个方面捕获：坐标点是否在形状的框里 + （命中了背景 || 命中的边框 || 命中的文字 || 命中了点击区）
     * 各个命中方法在不同形状里可能有多态实现：rectangle，line，container
     * 辉子 2020
     */
    shape.contains = (x, y) => {
        if (isNaN(x) || isNaN(y)) {
            return false;
        }

        let pos = convertPositionWithParents(shape, x, y);//处理旋转
        convertScrollWithParents(shape, pos);

        if (!isPointInRect(pos, shape.getBound())) {
            return false;
        }
        if (shape.drawer.containsBorder && shape.drawer.containsBorder(pos.x, pos.y)) {
            return true;
        }
        if (shape.containsRegion(x, y)) {
            return true;
        }
        if (shape.drawer.containsBack && shape.drawer.containsBack(pos.x, pos.y)) {
            return true;
        }
        if (shape.drawer.containsText && shape.drawer.containsText(pos.x, pos.y)) {
            return true;
        }
        return false;
    };

    /**
     * 是否命中优先级高的区域.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     * @return {*|boolean} true/false.
     */
    shape.priorityContains = (x, y) => {
        let pos = convertPositionWithParents(shape, x, y);//处理旋转
        convertScrollWithParents(shape, pos);
        return shape.drawer.containsBorder(pos.x, pos.y)
                || shape.containsRegion(x, y)
                || shape.getMouseOnConnector(x, y) !== null
    };

    /**
     * 粗略找到形状，然后用contains精确匹配
     * 为提高find性能
     */
    shape.roughContains = (x, y) => {
        if (isNaN(x) || isNaN(y)) {
            return false;
        }
        let pos = convertPositionWithParents(shape, x, y);
        convertScrollWithParents(shape, pos);
        return isPointInRect(pos, shape.getBound());
    };
}

let setSerialize = shape => {
    shape.serialize = () => {
        return shape._data;
    };

    shape.serialized = () => {
    };
    shape.deSerialized = shape => {
    };
    //-------------------end of serialization----------------
    shape.deSerialize = (serialized) => {
        shape.page.ignoreReact(() => {
            let isReady = shape.page.isReady;
            shape.page.isReady = false;
            shape._data = serialized;
            shape.load();
            shape.deSerialized(shape);
            shape.page.isReady = isReady;
        });
    };
}

let setDetection = (shape, detections) => {
    shape.clearDetections = () => detections.splice(0, detections.length);
    shape.clearDetections();

    /**
     * 删除对某个属性的监听器.
     *
     * @param propertyKey 属性名.
     */
    shape.removeDetection = (propertyKey) => {
        detections.forEach((d, index) => {
            if (d.props.has(propertyKey)) {
                d.props.size === 1 ? detections.splice(index, 1) : d.props.delete(propertyKey);
            }
        });
    }

    shape.addDetection(["visible", "background"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        shape.invalidateAlone();
    });

    shape.addDetection(["editBy"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        if (value !== undefined && preValue !== undefined && value.length === preValue.length) {
            value.orderBy("id");
            preValue.orderBy("id");
            let identical = true;
            for (let i = 0; i < value.length; i++) {
                if (preValue[i].id !== value[i].id) {
                    identical = false;
                    break;
                }
            }
            if (identical) {
                return;
            }
        }
        if (value === undefined) {
            shape.regions.remove(s => s.type === "lock");
        } else {
            // value.filter(u=>u.id!==shape.page.graph.session.id).forEach((u,i)=>{
            shape.regions.remove(r => r.type === "lock" && !value.contains(u => u.id === r.userId));
            value.filter(u => u.id !== shape.page.graph.session.id).forEach((u, i) => {
                const region = shape.regions.find(r => r.userId === u.id);
                if (region) {
                    region.index = i;
                } else {
                    lockRegion(shape, u.id, u.name, i);
                }
            })
        }
        shape.drawer.drawRegions();
    });

    shape.addDetection(["shareAble"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        shape.drawer.drawRegions();
    });

    shape.addDetection(["isFocused"], (property, value, preValue) => {
        if (value !== preValue) {
            // 触发一次focused change事件，内容不重要
            shape.page.triggerEvent({type: EVENT_TYPE.FOCUSED_SHAPE_CHANGE, value: [shape]});
            shape.page.triggerEvent({type: EVENT_TYPE.CONTEXT_CREATE, value: []});
        }
        // 合并后的代码，暂时注释 todo@xiafei
        // if (shape.isFocused !== preValue) {
        //     shape.render();
        // }
        shape.isFocused = value && shape.getSelectable();
        if (shape.isFocused) {
            shape.focused && shape.focused();
            if (shape.isInConfig()) {
                shape.page.graph.session.page = shape.page.id;
                shape.page.graph.session.shape = shape.id;//用该属性代替lockedBy
            }
        } else {
            shape.unfocused();
            if (shape.page.graph.session.shape === shape.id) {
                shape.page.graph.session.shape = undefined;
            }
        }

        if (shape.isFocused !== preValue) {
            shape.invalidateAlone();
        }
    });

    shape.addDetection(["text"], (property, value, preValue) => {
        if (shape.page) {
            shape.page.textChanged(shape, value, preValue);
            shape.textChanged(value, preValue);
        }
        shape.drawer && shape.drawer.renderText();
    });

    shape.addDetection(["id"], (property, value, preValue) => {
        if (!shape.isTypeof('container')) {
            return;
        }
        shape.page.shapes.filter(s => s.container === preValue).forEach(s => s.container = value);
        shape.page.graph.resetElementId(value, preValue);
    });

    shape.addDetection(["container"], (property, value, preValue) => {
        // 这里的第二个判断条件 value === ""的原因如下:
        // 1、在presentation的场景下，presentationPage中的childAllowed中会判断是否是presentationFrame
        // 2、如果被删除图形不是presentationFrame，那么就会调用shape.page.moveToContainer方法，重新给图形设置container.
        // 3、这样就会导致图形无法被删除.
        if (value === preValue || value === "") {
            return;
        }

        if (!shape.containerAllowed(shape.getContainer()) || !shape.getContainer().childAllowed(shape)) {
            shape.page.moveToContainer(shape, preValue);
            return;
        }

        if (preValue) {
            shape.preContainer = preValue;
            const previousContainer = shape.page.id === preValue ? shape.page : shape.page.getShapeById(preValue);
            if (previousContainer && previousContainer.isTypeof("container")) {
                previousContainer.shapeRemoved(shape, value);
            }
        }

        shape.getContainer().shapeAdded(shape, preValue);
        shape.containerChanged(preValue, value);
    });

    shape.addDetection(["rotateDegree"], (property, value, preValue) => {
        shape.drawer.transform();
        // shape.page.invalidateInteraction();
    });

    shape.addDetection(["backColor", "backAlpha", "cornerRadius", "dashWidth"], (property, value, preValue) => {
        if (preValue === value) {
            return;
        }
        if (shape.drawer !== undefined) {
            shape.render();
        }
    });

    shape.addDetection(["fontColor"], (property, value, preValue) => {
        if (preValue === value) {
            return;
        }
        if (shape.drawer !== undefined) {
            // todo@zhangyue 临时ict演示用，后面改回来.
            if (shape.type === "shapeComment") {
                shape.render();
            } else {
                // shape.doCommand(property, value);
            }
        }
    });

    shape.addDetection(["background"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        shape.drawer.backgroundRefresh();
    });

    shape.addDetection(["inDragging", "mousedownConnector"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }

        /*
         * * 注意 *
         * 当图形处于拖动状态下，或选中了connector时，需要将文本的pointerEvents事件设置为null
         * 否则，会导致鼠标不跟手，会导致坐标计算出现误差（具体原理暂不清楚）
         */
        if (value) {
            shape.drawer.disableTextPointerEvents();
        } else {
            if (shape.isFocused && shape.isEditing()) {
                shape.drawer.enableTextPointerEvents();
            }
        }
    });

    shape.getDetections = () => detections;
}

let setCoEdit = (shape, ignoreFilter) => {
    //shape.isCoEditing = false;//id !== undefined;//是否正在同步共享编辑的数据
    shape.allowCoEdit = true;//允许共享编辑，或者说是否需要共享编辑
    /**
     * 该shape是否已经修改
     */
    shape.dirty = false;
    /**
     * 相应协同属性变化，默认重绘
     * 辉子 2021
     */
    shape.reactCoEdit = () => shape.invalidateAlone();

    //判定该形状是否有属性改变了（若属性是数组或object，则无法响应变化，需手动触发该方法)
    //self.addDetection(self.serializedFields, (property, value, preValue) => {
    shape.propertyChanged = (property, value, preValue) => {
        if (value === preValue) {
            return false;
        }
        if (!shape.page.isReady) {
            return false;
        }
        //if (self.page.history.doing) return;
        if (!shape.serializable) {
            return false;
        }
        if (!shape.serializedFields.has(property)) {
            return false;
        }
        if (ignoreFilter && ignoreFilter(property, value, preValue)) {
            return false;
        }
        // if (shape.page.isCoEditing) {
        //     return;
        // }
        // if (shape.page.graph.collaboration.communicator.receiving) {
        //     return;
        // }

        shape.dirty = true;

        //send message to share with co-editors
        if (!(shape.allowCoEdit && shape.page.allowCoEdit)) {
            return true;
        }
        if (shape.page.ignoreCoEditFields && shape.page.ignoreCoEditFields.contains(f => f === property)) {
            return true;
        }
        if (shape.ignoreCoEditFields && shape.ignoreCoEditFields.contains(f => f === property)) {
            return true;
        }

        let id = shape.id;
        if (property === "id") {
            id = preValue;
        }//改 id是件讨厌的事情
        (!shape.page.dirties) && (shape.page.dirties = {});
        const sharedParent = shape.sharedParent();
        if (!shape.page.dirties[id]) {
            shape.page.dirties[id] = {};

            /*
             * 这里需要使用shape身上静态的pageId属性，否则，在page.sendDirties()中，可能会取到错误的pageId.
             * 例如：
             * 1、打开page1，异步渲染图形或修改图形属性
             * 2、切换页面到page2，此时对图形属性的修改还在继续
             * 3、触发propertyChanged方法，生成dirties
             * 4、page.sendDirties()中获取到dirties，并设置pageId，此时的pageId是page2
             * 5、page2消费到了page1的改动信息，生成错误图形
             */
            shape.page.dirties[id].pageId = shape.pageId || (shape.isTypeof("page") ? shape.id : null);
            shape.page.dirties[id].inShared = sharedParent.id;//标识该shape是不是隶属shared shape，所有属性需要share
        }
        if (value === null) {
            (!shape.page.dirties[id]["nullProperties"]) && (shape.page.dirties[id]["nullProperties"] = []);
            shape.page.dirties[id]["nullProperties"].push(property);
        } else {
            shape.page.dirties[id][property] = value;// isNumeric(value) ? Math.round(value) : value;
            //only happen when shape is in shared
            if (property === "x" && sharedParent.id !== "" && sharedParent !== shape.page) {
                shape.page.dirties[id]["dx"] = shape.x - sharedParent.x;
            }
            if (property === "y" && sharedParent.id !== "" && sharedParent !== shape.page) {
                shape.page.dirties[id]["dy"] = shape.y - sharedParent.y;
            }
        }
        shape.addFixedDirtyProperties(id);
        return true;
    };
    shape.addFixedDirtyProperties = (id) => {
        shape.page.dirties[id]["type"] = shape.type;
    }
}

let setConnector = shape => {
    shape.connectors = undefined;//[];

    /**
     * 找到鼠标在的connector
     * 鼠标命中优先级：connector，region，shape
     * 辉子 2020
     */
    shape.getMouseOnConnector = (x, y, condition = () => true) => {
        if (!shape.isFocused && !shape.linking) {
            return null;
        }
        if (shape.connectors === undefined) {
            return null;
        }
        let pos = convertPositionWithParents(shape, x, y);
        pos.x -= shape.x;
        pos.y -= shape.y;
        // 不可见的connector，也不可以被拖拽
        let conn = shape.connectors.reverse().find(connector => connector.getVisibility() && isPointInRect(pos, connector.getHitRegion()) && condition(connector));
        return conn === undefined ? null : conn;
    };

    /**
     * 获取坐标距离xy最近的connector.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     * @param condition 条件函数.
     * @return {*|null}
     */
    shape.getClosestConnector = (x, y, condition = () => true) => {
        if (!shape.isFocused && !shape.linking) {
            return null;
        }
        if (shape.connectors === undefined) {
            return null;
        }
        let pos = convertPositionWithParents(shape, x, y);
        pos.x -= shape.x;
        pos.y -= shape.y;

        let connector = null;
        let distance = undefined;
        shape.connectors.reverse().filter(c => c.getVisibility() && condition(c)).forEach(c => {
            const d = getDistance(pos.x, pos.y, c.x, c.y);
            if (distance === undefined || d < distance) {
                distance = d;
                connector = c;
            }
        });

        return connector;
    };

    /**
     * 初始化connectors:创建出对应的connectors
     * 不同的shape的connctor是不一样，比如line是收尾各一个connector，所有以rectangle为基类的shape有9个connector（参考rectangle.initConnectors)
     * 辉子 2021
     */
    shape.initConnectors = () => {
        shape.connectors = [];
    };

    shape.getConnectors = () => {
        (shape.connectors === undefined) && shape.initConnectors();
        return shape.connectors;
    }

    /**
     * 重新整理connector的位置，在shape resize之后
     * 不重新创建connector
     */
    shape.manageConnectors = () => {
        shape.getConnectors().forEach(c => c.refresh());
    };
}

let setRegion = shape => {
    shape.regions = [];//热区，可以点击，事件

    shape.addRegion = (region, index) => {
        if (index === undefined) {
            shape.regions.push(region);
        } else {
            shape.regions.insert(region, index);
        }
        for (let i = 0; i < shape.regions.length; i++) {
            shape.regions[i].index = i;
        }
    };
    shape.removeRegion = (index) => {
        shape.regions.splice(index, 1);
        for (let i = 0; i < shape.regions.length; i++) {
            shape.regions[i].index = i;
        }
        shape.drawer.drawRegions();
    };
    shape.containsRegion = (x, y) => shape.regions.find(r => r.visible && r.isMouseIn(x, y) && r.getSelectable()) !== undefined;

    /**
     * 找到鼠标在该shape命中的region
     */
    shape.getHitRegion = (x, y, pCursor) => {
        const pos = convertPositionWithParents(shape, x, y);

        // 这里需要遍历当前图形的父元素，看是否存在恰当的region.
        let region = null;
        let parent = shape;
        while (!region && parent !== shape.page) {
            region = parent.regions.reverse().find(r => r.getVisibility() && r.isMouseIn(pos.x, pos.y) && r.getSelectable(), null);
            parent.regions.reverse();
            parent = parent.getContainer();
        }

        if (shape.getMouseOnConnector(x, y) !== null) {
            region = null;
        }//如果有connector，不选中region
        if (!region) {
            region = {
                click: (x, y) => shape.click(x, y),
                dbClick: (x, y) => shape.dbClick(x, y),
                cursor: pCursor,
                editable: false,
                dubble: true,
                isMock: true
            }
            return region;
        } else {
            return {
                click: (x, y) => {
                    shape.page.regionClick(region);
                    region.click(x, y);
                }, dbClick: (x, y) => () => {
                    shape.page.regionDbClick(region);
                    region.dbClick(x, y);
                }, cursor: region.cursor, editable: region.editable, text: region.text, type: region.type
            };
        }
    };

    //设置独占锁region
    // lockRegion(shape);
}

let setMouseActions = (shape) => {
    shape.click = (x, y) => {
        (shape !== shape.page) && shape.page.playAudio("click");
        shape.runCode("clickCode");
        console.log(shape.type + " shape click " + "code:" + shape.clickCode);
    };
    shape.dbClick = (x, y) => {
    };

    shape.rotatePosition = position => {
        const pos = convertPositionWithParents(shape, position.x, position.y);
        position.x1 = pos.x;
        position.y1 = pos.y;
        shape.mouse = {x: position.x, y: position.y, x1: pos.x, y1: pos.y};
    };

    /**
     * 多选处理逻辑.
     *
     * @param position 位置信息.
     */
    const onSelectMultiply = (position) => {
        console.log("============== welink test: shape#mousedown 3");
        shape.isFocused ? shape.unSelect() : shape.select(position.x, position.y);
    };

    /**
     * 单选处理逻辑.
     *
     * @param position 位置信息.
     */
    const onSelectIndividually = (position) => {
        console.log("============== welink test: shape#mousedown 2");
        const focusedShapes = shape.page.getFocusedShapes();
        if (shape.isFocused) {
            // 如果选中了图形的文本，则需要取消其他图形的选中状态.
            if (shape.isOnText(position)) {
                focusedShapes.filter(s => s !== shape).forEach(s => s.unSelect());

                // 如果选中了文本，需要将mousedownShape设置为null，防止图形在编辑时可以被拖动.
                shape.page.mousedownShape = null;
            }
        } else {
            focusedShapes.forEach(s => s.unSelect());
            shape.select();
        }
    }

    shape.onMouseDown = position => {
        console.log("============== welink test: shape#mousedown 1");
        // 是否是多选操作.
        (position.e.shiftKey || position.e.ctrlKey) ? onSelectMultiply(position) : onSelectIndividually(position);
        shape.mouseOffsetX = position.x - shape.x;
        shape.mouseOffsetY = position.y - shape.y;
        shape.mousedownConnector = shape.getMouseOnConnector(position.x, position.y, c => c.dragable);
        shape.mousedownConnector && shape.mousedownConnector.onMouseDown(position);
        shape.mousedownRegion = shape.getHitRegion(position.x, position.y);
        shape.runCode("mouseDownCode");
    };

    shape.onLongClick = shape.onMouseDown;

    shape.onMouseUp = async position => {
        if (shape.mousedownConnector !== null) {
            shape.page.cancelClick = true;
            await shape.mousedownConnector.release(position);
            shape.mousedownConnector = null;
        }
        if (shape.inDragging) {
            shape.inDragging = false;
            shape.reset(position.x, position.y);
            shape.endDrag(shape.page.find(position.x, position.y, s => s.isTypeof('container') && s !== shape), position);
        }
        shape.runCode("mouseUpCode");
    };
    shape.onMouseMove = position => {
        shape.runCode("mouseMoveCode");
        if (shape.page.cursor === CURSORS.PEN) {
            return;
        }

        let conn = shape.getMouseOnConnector(position.x, position.y);
        shape.mouseOnConnector = conn;

        // 类型为connection的connector，在鼠标移动时，不应该进行处理，否则会导致鼠标移动绘制异常.
        if (conn !== null && conn.type !== "connection") {
            shape.page.cursor = conn.direction.cursor;
        } else {
            const r = shape.getHitRegion(position.x, position.y, CURSORS.MOVE);
            if (r.isMock) {
                // 鼠标在图形的文本上，并且图形处于编辑态，才将cursor样式修改为TEXT.
                if (shape.isOnText(position) && shape.isEditing()) {
                    shape.page.cursor = CURSORS.TEXT;
                    return;
                }

                if (!shape.moveable) {
                    // 如果图形的cursorStyle不存在，则设置为默认.
                    if (shape.cursorStyle) {
                        shape.page.cursor = shape.cursorStyle;
                    } else {
                        shape.page.cursor = CURSORS.DEFAULT;
                    }
                    return;
                }
            }

            shape.mouseOnRegion = r;
            shape.page.cursor = r.cursor;
            shape.page.showTip(position.x, position.y, r.text, shape);
        }
    };
    shape.onMouseOut = () => {

    };
    shape.onMouseIn = () => {

    };

    shape.onMouseHold = position => {

    }

    shape.getDragable = () => shape.dragable && shape.getContainer().isAllowChildDrag();

    shape.beginDrag = () => {
    };

    shape.getDockContainerThatPermitChildDraggable = () => {
        let container = shape.getContainer();
        while (container !== shape.page) {
            if (container.dockMode !== DOCK_MODE.NONE && container.isAllowChildDrag()) {
                return container;
            }
            container = container.getContainer();
        }
        return null;
    };

    shape.endDrag = (target, position) => {
        const container = shape.getDockContainerThatPermitChildDraggable();
        if (container) {
            container.invalidate();
            container.getShapes().forEach(child => {
                const dirty = position.context.shapes.find(s => s.shape === child);
                dirty.x.value = child.x;
                dirty.y.value = child.y;
                dirty.container.value = child.container;
            });
        }

        _effectLines(shape);
    }
    shape.dragging = (target, position) => {
        // 如果存在container处于DockMode模式，并且允许子元素被拖动，则需要记录其所有子元素的位置信息，以便后续的撤销重做.
        const container = shape.getDockContainerThatPermitChildDraggable();
        if (container) {
            container.getShapes().forEach(child => {
                let dirty = position.context.shapes.find(s => s.shape === child);
                if (!dirty) {
                    dirty = {shape: child, x: {}, y: {}, container: {}};
                    dirty.x.preValue = child.x;
                    dirty.y.preValue = child.y;
                    dirty.container.preValue = child.container;
                    position.context.shapes.push(dirty);
                }
            });
            container.invalidate();
        }
    };

    shape.onConnectorDragged = connector => {
    };

    shape.isInDragging = () => {
        return shape.inDragging;
    };

    /**
     * resize图形或旋转图形.
     *
     * @param position 位置信息.
     * @param focusedShapes 选中的图形.
     */
    shape.resizeOrRotate = (position, focusedShapes) => {
        shape.page.cursor = shape.mousedownConnector.direction.cursor;
        const originalX = position.x;
        const originalY = position.y;
        const xDiff = originalX - shape.x - shape.width / 2;
        const yDiff = originalY - shape.y - shape.height / 2;
        focusedShapes.forEach(s => {
            // 如果父元素在选中图形的列表里，则不处理当前图形.
            let parent = s.getContainer();
            while (parent !== shape.page) {
                if (focusedShapes.includes(parent)) {
                    return;
                } else {
                    parent = parent.getContainer();
                }
            }
            s.mousedownConnector = s.connectors.find(c => c.isType(shape.mousedownConnector.type) && c.dragable);
            position.x = xDiff + s.width / 2 + s.x;
            position.y = yDiff + s.height / 2 + s.y;
            s.mousedownConnector && s.mousedownConnector.onMouseDrag(position);
        });
        position.x = originalX;
        position.y = originalY;
    };

    /**
     * 响应鼠标拖拽事件.
     *
     * @param position 位置信息.
     */
    shape.onMouseDrag = (position) => {
        if (!shape.isInConfig()) {
            return;
        }

        if (!shape.moveable) {
            shape.unSelect();
            shape.page.mousedownShape = shape.page;
            return;
        }

        const shapes = shape.page.getFocusedShapes().filter(s => s.getDragable());

        // resize或rotate选中的shapes
        if (shape.mousedownConnector) {
            shape.resizeOrRotate(position, shapes);
            return;
        }

        if (shape.mousedownRegion && shape.mousedownRegion.dragable) {//move region
            shape.mousedownRegion.onMouseDrag(position);
            return;
        }

        //移动选择的shapes
        shape.inDragging = true;
        shapes.forEach(s => {
            let parent = s.getContainer();
            if (parent === shape.page) {
                s.dragTo(position);
                shape.page.moveToContainer(s);
            } else {
                while (parent !== shape.page) {
                    let noDock = parent.dockMode === DOCK_MODE.NONE && s.pDock === PARENT_DOCK_MODE.NONE;
                    if (parent.isChildDragable() || noDock) {//设计上dockmode不应该出现在shape，只能出现在container，这里做了妥协，未来可以重构 huzi
                        if (!noDock) {
                            if (parent.dockMode === DOCK_MODE.VERTICAL) {
                                position.deltaX = 0;
                                if (s.y + position.deltaY < parent.y
                                    || s.y + s.height + position.deltaY > parent.y + parent.height) {
                                    position.deltaY = 0;
                                }
                            } else if (parent.dockMode === DOCK_MODE.HORIZONTAL) {
                                position.deltaY = 0;
                                if (s.x + position.deltaX < parent.x
                                    || s.x + s.width + position.deltaX > parent.x + parent.width) {
                                    position.deltaX = 0;
                                }
                            }
                        }
                        s.dragTo(position);
                        shape.page.moveToContainer(s);
                        break;
                    } else {
                        s = parent;
                        parent = parent.getContainer();
                    }
                }
            }
        });

        shape.dragging(shape.page.find(position.x, position.y, s => s.isTypeof('container') && s !== shape), position);
        shape.runCode("draggingCode");
    };
}

let setKeyAction = shape => {
    shape.numberPressed = (number) => shape.runCode("numberPressedCode");

    shape.keyPressed = e => {
        shape.runCode("keyPressedCode");
        if ((e.ctrlKey || e.metaKey) && (e.code === "KeyS")) {
            // shape.downloadImage();
            return false;
        }

        if ((e.ctrlKey || e.metaKey) && (e.code === "KeyF")) {
            shape.removeTextFormat();
            return false;
        }

        // if ((e.ctrlKey || e.metaKey) && e.code.indexOf("Digit") >= 0) {
        //     try {
        //         shape.numberPressed(parseInt(e.code.substr(5, 1)));
        //     } catch (e) {
        //         console.error(e);
        //     }
        //     return false;
        // }

        if (e.code === "Escape") {
            shape.unSelect();
            shape.restore();
        }
        if (!shape.moveable) {
            return false;
        }
        let direction = {x: 0, y: 0};
        let step = 2;
        let shiftStep = step * 10;
        if (e.key.indexOf("Left") >= 0) {
            direction.x = -step;
        }
        if (e.key.indexOf("Right") >= 0) {
            direction.x = step;
        }
        if (e.key.indexOf("Up") >= 0) {
            direction.y = -step;
        }
        if (e.key.indexOf("Down") >= 0) {
            direction.y = step;
        }
        if (e.shiftKey && e.key.indexOf("Left") >= 0) {
            direction.x = -shiftStep;
        }
        if (e.shiftKey && e.key.indexOf("Right") >= 0) {
            direction.x = shiftStep;
        }
        if (e.shiftKey && e.key.indexOf("Up") >= 0) {
            direction.y = -shiftStep;
        }
        if (e.shiftKey && e.key.indexOf("Down") >= 0) {
            direction.y = shiftStep;
        }
        if (direction.x !== 0 || direction.y !== 0) {
            // self.x += direction.x;
            // self.y += direction.y;
            const shapes = shape.page.getFocusedShapes();
            shapes.forEach(s => {
                s.moveTo(s.x + direction.x, s.y + direction.y);
            });
            // self.invalidate();
            return false;
        }
    };
}

let setCoordinateIndex = shape => {
    /**
     * 将形状放进位置缓存
     * 包括两个步骤，清除缓存和建立缓存
     * 辉子 2021
     */
    shape.indexCoordinate = () => {
        if (shape.page.isMouseDown()) {
            return;
        }
        shape.clearCoordinateIndex();
        if (shape.container === "") {
            return;
        }
        if (!shape.getVisibility()) {
            return;
        }
        shape.createCoordinateIndex();
    };

    /**
     * 清除所有自己的索引
     */
    shape.clearCoordinateIndex = () => {
        // @maliya 增加数组类型判断，部分场景发现会有对象值，导致js报错
        const areas = shape.areas && Array.isArray(shape.areas) ? shape.areas : [];
        areas.forEach(a => a.shapes.remove(s => s.id === shape.id));
        shape.areas = [];
    };

    shape.createCoordinateIndex = () => {
        const STEP = shape.page.areaStep;
        shape.areas = [];
        const frame = shape.getShapeFrame(true);
        let x1 = frame.x1;
        let y1 = frame.y1;
        const x2 = frame.x2;
        const y2 = frame.y2;
        x1 = Math.floor(x1 / STEP) * STEP;
        y1 = Math.floor(y1 / STEP) * STEP;
        for (let x = x1; x < x2; x += STEP) {
            for (let y = y1; y < y2; y += STEP) {
                const area = shape.page.addShapeToAreas(x, y, shape);

                // 双向关联，为删除性能.
                shape.areas.push(area);
            }
        }
    };

    shape.getShapeFrame = withMargin => {
        const margin = withMargin ? shape.margin : 0;
        let x1 = (shape.width > 0 ? shape.x : (shape.x + shape.width)) - margin;
        let y1 = (shape.height > 0 ? shape.y : (shape.y + shape.height)) - margin;
        let x2 = x1 + Math.abs(shape.width) + 2 * margin;
        let y2 = y1 + Math.abs(shape.height) + 2 * margin;

        let p1 = convertPositionWithParents(shape, x1, y1);
        let p2 = convertPositionWithParents(shape, x2, y1);
        let p3 = convertPositionWithParents(shape, x2, y2);
        let p4 = convertPositionWithParents(shape, x1, y2);

        return {
            x1: Math.min(p1.x, p2.x, p3.x, p4.x),
            x2: Math.max(p1.x, p2.x, p3.x, p4.x),
            y1: Math.min(p1.y, p2.y, p3.y, p4.y),
            y2: Math.max(p1.y, p2.y, p3.y, p4.y)
        };
    };
}

export {shape, cachePool};