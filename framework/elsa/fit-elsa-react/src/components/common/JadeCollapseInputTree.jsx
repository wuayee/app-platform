import {Collapse} from 'antd';
import JadeInputTree from "./JadeInputTree.jsx";

const {Panel} = Collapse;

/**
 * 带input的树形组件.主要用于api调用节点.
 *
 * @param data 数据.
 * @param updateItem 修改方法.
 * @return {JSX.Element}
 * @constructor
 */
export default function JadeCollapseInputTree({data, updateItem}) {
    return (<>
        <Collapse bordered={false} className="jade-collapse-custom-background-color"
                  defaultActiveKey={["jadeInputTreePanel"]}>
            <Panel
                key={"jadeInputTreePanel"}
                header={
                    <div>
                        <span className='jade-panel-header-font'>输入</span>
                    </div>
                }
                className="jade-panel"
            >
                <JadeInputTree data={data} updateItem={updateItem}/>
            </Panel>
        </Collapse>
    </>);
};