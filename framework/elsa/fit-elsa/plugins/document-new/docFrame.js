import {reference} from "../../core/reference.js";
import {DOCK_MODE} from "../../common/const.js";
import {docFrameDrawer} from "./drawer/docFrameDrawer.js";

/**
 * 类似presentation的frame，用于承载文档.
 *
 * @author z00559346 张越.
 */
export const docFrame = (id, x, y, width, height, parent) => {
    const self = reference(id, x, y, width, height, parent, docFrameDrawer);
    self.type = "docFrame";
    self.namespace = "document";

    self.resize(1600, 900);
    self.moveTo(0, 0);

    self.backColor = "white";
    self.borderWidth = 0;
    self.dashWidth = 0;
    self.moveable = false;
    self.deletable = false;
    self.allowCoEdit = false;
    self.allowLink = false;
    self.selectable = false;
    self.cursorStyle = "default";

    // 被选中也不会画出连接点.
    self.connectors = [];

    self.childAllowed = s => true;

    /**
     * 重写该方法，当 {@link #docFrame} 初始化时，需要创建 {@link docSection} 对象.
     *
     * @param args 参数.
     * @override
     */
    const initialize = self.initialize;
    self.initialize = (args) => {
        createDocSection();
        initialize.apply(self, [args]);
    };

    const createDocSection = () => {
        const docSection = self.page.createNew("docSection", 0, 0, null, {hideText: false});
        self.page.ignoreReact(() => {
            docSection.pDock = DOCK_MODE.FILL;
            docSection.dragable = false;
            docSection.deletable = false;
            docSection.moveable = true;
            docSection.connectors = [];
            docSection.overflowHidden = true;
        });
    };

    /**
     * 当获取itemPad时，需要通过page的div来进行计算.
     *
     * @override
     */
    const get = self.get;
    self.get = (field) => {
        if (field === "itemPad") {
            return [0, 0, 0, 0];
        }
        return get.apply(self, [field]);
    };

    /**
     * 获取顶层结构化文档.
     *
     * @return {*}
     */
    self.getTopSection = () => {
        return self.getShapes(s => s.isTypeof("docSection") && s.isTopSection())[0];
    }

    return self;
};