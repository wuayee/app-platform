
import React, { useEffect, useState } from 'react';
import { Radio, Input } from 'antd';
import { compareMap } from '../common/condition';

const CompareForm = (props) => {
  const { filterCurrent, setFilterCurrent } = props;
  const [logicValue, setLogicValue] = useState('');
  const [logicType, setLogicType] = useState('lt');

  useEffect(() => {
    setLogicValue(Object.values(filterCurrent.value)[0]);
    setLogicType(Object.keys(filterCurrent.value)[0]);
  }, []);
  const handleMagicChange = (e) => {
    setLogicType(e.target.value);
    let obj = JSON.parse(JSON.stringify(filterCurrent));
    obj.value = { [e.target.value]: logicValue };
    setFilterCurrent(obj);
  };
  const inputChange = (e) => {
    setLogicValue(e.target.value);
    let obj = JSON.parse(JSON.stringify(filterCurrent));
    obj.value = { [logicType]: e.target.value };
    setFilterCurrent(obj);
  }
  return <>
    <div className='filter-compare-content'>
      <Radio.Group onChange={handleMagicChange} value={logicType}>
        { Object.keys(compareMap).map((item, index) => {
          return <Radio value={item} key={index} >{compareMap[item]}</Radio>
        })}
      </Radio.Group>
      <div style={{ margin: '10px 0' }}>
        <Input value={logicValue} placeholder='请输入' onChange={inputChange} />
      </div>
    </div>
  </>
};


export default CompareForm;
