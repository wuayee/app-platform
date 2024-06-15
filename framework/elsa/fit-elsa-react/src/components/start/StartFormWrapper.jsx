import {useState} from 'react';
import {Button, Collapse, Popover} from 'antd';
import {DeleteOutlined, QuestionCircleOutlined, PlusOutlined} from '@ant-design/icons';
import StartInputForm from "./StartInputForm.jsx";
import Memory from './Memory.jsx';
import "./style.css";
import {useDataContext, useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import {v4 as uuidv4} from "uuid";

const {Panel} = Collapse;

/**
 * 开始表单Wrapper
 *
 * @param disabled 是否禁用.
 * @returns {JSX.Element} 开始表单Wrapper的DOM
 */
export default function StartFormWrapper({disabled}) {
    const dispatch = useDispatch();
    const data = useDataContext();
    const shape = useShapeContext();
    const config = shape.graph.configs.find(node => node.node === "startNodeStart");
    const items = data.find(item => item.name === "input").value // 找出 name 为 "input" 的项，获取value值

    // items中所有初始都为打开状态
    const [openItems, setOpenItems] = useState(() => {
        return items.map(item => item.id);
    });

    // 添加新元素到 items 数组中，并将其 key 添加到当前展开的面板数组中
    const addItem = () => {
        const newItemId = "input_" + uuidv4();
        setOpenItems([...openItems, newItemId]); // 将新元素 key 添加到 openItems 数组中
        dispatch({actionType: "addInputParam", id: newItemId});
    };

    const renderAddInputIcon = () => {
        const configObject = data.find(item => item.name === "input")
                ?.config
                ?.find(configItem => configItem.hasOwnProperty("allowAdd")); // 查找具有 "allowAdd" 属性的对象
        if (configObject ? configObject.allowAdd : false) {
            return (<>
                <Button disabled={disabled}
                        type="text"
                        className="icon-button"
                        onClick={addItem}
                        style={{"height": "32px", marginLeft: "auto", marginRight: "12px"}}>
                    <PlusOutlined/>
                </Button>
            </>);
        }
    };

    const renderDeleteIcon = (item) => {
        if (!item.disableModifiable) {
            return (<>
                <Button disabled={disabled}
                        type="text"
                        className="icon-button"
                        style={{"height": "22px", "marginLeft": "auto"}}
                        onClick={() => handleDelete(item.id)}>
                    <DeleteOutlined/>
                </Button>
            </>);
        }
    };

    const handleDelete = (itemId) => {
        const updatedOpenItems = openItems.filter((key) => key !== itemId);
        setOpenItems(updatedOpenItems);
        dispatch({actionType: "deleteInputParam", id: itemId});
    };

    const content = (<div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
            <p>定义启动工作流所需的输入参数，这些内容将由</p>
            <p>大模型在机器人对话过程中读取，允许大模型</p>
            <p>在适当的时间启动工作流并填写正确的信息。</p>
        </div>);

    return (<>
        <div>
            <div style={{
                display: "flex", alignItems: "center", marginBottom: "8px", paddingLeft: "8px", paddingRight: "4px", height: "32px"
            }}>
                <div className="jade-panel-header-font">输入</div>
                <Popover content={content}>
                    <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                </Popover>
                {renderAddInputIcon()}
            </div>
            <Collapse bordered={false}
                      activeKey={openItems}
                      onChange={(keys) => setOpenItems(keys)}
                      className="jade-custom-collapse">
                {
                    items.map((item) => (
                            <Panel
                                key={item.id}
                                header={
                                    <div className="panel-header">
                                        <span className="jade-panel-header-font">{item.name}</span> {/* 显示Name值的元素 */}
                                        {renderDeleteIcon(item)}
                                    </div>
                                }
                                className="jade-panel"
                                style={{marginBottom: 8, borderRadius: "8px", width: "100%"}}
                            >
                                <div className={"jade-custom-panel-content"}>
                                    <StartInputForm item={item}/>
                                </div>
                            </Panel>
                    ))
                }
            </Collapse>
            <Collapse bordered={false}
                      className="jade-custom-collapse"
                      defaultActiveKey={["historicalRecordsPanel"]}>
                {
                    <Panel
                        key={"historicalRecordsPanel"}
                        header={
                            <div className="panel-header"
                                 style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                                <span className="jade-panel-header-font">历史记录</span>
                            </div>
                        }
                        className="jade-panel"
                        style={{width: "100%"}}
                    >
                        <div className={"jade-custom-panel-content"}>
                            <Memory disabled={disabled} config={config}/>
                        </div>
                    </Panel>
                }
            </Collapse>
        </div>
    </>);
};