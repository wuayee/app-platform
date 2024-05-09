import {InputNumber} from "antd";
import "./style.css";
import PropTypes from "prop-types";

ByTokenSize.propTypes = {
    propValue: PropTypes.string.isRequired, // 确保 propValue 是一个必需的number类型
    onValueChange: PropTypes.func.isRequired, // 确保 onNameChange 是一个必需的函数类型
};

/**
 * Memory按Token大小选取
 *
 * @param propValue 前端渲染的值
 * @param onValueChange 参数变化所需调用方法
 * @returns {JSX.Element} Memory按Token大小的Dom
 */
export default function ByTokenSize({propValue, onValueChange}) {
    const intValue = parseInt(propValue);

    const handleChange = (val) => {
        // 去除小数部分，强制转换为整数
        const integerValue = Math.floor(val);
        onValueChange("Integer", integerValue.toString());
    };

    return (<div style={{display: 'flex', alignItems: 'center'}}>
            <InputNumber
                style={{
                    width: "100%",
                }}
                min={1}
                max={10000}
                step={100}
                onChange={handleChange}
                stringMode
                value={!isNaN(intValue) ? intValue : 1000}
            />
        </div>);
}