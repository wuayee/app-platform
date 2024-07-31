import {Button, Collapse, Row} from 'antd';
import {CloseOutlined, EyeOutlined} from '@ant-design/icons';
import {useDataContext, useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import "../common/style.css";
import PropTypes from "prop-types";
import {CustomizedSelectButton} from "@/components/common/CustomizedSelectButton.jsx";
import React from "react";
import ToolIcon from "../asserts/icon-plugin-type-tool.svg?react";
import WorkFlowIcon from "../asserts/icon-workflow.svg?react";

const {Panel} = Collapse;

PluginForm.propTypes = {
    config: PropTypes.object.isRequired, // 确保 config 是一个必需的object类型
    disable: PropTypes.bool
};

/**
 * 大模型节点插件表单。
 *
 * @param config 相关配置。
 * @param disabled 是否禁用.
 * @returns {JSX.Element} 大模型节点技能表单的DOM。
 */
export default function PluginForm({config, disabled}) {
    const data = useDataContext();
    const dispatch = useDispatch();
    const shape = useShapeContext();
    const plugins = data.inputParams.find(item => item.name === "plugins");

    const renderPluginTypeIcon = (item) => {
        return (<>
            {item.type !== "tool" ? <ToolIcon className="jade-plugin-type-icon"/> :
                <WorkFlowIcon className="jade-plugin-type-icon"/>}
        </>);
    };

    const openNewTab = (url) => {
        window.open(url, '_blank');
    };

    const renderEyeIcon = (jumpUrl) => {
        return (<>
            <Button disabled={disabled}
                    type="text"
                    className="icon-button"
                    style={{"height": "22px", "marginRight": "auto", "padding": "0 4px"}}
                    onClick={() => openNewTab(jumpUrl)}>
                <EyeOutlined/>
            </Button>
        </>);
    };

    /**
     * 删除插件
     *
     * @param itemId 插件id
     */
    const handleDelete = (itemId) => {
        dispatch({actionType: "deletePlugin", id: itemId});
    };

    const renderDeleteIcon = (item) => {
        return (<>
            <Button disabled={disabled}
                    type="text"
                    className="icon-button"
                    style={{"height": "22px", "marginLeft": "auto"}}
                    onClick={() => handleDelete(item.id)}>
                <CloseOutlined/>
            </Button>
        </>);
    };

    const onSelect = (data) => {
        dispatch({actionType: "changePluginConfig", id: itemId, value: value});
    };

    const pluginSelectEvent = {
        type: "SELECT_PLUGIN",
        value: {
            shapeId: shape.id,
            selectedPlugins: shape.flowMeta.jober.converter.entity.inputParams[1].value,
            onSelect: onSelect
        }
    };

    return (
        <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["pluginPanel"]}>
            {
                <>
                    <Panel
                        key={"pluginPanel"}
                        header={
                            <div className="panel-header"
                                 style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                                <span className="jade-panel-header-font">插件</span>
                            </div>
                        }
                        className="jade-panel"
                    >
                        <div className={"jade-custom-panel-content"}>
                            <CustomizedSelectButton buttonText={"添 加"} customizedEvent={pluginSelectEvent}/>
                            <div className={"jade-custom-multi-item-container"}>
                                {plugins.value.map((item) => (<>
                                    <Row key={`knowledgeRow-${item.id}`}>
                                        <div className={"jade-custom-multi-select-with-slider-div"}>
                                            {renderPluginTypeIcon(item)}
                                            <span className={"jade-custom-multi-select-item"}>
                                        {item.value ?? ""}
                                    </span>
                                            {renderEyeIcon(item.url)}
                                            {renderDeleteIcon(item)}
                                        </div>
                                    </Row>
                                </>))}
                            </div>
                        </div>
                    </Panel>
                </>
            }
        </Collapse>
    );
}