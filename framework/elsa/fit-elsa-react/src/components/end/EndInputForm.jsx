/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import {JadeInputTree} from '@/components/common/JadeInputTree.jsx';
import React from 'react';
import PropTypes from 'prop-types';
import ArrayUtil from '@/components/util/ArrayUtil.js';
import {Collapse} from 'antd';
import {Trans} from 'react-i18next';
import {useDispatch} from '@/components/DefaultRoot.jsx';
import {JadePanelHeader} from '@/components/common/JadePanelHeader.jsx';

const {Panel} = Collapse;

/**
 * 结束节点输入表单.
 *
 * @param inputParams 输入数据.
 * @param shapeStatus 节点状态.
 * @param flowType 流程类型.
 * @constructor
 */
const _EndInputForm = ({inputParams, shapeStatus}) => {
    const dispatch = useDispatch();
    const tips =
        <div className={'jade-font-size'} style={{lineHeight: '1.2'}}>
            <Trans i18nKey='endOutputPopover' components={{p: <p/>}}/>
        </div>;

    // item被修改.
    const updateItem = (id, changes) => {
        dispatch({type: 'update', id, changes});
    };

    const deleteItem = (id) => {
        dispatch({type: 'deleteInput', id: id});
    };

    const addItem = () => {
        dispatch({type: 'addInput'});
    };

    return (<>
        <Collapse bordered={false} className='jade-custom-collapse' defaultActiveKey={['Output variable']}>
            <Panel
              header={<JadePanelHeader text={'output'} tips={tips} shapeStatus={shapeStatus} onClick={addItem}/>}
              className='jade-panel'
              key='Output variable'
            >
                <JadeInputTree
                  shapeStatus={shapeStatus}
                  data={inputParams}
                  updateItem={updateItem}
                  onDelete={deleteItem}
                  defaultExpandAll={true}
                  width={320}
                />
            </Panel>
        </Collapse>
    </>);
};

_EndInputForm.propTypes = {
    inputParams: PropTypes.array,
    shapeStatus: PropTypes.object,
    flowType: PropTypes.string,
};

const areEqual = (prevProps, nextProps) => {
    return prevProps.shapeStatus === nextProps.shapeStatus &&
        prevProps.flowType === nextProps.flowType &&
        ArrayUtil.isEqual(prevProps.inputParams, nextProps.inputParams);
};

export const EndInputForm = React.memo(_EndInputForm, areEqual);