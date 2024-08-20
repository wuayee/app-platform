import {useDispatch} from "@/components/DefaultRoot.jsx";
import ArrayUtil from "@/components/util/ArrayUtil.js";
import React from "react";
import PropTypes from "prop-types";
import JadeInputTree from "@/components/common/JadeInputTree.jsx";
import JadeInputTreeCollapse from "@/components/common/JadeInputTreeCollapse.jsx";

_EvaluationInvokeInput.propTypes = {
    inputData: PropTypes.array,
    disabled: PropTypes.bool
};

/**
 * 评估节点入参展示和入参赋值
 *
 * @param inputData 输入数据
 * @param disabled 是否禁用
 * @returns {JSX.Element}
 */
function _EvaluationInvokeInput({inputData, disabled}) {
    const dispatch = useDispatch();

    /**
     * 更新input
     *
     * @param id 需要更新的值的id
     * @param changes 需要改变的属性
     */
    const updateItem = (id, changes) => {
        dispatch({type: "update", id, changes});
    };

    /**
     * 返回评估节点input选项，所有类型不支持输入
     *
     * @return {[{label: string, value: string}]} 评估节点input选项
     */
    const getOptions = (node) => {
        switch (node.type) {
            case "Object":
                if (node.hasOwnProperty("generic") || node.props === undefined) {
                    return [{value: "Reference", label: "引用"}];
                } else {
                    return [{value: "Reference", label: "引用"},
                        {value: "Expand", label: "展开"}
                    ];
                }
            case "Array":
            default:
                return [{value: "Reference", label: "引用"}];
        }
    };

    return (<>
        <JadeInputTreeCollapse data={inputData} disabled={disabled}>
            <JadeInputTree disabled={disabled} data={inputData} updateItem={updateItem} getOptions={getOptions}/>
        </JadeInputTreeCollapse>
    </>);
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.disabled === nextProps.disabled && ArrayUtil.isEqual(prevProps.inputData, nextProps.inputData);
};

export const EvaluationInput = React.memo(_EvaluationInvokeInput, areEqual);