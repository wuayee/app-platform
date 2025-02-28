/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import {useDispatch} from '@/components/DefaultRoot.jsx';
import ArrayUtil from '@/components/util/ArrayUtil.js';
import React from 'react';
import PropTypes from 'prop-types';
import JadeInputTreeCollapse from '@/components/common/JadeInputTreeCollapse.jsx';
import {JadeInputTree} from '@/components/common/JadeInputTree.jsx';

_InvokeInput.propTypes = {
    inputData: PropTypes.array,
    shapeStatus: PropTypes.object
}

/**
 * fit接口入参展示和入参赋值
 *
 * @returns {JSX.Element}
 */
function _InvokeInput({inputData, shapeStatus}) {
    const dispatch = useDispatch();

    /**
     * 更新input
     *
     * @param id 需要更新的值的id
     * @param changes 需要改变的属性
     */
    const updateItem = (id, changes) => {
        dispatch({type: 'update', id, changes});
    };

    return (<>
        <JadeInputTreeCollapse data={inputData} disabled={shapeStatus.disabled}>
            <JadeInputTree shapeStatus={shapeStatus} data={inputData} updateItem={updateItem}/>
        </JadeInputTreeCollapse>
    </>);
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.shapeStatus === nextProps.shapeStatus
        && ArrayUtil.isEqual(prevProps.inputData, nextProps.inputData);
};

export const InvokeInput = React.memo(_InvokeInput, areEqual);