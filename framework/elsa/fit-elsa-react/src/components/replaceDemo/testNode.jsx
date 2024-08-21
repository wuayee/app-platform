import {CloudSyncOutlined} from "@ant-design/icons";
import {jadeNode} from "@/components/base/jadeNode.jsx";

export const testNode = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer);
    self.type = "testNode";
    self.text = "测试组件";
    self.componentName = "testComponent";

    /**
     * @override
     */
    self.getHeaderIcon = () => {
        return <CloudSyncOutlined/>;
    };

    return self;
};
