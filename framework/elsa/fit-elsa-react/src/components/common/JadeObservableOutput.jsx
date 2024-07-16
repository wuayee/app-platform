import {Button, Col, Collapse, Form, Popover, Row, Tree} from "antd";
import {QuestionCircleOutlined} from "@ant-design/icons";
import React, {useEffect, useState} from "react";
import {JadeStopPropagationSelect} from "@/components/common/JadeStopPropagationSelect.jsx";
import {useDataContext, useDispatch, useFormContext, useShapeContext} from "@/components/DefaultRoot.jsx";
import AddSubItem from '../asserts/icon-add-subitem.svg?react';
import DeleteItem from '../asserts/icon-delete.svg?react';
import TreeSwitcherIcon from "@/components/common/TreeSwitcherIcon.jsx";
import {DATA_TYPES} from "@/common/Consts.js";
import {JadeInput} from "@/components/common/JadeInput.jsx";

const {Panel} = Collapse;

/**
 * code节点输出组件
 *
 * @return {JSX.Element}
 * @constructor
 */

export default function JadeObservableOutput({disabled}) {
    const jadeConfig = useDataContext();
    const [outputTreeData, setOutputTreeData] = useState(null);
    const output = jadeConfig.outputParams.find(item => item.name === "output");
    const shape = useShapeContext();
    const dispatch = useDispatch();

    // 组件初始化时注册observable.
    useEffect(() => {
        const outputData = [convertToTreeData(output, 1, null)];
        setOutputTreeData(outputData);
        // 组件unmount时，删除observable.
        return () => {
            shape.page.removeObservable(shape.id);
        };
    }, []);

    useEffect(() => {
        const outputData = [convertToTreeData(output, 1, null)];
        setOutputTreeData(outputData);
    }, [jadeConfig]);

    /**
     * 将jadeConfig转换成TreeData
     *
     * @param data output数据
     * @param level 层级
     * @param parent 父id
     * @return {{}}
     */
    const convertToTreeData = (data, level, parent) => {
        if (!data) {
            return {};
        }
        shape.page.registerObservable({
            nodeId: shape.id,
            observableId: data.id,
            value: data.name,
            type: data.type,
            parentId: parent ? parent.id : null
        });
        const {id, name, type, value} = data;
        const children = Array.isArray(value) ? value.map(item => convertToTreeData(item, level + 1, data)) : [];
        return {
            key: id,
            title: name,
            type: type,
            children: children,
            level: level,
            expanded: true
        };
    };

    const content = (<div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
        <p>代码运行完成后输出的变量，必须保证次数定义的变量名、</p>
        <p>变量类型与代码的return对象中完全一致</p></div>);

    /**
     * 渲染tree数据
     *
     * @param node 节点数据
     * @return {JSX.Element}
     */
    const renderTreeNode = (node) => {
        return <TreeNode node={node} disabled={disabled} shape={shape} dispatch={dispatch} output={output}/>
    };

    return (<>
        <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["codeOutputPanel"]}>
            {<Panel key={"codeOutputPanel"}
                    header={<div className="panel-header">
                        <span className="jade-panel-header-font">输出</span>
                        <Popover content={content}>
                            <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                        </Popover>
                    </div>}
                    className="jade-panel"
            >
                <Tree blockNode={true}
                      switcherIcon={({expanded}) => <TreeSwitcherIcon expanded={expanded}/>}
                      showLine
                      defaultExpandAll
                      treeData={outputTreeData}
                      titleRender={renderTreeNode}
                />
            </Panel>}
        </Collapse>
    </>);
}

/**
 * 输出树状展示组件
 *
 * @param node 节点数据
 * @param disabled 是否禁用
 * @param shape 图形
 * @param dispatch 发送数据的动作
 * @param output 输出数据
 * @return {JSX.Element} 组件
 * @constructor
 */
