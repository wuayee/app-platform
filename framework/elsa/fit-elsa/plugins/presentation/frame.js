import {FONT_WEIGHT, PAGE_MODE} from '../../common/const.js';
import {reference} from '../../core/reference.js';
import {containerDrawer} from '../../core/drawers/containerDrawer.js';
import {hitRegion} from "../../core/hitRegion.js";
import {shapeComment} from "../../core/rectangle.js";
import {getPixelRatio, sleep} from '../../common/util.js';

/**
 * 演讲稿中的可显示部分
 * -------------------------------
 * |       presentationPage       |
 * |  -------------------------   |
 * |  |                        |  |
 * |  |                        |  |
 * |  |  presentationFrame     |  |
 * |  |                        |  |
 * |  |                        |  |
 * |  -------------------------   |
 * |                              |
 * -------------------------------
 * 辉子 2021
 */
const presentationFrame = (id, x, y, width, height, parent) => {
    const WIDTH = 1600;
    const HEIGHT = 900;
    const TEMPLATE_COLOR = "darkOrange";
    const BORDER_COLOR = "darkgray"
    let self = reference(id, x, y, width, height, parent, containerDrawer);
    self.type = "presentationFrame";
    self.namespace = "presentation";
    self.resize(WIDTH, HEIGHT);
    self.backColor = "white";
    self.borderWidth = 0;
    self.dashWidth = 0;
    self.moveable = false;
    self.deletable = false;
    self.moveTo(0, 0);
    self.dbClick = () => {
    };
    self.allowCoEdit = false;
    self.allowLink = false;
    self.selectable = false;
    self.cursorStyle = "default";

    // 被选中也不会画出连接点.
    self.connectors = [];
    self.childAllowed = s => true;

    let referedData;
    /**
     * 优先找到本presentation里的引用page
     * 如果引用不到再到库里找
     * 辉子 2021
     */
    let loadReferenceData = self.loadReferenceData
    self.loadReferenceData = () => {
        let data = {shapes: []};
        if (self.referencePage !== "") {
            let p = self.page.graph.getPageDataById(self.referencePage);
            if (p === undefined) {
                return loadReferenceData.call(self);
            } else {
                data.shapes = p.shapes.filter(s => !(s.type !== "presentationFrame" && s.container === self.referencePage));
            }
        }
        referedData = data;
        return data;
    };

    self.shapeDeSerialized = shape => {
        if (shape.isPlaceHolder !== 3) return;
        //同步已经在页面生成placeholder，属性与模板同步，已经改变的属性不同步
        reactPlaceHolder(shape);
        const refered = referedData.shapes.find(s => s.id === shape.referenceId);//找到模板里的placeholder
        if (!refered && !shape.placedProperties) {
            shape.remove();
        } else {
            ingoreSync(() => {
                for (let f in refered) {
                    if (shape.placedProperties && shape.placedProperties[f] !== undefined) continue;
                    if (f === "isPlaceHolder") continue;
                    if (f === "container") continue;

                    // 由于引用后图形的id与模板中的id已经不一致，因此这里不再对id进行修改.
                    if (f === "id") continue;
                    shape[f] = refered[f];
                }
            });
        }
    };

    let syncIgnored = false;
    const ingoreSync = f => {
        syncIgnored = true;
        f();
        syncIgnored = false;
    }

    const reactPlaceHolder = shape => {
        const propertyChanged = shape.propertyChanged;
        shape.serializedFields.batchAdd("placedProperties");
        shape.propertyChanged = (property, value, preValue) => {
            if (!propertyChanged.call(shape, property, value, preValue)) return;
            if (syncIgnored) return;
            if (property === "placedProperties" || property === "index") return;
            const properties = {};
            if (shape.placedProperties) {
                for (let f in shape.placedProperties) {
                    properties[f] = shape.placedProperties[f];
                }
            }
            properties[property] = value;
            shape.placedProperties = properties;
        };
    };

    self.filterRefered = shapesData => {
        return shapesData.filter(s => {
            return !self.referenceData.placed || !self.referenceData.placed.contains(p => p === s.id);
        });
    };

    self.checkReferedShape = shape => {
        if (shape.isPlaceHolder) {
            shape.serializable = true;
            shape.container = self.id;
            !self.referenceData.placed && (self.referenceData.placed = []);
            if (!self.referenceData.placed.contains(p => p === shape.referenceId)) {
                const data = {};
                for (let f in self.referenceData) {
                    data[f] = self.referenceData[f];
                }
                data.placed.push(shape.referenceId);
                self.referenceData = data;
            }
            shape.isPlaceHolder = 3;//二进制11
            reactPlaceHolder(shape);
            return false;
        }
        return true;
    }

    /**
     * 始终让frame居中
     * 辉子 2021
     */
    let mediate = () => {
        self.moveTo(0, 0);
        const x = (self.page.width - self.width * self.page.scaleX) / (2 * self.page.scaleX);
        const y = (self.page.height - self.height * self.page.scaleY) / (2 * self.page.scaleY);
        self.page.moveTo(x, y);
    };

    self.managePageComment = () => {
        // if (!self.page.graph.enableSocial || self.page.mode !== PAGE_MODE.VIEW) {
        //     return;
        // }
        if (self.container !== self.page.id) {
            return;
        }
        let pc = self.page.shapes.find(s => s.id === "pageComment");
        if (!pc) {
            pc = self.page.ignoreReact(() => pageComment(self));
        }
        pc.container = self.id;
        pc.visible = self.page.graph.enableSocial & self.page.mode === PAGE_MODE.VIEW;
        pc.invalidate();
    }

    let invalidateAlone = self.invalidateAlone;
    self.getIfMaskItems = () => {
        return true;
    }
    self.invalidateAlone = () => {
        mediate();
        // @maliya 编辑态模式下无需显示评论框，避免因评论框图形导致 图形之间切换图层顺序 发生混乱
        self.managePageComment();
        self.ifMaskItems = self.getIfMaskItems();
        if (self.page.isTemplate) {
            self.borderColor = TEMPLATE_COLOR;
        } else {
            self.borderColor = BORDER_COLOR;
        }
        invalidateAlone.call(self);
    }

    /**
     * 重写getSelectable方法，当演示模式和view模式下，允许用户操作
     * @returns {boolean}
     */
    // self.getSelectable = () => {
    //     return (self.page.mode === PAGE_MODE.VIEW && self.container === self.page.id) || self.isInConfig();
    // }

    /**
     * 重写getConfigurations方法.page和shape的配置不一致.
     *
     * @return {*[]} 配置数组.
     */
    self.getConfigurations = () => {
        const backColorConfig = self.configFactory.get("backColor", self);
        backColorConfig.group[0].name = "设置背景格式";
        backColorConfig.group[1].name = "";
        return [backColorConfig];
    }

    //-----------------------field change detection---------------------
    // self.serializedFields.batchAdd("pros");
    // self.serializedFields.batchAdd("cons");
    // self.serializedFields.batchAdd("socialCode");// 赞，踩，评价发生后会触发该代码
    // self.serializedFields.batchDelete("x", "y", "borderColor");

    self.addDetection(["width", "height"], (property, value, preValue) => mediate());
    self.addDetection(["referencePage"], (property, value, preValue) => {
        /*
         * 在协同场景下，当无页面场景下，协同方新建一页，会触发两次该方法，导致页面展示异常。问题原因：
         * 1、此时refer会重复创建图形，由于图形已存在，并且覆写了containerAllowed方法，此时直接从缓存中获取图形.
         * 2、获取到图形后在page.createShape()中会调用containerAllowed方法，导致返回false.
         *     a、导致false的原因是因为从缓存中获取到shape之后，会重新设置图形的container.
         *     b、对模板图形来说，重新设置container之后就会出现问题.
         * 3、由于container不匹配会返回null，导致出现异常.
         * 所以这里需要对重复的赋值进行过滤.若basePage一致，则不再执行.
         */
        if (preValue === value) {
            return;
        }
        self.refer();
    });

    /**
     * 重写initialize方法.
     * 1、将referenceVector的allowLink设置为false.
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        updateReferenceVector();
    };

    /**
     * 重写reset方法.
     * 1、当发生分页操作时，不会触发initialize方法，只会触发reset方法.因此这里也需要重写reset方法，来设置referenceVector的值.
     */
    const reset = self.reset;
    self.reset = () => {
        reset.apply(self);
        updateReferenceVector();
    };

    const updateReferenceVector = () => {
        self.getShapes().filter(s => s.isType("referenceVector")).forEach(s => {
            s.allowLink = false;
            s.selectable = false;
        });
    };

    self.prosRegion = prosRegion(self, () => 10, () => self.height - 75);
    self.consRegion = consRegion(self, () => 80, () => self.height - 75);

    return self;
};

