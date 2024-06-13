import {Button, ConfigProvider, Input} from "antd";
import {useState} from "react";
import {useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";

/**
 * 自定义模型选择组件.
 * 该组件点击选择时只触发SELECT_MODEL事件，如何选择由事件监听方定义，选择后调用回调设置即可.
 *
 * @param disabled 是否禁用.
 * @param defaultValue 默认值.
 * @return {JSX.Element} 组件.
 * @constructor
 */
export const CustomizedModelSelect = ({disabled, defaultValue}) => {
    const [value, setValue] = useState(defaultValue);
    const dispatch = useDispatch();
    const shape = useShapeContext();

    const triggerSelect = (e) => {
        e.preventDefault();
        shape.page.triggerEvent({
            type: "SELECT_MODEL",
            value: {
                taskName: shape.flowMeta.jober.converter.entity.inputParams[0].value,
                shapeId: shape.id,
                selectedModel: shape.flowMeta.jober.converter.entity.inputParams[1].value,
                onSelect: onSelect
            }
        });
    };

    // 选择了model之后的回调.
    const onSelect = (data) => {
        setValue(data.name);
        dispatch({type: "insertOrUpdateModel", data});
    };

    return (<>
        <ConfigProvider theme={{components: {Button: {borderColorDisabled: "true"}}}}>
            <div className="model-text-container model-text">模型</div>
            <div className="model-select-container"
                 style={{display: "flex", border: "1px solid rgb(128, 128, 128, 0.2)", borderRadius: 4}}>
                <Input readOnly
                       className="model-select model-select-text"
                       disabled={disabled}
                       onMouseDown={(e) => triggerSelect(e)}
                       value={value}
                       defaultValue={value}/>
                <Button
                        className="button-select button-select-text"
                        disabled={disabled}
                        onClick={e => triggerSelect(e)}>选择</Button>
            </div>
        </ConfigProvider>
    </>);
}