import {Col, ConfigProvider, Form, Input, Row, Tree} from 'antd';
import "./jadeInputTree.css";
import PropTypes from "prop-types";
import {JadeStopPropagationSelect} from "./JadeStopPropagationSelect.jsx";
import {JadeReferenceTreeSelect} from "./JadeReferenceTreeSelect.jsx";

/**
 * 构建节点，只有Object节点有子孙节点.
 *
 * @param nodeData 节点数据.
 * @param level 层级.
 * @return {{title, isLeaf: boolean, key}|{children: *, title, key}} 树节点.
 */
const convert = (nodeData, level) => {
    if (nodeData.from === "Expand") {
        return {
            id: nodeData.id,
            title: nodeData.name,
            type: nodeData.type,
            key: nodeData.id,
            level: level,
            from: nodeData.from,
            children: nodeData.value.map(v => convert(v, level + 1))
        };
    } else {
        const ans = {
            id: nodeData.id,
            title: nodeData.name,
            type: nodeData.type,
            key: nodeData.id,
            level: level,
            value: nodeData.value,
            from: nodeData.from,
            referenceKey: nodeData.referenceKey,
            referenceNode: nodeData.referenceNode,
            referenceId: nodeData.referenceId,
            isLeaf: true,
        };
        if (nodeData.generic) {
            ans.generic = nodeData.generic
        }
        if (nodeData.type === "Object") {
            ans.props = nodeData.props;
        }

        return ans;
    }
};

JadeInputTree.propTypes = {
    data: PropTypes.array.isRequired, updateItem: PropTypes.func.isRequired
};

const INPUT_WIDTH = 110;
const LEVEL_DISTANCE = 24;

/**
 * 带input的树形组件.主要用于api调用节点.
 *
 * @param data 数据.
 * @param updateItem 修改方法.
 * @return {JSX.Element}
 * @constructor
 */
export default function JadeInputTree({data, updateItem}) {
    const treeData = data.map(d => convert(d, 0));

    /**
     * input发生变化时的回调.
     *
     * @param id 唯一标志.
     * @param key 键值.
     * @param e 事件对象.
     */
    const onInputChange = (id, key, e) => {
        updateItem(id, [{key, value: e.target.value}]);
    };

    /**
     * 当reference的value变化时的处理方法.
     *
     * @param id 唯一标志.
     * @param key 键值.
     */
    const onReferenceValueChange = (id, key) => {
        updateItem(id, [{key: "referenceKey", value: key}]);
    };

    /**
     * 当reference的key变化时的处理方法.
     *
     * @param id 唯一标志.
     * @param e 返回值.
     */
    const onReferenceKeyChange = (id, e) => {
        updateItem(id, [{key: "referenceNode", value: e.referenceNode},
            {key: "referenceId", value: e.referenceId},
            {key: "value", value: e.value}]);
    };

    /**
     * 获取value的input组件.
     *
     * @param node 节点.
     * @return {JSX.Element|null}
     */
    const getValueInput = (node) => {
        if (node.from === "Input") {
            return <Input className="jade-input" style={{borderRadius: "0px 8px 8px 0px"}} placeholder={"请输入"}
                          value={node.value}
                          onChange={(e) => onInputChange(node.id, "value", e)}/>;
        } else if (node.from === "Reference") {
            return <JadeReferenceTreeSelect className="jade-input-tree-title-tree-select jade-select"
                                            rules={[{required: true, message: "字段值不能为空"}]}
                                            reference={node}
                                            onReferencedKeyChange={(e) => onReferenceKeyChange(node.id, e)}
                                            onReferencedValueChange={(v) => onReferenceValueChange(node.id, v)}/>;
        } else {
            return null;
        }
    };

    /**
     * 获取options数据.
     *
     * @param node 节点.
     * @return {[{label: string, value: string}]|[{label: string, value: string},{label: string, value: string}]} 选项数组.
     */
    const getOptions = (node) => {
        switch (node.type) {
            case "Object":
                if (node.hasOwnProperty("generic")) {
                    return [{value: "Reference", label: "引用"}];
                } else {
                    return [{value: "Reference", label: "引用"}, {value: "Expand", label: "展开"}];
                }
            case "Array":
                return [{value: "Reference", label: "引用"}];
            default:
                // 现阶段默认只能选择Reference
                return [{value: "Reference", label: "引用"}];
        }
    };

    /**
     * 自定义标题展示.
     *
     * @param node 节点数据.
     * @return {JSX.Element} react 组件对象.
     */
    const displayTitle = (node) => {
        const inputWidth = INPUT_WIDTH - node.level * LEVEL_DISTANCE;
        return (<>
            <div className="jade-input-tree-title">
                <Row wrap={false}>
                    <Col flex={"0 0 " + inputWidth + "px"}>
                        <Form.Item
                                name={`property-${node.id}`}
                        >
                            <div className="jade-input-tree-title-child"
                                 style={{display: "flex", alignItems: "center"}}>
                                <span>{node.title}</span>
                            </div>
                        </Form.Item>
                    </Col>
                    <Col flex="0 0 70px" style={{paddingRight: 0}}>
                        <Form.Item
                                name={`value-select-${node.id}`}
                        >
                        <div className="jade-input-tree-title-child">
                            <JadeInputTreeSelect node={node} options={getOptions(node)} updateItem={updateItem}/>
                        </div>
                        </Form.Item>
                    </Col>
                    <Col >
                        <div className="jade-input-tree-title-child">
                            {getValueInput(node)}
                        </div>
                    </Col>
                </Row>
            </div>
        </>);
    };

    return (<>
        <ConfigProvider theme={{components: {Tree: {nodeSelectedBg: "transparent", nodeHoverBg: "transparent"}}}}>
            <Tree blockNode={true} treeData={treeData} className={"jade-ant-tree"}
                  titleRender={displayTitle} showLine={true}/>
        </ConfigProvider>
    </>);
};

/**
 * 适配JadeInputTree的select框.
 *
 * @param node 节点.
 * @param options 可选项.
 * @param updateItem 修改item的方法.
 * @return {JSX.Element}
 * @constructor
 */
const JadeInputTreeSelect = ({node, options, updateItem}) => {
    /**
     * 处理选择变化事件.
     *
     * @param v 变化后的值.
     */
    const handleItemChange = (v) => {
        if (v === "Expand") {
            updateItem(node.id, [{key: "from", value: v},
                {key: "referenceNode", value: null},
                {key: "referenceId", value: null},
                {key: "referenceKey", value: null},
                {key: "value", value: node.props}
            ]);
        } else if (v === "Input") {
            updateItem(node.id, [{key: "from", value: v},
                {key: "referenceNode", value: null},
                {key: "referenceId", value: null},
                {key: "referenceKey", value: null},
                {key: "value", value: null}
            ]);
        } else {
            updateItem(node.id, [{key: "from", value: v}]);
        }
    };

    return (<>
        <JadeStopPropagationSelect
                style={{background: "#f7f7f7", width: "100%"}}
                placeholder={"请选择"}
                defaultValue={node.from}
                className={"jade-input-tree-title-select jade-select"}
                onChange={handleItemChange}
                options={options}
        />
    </>);
};