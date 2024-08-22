import {Button} from "antd";
import {CloseOutlined} from "@ant-design/icons";
import {forwardRef, useEffect, useImperativeHandle, useRef} from "react";
import SectionFactory from "@/components/flowRunComponent/SectionFactory.jsx";
import {useTranslation} from "react-i18next";

/**
 * 流程测试详情报告
 *
 * @param shape 节点图形
 * @param showResultPanel 是否展示详情报告
 * @param handleExpandResult 回调
 * @return {JSX.Element}
 * @constructor
 */
const RunReport = forwardRef(({shape, showResultPanel, handleExpandResult}, ref) => {
    const resultPanelRef = useRef(null);
    const {t} = useTranslation();

    useImperativeHandle(ref, () => {
        return {
            getRunReportRect: () => {
                if (!resultPanelRef.current) {
                    return null;
                }
                const {x, y, width, height} = resultPanelRef.current.getBoundingClientRect();
                return {
                    x: x,
                    y: y,
                    width: width,
                    height: height
                }
            }
        };
    });

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
                <div className="header-title">{shape.text} {t('runResult')}</div>
                <Button type="link" onClick={handleExpandResult} icon={<CloseOutlined/>}/>
            </div>
            <SectionFactory shape={shape}/>
        </div>);
});

export default RunReport;