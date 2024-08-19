import {Switch, Form} from "antd";
import "./style.css";
import PropTypes from "prop-types";
import {useTranslation} from "react-i18next";

Required.propTypes = {
    itemId: PropTypes.string.isRequired, // 确保 itemId 是一个必需的字符串
    disableModifiable: PropTypes.bool.isRequired, // 确保 disableModifiable 是一个必需的bool值
    onChange: PropTypes.func.isRequired, // 确保 onChange 是一个必需的函数
};

/**
 * 开始节点关于入参是否必填。
 *
 * @param itemId 名称所属Item的唯一标识
 * @param propValue 描述的初始值
 * @param disableModifiable 该字段是否禁止修改
 * @param onChange 值被修改时调用的函数
 * @returns {JSX.Element} 开始节点关于入参是否必填的Dom。
 */
export default function Required({itemId, propValue, disableModifiable, onChange}) {
    const { t } = useTranslation();
    return (<>
        <Form.Item className="jade-form-item" name={`required-${itemId}`}>
            <div className="required-wrapper"> {/* 添加一个外层容器，并为其应用样式 */}
                <span className={"jade-font-size"}
                      style={{color: "rgba(28,31,35,.35)"}}>{t('requiredOrNot')}</span> {/* 使用 span 元素代替 div，使其更适合文本内容 */}
                <div className="switch-container"> {/* 使用一个额外的 div 容器来包裹 Switch，并应用样式 */}
                    <Switch
                        className="required-switch"
                        value={propValue ?? true}
                        disabled={disableModifiable}
                        onChange={e => onChange("isRequired", e)}
                    />
                </div>
            </div>
        </Form.Item>
    </>);
}