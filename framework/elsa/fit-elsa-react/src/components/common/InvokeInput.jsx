import {useDispatch} from "@/components/DefaultRoot.jsx";
import ArrayUtil from "@/components/util/ArrayUtil.js";
import React from "react";
import PropTypes from "prop-types";
import JadeInputTree from "@/components/common/JadeInputTree.jsx";
import JadeInputTreeCollapse from "@/components/common/JadeInputTreeCollapse.jsx";

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

    return (<>
        <JadeInputTreeCollapse data={inputData} disabled={disabled}>
            <JadeInputTree disabled={disabled} data={inputData} updateItem={updateItem}/>
        </JadeInputTreeCollapse>
    </>);
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.disabled === nextProps.disabled && ArrayUtil.isEqual(prevProps.inputData, nextProps.inputData);
};

export const InvokeInput = React.memo(_InvokeInput, areEqual);