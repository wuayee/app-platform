import {Col, Form, Row, Tree, Typography} from 'antd';
import "./jadeInputTree.css";
import PropTypes from "prop-types";
import {JadeStopPropagationSelect} from "./JadeStopPropagationSelect.jsx";
import {JadeReferenceTreeSelect} from "./JadeReferenceTreeSelect.jsx";
import {useFormContext} from "@/components/DefaultRoot.jsx";
import {JadeInput} from "@/components/common/JadeInput.jsx";

const {Text} = Typography;

/**
 * 构建树.
 *
 * @param data 数据.
 * @param level 层级.
 * @return {Omit<*, "name">} 树状结构.
 */
const convert = (data, level) => {
    const {name, ...ans} = data;
    ans.level = level;
    ans.title = name;
    ans.key = data.id;
    ans.isRequired = data.isRequired ?? true;
    ans.isLeaf = data.from !== "Expand";
    if (!ans.isLeaf) {
        ans.children = data.value.map(v => convert(v, level + 1));
    }
    return ans;
};

JadeInputTree.propTypes = {
    data: PropTypes.array.isRequired, updateItem: PropTypes.func.isRequired
};

const INPUT_WIDTH = 100;
const LEVEL_DISTANCE = 24;

/**
 * 带input的树形组件.主要用于api调用节点.
 *
 * @param data 数据.
 * @param updateItem 修改方法.
 * @param disabled 是否禁用.
 * @return {JSX.Element}
 * @constructor
 */
