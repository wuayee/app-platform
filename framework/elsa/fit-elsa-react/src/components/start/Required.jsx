import {Switch} from "antd";
import "./style.css";

/**
 * 开始节点关于入参是否必填。
 *
 * @returns {JSX.Element} 开始节点关于入参是否必填的Dom。
 */
export default function Required() {
    return (<div className="required-wrapper"> {/* 添加一个外层容器，并为其应用样式 */}
            <span>是否必填</span> {/* 使用 span 元素代替 div，使其更适合文本内容 */}
            <div className="switch-container"> {/* 使用一个额外的 div 容器来包裹 Switch，并应用样式 */}
                <Switch defaultChecked className="required-switch"/> {/* 添加一个 Switch，并应用样式 */}
            </div>
        </div>);
}