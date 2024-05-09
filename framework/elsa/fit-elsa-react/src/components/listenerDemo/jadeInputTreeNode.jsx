import {jadeNode} from "@/components/jadeNode.jsx";
import {CloudSyncOutlined} from "@ant-design/icons";

export const jadeInputTreeNode = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "jadeInputTreeNode";
    self.text = "被监听者";
    self.componentName = "jadeInputTreeComponent";
    self.width = 360;

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return <CloudSyncOutlined/>;
    };

    return self;
};