/**
 * 点赞和点踩的基类
 * 辉子 2021
 */
const situationRegion = (shape, x, y, procons, color) => {
    let getx = (shape, region) => shape.x + x();
    let gety = (shape, region) => shape.y + y();
    let getWidth = (shape, region) => self.width * (getPixelRatio() > 1 ? 1.5 : 1);
    let getHeight = (shape, region) => self.height * (getPixelRatio() > 1 ? 1.5 : 1);

    let self = hitRegion(shape, getx, gety, getWidth, getHeight);
    self.max = 100;
    self.width = self.height = 48;
    self.offset = {};

    self.getVisibility = () => (shape.container === shape.page.id) && shape.page.allowPresent();// && shape.page.graph.enableSocial;
    let r = 2;
    self.drawStatic = context => {
        context.beginPath();
        context.font = "normal bold 18px itim";
        context.lineWidth = 1;
        context.fillStyle = shape.page.graph.enableSocial ? color : "gray";
        let number = shape.page[procons] === undefined ? 0 : shape.page[procons];
        number = number > 9999 ? 9999 : number;
        let width = context.measureText(number).width;
        context.fillText(number, (self.width - width) / 2, 46);
        context.strokeStyle = "white";
        context.strokeText(number, (self.width - width) / 2, 46);
    };

    /**
     * 点击图标事件
     * 辉子 2021
     */
    self.click = async () => {
        if (!shape.page.graph.enableSocial) {
            return;
        }
        shape.page.graph.collaboration.invoke({
            method: "appreciate", mode: shape.page.mode, page: shape.page.id, value: {procons}
        });
        self.run();
        shape.page.graph.enableSocial = false;
        shape.invalidateAlone();
        await sleep(2000);
        shape.page.graph.enableSocial = true;
        shape.invalidateAlone();
    };

    /**
     * 点击图标后开始运行基类
     * 允许用户代码，但目前调不到，供未来扩展用
     * 辉子 2021
     */
    self.run = () => {
        self.running = true;
        // if (shape.socialCode === undefined) return;
        // try {
        //     eval(shape.socialCode);
        // } catch (e) {
        //     console.warn("social code executing error:\n" + e);
        // }
    };

    return self;
};
/**
 * 点赞图标
 * 放烟花动画
 * 辉子 2021
 */
