import {jadeNode} from "@/components/jadeNode.jsx";
import {CloudSyncOutlined} from "@ant-design/icons";

export const listener1Node = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "listener1Node";
    self.text = "被监听者";
    self.componentName = "listener1Component";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return <CloudSyncOutlined/>;
    };

    return self;
};