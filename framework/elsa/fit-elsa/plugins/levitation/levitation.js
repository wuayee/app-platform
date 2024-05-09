import {ALIGN, CURSORS, DOCK_MODE, FONT_WEIGHT} from "../../common/const.js";
import {container} from "../../core/container.js";
import {uuid} from "../../common/util.js";
import {ENV_CONFIG} from "../../config/envConfig.js";

/**
 * 悬浮窗相关操作.
 */
export const levitation = (() => {
    const popup = {};

    /**
     * 展示悬浮框.
     *
     * @param shape 目标shape.
     */
    popup.show = (shape) => {
        shape.page.removeLevitation();
        const menuScript = [{
            text: '悬浮', clickAction: (shape) => {
                shape.suspension();
            }
        }, {
            text: '嵌入', clickAction: (shape) => {
                shape.embed();
            }
        }];
        createToolContainer(shape, menuScript).invalidate();
    };

    return (shape) => {
        if (shape.dragable) {
            popup.show(shape);
        }
    };
})();

/**
 * 创建工具项容器.
 *
 * @param locationShape 定位shape.用于计算容器的位置信息.
 * @param targetShape 目标shape.工具项具体要操作的shape.
 * @param menuScripts 工具项的配置信息.
 * @return {Atom}
 */
const createToolItemContainer = (locationShape, targetShape, menuScripts) => {
    const x = locationShape.x + locationShape.width + 10;
    const y = locationShape.y;
    const width = 110 / locationShape.page.scaleX;
    const height = 300 / locationShape.page.scaleY;
    const self = levitationContainer(uuid(), x, y, width, height, targetShape.getContainer());
    self.name = "toolItems";
    self.dockMode = DOCK_MODE.VERTICAL;
    menuScripts.forEach(m => createToolItem(self, targetShape, m));
    return self;
};

/**
 * 创建工具容器.
 *
 * @param shape 目标shape.
 * @param menuScripts 工具项配置列表.
 * @return {Atom} 工具容器.
 */
const createToolContainer = (shape, menuScripts) => {
    const rect = shape.drawer.parent.getBoundingClientRect();
    let x = shape.x + rect.width + 5;
    let y = shape.y;
    const rotateDegree = shape.get("rotateDegree");
    if (rotateDegree === 0) {
        const x = rect.x - shape.x;
        const y = rect.y - shape.y;
        shape.offsetX = x;
        shape.offsetY = y;
    } else {
        x = x + (rect.x - shape.x - shape.offsetX);
        y = y + (rect.y - shape.y - shape.offsetY);
    }

    const self = levitationContainer(uuid(), x, y, 32, 32, shape.getContainer());
    const image = createToolImage(self);
    self.name = "tool";
    self.click = () => {
        self.page.shapes.filter(s => s.name === 'toolItems').forEach(s => s.remove());
        image.src = ENV_CONFIG.levitationActive;
        createToolItemContainer(self, shape, menuScripts).invalidate();
    }
    self.onMouseDown = () => {
    };
    self.indexCoordinate = () => {
        self.clearCoordinateIndex();
        if (self.container === "") {
            return;
        }
        if (!self.getVisibility()) {
            return;
        }
        self.createCoordinateIndex();
    };
    self.dockMode = DOCK_MODE.FILL;
    return self;
};

/**
 * 悬浮框容器类.
 *
 * @param id 唯一标识.
 * @param x 横坐标.
 * @param y 纵坐标.
 * @param width 宽度.
 * @param height 高度.
 * @param parent 父容器.
 * @return {*} 悬浮框容器对象.
 */
const levitationContainer = (id, x, y, width, height, parent) => {
    const self = parent.page.ignoreReact(() => createLevitation(() => container(id, x, y, width, height, parent)));
    self.getIndex();
    const padX = 6 / self.page.scaleX, padY = 6 / self.page.scaleY;
    self.itemPad = [padX, padX, padY, padY];
    self.serializable = false;
    self.backColor = "rgba(255,255,255,1)";
    self.cornerRadius = 6 / self.page.scaleX;
    self.shadow = true;
    self.autoFit = true;
    self.resizeable = false;
    self.rotateable = false;
    self.selectable = false;
    self.dragable = false;
    self.deletable = false;
    self.moveable = false;
    self.hideText = true;
    self.cursorStyle = CURSORS.HAND;
    return self;
}

/**
 * 创建工具项.
 *
 * @param toolItemContainer 工具项容器.
 * @param parent shape.
 * @param menuScript
 * @return {*}
 */
const createToolItem = (toolItemContainer, parent, menuScript) => {
    const item = createLevitation(() => toolItemContainer.page.createShape("text"));
    item.serializable = false;
    item.resizeable = false;
    item.selectable = true;
    item.dragable = false;
    item.deletable = false;
    item.moveable = false;
    item.name = "toolItem";
    item.hAlign = ALIGN.LEFT;
    item.cursorStyle = CURSORS.HAND;
    item.fontSize = 14 / toolItemContainer.page.scaleY;
    item.fontWeight = FONT_WEIGHT.LIGHTER;
    item.backColor = "rgba(0,0,0,0)";
    item.height = 25 / toolItemContainer.page.scaleY;
    item.text = menuScript.text;
    item.container = toolItemContainer.id;
    item.drawer.containsText = () => false;
    item.onMouseOut = () => item.backColor = "rgba(0,0,0,0)";
    item.onMouseIn = () => item.backColor = "rgba(0,0,0,0.1)";

    /**
     * 重写click事件，不同的项触发不同的点击动作.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     */
    item.click = (x, y) => {
        menuScript.clickAction(parent, x, y);
        parent.page.shapes.filter(s => s.name === 'toolItems').forEach(s => s.remove());
        parent.isFocused = true;
    }

    /**
     * 重写onMouseDowned方法，不去除其他shape的focused状态.
     */
    item.onMouseDowned = () => {
    };

    item.onMouseDown = (position) => {
        // 防止触发编辑器事件
        position.e.preventDefault();
    };
    return item;
}

/**
 * 创建图片.放在toolContainer中.
 *
 * @param tool 容器.
 * @return {*} image对象.
 */
const createToolImage = (tool) => {
    const image = createLevitation(() => tool.page.createShape("image"));
    image.name = 'toolImage';
    image.width = 20;
    image.height = 20;
    image.src = ENV_CONFIG.levitationDefault;
    image.container = tool.id;
    image.serializable = false;
    image.selectable = true;
    image.editable = false;
    image.dragable = false;
    image.deletable = false;
    image.moveable = false;
    image.cursorStyle = CURSORS.HAND;

    /**
     * 将click事件委托给父容器处理.
     */
    image.click = () => {
        tool.click();
    }

    image.onMouseDown = () => {
    };

    /**
     * 重写onMouseDowned方法，不去除其他shape的focused状态.
     */
    image.onMouseDowned = () => {
    };

    image.indexCoordinate = () => {
        image.clearCoordinateIndex();
        if (image.container === "") {
            return;
        }
        if (!image.getVisibility()) {
            return;
        }
        image.createCoordinateIndex();
    };
    return image;
}

/**
 * 创建悬浮窗的公共方法.
 *
 * @param creator 创建器.
 * @return {*} 悬浮窗组件.
 */
const createLevitation = (creator) => {
    const levitation = creator();
    levitation.isLevitation = true;
    return levitation;
}