const prosRegion = (shape, x, y) => {
    const STEPS = 10, WIDTH = 100;
    let self = situationRegion(shape, x, y, "pros", "darkgreen");
    self.type = "pros";
    self.offset = {x, y, width: self.width, height: self.height};

    let drawStatic = self.drawStatic;
    self.drawStatic = context => {
        drawStatic.call(self, context);
        let p = (shape.pros === undefined ? 0 : shape.pros) / self.max;
        if (p > 1) {
            p = 1;
        }
        context.strokeStyle = shape.page.graph.enableSocial ? "whitesmoke" : "gray";
        let g = context.createLinearGradient(0, 0, 48, 48);
        if (shape.page.graph.enableSocial) {
            g.addColorStop(0, "rgba(0,100,0," + (p / 2 + 0.5) + ")");
            g.addColorStop(1, "rgba(0,100,0," + p + ")");
        } else {
            g.addColorStop(0, "whitesmoke");
            g.addColorStop(1, "lightgray");

        }
        context.fillStyle = g;
        context.beginPath();
        context.moveTo(self.width / 2, 2);
        context.lineTo(10, 12);
        context.lineTo(18, 12);
        context.lineTo(18, 15);
        context.lineTo(30, 15);
        context.lineTo(30, 12);
        context.lineTo(38, 12);
        context.closePath();

        //context.beginPath();
        context.rect(18, 16, 12, 4);
        context.rect(18, 21, 12, 3);

        context.stroke();
        //context.globalAlpha = 0.5;
        context.fill();

    };

    return self;
};

