import {Collapse} from 'antd';

const {Panel} = Collapse;

/**
 * 兜底条件表单。
 *
 * @returns {JSX.Element} 兜底条件表单的DOM。
 */
export default function ElseForm() {
    return (
        <Panel
            key={"elsePanel"}
            header={
                <div className="panel-header">
                    <span className="jade-panel-header-font">Else</span>
                </div>
            }
            className="jade-panel"
        >
        </Panel>
    );
}