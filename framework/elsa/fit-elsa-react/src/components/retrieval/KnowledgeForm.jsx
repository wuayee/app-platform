import React from "react";
import {Button, Col, Collapse, Form, Popover, Row} from "antd";
import {QuestionCircleOutlined, MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import KnowledgeConfig from "@/components/retrieval/KnowledgeConfig.jsx";
import {useDataContext, useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import {v4 as uuidv4} from "uuid";
import JadeScrollSelect from "@/components/common/JadeScrollSelect.jsx";

const {Panel} = Collapse;

/**
 * 知识节点组件
 *
 * @param disabled 禁用状态.
 * @returns {JSX.Element}
 */
export default function KnowledgeForm({disabled}) {
    // 保存下拉框选项
    const dispatch = useDispatch();
    const data = useDataContext();
    const shape = useShapeContext();
    const getKnowledge = data && [...data.inputParams.find(item => item.name === "knowledge").value];
    const baseUrl = shape.graph.configs && shape.graph.configs.find(config => config.node === "knowledgeState").urls.knowledgeUrl;

    /**
     * 构造当前组件的url
     *
     * @param page 页码
     * @return {string}
     */
    const buildUrl = (page) => {
        return baseUrl + '?pageNum=' + page + '&pageSize=10';
    }

    /**
     * 获取button的状态
     *
     * @return {boolean} 是否禁用
     */
    const getButtonDisable = () => {
        return getKnowledge.length <= 1;
    };

    /**
     * 添加一个知识库
     *
     * @param e 事件
     */
    const addItem = (e) => {
        dispatch({type: "addKnowledge", id: uuidv4()});
        e.stopPropagation();
    };

    /**
     * 知识库选择发生变化
     *
     * @param id 对应的id
     * @param key 字段名
     * @param value 字段值
     * @param options 所有选项数据
     */
    const handleKnowledgeChange = (id, key, value, options) => {
        dispatch({type: "editKnowledge", id: id, key: key, value: options.find(option => option.id === value)});
    };

    /**
     * 清除知识库选项
     *
     * @param id 对应的id
     */
    const handleKnowledgeClear = (id) => {
        dispatch({type: "clearKnowledge", id: id});
    };

    /**
     * 删除知识库
     *
     * @param itemId 知识库id
     */
    const handleDelete = (itemId) => {
        dispatch({type: "deleteKnowledge", id: itemId});
    };

    /**
     * 获取需要的数据
     *
     * @param response 接口返回信息
     * @return {*}
     */
    const dealResponse = (response) => {
        return response.data.items;
    };

    /**
     * 获取工具组件的options
     *
     * @param options 选项
     * @return {*}
     */
    const getOptions = (options) => options.map(option => ({
        value: option.id,
        label: option.name,
    }));

    /**
     * 获取当前选中框中选中的值
     *
     * @param item 当前的选中框对象
     * @return {*|string}
     */
    const getValue = item => {
        if (item.value && item.value.length !== 0) {
            return item.value.find(value => value.name === 'name').value;
        } else {
            return '';
        }
    };

    const tips = <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
        <p>选择需要匹配的知识范围，</p>
        <p>仅从所选知识中调出信息</p>
    </div>;

    return (<Collapse bordered={false} className="jade-custom-collapse"
                      style={{marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%"}}
                      defaultActiveKey={['Knowledge']}>
        <Panel
            style={{marginBottom: 8, borderRadius: "8px", width: "100%"}}
            header={<div
                style={{display: 'flex', alignItems: 'center'}}>
                <span className="jade-panel-header-font">知识库</span>
                <Popover content={tips}>
                    <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                </Popover>
                {/*<Button type="text" className="icon-button" onClick={(event) => addItem(event)}*/}
                {/*        style={{height: "22px", marginLeft: "auto"}}>*/}
                {/*    <PlusOutlined/>*/}
                {/*</Button>*/}
            </div>}
            className="jade-panel"
            key='Knowledge'
        >
            <div className={"jade-custom-panel-content"}>
                {getKnowledge.map((item) => (<Row
                    key={`knowledgeRow-${item.id}`}
                    gutter={16}
                >
                    {/*<Col span={22}>*/}
                    {/*    <Form.Item*/}
                    {/*            key={`knowledge-${item.id}`}*/}
                    {/*            style={{marginBottom: '8px'}}*/}
                    {/*            id={`knowledge-${item.id}`}*/}
                    {/*    >*/}
                    {/*        <JadeScrollSelect*/}
                    {/*                allowClear*/}
                    {/*                placeholder="选择知识库"*/}
                    {/*                id={`valueSource-select-${item.id}`}*/}
                    {/*                onClear={() => handleKnowledgeClear(item.id)}*/}
                    {/*                onChange={(value, options) => handleKnowledgeChange(item.id, 'value', value, options)}*/}
                    {/*                buildUrl={buildUrl}*/}
                    {/*                disabled={false}*/}
                    {/*                getOptions={getOptions}*/}
                    {/*                dealResponse={dealResponse}*/}
                    {/*                value={getValue(item)}*/}
                    {/*        />*/}
                    {/*    </Form.Item>*/}
                    {/*</Col>*/}
                    {/*<Col span={2} style={{paddingLeft: "2px"}}>*/}
                    {/*    <Form.Item key={`button-${item.id}`} style={{marginBottom: '8px'}}>*/}
                    {/*        <Button disabled={getButtonDisable()}*/}
                    {/*                type="text" className="icon-button"*/}
                    {/*                style={{alignItems: "center", marginLeft: "auto"}}*/}
                    {/*                onClick={() => handleDelete(item.id)}>*/}
                    {/*            <MinusCircleOutlined/>*/}
                    {/*        </Button>*/}
                    {/*    </Form.Item>*/}
                    {/*</Col>*/}
                </Row>))}
                <KnowledgeConfig disabled={disabled}/>
            </div>
        </Panel>
    </Collapse>);
}