export default function JadeInputTree({data, updateItem, disabled}) {
    const form = useFormContext();
    const treeData = data.map(d => convert(d, 0));

    /**
     * input发生变化时的回调.
     *
     * @param id 唯一标志.
     * @param key 键值.
     * @param e 事件对象.
     */
    const onInputChange = (id, key, e) => {
        const value = e.target.value.trim() === '' ? null : e.target.value;
        if (value === null) {
            form.setFieldsValue({[`value-${id}`]: null});
        }
        updateItem(id, [{key, value}]);
    };

    /**
     * input失去焦点时的回调.
     *
     * @param node 对应的节点.
     */
    const onInputBlur = (node) => {
        function nodeValueJsonization() {
            let value = node.value;
            const type = node.type;

            if (type === "Object" || type === "Array") {
                try {
                    value = JSON.parse(value);
                } catch(error) {
                    console.error("Input value is invalid json.");
                }
            }
            const key = "value";
            updateItem(node.id, [{key, value}]);
        }

        nodeValueJsonization();
    }

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
            {key: "referenceKey", value: e.referenceKey},
            {key: "value", value: e.value},
            {key: "type", value: e.type}]);
    };

    /**
     * 获取value的input组件.
     *
     * @param node 节点.
     * @return {JSX.Element|null}
     */
    const getValueInput = (node) => {
        if (node.from === "Input") {
            return <Form.Item
                id={`value-${node.id}`}
                name={`value-${node.id}`}
                rules={node.isRequired ? [{required: true, message: "字段值不能为空"}] : []}
                initialValue={node.value}
                validateTrigger="onBlur"
            >
                <JadeInput disabled={disabled}
                           className="jade-input"
                           style={{borderRadius: "0px 8px 8px 0px"}}
                           placeholder={"请输入"}
                           value={node.value}
                           onChange={(e) => onInputChange(node.id, "value", e)}
                           onBlur={() => onInputBlur(node)}
                />
            </Form.Item>;
        } else if (node.from === "Reference") {
            return <JadeReferenceTreeSelect className="jade-input-tree-title-tree-select jade-select"
                                            disabled={disabled}
                                            rules={node.isRequired ? [{required: true, message: "字段值不能为空"}] : []}
                                            reference={node}
                                            onReferencedKeyChange={(e) => onReferenceKeyChange(node.id, e)}
                                            onReferencedValueChange={(v) => onReferenceValueChange(node.id, v)}
                                            level={node.level}/>;
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
                if (node.hasOwnProperty("generic") || node.props === undefined) {
                    return [{value: "Reference", label: "引用"}, {value: "Input", label: "输入"}];
                } else {
                    return [{value: "Reference", label: "引用"},
                        {value: "Input", label: "输入"},
                        {value: "Expand", label: "展开"}
                    ];
                }
            case "Array":
            default:
                return [{value: "Reference", label: "引用"}, {value: "Input", label: "输入"}];
        }
    };

    /**
     * 自定义标题展示.
     *
     * @param node 节点数据.
     * @return {JSX.Element} react 组件对象.
     */
    const displayTitle = (node) => {
        const getToolTip = () => {
            return {
                title: node.title,
                color: "white",
                overlayInnerStyle: {fontSize: "12px", color: "rgb(128, 128, 128)", background: "white"}
            };
        };

        const inputWidth = INPUT_WIDTH - node.level * LEVEL_DISTANCE;
        return (<>
            <div className="jade-input-tree-title">
                <Row wrap={false}>
                    <Col flex={"0 0 " + inputWidth + "px"}>
                        <Form.Item name={`property-${node.id}`}>
                            <div className="jade-input-tree-title-child"
                                 style={{display: "flex", alignItems: "center"}}>
                                {node.isRequired && <span className="jade-required-indicator">*</span>}
                                <Text ellipsis={{tooltip: getToolTip()}}
                                      className="huggingface-light-font"
                                      style={{maxWidth: inputWidth - 15}}>{node.title}</Text>
                            </div>
                        </Form.Item>
                    </Col>
                    <Col flex="0 0 70px" style={{paddingRight: 0}}>
                        <Form.Item name={`value-select-${node.id}`}>
                            <div className="jade-input-tree-title-child">
                                <JadeInputTreeSelect node={node}
                                                     options={getOptions(node)}
                                                     updateItem={updateItem}
                                                     disabled={disabled}/>
                            </div>
                        </Form.Item>
                    </Col>
                    <Col>
                        <div className="jade-input-tree-title-child">
                            {getValueInput(node)}
                        </div>
                    </Col>
                </Row>
            </div>
        </>);
    };

    const renderTreeNodes = (data) =>
        data.map((item) => {
            const isRootNode = item.level === 0;
            const className = isRootNode ? "jade-hide-tree-left-line jade-tree-node" : 'jade-tree-node';

            if (item.children) {
                return (
                    <Tree.TreeNode title={displayTitle(item)} key={item.key} className={className}>
                        {renderTreeNodes(item.children)}
                    </Tree.TreeNode>
                );
            }
            return <Tree.TreeNode title={displayTitle(item)} key={item.key} className={className}/>;
        });

    return (<>
        <div style={{paddingLeft: "15px"}}>
            <Row wrap={false}>
                <Col flex={"0 0 " + (INPUT_WIDTH + 15) + "px"}>
                    <span className={"jade-second-title-text"}>字段名称</span>
                </Col>
                <Col>
                    <span className={"jade-second-title-text"}>字段值</span>
                </Col>
            </Row>
        </div>
        <Tree blockNode={true} className={"jade-ant-tree"} showLine={true}>
            {renderTreeNodes(treeData)}
        </Tree>
    </>);
};

/**
 * 适配JadeInputTree的select框.
 *
 * @param node 节点.
 * @param options 可选项.
 * @param updateItem 修改item的方法.
 * @param disabled 禁用.
 * @return {JSX.Element}
 * @constructor
 */
const JadeInputTreeSelect = ({node, options, updateItem, disabled}) => {
    const form = useFormContext();

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
            form.setFieldsValue({[`value-${node.id}`]: null});
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
                disabled={disabled}
                placeholder={"请选择"}
                defaultValue={node.from}
                className={"jade-input-tree-title-select jade-select"}
                onChange={handleItemChange}
                options={options}
        />
    </>);
};