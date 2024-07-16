import {forwardRef, useImperativeHandle, useRef, useState} from "react";
import {Button, Flex} from "antd";
import RunReport from "@/components/flowRunComponent/RunReport.jsx";
import Icon from "@ant-design/icons";
import IconRunningLoading from '../asserts/icon-running-loading.svg?react';
import IconRunningResult from '../asserts/icon-running-result.svg?react';
import IconRunningUnRunning from '../asserts/icon-running-unRunning.svg?react';
import IconRunningSuccess from '../asserts/icon-running-success.svg?react';
import IconRunningFailed from '../asserts/icon-running-failed.svg?react';
import "./style.css";
import {NODE_STATUS} from "@";

// 左右上下的边距.
const MARGIN = {
    left: 20,
    right: 20,
    top: 64,
    bottom: 20
};

/**
 * 运行状态面板
 *
 * @type {*} 面板组件.
 */
const RunningStatusPanel = forwardRef(function ({shape, onReportShow}, ref) {
    const [showResultPanel, setShowResultPanel] = useState(false);
    const [, setHeight] = useState(shape.height);
    const reportRef = useRef();
    const status = getStatus(shape.runStatus);

    // 对外暴露方法.
    useImperativeHandle(ref, () => {
        return {
            getRunReportRect: () => {
                return reportRef.current && reportRef.current.getRunReportRect();
            },
            setHeight: (height) => {
                setHeight(height);
            }
        }
    });

    const getWidth = () => {
        return shape.width + MARGIN.left + MARGIN.right;
    };

    const getHeight = () => {
        return shape.height + MARGIN.top + MARGIN.bottom;
    };

    const getStyles = () => {
        return {
            left: `-${MARGIN.left}px`,
            top: `-${MARGIN.top}px`,
            width: getWidth(),
            height: getHeight(),
        };
    };

    /**
     * 展开关闭测试报告的回调
     */
    const handleExpandResult = () => {
        setShowResultPanel(!showResultPanel);
        setTimeout(() => {
            if (!showResultPanel) {
                onReportShow();
            }
        });
    };

    return (<>
        <div className={'running-status-panel'} style={getStyles()}>
            <Flex style={{width: '95%', height: 20, marginLeft: MARGIN.left, marginTop: (MARGIN.top - 20) / 2}}
                  justify={'space-between'}
                  align={'center'}>
                <Flex>
                    <div className={'icon-container'}>{status.getIcon()}</div>
                    <div className="run-text"><span>{status.title}</span></div>
                    {status.getTime(shape)}
                </Flex>
                <div>
                    <Button type="text"
                            className={`button-text button-text-no-hover-effect`}
                            disabled={!status.enableReport}
                            onMouseDown={(e) => {
                                // 防止每次点击触发elsa的鼠标事件，导致图形取消选中.
                                e.stopPropagation();
                                shape.page.getFocusedShapes().forEach(s => s.unSelect());
                                shape.select();
                            }}
                            onClick={handleExpandResult}
                            icon={<Icon component={IconRunningResult}/>}>
                        {showResultPanel ? '收起运行结果' : '运行结果'}
                    </Button>
                </div>
            </Flex>
        </div>
        {showResultPanel && (
                <RunReport shape={shape}
                           ref={reportRef}
                           showResultPanel={showResultPanel}
                           handleExpandResult={handleExpandResult}/>
        )}
    </>);
});

const SvgComponent = (props) => {
    const {SvgCom, width, height, ...rest} = props;
    return <SvgCom width={20} height={20} {...rest}/>;
}

const TimeComponent = ({shape}) => {
    return (<>
        <div className={`time-text-container time-text-container-${shape.runStatus}`}>
            <div className={`time-text time-text-${shape.runStatus}`}>
                {shape.cost ? (shape.cost / 1000) + "s" : "0.000s"}
            </div>
        </div>
    </>);
};

const getStatus = (nodeStatus) => {
    switch (nodeStatus) {
        case NODE_STATUS.SUCCESS:
            return {
                title: "运行成功",
                enableReport: true,
                getTime: (shape) => {
                    return <TimeComponent shape={shape}/>;
                },
                getIcon: () => {
                    return <Icon component={(props) => <SvgComponent {...props} SvgCom={IconRunningSuccess}/>} />;
                }
            };
        case NODE_STATUS.ERROR:
            return {
                title: "运行失败",
                enableReport: true,
                getTime: (shape) => {
                    return <TimeComponent shape={shape}/>;
                },
                getIcon: () => {
                    return <Icon component={(props) => <SvgComponent {...props} SvgCom={IconRunningFailed}/>} />;
                }
            };
        case NODE_STATUS.RUNNING:
            return {
                title: "运行中",
                enableReport: false,
                getTime: () => {
                    return null;
                },
                getIcon: () => {
                    return <Icon spin={true} component={(props) => <SvgComponent {...props} SvgCom={IconRunningLoading}/>} />;
                }
            };
        case NODE_STATUS.UN_RUNNING:
            return {
                title: "未运行",
                enableReport: false,
                getTime: () => {
                    return null;
                },
                getIcon: () => {
                    return <Icon component={(props) => <SvgComponent {...props} SvgCom={IconRunningUnRunning}/>} />;
                }
            };
        default:
            throw new Error("Unsupported node status[" + nodeStatus + "]");
    }
};

export default RunningStatusPanel;