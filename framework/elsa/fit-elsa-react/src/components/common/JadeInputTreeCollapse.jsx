import {Collapse, Popover} from 'antd';
import {QuestionCircleOutlined} from "@ant-design/icons";
import PropTypes from "prop-types";

const {Panel} = Collapse;

JadeInputTreeCollapse.propTypes = {
    data: PropTypes.array.isRequired,
    children: PropTypes.array.isRequired
};

/**
 * 带input的树形组件.主要用于api调用节点.
 *
 * @param data 数据.
 * @param children 子组件列表.
 * @return {JSX.Element}
 * @constructor
 */
export default function JadeInputTreeCollapse({data, children}) {
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
        <Collapse bordered={false} className="jade-custom-collapse"
                  defaultActiveKey={["jadeInputTreePanel"]}>
            <Panel
                key={"jadeInputTreePanel"}
                header={
                    <div>
                        <span className='jade-panel-header-font'>输入</span>
                        {content ? (
                            <Popover content={content}>
                                <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                            </Popover>
                        ) : null}
                    </div>
                }
                className="jade-panel"
            >
                <div className={"jade-custom-panel-content"}>
                    {children}
                </div>
            </Panel>
        </Collapse>
    </>);
}