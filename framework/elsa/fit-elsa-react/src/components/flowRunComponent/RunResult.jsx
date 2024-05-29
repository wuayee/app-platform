import {Button, Layout} from 'antd';
import {CheckCircleFilled, CloseCircleFilled, LoadingOutlined} from '@ant-design/icons';
import "./style.css"
import RunReport from "@/components/flowRunComponent/RunReport.jsx";
import {useState} from "react";

const {Header, Content} = Layout;

const map = new Map;
map.set("success", "运行成功");
map.set("failed", "运行失败");
map.set("running", "试运行中");

/**
 * 流程测试概览
 *
 * @param shape 节点图形
 * @return {JSX.Element}
 * @constructor
 */
const RunResult = ({shape}) => {
    const [showResultPanel, setShowResultPanel] = useState(false);
    const status = shape.runStatus;

    /**
     * 展开关闭测试报告的回调
     */
    const handleExpandResult = () => {
        setShowResultPanel(!showResultPanel);
    };

    /**
     * 获取流程测试概览图标
     *
     * @param status
     * @return {JSX.Element}
     */
    const getRunIcon = status => {
        if (status === "failed") {
            return <CloseCircleFilled className="success-icon"/>;
        } else if (status === "success") {
            return <CheckCircleFilled className="failed-icon"/>;
        } else if (status === "running") {
            return <LoadingOutlined className="running-icon"/>;
        }
    };

    return (
            <Layout>
                <Content>
                    {/*todo 需要根据shape的状态，选择使用哪个css样式*/}
                    <div className={`graph-header-common graph-header-${status}`}>
                        {getRunIcon(status)}
                        <div className="run-text">{map.get(status)}</div>
                        <div className={`time-text-container time-text-container-${status}`}>
                            <div className={`time-text time-text-${status}`}>{shape.cost ? (shape.cost / 1000) + "s" : "0.000s"}</div>
                        </div>
                        <Button type="text" className={`button-text button-text-no-hover-effect`}
                                onClick={handleExpandResult}>
                            {showResultPanel ? '收起运行结果' : '展开运行结果'}
                        </Button>
                    </div>
                    {showResultPanel && (
                            <RunReport shape={shape} showResultPanel={showResultPanel}
                                       handleExpandResult={handleExpandResult}/>
                    )}
                </Content>
            </Layout>
    );
};

export default RunResult;
