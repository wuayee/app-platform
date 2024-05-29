import {Button} from "antd";
import {CloseOutlined} from "@ant-design/icons";
import {useEffect, useRef} from "react";
import SectionFactory from "@/components/flowRunComponent/SectionFactory.jsx";

/**
 * 流程测试详情报告
 *
 * @param shape 节点图形
 * @param showResultPanel 是否展示详情报告
 * @param handleExpandResult 回调
 * @return {JSX.Element}
 * @constructor
 */
export default function RunReport({shape, showResultPanel, handleExpandResult}) {
    const resultPanelRef = useRef(null);

    useEffect(() => {
        const updateResultPanelPosition = () => {
            if (resultPanelRef.current) {
                resultPanelRef.current.style.left = `${shape.width + 20}px`;
                resultPanelRef.current.style.top = `0px`;
            }
        };
        updateResultPanelPosition();
    }, [showResultPanel]);

    return (
            <div className="result-panel" ref={resultPanelRef}>
                <div className="result-header">
                    <div className="header-title">{shape.text} 运行结果</div>
                    <Button type="link" onClick={handleExpandResult} icon={<CloseOutlined/>}/>
                </div>
                <SectionFactory shape={shape}/>
            </div>);
}