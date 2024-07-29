import {ModelForm} from "./ModelForm.jsx";
import {JadeInputForm} from '../common/JadeInputForm.jsx';
import {LlmOutput} from './LlmOutput.jsx';
import SkillForm from "./SkillForm.jsx";
import "./style.css";
import {useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import {useEffect, useState} from "react";
import httpUtil from "../util/httpUtil.jsx";
import PropTypes from "prop-types";

LlmFormWrapper.propTypes = {
    data: PropTypes.object.isRequired,
    disabled: PropTypes.bool
};

/**
 * 大模型表单Wrapper
 *
 * @param data 数据.
 * @param disabled 是否禁用.
 * @returns {JSX.Element} 大模型表单Wrapper的DOM
 */
export default function LlmFormWrapper({data, disabled}) {
    const dispatch = useDispatch();
    const shape = useShapeContext();
    let config;
    if (!shape || !shape.graph || !shape.graph.configs) {
        console.error('Cannot get shape.graph.configs.');
    } else {
        config = shape.graph.configs.find(node => node.node === "llmNodeState");
    }
    const [modelOptions, setModelOptions] = useState([]);
    const [toolOptions, setToolOptions] = useState([]);
    const [workflowOptions, setWorkflowOptions] = useState([]);
    const modelData = {
        model: data.inputParams.find(item => item.name === "model"),
        temperature: data.inputParams.find(item => item.name === "temperature"),
        systemPrompt: data.inputParams.find(item => item.name === "systemPrompt"),
        prompt: data.inputParams.filter(item => item.name === "prompt")
                .flatMap(item => item.value)
                .find(item => item.name === "template")
    };

    const initItems = () => {
        return data.inputParams
            .filter(item => item.name === "prompt") // 找出 name 为 "input" 的项
            .flatMap(item => item.value) // 将每个符合条件的项的 value 属性展开成一个数组
            .filter(item => item.name === "variables") // 找出 name 为 "variable" 的项
            .flatMap(item => item.value) // 将每个符合条件的项的 value 属性展开成一个数组
    };

    const addItem = (id) => {
        dispatch({actionType: "addInputParam", id: id});
    };

    const updateItem = (id, value) => {
        dispatch({actionType: "changeInputParams", id: id, updateParams: value});
    };

    const deleteItem = (id) => {
        dispatch({actionType: "deleteInputParam", id: id});
    };

    const content = (<>
        <div>
            <p>输入需要添加到提示词模板中的信息，可被提示词模板引用</p>
        </div>
    </>);

    useEffect(() => {
        if (!config || !config.urls) {
            console.error('Cannot get config.urls.');
        } else {
            if (!config.urls.llmModelEndpoint) {
                console.error('Cannot get config.urls.llmModelEndpoint.');
            } else {
                // 发起网络请求获取 options 数据
                httpUtil.get(config.urls.llmModelEndpoint + '/models', {}, (jsonData) => setModelOptions(jsonData.data.map(item => {
                    return {
                        value: item.id,
                        label: item.id
                    };
                })));
            }
            if (!config.urls.toolListEndpoint) {
                console.error('Cannot get config.urls.toolListEndpoint.');
            } else {
                httpUtil.get(config.urls.toolListEndpoint + '/tools?pageNum=1&pageSize=10&includeTags=FIT&excludeTags=Config', {}, (jsonData) => setToolOptions(jsonData.data.map(item => {
                    return {
                        value: item.uniqueName,
                        label: item.name
                    };
                })));
            }
            if (!config.urls.workflowListEndpoint) {
                console.error('Cannot get config.urls.workflowListEndpoint.');
            } else {
                httpUtil.get(config.urls.workflowListEndpoint + '/tools?pageNum=1&pageSize=10&includeTags=WATERFLOW', {}, (jsonData) => setWorkflowOptions(jsonData.data.map(item => {
                    return {
                        value: item.uniqueName,
                        label: item.name
                    };
                })));
            }
        }
    }, []); // useEffect 依赖数组为空，表示只在组件挂载时执行一次

    return (
        <div>
            <JadeInputForm disabled={disabled}
                           items={initItems()}
                           addItem={addItem}
                           updateItem={updateItem}
                           deleteItem={deleteItem}
                           content={content}/>
            <ModelForm disabled={disabled} modelData={modelData} shapeId={shape.id} modelOptions={modelOptions}/>
            {/*<SkillForm disabled={disabled} toolOptions={toolOptions} workflowOptions={workflowOptions} config={config}/>*/}
            <LlmOutput outputItems={data.outputParams}/>
        </div>
    );
}