/**
 * 点踩图标：闪电下雨
 * 辉子 2021
 */
const consRegion = (shape, x, y) => {
    let self = situationRegion(shape, x, y, "cons", "darkred");
    self.type = "cons";
    self.offset = {x, y, width: self.width, height: self.height};

    let drawStatic = self.drawStatic;
    self.drawStatic = context => {
        drawStatic.call(self, context);
        let p = (shape.cons === undefined ? 0 : shape.cons) / self.max;
        if (p > 1) {
            p = 1;
        }
        context.strokeStyle = shape.page.graph.enableSocial ? "whitesmoke" : "gray";
        let g = context.createLinearGradient(0, 0, 48, 48);
        if (shape.page.graph.enableSocial) {
            g.addColorStop(0, "rgba(100,0,0," + p + ")");
            g.addColorStop(1, "rgba(100,0,0," + (p / 2 + 0.5) + ")");
        } else {
            g.addColorStop(0, "whitesmoke");
            g.addColorStop(1, "lightgray");

        }
        context.fillStyle = g;
        context.beginPath();
        context.moveTo(self.width / 2, 24);
        context.lineTo(10, 16);
        context.lineTo(18, 16);
        context.lineTo(18, 11);
        context.lineTo(30, 11);
        context.lineTo(30, 16);
        context.lineTo(38, 16);
        context.closePath();

        context.rect(18, 6, 12, 4);
        context.rect(18, 2, 12, 3);

        context.stroke();
        context.fill();
    };
    return self;
};

const pageComment = frame => {
    const WIDTH = 150, HEIGHT = getPixelRatio() > 1 ? 45 : 32;
    const self = shapeComment(frame.x + WIDTH, frame.y + frame.height - HEIGHT - 10, frame, "pageComment");
    self.type = "pageComment";
    self.backColor = "rgba(255,255,255,0.3)";
    self.height = HEIGHT
    self.fontSize = getPixelRatio() > 1 ? 20 : 15;
    self.fontWeight = FONT_WEIGHT.BOLD;
    self.editedRemove = false;
    self.focusBackColor = "whitesmoke";
    self.getSelectable = () => {
        return self.page.mode === PAGE_MODE.VIEW;
    }

    // self.click = self.dbClick = () => self.beginEdit();

    const invalidateAlone = self.invalidateAlone;
    self.invalidateAlone = () => {
        if (frame.container !== frame.page.id || self.page.mode !== PAGE_MODE.VIEW) {
            self.visible = false;
        } else {
            self.y = frame.y + frame.height - HEIGHT - 25;
            self.width = frame.width / 4 < 300 ? 300 : frame.width / 4;
        }
        invalidateAlone.call(self);
    };

    const containsBorder = self.drawer.containsBorder;
    self.drawer.containsBorder = (x, y) => {
        return !self.isEditing() && containsBorder(self.drawer, [x, y]);
    }

    return self;
}

export {presentationFrame};