const TreeNode = ({node, disabled, shape, dispatch, output}) => {
    const dataTypes = Object.values(DATA_TYPES);
    const inputWidth = 140 - (node.level - 1) * 24;
    const {key, title, type, level} = node;
    // 输出的最大层数是4
    const maxLevel = 4;

    /**
     * 是否不可被选择
     *
     * @param dataType 数据类型
     * @return {boolean} 是否不可别选择
     */
    const isTypeDisable = (dataType) => {
        return level === maxLevel && dataType === 'Object';
    };

    const isObjectType = type => type === 'Object';
    const form = useFormContext();
    const options = dataTypes.map(dataType => ({
        value: dataType,
        label: dataType,
        disabled: disabled || isTypeDisable(dataType)
    }));

    /**
     * 处理输入发生变化的动作
     *
     * @param id id
     * @param type 事件类型
     * @param changes 需要修改的属性
     */
    const handleItemChange = (id, type, changes) => {
        dispatch({type: type, id: id, changes: changes});
    };

    /**
     * 更新input的属性
     *
     * @param id id
     * @param type 事件类型
     * @param e event事件
     */
    const editOutputName = (id, type, e) => {
        handleItemChange(id, type, [{key: 'name', value: e.target.value}]);
        form.setFieldsValue({[`property-${shape.id}-${id}`]: e.target.value});
    };

    /**
     * 更新属性类型
     *
     * @param id id
     * @param type 事件类型
     * @param value 目标值
     */
    const editOutputType = (id, type, value) => {
        const changes = [{key: "type", value: value}];
        if (value === "Object") {
            changes.push({key: "value", value: []})
        } else {
            changes.push({key: "value", value: ''})
        }
        shape.page.removeObservable(shape.id);
        handleItemChange(id, type, changes);
        form.setFieldsValue({[`value-select-${shape.id}-${id}`]: value});
    };

    /**
     * 添加子项
     *
     * @param key 需要添加子项的父项的id
     */
    const handleAddSubItem = (key) => {
        dispatch({type: "addSubItem", id: key});
    };

    /**
     * 删除一项
     *
     * @param key 需要删除的数据id
     */
    const handleDelete = key => {
        findChildIds(output.value, key).forEach(id => {
            shape.page.removeObservable(shape.id, id);
        });
        dispatch({type: "deleteRow", id: key});
    };

    /**
     * 查找目标数据id以及对应子数据的id
     *
     * @param data 数据
     * @param targetId 需要查询的数据
     * @return {*[]} id数组
     */
    const findChildIds = (data, targetId) => {
        let resultIds = [];

        const _recursiveFind = (arr, id) => {
            for (let item of arr) {
                if (item.id === id) {
                    resultIds.push(item.id);
                    if (item.type === "Object" && Array.isArray(item.value)) {
                        for (let child of item.value) {
                            resultIds.push(child.id);
                        }
                    }
                } else if (item.type === "Object" && Array.isArray(item.value)) {
                    _recursiveFind(item.value, id);
                }
            }
        };

        _recursiveFind(data, targetId);
        return resultIds;
    };

    /**
     * 获取最后一列的组件
     *
     * @param level 数据层级
     * @return {JSX.Element} 按钮或者空
     */
    const getDeleteButton = (level) => {
        return level > 1 ? <Button type="text"
                                   icon={<DeleteItem/>}
                                   disabled={disabled}
                                   onClick={() => handleDelete(key)}/> : <></>;
    };

    return (<>
        <Row align="middle" wrap={false}>
            <Col flex={"0 0 " + inputWidth + "px"} style={{marginRight: '8px'}}>
                <Form.Item name={`property-${shape.id}-${key}`}
                           id={`property-${node.id}-${key}`}
                           rules={[{required: true, message: "字段值不能为空"}, {
                               pattern: /^[^\s]*$/,
                               message: "禁止输入空格"
                           }]}
                           initialValue={title}
                >
                    <JadeInput className="jade-input"
                               disabled={level === 1 || disabled}
                               onChange={(e) => editOutputName(key, 'editOutputName', e)}
                    />
                </Form.Item>
            </Col>
            <Col flex="0 0 100px" style={{alignSelf: "normal"}}>
                <Form.Item name={`value-select-${shape.id}-${key}`}
                           id={`type-${node.id}-${key}`}
                           initialValue={type}
                >
                    <JadeStopPropagationSelect
                            style={{borderRadius: 4}}
                            disabled={disabled}
                            onChange={(value) => editOutputType(key, 'editOutputType', value)}
                            options={options}
                    />
                </Form.Item>
            </Col>
            <Col flex="0 0 30px" style={{display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                <Button disabled={disabled || !(isObjectType(type))}
                        type="text"
                        style={{
                            margin: 0,
                            padding: 0,
                            visibility: (isObjectType(type)) ? 'visible' : 'hidden'
                        }}
                        icon={<AddSubItem/>}
                        onClick={() => handleAddSubItem(key)}
                />
            </Col>
            <Col flex="0 0 15px" style={{display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
                {getDeleteButton(level)}
            </Col>
        </Row>
    </>);
};