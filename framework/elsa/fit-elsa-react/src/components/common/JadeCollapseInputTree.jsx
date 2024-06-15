import {Collapse, Popover} from 'antd';
import JadeInputTree from "./JadeInputTree.jsx";
import {InfoCircleOutlined} from "@ant-design/icons";
import React from "react";

const {Panel} = Collapse;

/**
 * 带input的树形组件.主要用于api调用节点.
 *
 * @param data 数据.
 * @param updateItem 修改方法.
 * @param disabled 禁用.
 * @return {JSX.Element}
 * @constructor
 */
export default function JadeCollapseInputTree({data, updateItem, disabled}) {
    const getContent = () => {
        const contentItems = data
            .filter(item => item.description)  // 过滤出有描述的项目
            .map((item) => (
                <p key={item.id}>{item.name}: {item.description}</p>
            ));

        if (contentItems.length === 0) {
            return null;  // 如果没有内容，返回null
        }

        return (
            <div className={"jade-font-size"} style={{ lineHeight: "1.2" }}>
                <p>参数介绍：</p>
                {contentItems}
            </div>
        );
    };

    const content = getContent();

    return (<>
        <Collapse bordered={false} className="jade-collapse-custom-background-color"
                  defaultActiveKey={["jadeInputTreePanel"]}>
            <Panel
                key={"jadeInputTreePanel"}
                header={
                    <div>
                        <span className='jade-panel-header-font'>输入</span>
                        {content ? (
                            <Popover content={content}>
                                <InfoCircleOutlined className="jade-panel-header-popover-content"/>
                            </Popover>
                        ) : null}
                    </div>
                }
                className="jade-panel transparent-background"
            >
                <JadeInputTree disabled={disabled} data={data} updateItem={updateItem}/>
            </Panel>
        </Collapse>
    </>);
};