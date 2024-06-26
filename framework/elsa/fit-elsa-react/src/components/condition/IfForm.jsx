import {Button, Col, Collapse, Form, Input, InputNumber, Row, Switch} from 'antd';
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import {JadeStopPropagationSelect} from "@/components/common/JadeStopPropagationSelect.jsx";
import {JadeReferenceTreeSelect} from "@/components/common/JadeReferenceTreeSelect.jsx";
import "./style.css";
import {useFormContext} from "@/components/DefaultRoot.jsx";
import {UNARY_OPERATOR} from "@/common/Consts.js";

const {Panel} = Collapse;

/**
 * 判断条件表单。
 *
 * @returns {JSX.Element} 判断条件表单的DOM。
 */
export default function IfForm({branch, name, index, totalItemNum, deleteBranch, changeConditionRelation,
                                   addCondition, deleteCondition, changeConditionConfig}) {
    const form = useFormContext();

    const unaryOperators = Object.values(UNARY_OPERATOR);

    const binaryOperators = ['equal', 'not equal', 'longer than', 'longer than or equal', 'shorter than', 'shorter than or equal', 'contain', 'not contain', 'greater than', 'greater than or equal', 'less than', 'less than or equal'];

    const handleDeleteBranch = () => {
        deleteBranch(branch.id);
    };

    const handleDeleteCondition = (conditionId) => {
        deleteCondition(branch.id, conditionId);
    };

    const handleConditionRelationChange = (updatedConditionRelation) => {
        changeConditionRelation(branch.id, updatedConditionRelation);
    };

    const handleConditionChange = (conditionId, value) => {
        form.setFieldsValue({[`condition-${conditionId}`]: value});
        changeConditionConfig(branch.id, conditionId, [{key: "condition", value: value}]);
    };

    const handleReferenceValueChange = (conditionId, itemId, v, t) => {
        changeConditionConfig(branch.id, conditionId, [{key: itemId, value: [{key: "referenceKey", value: v}, {key: "type", value: t}]}]);
    };

    const handleReferenceKeyChange = (conditionId, itemId, e) => {
        changeConditionConfig(branch.id, conditionId, [{key: itemId, value: [{key: "referenceNode", value: e.referenceNode},
                {key: "referenceId", value: e.referenceId},
                {key: "value", value: e.value}]}]);
    };

    const handleItemChange = (conditionId, itemId, changeParams) => {
        changeConditionConfig(branch.id, conditionId, [{key: itemId, value: changeParams}]);
    };

    const getConditionOptionsByReferenceType = (referenceType) => {
        switch (referenceType.toLowerCase()) {
            case 'string':
                return [
                    {value: 'equal', label: 'equal'},
                    {value: 'not equal', label: 'not equal'},
                    // {value: 'longer than', label: 'longer than'},
                    // {value: 'longer than or equal', label: 'longer than or equal'},
                    // {value: 'shorter than', label: 'shorter than'},
                    // {value: 'shorter than or equal', label: 'shorter than or equal'},
                    // {value: 'contain', label: 'contain'},
                    // {value: 'not contain', label: 'not contain'},
                    {value: UNARY_OPERATOR.IS_EMPTY_STRING, label: 'is empty'},
                    {value: UNARY_OPERATOR.IS_NOT_EMPTY_STRING, label: 'is not empty'},
                    {value: UNARY_OPERATOR.IS_NULL, label: 'is null'},
                    {value: UNARY_OPERATOR.IS_NOT_NULL, label: 'is not null'},
                ];
            case 'boolean':
                return [
                    {value: 'equal', label: 'equal'},
                    {value: 'not equal', label: 'not equal'},
                    {value: UNARY_OPERATOR.IS_NULL, label: 'is null'},
                    {value: UNARY_OPERATOR.IS_NOT_NULL, label: 'is not null'},
                    {value: UNARY_OPERATOR.IS_TRUE, label: 'is true'},
                    {value: UNARY_OPERATOR.IS_FALSE, label: 'is false'},
                ];
            case 'integer':
            case 'number':
                return [
                    {value: 'equal', label: 'equal'},
                    {value: 'not equal', label: 'not equal'},
                    {value: UNARY_OPERATOR.IS_NULL, label: 'is null'},
                    {value: UNARY_OPERATOR.IS_NOT_NULL, label: 'is not null'},
                    {value: 'greater than', label: 'greater than'},
                    {value: 'greater than or equal', label: 'greater than or equal'},
                    {value: 'less than', label: 'less than'},
                    {value: 'less than or equal', label: 'less than or equal'},
                ];
            case 'array<string>':
            case 'array<integer>':
            case 'array<boolean>':
            case 'array<number>':
                return [
                    // {value: 'longer than', label: 'longer than'},
                    // {value: 'longer than or equal', label: 'longer than or equal'},
                    // {value: 'shorter than', label: 'shorter than'},
                    // {value: 'shorter than or equal', label: 'shorter than or equal'},
                    // {value: 'contain', label: 'contain'},
                    // {value: 'not contain', label: 'not contain'},
                    {value: UNARY_OPERATOR.IS_EMPTY, label: 'is empty'},
                    {value: UNARY_OPERATOR.IS_NOT_EMPTY, label: 'is not empty'},
                ];
            default:
                return [];
        }
    };

    /**
     * 根据总的组件数目渲染优先级样式
     *
     * @return {JSX.Element|null}
     */
    const renderPriority = () => {
        if (totalItemNum > 2) {
            return <div className={"priority-tag"}><div className={"priority-inner-text jade-font-size"}>Priority {index + 1}</div></div>
        } else {
            return null;
        }
    };

    /**
     * 根据总的组件数目渲染删除按钮
     *
     * @return {JSX.Element|null}
     */
    const renderDeleteIcon = () => {
        if (totalItemNum > 2) {
            return <Button type="text" className="jade-panel-header-icon-position icon-button"
                           onClick={() => handleDeleteBranch()}>
                <MinusCircleOutlined/>
            </Button>
        } else {
            return null;
        }
    };

    /**
     * 若条件分支中条件数大于1，则需要渲染左侧图示，用于配置条件之间的关系 e.g. and/or
     *
     * @return {JSX.Element|null}
     */
    const renderLeftDiagram = () => {
        return <div className={"condition-left-diagram"}>
            <JadeStopPropagationSelect
                id={`condition-left-select-${branch.id}`}
                className="jade-select operation"
                placement="bottomLeft"
                popupClassName={"condition-left-drop"}
                onChange={(value) => handleConditionRelationChange(value)}
                options={[
                    {value: 'and', label: 'And'},
                    {value: 'or', label: 'Or'},
                ]}
                value={branch.conditionRelation}
            />
        </div>
    };

    const renderDeleteConditionIcon = (index, conditionId) => {
        if (index > 0) {
            return <Form.Item>
                <Button type="text" className="icon-button"
                        style={{height: "100%"}}
                        onClick={() => handleDeleteCondition(conditionId)}>
                    <MinusCircleOutlined/>
                </Button>
            </Form.Item>
        }
    };

    // 根据 condition 设置 rules
    const selectedRightSideValueRules = (condition, rule) => {
        return unaryOperators.includes(condition) ? [{}] : (rule || [{ required: true, message: '字段值不能为空' }]);
    };


    // 根据不同的值渲染不同的组件
    const renderRightSideValueComponent = (conditionId, item, referenceType, condition) => {
        switch (item.from) {
            case 'Reference':
                return <>
                    <JadeReferenceTreeSelect
                        className="value-custom jade-select"
                        disabled={unaryOperators.includes(condition)}
                        rules={selectedRightSideValueRules(condition)}
                        reference={item}
                        onReferencedValueChange={(v, t) => handleReferenceValueChange(conditionId, item.id, v, t)}
                        onReferencedKeyChange={(e) => handleReferenceKeyChange(conditionId, item.id, e)}
                    />
                </>
            case 'Input':
                switch (referenceType) {
                    case 'String':
                    case 'Array<String>':
                        return <Form.Item
                            id={`value-${item.id}`}
                            name={`value-${item.id}`}
                            rules={selectedRightSideValueRules(condition)}
                            initialValue={item.value}
                        >
                            <Input
                                className="value-custom jade-input"
                                disabled={unaryOperators.includes(condition)}
                                value={item.value}
                                onChange={(e) => handleItemChange(conditionId, item.id, [{key: "value", value: e.target.value}])}
                            />
                        </Form.Item>;
                    case 'Boolean':
                    case 'Array<Boolean>':
                        return <Form.Item
                            id={`value-${item.id}`}
                            name={`value-${item.id}`}
                            initialValue={item.value}
                            rules={selectedRightSideValueRules(condition)}
                            style={{marginLeft: "8px"}}
                        >
                            <Switch disabled={unaryOperators.includes(condition)} onChange={(e) => handleItemChange(conditionId, item.id, [{key: "value", value: e}])} value={item.value} defaultChecked/>
                        </Form.Item>;
                    case 'Integer':
                    case 'Number':
                    case 'Array<Number>':
                    case 'Array<Integer>':
                        return <Form.Item
                            id={`value-${item.id}`}
                            name={`value-${item.id}`}
                            rules={selectedRightSideValueRules(condition)}
                            initialValue={item.value}
                        >
                            <InputNumber
                                className="value-custom jade-input"
                                disabled={unaryOperators.includes(condition)}
                                step={1}
                                onChange={(e) => handleItemChange(conditionId, item.id, [{key: "value", value: e}])}
                                stringMode
                            />
                        </Form.Item>;
                    default:
                        return <Form.Item
                            id={`value-${item.id}`}
                            name={`value-${item.id}`}
                            rules={selectedRightSideValueRules(condition, [{required: true, message: "字段值不能为空"}, {
                                pattern: /^[^\s]*$/,
                                message: "禁止输入空格"
                            },])}
                            initialValue={item.value}
                        >
                            <Input
                                className="value-custom jade-input"
                                disabled={unaryOperators.includes(condition)}
                                value={item.value}
                                onChange={(e) => handleItemChange(conditionId, item.id, [{key: "value", value: e.target.value}])}
                            />
                        </Form.Item>;
                }
            default:
                return <></>;
        }
    };

    const onLeftReferenceTreeSelectReferencedValueChange = (condition, v, t) => {
        const left = condition.value.find(item => item.name === "left");
        if (left.type === t && left.referenceKey === v) {
            // 若与原来一样，不必刷新
            return;
        }
        handleReferenceValueChange(condition.id, condition.value.find(item => item.name === "left").id, v, t);
        handleConditionChange(condition.id, null);
        const rightValue = t === "Boolean" ? false : "";
        if (condition.value.find(item => item.name === "right").from === "Input") {
            form.setFieldsValue({[`value-${condition.value.find(item => item.name === "right").id}`]: rightValue})
            handleItemChange(condition.id, condition.value.find(item => item.name === "right").id, [{
                key: "value",
                value: rightValue
            }]);
        }
    };

    const onRightFromSelectChange = (condition, value) => {
        const rightValue = condition.value.find(item => item.name === "left").type === "Boolean" ? false : "";
        form.setFieldsValue({[`value-${condition.value.find(item => item.name === "right").id}`]: rightValue})
        handleItemChange(condition.id, condition.value.find(item => item.name === "right").id,
            [
                {key: "from", value: value},
                {key: "type", value: condition.value.find(item => item.name === "left").type},
                {key: "value", value: rightValue},
                {key: "referenceNode", value: ""}, {key: "referenceId", value: ""}, {key: "referenceKey", value: ""}
            ]);
    };

    const renderCondition = (condition, index) => {
        return <Row gutter={16} key={"row-" + index} style={{marginBottom: "6px", marginRight: 0}}>
            <Col span={6}>
                <JadeReferenceTreeSelect
                    className="jade-select"
                    rules={[{required: true, message: "字段值不能为空"}]}
                    reference={condition.value.find(item => item.name === "left")}
                    onReferencedValueChange={(v, t) => {onLeftReferenceTreeSelectReferencedValueChange(condition, v, t);}}
                    onReferencedKeyChange={(e) => handleReferenceKeyChange(condition.id, condition.value.find(item => item.name === "left").id, e)}
                />
            </Col>
            <Col span={6}>
                <Form.Item
                    name={`condition-${condition.id}`}
                    rules={[{required: true, message: "字段值不能为空"}]}
                    initialValue={condition.condition}
                >
                    <JadeStopPropagationSelect
                        className="jade-select"
                        style={{width: "100%"}}
                        placeholder="请选择条件"
                        options={getConditionOptionsByReferenceType(condition.value.find(item => item.name === "left").type)}
                        value={condition.condition}
                        onChange={(e) => handleConditionChange(condition.id, e)}
                    />
                </Form.Item>
            </Col>
            <Col span={4} style={{paddingRight: 0}}>
                <Form.Item>
                    <JadeStopPropagationSelect
                        id={`from-select-${condition.id}`}
                        disabled={unaryOperators.includes(condition.condition)}
                        className="value-source-custom jade-select"
                        style={{width: "100%"}}
                        onChange={(value) => onRightFromSelectChange(condition, value)}
                        options={[
                            {value: 'Reference', label: '引用'},
                            {value: 'Input', label: '输入'},
                        ]}
                        value={condition.value.find(item => item.name === "right").from}
                    />
                </Form.Item>
            </Col>
            <Col span={7} style={{paddingLeft: 0}}>
                {renderRightSideValueComponent(condition.id, condition.value.find(item => item.name === "right"), condition.value.find(item => item.name === "left").type, condition.condition)} {/* 渲染对应的组件 */}
            </Col>
            <Col span={1} style={{paddingLeft: 0}}>
                {renderDeleteConditionIcon(index, condition.id)}
            </Col>
        </Row>
    };

    /**
     * 渲染每个条件分支中的表格
     *
     * @return {JSX.Element|null}
     */
    const renderForm = () => {
        return <div style={{ display: 'flex'}}>
            {branch.conditions.length > 1 && renderLeftDiagram()}
            <div
                className={"jade-form condition-right-component"}
            >
                <Row gutter={16}>
                    <Col span={6}>
                        <Form.Item>
                            <span className="jade-font-size jade-font-color">变量</span>
                        </Form.Item>
                    </Col>
                    <Col span={6}>
                        <Form.Item>
                            <span className="jade-font-size jade-font-color">条件</span>
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item>
                            <span className="jade-font-size jade-font-color">比较对象</span>
                        </Form.Item>
                    </Col>
                </Row>
                {branch.conditions.map((condition, index) => renderCondition(condition, index))}
                <Row gutter={16} style={{marginBottom: "6px", marginRight: 0}}>
                    <Button type="link" className="icon-button" onClick={() => addCondition(branch.id)}
                            style={{height: "32px", paddingLeft: "8px"}}>
                        <PlusOutlined/>
                        <span>添加条件</span>
                    </Button>
                </Row>
            </div>
        </div>
    };

    return (
        <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["ifPanel"]}>
            {
                <Panel
                    key={"ifPanel"}
                    header={
                        <div className="panel-header">
                            <span className="jade-panel-header-font">{name}</span>
                            {renderPriority()}
                            {renderDeleteIcon()}
                        </div>
                    }
                    className="jade-panel"
                >
                    <div className={"jade-custom-panel-content"}>
                        {renderForm()}
                    </div>
                </Panel>
            }
        </Collapse>
    );
}