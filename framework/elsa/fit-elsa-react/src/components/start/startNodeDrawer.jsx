import {jadeNodeDrawer} from "@/components/base/jadeNodeDrawer.jsx";
import StartIcon from '../asserts/icon-start.svg?react'; // 导入背景图片

/**
 * 开始节点绘制器
 *
 * @override
 */
export const startNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);
    self.type = "startNodeDrawer";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return (<>
            <StartIcon className="jade-node-custom-header-icon"/>
        </>);
    };

    /**
     * @override
     */
    self.getHeaderTypeIcon = () => {
    };

    /**
     * 开始节点header只显示重命名选项
     *
     * @override
     */
    self.getToolMenus = () => {
        return [{
            key: '1', label: "重命名", action: (setEdit) => {
                setEdit(true);
            }
        }];
    };

    return self;
};