import {Button, Collapse, Row} from 'antd';
import {MinusCircleOutlined, EyeOutlined, PlusOutlined} from '@ant-design/icons';
import {useDataContext, useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import "../common/style.css";
import PropTypes from "prop-types";
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
export default function PluginForm({disabled}) {
    const data = useDataContext();
    const dispatch = useDispatch();
    const shape = useShapeContext();
    const plugins = data.inputParams.find(item => item.name === "plugins");

    const renderPluginTypeIcon = (tags) => {
        // 检查 tags 是否为有效的数组
        if (!Array.isArray(tags)) {
            console.error("tags is not an array:", tags);
            return null;
        }

        return (<>
            {tags.find(item => item.value === "WATERFLOW") ? <WorkFlowIcon className="jade-plugin-type-icon"/> :
                <ToolIcon className="jade-plugin-type-icon"/>}
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
                    style={{"height": "22px", "marginLeft": "auto", "padding": "0 4px"}}
                    onClick={() => handleDelete(item.id)}>
                <MinusCircleOutlined/>
            </Button>
        </>);
    };

    const getSelectedPluginUniqueNames = () => {
        if (!plugins || !Array.isArray(plugins.value)) {
            return [];
        }

        return plugins.value.flatMap(plugin =>
            Array.isArray(plugin.value) ? plugin.value.flatMap(innerPlugin => {
                if (Array.isArray(innerPlugin.value)) {
                    const selectedPlugin = {};
                    innerPlugin.value.forEach(innerMostPlugin => {
                        if (innerMostPlugin.name === "tags" && Array.isArray(innerMostPlugin.value)) {
                            selectedPlugin[innerMostPlugin.name] = innerMostPlugin.value.map(tagItem => tagItem.value);
                        } else if (innerMostPlugin.name && innerMostPlugin.value !== undefined) {
                            selectedPlugin[innerMostPlugin.name] = innerMostPlugin.value;
                        }
                    });
                    return selectedPlugin;
                }
                return [];
            }) : []
        );
    };

    const onSelect = (data) => {
        dispatch({actionType: "changePluginConfig", value: data});
    };

    const pluginSelectEvent = {
        type: "SELECT_PLUGIN", value: {
            shapeId: shape.id,
            selectedPluginUniqueNames: getSelectedPluginUniqueNames(),
            onSelect: onSelect
        }
    };

    const triggerSelect = (e) => {
        e.preventDefault();
        shape.page.triggerEvent(pluginSelectEvent);
        e.stopPropagation(); // 阻止事件冒泡
    };

    return (<Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["pluginPanel"]}>
            {<>
                <Panel
                    key={"pluginPanel"}
                    header={<div className="panel-header"
                                 style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                        <span className="jade-panel-header-font">插件</span>
                        <Button disabled={disabled}
                                type="text" className="icon-button jade-panel-header-icon-position"
                                onClick={(event) => triggerSelect(event)}>
                            <PlusOutlined/>
                        </Button>
                    </div>}
                    className="jade-panel"
                >
                    <div className={"jade-custom-panel-content"}>
                        <div className={"jade-custom-multi-item-container"}>
                            {plugins && plugins.value && plugins.value.length > 0 ? (plugins.value.map((item) => {
                                const pluginValueList = item.value[0]?.value;
                                return (
                                    <Row key={`pluginRow-${item.id}`}>
                                        <div className={"jade-custom-multi-select-with-slider-div"}>
                                            {renderPluginTypeIcon(pluginValueList?.find(item => item.name === "tags")?.value ?? [])}
                                            <span className={"jade-custom-multi-select-item"}>
                                                {pluginValueList?.find(item => item.name === "name")?.value ?? ""}
                                            </span>
                                            {renderEyeIcon(pluginValueList?.find(item => item.name === "uri")?.value ?? "")}
                                            {renderDeleteIcon(item)}
                                        </div>
                                    </Row>);
                            })) : (<></>)}
                        </div>
                    </div>
                </Panel>
            </>}
        </Collapse>);
}