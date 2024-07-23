import {useDispatch} from "@/components/DefaultRoot.jsx";
import React from "react";
import {CodePanel} from "@/components/code/CodePanel.jsx";
import {JadeObservableOutput} from "@/components/common/JadeObservableOutput.jsx";
import {JadeInputForm} from "@/components/common/JadeInputForm.jsx";
import PropTypes from "prop-types";

CodeWrapper.propTypes = {
    data: PropTypes.object.isRequired,
    disabled: PropTypes.bool
};

/**
 * code节点组件
 *
 * @return {*}
 * @constructor
 */
export default function CodeWrapper({data, disabled}) {
    const dispatch = useDispatch();
    const output = data.outputParams.find(item => item.name === "output");
    const input = data.inputParams;
    /**
     * 初始化数据
     *
     * @return {*}
     */
    const initItems = () => {
        return data.inputParams.find(item => item.name === "args").value
    };

    /**
     * 添加输入的变量
     *
     * @param id id 数据id
     */
    const addItem = (id) => {
        dispatch({type: "addInput", id: id});
    };

    /**
     * 更新入参变量属性名或者类型
     *
     * @param id 数据id
     * @param value 新值
     */
    const updateItem = (id, value) => {
        dispatch({type: "editInput", id: id, changes: value});
    };

    /**
     * 删除input
     *
     * @param id 需要删除的数据id
     */
    const deleteItem = (id) => {
        dispatch({type: "deleteInput", id: id});
    };

    const content = (<>
        <div>
            <p>输入需要添加到代码的变量，代码中可以直接引用此处添加的变量</p>
        </div>
    </>);

    return (<>
        <JadeInputForm disabled={disabled}
                       items={initItems()}
                       addItem={addItem}
                       updateItem={updateItem}
                       deleteItem={deleteItem}
                       content={content}/>
        <CodePanel disabled={disabled} input={input} dispatch={dispatch}/>
        <JadeObservableOutput disabled={disabled} output={output}/>
    </>);
}