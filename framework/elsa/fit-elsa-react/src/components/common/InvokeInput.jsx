import JadeCollapseInputTree from "@/components/common/JadeCollapseInputTree.jsx";
import {useDispatch} from "@/components/DefaultRoot.jsx";
import ArrayUtil from "@/components/util/ArrayUtil.js";
import React from "react";
import PropTypes from "prop-types";

_InvokeInput.propTypes = {
    inputData: PropTypes.array,
    disabled: PropTypes.bool
}

/**
 * fit接口入参展示和入参赋值
 *
 * @returns {JSX.Element}
 */
function _InvokeInput({inputData, disabled}) {
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

    return (<JadeCollapseInputTree data={inputData} updateItem={updateItem} disabled={disabled}/>);
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.disabled === nextProps.disabled && ArrayUtil.isEqual(prevProps.inputData, nextProps.inputData);
};

export const InvokeInput = React.memo(_InvokeInput, areEqual);