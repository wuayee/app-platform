import React, {
    createContext,
    forwardRef,
    useContext,
    useEffect,
    useImperativeHandle,
    useReducer,
    useRef,
    useState
} from "react";
import "./contentStyle.css";
import {Form} from "antd";
import RunResult from "@/components/flowRunComponent/RunResult.jsx";
import {NODE_STATUS} from "@/common/Consts.js";

const DataContext = createContext(null);
const ShapeContext = createContext(null);
const DispatchContext = createContext(null);
const FormContext = createContext(null);

/**
 * 默认根节点，作为其他所有组件的容器.
 *
 * @param shape 图形.
 * @param component 待加载组件.
 * @return {JSX.Element}
 * @constructor
 */
export const DefaultRoot = forwardRef(function ({shape, component, onReportShow}, ref) {
    const [data, dispatch] = useReducer(component.reducers, component.getJadeConfig());
    const id = "react-root-" + shape.id;
    const [form] = Form.useForm();
    const [runStatus, setRunStatus] = useState(shape.runStatus);
    const runReportRef = useRef();

    useImperativeHandle(ref, () => {
        return {
            getRunReportRect: () => {
                return runReportRef.current && runReportRef.current.getRunReportRect();
            }
        };
    });

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
    }, []);

    // 当state变化是调用.
    useEffect(() => {
        shape.graph.onChangeCallback && shape.graph.onChangeCallback();
    }, [data]);

    /**
     * 设置shape的流程测试状态
     *
     * @param status 状态
     */
    shape.setRunStatus = status => {
        setRunStatus(status);
        shape.runStatus = status;
    };

    return (<>
        {runStatus !== NODE_STATUS.DEFAULT && <RunResult shape={shape} ref={runReportRef} onReportShow={onReportShow}/>}
        <div id={id} style={{display: "block"}}>
            <Form form={form}
                  name={`form-${shape.id}`}
                  layout="vertical" // 设置全局的垂直布局
                  className={"jade-form"}
            >
                {shape.getHeaderComponent()}
                <FormContext.Provider value={form}>
                    <ShapeContext.Provider value={shape}>
                        <DataContext.Provider value={data}>
                            <DispatchContext.Provider value={dispatch}>
                                <div className="react-node-content" style={{borderRadius: shape.borderRadius + "px"}}>
                                    {component.getReactComponents()}
                                </div>
                            </DispatchContext.Provider>
                        </DataContext.Provider>
                    </ShapeContext.Provider>
                </FormContext.Provider>
            </Form>
        </div>
    </>);
});

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