import {jadeNodeDrawer} from "@/components/base/jadeNodeDrawer.jsx";
import {EndNodeHeader} from "@/components/end/EndNodeHeader.jsx";
import EndIcon from "../asserts/icon-end.svg?react"; // 导入背景图片

/**
 * end节点绘制器
 *
 * @override
 */
export const endNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);
    self.type = "endNodeDrawer";

    /**
     * @override
     */
    self.getHeaderComponent = (shapeStatus) => {
        return (<EndNodeHeader shape={shape} shapeStatus={shapeStatus}/>);
    };

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (<>
            <EndIcon className="jade-node-custom-header-icon"/>
        </>);
    };

    /**
     * @override
     */
    self.getHeaderTypeIcon = () => {
    };

    /**
     * @override
     */
    const getToolMenus = self.getToolMenus;
    self.getToolMenus = () => {
        if (shape.page.shapes.filter(s => s.type === shape.type).length === 1) {
            return [{
                key: '1', label: "复制", action: () => {
                    shape.duplicate();
                }
            }, {
                key: '2', label: "重命名", action: (setEdit) => {
                    setEdit(true);
                }
            }];
        }
        return getToolMenus.apply(self);
    };

    return self;
};