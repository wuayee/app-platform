import {jadeNode} from "@/components/jadeNode.jsx";
import {CloudSyncOutlined} from "@ant-design/icons";

export const listener2Node = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "listener2Node";
    self.text = "监听者";
    self.componentName = "listener2Component";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return <CloudSyncOutlined/>;
    };

    return self;
};