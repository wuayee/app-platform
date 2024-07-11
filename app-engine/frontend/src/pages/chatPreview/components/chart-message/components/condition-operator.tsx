
import React, { useEffect, useState } from 'react';
import { Radio } from 'antd';
import { operatorMap } from '../common/condition';

const ConditionOpreator = (props) => {
  const { filterCurrent, setFilterCurrent } = props;
  const [operator, setOperator] = useState('in');

  useEffect(() => {
    setOperator(filterCurrent.operator);
  }, [])
  const onChange = (e) => {
    setOperator(e.target.value);
    let obj = JSON.parse(JSON.stringify(filterCurrent));
    obj.operator = e.target.value;
    setFilterCurrent(obj);
  }
  return <>
    <div className='filter-oprator'>
      <Radio.Group onChange={onChange} value={operator}>
        { Object.keys(operatorMap).map((item, index) => {
          return <Radio value={item} key={index} >{operatorMap[item]}</Radio>
        })}
      </Radio.Group>
    </div>
  </>
};


export default ConditionOpreator;
