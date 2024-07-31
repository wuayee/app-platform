import React, {createContext, useContext, useEffect, useReducer, useRef} from "react";
import "./contentStyle.css";
import {ConfigProvider, Form} from "antd";
import {useUpdateEffect} from "@/components/common/UseUpdateEffect.jsx";
import {EVENT_TYPE} from "@fit-elsa/elsa-core";

const DataContext = createContext(null);
const ShapeContext = createContext(null);
const DispatchContext = createContext(null);
const FormContext = createContext(null);

/**
 * 默认根节点，作为其他所有组件的容器.
 *
 * @param shape 图形.
 * @param component 待加载组件.
 * @param disabled 是否禁用.
 * @return {JSX.Element}
 * @constructor
 */
export const DefaultRoot = ({shape, component, disabled}) => {
    const [data, dispatch] = useReducer(component.reducers, component.getJadeConfig());
    const id = "react-root-" + shape.id;
    const [form] = Form.useForm();
    const domRef = useRef();

    /**
     * 用于图形可获取组件中的数据.
     *
     * @return {any} 组件中的数据.
     */
    shape.getLatestJadeConfig = () => {
        return JSON.parse(JSON.stringify(data));
    };

    /**
     * 校验当前节点的form输入是否合法.
     *
     * @return Promise 校验结果
     */
    shape.validateForm = () => {
        return form.validateFields();
    };

    // 相当于 componentDidMount
    useEffect(() => {
        shape.observe();
        shape.page.addEventListener(EVENT_TYPE.FOCUSED_SHAPES_CHANGE, onFocusedShapeChange);
        shape.page.triggerEvent({type: "shape_rendered", value: {id: shape.id}});
        return () => {
            shape.page.removeEventListener(EVENT_TYPE.FOCUSED_SHAPES_CHANGE, onFocusedShapeChange);
        };
    }, []);

    const onFocusedShapeChange = () => {
        if (!domRef.current) {
            return;
        }
        const focusedShapes = shape.page.getFocusedShapes();
        if (focusedShapes.includes(shape)) {
            domRef.current.style.pointerEvents = focusedShapes.length > 1 ? "none" : "auto";
        } else {
            domRef.current.style.pointerEvents = "auto";
        }
    };

    // 第一次进来不会触发，第一次发生变化时才触发.
    useUpdateEffect(() => {
        shape.graph.dirtied(null, {action: "jade_node_config_change", shape: shape.id});
    }, [data]);

    return (<>
        <ConfigProvider theme={{
            components: {
                Tree: {nodeSelectedBg: "transparent", nodeHoverBg: "transparent"}
            }
        }}>
            <div id={id} style={{display: "block"}} ref={domRef}>
                <Form form={form}
                      name={`form-${shape.id}`}
                      layout="vertical" // 设置全局的垂直布局
                      className={"jade-form"}
                >
                    {shape.drawer.getHeaderComponent(disabled)}
                    <FormContext.Provider value={form}>
                        <ShapeContext.Provider value={shape}>
                            <DataContext.Provider value={data}>
                                <DispatchContext.Provider value={dispatch}>
                                    <div className="react-node-content"
                                         style={{borderRadius: shape.borderRadius + "px"}}>
                                        {component.getReactComponents(disabled, data)}
                                    </div>
                                </DispatchContext.Provider>
                            </DataContext.Provider>
                        </ShapeContext.Provider>
                    </FormContext.Provider>
                    {shape.drawer.getFooterComponent()}
                </Form>
            </div>
        </ConfigProvider>
    </>);
};

export function useDataContext() {
    return useContext(DataContext);
}

export function useShapeContext() {
    return useContext(ShapeContext);
}

export function useDispatch() {
    return useContext(DispatchContext);
}

export function useFormContext() {
    return useContext(FormContext);
}