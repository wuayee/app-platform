
import React, { useEffect, useState, useImperativeHandle } from 'react';
import { Select } from 'antd';
import ListFilter from './condition-list-form';
import DateFilter from './condition-date-form';
import CompareFilter from './codition-compare-form';
import CheckFilter from './condition-checkbox-form';
import OpratorFilter from './condition-operator';
import { compareMap, operatorMap, conditionMap } from '../common/condition';

const AddConditionForm = (props) => {
  const { formData, conditions, addRef } = props;
  const [filterItem, setFilterItem] = useState({
    label: '',
    prop: '',
    filterType: '',
    operator: 'in',
    options: [],
    category: '',
    value: [],
  });
  const [options, setOptions] = useState([]);
  const excludes = ['currency', 'filter', 'period_id', 'filter'];
  const allFields = Object.values(conditionMap).flat(1);
  const category = conditions.at(-1).category;
  
  useEffect(() => {
    getSelectOptions();
  }, [])

  // 设置字段下拉
  const getSelectOptions = () => {
    let arr = [];
    let data = JSON.parse(formData);
    arr = allFields.filter(item => {
      return !excludes.includes(item.prop) && !data.hasOwnProperty(item.prop) && !data[category].hasOwnProperty(item.prop);
    }).map(item => {
      return { label: item.label, value: item.label }
    });
    setOptions(arr);
  }
  const onChange = (val) => {
    const aimItem = allFields.find(item => item.label === (val));
    let currentItem = JSON.parse(JSON.stringify(aimItem));
    currentItem.category = category;
    currentItem.operator = 'in';
    currentItem.value = [];
    setFilterItem(currentItem);
  }
  // 设置表单类型
  const setFormDom = (type) => {
    switch (type) {
      case 'radio':
        return <ListFilter filterCurrent={filterItem} setFilterCurrent={setFilterItem}/>
        break;
      case 'time':
        return <DateFilter filterCurrent={filterItem} setFilterCurrent={setFilterItem}/>
        break;
      case 'compare':
        return <CompareFilter filterCurrent={filterItem} setFilterCurrent={setFilterItem} />
        break;
      default:
        return <CheckFilter filterCurrent={filterItem} setFilterCurrent={setFilterItem} formData={formData}/>
    }
  };
  const getFilterData = () => {
    return filterItem;
  }
   // 给父组件的测试回调
   useImperativeHandle(addRef, () => {
      return {
        'getFilterData': getFilterData,
      }
    })
  return <>
    <div className='add-form'>
      <div className='add-form-label'>字段名</div>
      <div className='add-form-content'>
        <Select
          showSearch
          placeholder='请输入搜索'
          optionFilterProp='label'
          onChange={onChange}
          options={options}
        />
      </div>
      <div className='add-form-label'>字段值</div>
      <div className={`add-form-content ${!filterItem.label.length ? 'no-click' : ''}`}>
        { filterItem.operator &&  <OpratorFilter filterCurrent={filterItem} setFilterCurrent={setFilterItem}/>}
        { setFormDom(filterItem.filterType) }
      </div>
    </div>
  </>
};


export default AddConditionForm;
