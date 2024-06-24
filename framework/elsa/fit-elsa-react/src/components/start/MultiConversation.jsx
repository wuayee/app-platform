import {Collapse, Switch} from 'antd';
import PropTypes from "prop-types";
import MultiConversationContent from "@/components/start/MultiConversationContent.jsx";

const {Panel} = Collapse;

MultiConversation.propTypes = {
    itemId: PropTypes.string.isRequired, // 确保 itemId 是一个必需的string类型
};

/**
 * 多轮对话组件
 *
 * @param itemId 组件唯一标识.
 * @param config 配置.
 * @param className 对应的样式类名.
 * @param disabled 禁用.
 * @param props 相关属性结构体.
 * @returns {JSX.Element} 多轮对话组件的Dom
 */
export default function MultiConversation({
                                              itemId,
                                              config = {},
                                              className = '',
                                              disabled = false,
                                              props = {},
                                          }) {

    const switchValue = props?.switch?.value ?? true;
    const onSwitchChange = props?.switch?.onChange || {}

    return (<>
        <div className={className}>
            <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["multiConversationPanel"]}>
                {<Panel
                    key={"multiConversationPanel"}
                    header={<div className="panel-header">
                        <span className="jade-panel-header-font">多轮对话</span>
                        <Switch
                            value={switchValue}
                            disabled={disabled}
                            onClick={(value, event) => event.stopPropagation()}
                            onChange={e => onSwitchChange(e)}
                        />
                    </div>}
                    style={{width: "100%"}}
                >
                    <MultiConversationContent itemId={itemId} disabled={disabled} config={config}
                                              className={`${className}-content`}
                                              props={props}/>
                </Panel>}
            </Collapse>
        </div>
    </>);
}