import {Input} from "antd";
import {useEffect} from "react";
import {useShapeContext} from "@/components/DefaultRoot.jsx";

/**
 * 可被监听的Input组件.
 *
 * @param props 参数.
 * @return {JSX.Element}
 * @constructor
 */
export const JadeObservableInput = (props) => {
    const {onChange, ...rest} = props;
    if (!rest.id) {
        throw new Error("JadeObservableInput requires an id property.");
    }

    const shape = useShapeContext();
    if (!shape) {
        throw new Error("JadeObservableInput must be wrapped by ShapeContext.");
    }

    /**
     * 输入被修改时调用.
     *
     * @param e 事件对象.
     * @private
     */
    const _onChange = (e) => {
        onChange && onChange(e);

        // 触发节点的emit事件.
        shape.emit(rest.id, e.target.value);
    };

    // 组件初始化时注册observable.
    useEffect(() => {
        shape.page.registerObservable(shape.id, rest.id, rest.value, rest.parent);

        // 组件unmount时，删除observable.
        return () => {
            shape.page.removeObservable(shape.id, rest.id);
        };
    }, []);

    return <><Input {...rest} onChange={(e) => _onChange(e)} /></>
};