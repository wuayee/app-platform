import {Collapse, Popover} from "antd";
import {QuestionCircleOutlined} from "@ant-design/icons";
import React from "react";
import "./style.css";
import {JadeObservableTree} from "@/components/common/JadeObservableTree.jsx";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";

const {Panel} = Collapse;

_OutputForm.propTypes = {
    outputParams: PropTypes.array.isRequired
};

/**
 * 内容输出组件
 *
 * @returns {JSX.Element}
 * @constructor
 */
function _OutputForm({outputParams}) {
    const {t} = useTranslation();

    const tips = <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
        <p>{t('knowledgeBaseOutputPopover')}</p>
    </div>;

    return (
        <Collapse
            bordered={false} className="jade-custom-collapse"
            style={{marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%"}}
            defaultActiveKey={['Output']}>
            <Panel
                header={
                    <div
                        style={{display: 'flex', alignItems: 'center', paddingLeft: '-16px'}}>
                        <span className="jade-panel-header-font">{t('output')}</span>
                        <Popover content={tips}>
                            <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                        </Popover>
                    </div>
                }
                className="jade-panel"
                key='Output'
            >
                <div className={"jade-custom-panel-content"}>
                    <JadeObservableTree data={outputParams}/>
                </div>
            </Panel>
        </Collapse>
    )
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.outputParams === nextProps.outputParams && prevProps.disabled === nextProps.disabled;
};

export const OutputForm = React.memo(_OutputForm, areEqual);