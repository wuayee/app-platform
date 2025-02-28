/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState } from 'react';
import { Form } from 'antd';

const InspirationList = (props) => {
  const { inspirationValues, clickInspiration, handleDeleteIns } = props;
  const [showOperateIndex, setShowOperateIndex] = useState('');
  const [showInspControl, setShowInspControl] = useState(true);
  // hover显示操作按钮
  const handleHoverItem = (index, operate) => {
    if (operate === 'enter') {
      setShowOperateIndex(index);
    } else {
      setShowOperateIndex('');
    }
  };

  // 获取编辑删除按钮
  const showOperate = (item) => {
    return (<span className='right'>
      <img src="./src/assets/images/edit_btn.svg" alt="" onClick={() => clickInspiration(item)} className={inspirationValues?.showInspiration ? '' : 'not-allowed'} />
      <img src="./src/assets/images/delete_btn.svg" alt="" onClick={() => handleDeleteIns(item.id)} className={inspirationValues?.showInspiration ? '' : 'not-allowed'} />
    </span>);
  };
  
  return <>
    <div>
      {inspirationValues && inspirationValues.inspirations.length ? <Form.Item
        name={['inspiration', 'inspirations']}
        label=''
        style={{
          marginTop: '10px',
          display: showInspControl ? 'block' : 'none',
        }}
      >
        {
          inspirationValues && inspirationValues.inspirations.map((item, index) => (
            <div className='inspiration-container' key={item.id} onMouseEnter={() => handleHoverItem(index, 'enter')} onMouseLeave={() => handleHoverItem(index, 'leave')}>
              <div className='card-title'>
                <span className='left'>
                  {item.name}
                </span>
                {index === showOperateIndex && showOperate(item)}
              </div>
              <div className='card-prompt'>
                {item.description}
              </div>
            </div>
          ))
        }
      </Form.Item> : ''}
    </div>
  </>
};

export default InspirationList;