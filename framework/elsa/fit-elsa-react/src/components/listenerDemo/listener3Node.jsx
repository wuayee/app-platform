import {CloudSyncOutlined} from "@ant-design/icons";
import {jadeNode} from "@/components/jadeNode.jsx";

/**
 * demo用.
 */
export const listener3Node = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "listener3Node";
    self.text = "被监听者";
    self.componentName = "listener3Component";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return <CloudSyncOutlined/>;
    };

    return self;
};