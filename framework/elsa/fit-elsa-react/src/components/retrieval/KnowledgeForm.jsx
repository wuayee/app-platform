import React from "react";
import {Button, Collapse, Popover, Row} from "antd";
import {MinusCircleOutlined, PlusOutlined, QuestionCircleOutlined} from "@ant-design/icons";
import {KnowledgeConfig} from "@/components/retrieval/KnowledgeConfig.jsx";
import {useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";

const {Panel} = Collapse;

_KnowledgeForm.propTypes = {
    knowledge: PropTypes.array.isRequired,
    maximum: PropTypes.number.isRequired,
    disabled: PropTypes.bool
};

/**
 * 知识节点组件
 *
 * @param knowledge 知识库利列表.
 * @param maximum 最大值.
 * @param disabled 禁用状态.
 * @returns {JSX.Element}
 */
function _KnowledgeForm({knowledge, maximum, disabled}) {
    // 保存下拉框选项
    const dispatch = useDispatch();
    const shape = useShapeContext();
    const {t} = useTranslation();

    /**
     * 删除知识库
     *
     * @param itemId 知识库id
     */
    const handleDelete = (itemId) => {
        dispatch({type: "deleteKnowledge", id: itemId});
    };

    const renderDeleteIcon = (item) => {
        return (<>
            <Button disabled={disabled}
                    type="text"
                    className="icon-button"
                    style={{"height": "100%", "marginLeft": "auto", "padding": "0 4px"}}
                    onClick={() => handleDelete(item.id)}>
                <MinusCircleOutlined/>
            </Button>
        </>);
    };

    const onSelect = (data) => {
        dispatch({type: "updateKnowledge", value: data});
    };

    const getSelectedKnowledgeBases = () => {
        return knowledge.map(obj => {
            const innerValue = obj.value;
            return innerValue.reduce((acc, curr) => {
                acc[curr.name] = curr.value;
                return acc;
            }, {});
        });
    };

    const knowledgeBaseSelectEvent = {
        type: "SELECT_KNOWLEDGE_BASE",
        value: {
            shapeId: shape.id,
            selectedKnowledgeBases: getSelectedKnowledgeBases(),
            onSelect: onSelect
        }
    };

    const tips = <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
        <p>选择需要匹配的知识范围，</p>
        <p>仅从所选知识中调出信息</p>
    </div>;

    const triggerSelect = (e) => {
        e.preventDefault();
        shape.page.triggerEvent(knowledgeBaseSelectEvent);
        e.stopPropagation(); // 阻止事件冒泡
    };

    return (<Collapse bordered={false} className="jade-custom-collapse"
                      style={{marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%"}}
                      defaultActiveKey={['Knowledge']}>
        <Panel style={{marginBottom: 8, borderRadius: "8px", width: "100%"}}
               header={<div
                   style={{display: 'flex', alignItems: 'center'}}>
                   <span className="jade-panel-header-font">{t('knowledgeBase')}</span>
                   <Popover content={tips}>
                       <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                   </Popover>
                   <Button disabled={disabled}
                           type="text" className="icon-button jade-panel-header-icon-position"
                           onClick={(event) => triggerSelect(event)}>
                       <PlusOutlined/>
                   </Button>
               </div>}
               className="jade-panel"
               key="Knowledge"
        >
            <div className={"jade-custom-panel-content"}>
                <div className={"jade-custom-multi-item-container"}>
                    {knowledge
                        // 历史数据/模板中知识库自带一个空数组的object结构体，这里不需要渲染这个所以加上此条件
                        .filter(item => item.value && item.value.length > 0)
                        .map((item) => (<>
                            <Row key={`knowledgeRow-${item.id}`}>
                                <div className={"jade-custom-multi-select-with-slider-div item-hover"}>
                                <span className={"jade-custom-multi-select-item"}>
                                    {item.value?.find(subItem => subItem.name === "name")?.value ?? ""}
                                </span>
                                    {renderDeleteIcon(item)}
                                </div>
                            </Row>
                        </>))}
                </div>
                <KnowledgeConfig maximum={maximum} disabled={disabled}/>
            </div>
        </Panel>
    </Collapse>);
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.knowledge === nextProps.knowledge
        && prevProps.maximum === nextProps.maximum
        && prevProps.disabled === nextProps.disabled;
};

export const KnowledgeForm = React.memo(_KnowledgeForm, areEqual);