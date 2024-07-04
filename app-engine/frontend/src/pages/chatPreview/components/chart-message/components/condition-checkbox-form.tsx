
import React, { useEffect, useState, useRef } from 'react';
import { Checkbox, Input, Button } from 'antd';
import { getOptions } from '@shared/http/aipp';
import { getOptionsByCasecade } from '../utils/chart-condition';
const { Search } = Input;

const CheckBoxForm = (props) => {
  const { filterCurrent, setFilterCurrent, formData } = props;
  const [datasetOptions, setDatasetOptions] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [checkedList, setCheckedList] = useState([]);
  const checkedArr = useRef([]);
  const optionList = useRef([]);

  useEffect(() => {
    dataInit();
  }, [filterCurrent]);
  // 初始化数据
  const dataInit = () => {
    if (Object.prototype.hasOwnProperty.call(filterCurrent, 'belongs') && filterCurrent.options.length === 0) {
      const data = getOptionsByCasecade(filterCurrent, JSON.parse(formData));
      const options =  Array.from(
        new Set([...filterCurrent.value, ...data])
      ).map((value) => {
        return {
          label: value,
          value,
        };
      });
      setDatasetOptions(() => {
        optionList.current = options;
        return options
      });
    } else {
      let options = filterCurrent.options.filter((item) =>
        item.label.includes(searchText)
      );
      setDatasetOptions(() => {
        optionList.current = options;
        return options
      });
    }
    let checkedData = filterCurrent.value || [];
    checkedArr.current = JSON.parse(JSON.stringify(checkedData));
    setCheckedList(checkedData);
  }
  // 选中
  const onChange = (e) => {
    const { value } = e.target;
    if (!checkedArr.current.includes(value)) {
      checkedArr.current.push(value);
    } else {
      checkedArr.current = checkedArr.current.filter(item => item !== value);
    }
    let arr = JSON.parse(JSON.stringify(checkedArr.current));
    valueConfirm(arr);
    setCheckedList(arr);
  }
  // 搜索
  const onSearch = (value) => {
    if (filterCurrent.filterType === 'backEnd') {

    } else {
      normalSearch(value);
    }
  }
  // 前端搜索
  const normalSearch = (val) => {
    let options = optionList.current.map(item => {
      if (val.length) {
        item.hide = (item.label.indexOf(val) === -1);
      } else {
        item.hide = false;
      }
      return item
    });
    setDatasetOptions(options);
  }
  // 后端搜索
  const requestSearch = (arr) => {
    
  }
  // 全选
  const handleChoseAll = () => {
    let arr = [];
    if (checkedList.length !== datasetOptions.length) {
      arr = datasetOptions.map(item => item.value);
    }
    checkedArr.current = [ ...arr ];
    valueConfirm(arr);
    setCheckedList(arr);
  }
  // 确定回显数据
  const valueConfirm = (arr) => {
    let obj = JSON.parse(JSON.stringify(filterCurrent));
    obj.value = arr;
    setFilterCurrent(obj);
  }
  return <>
    <div className='check-box-form'>
      <div className='check-search'>
        <Search onSearch={onSearch} placeholder='请输入关键字' allowClear/>
        {
          datasetOptions.length > 0 &&
          <Button type='primary' onClick={handleChoseAll}>
            { checkedList.length === datasetOptions.length ? '取消全选' : '全选' }
          </Button>
        }
      </div>
      <div className='check-content'>
        { datasetOptions.map(item => {
          return  <div key={item.value} className={['check-content-item', item.hide ? 'item-hidden': null].join(' ')}>
                    <Checkbox value={item.value} checked={checkedList.includes(item.value)} onChange={onChange}>
                      <span className='item-text'>{item.label}</span>
                    </Checkbox>
                  </div>
        }) }
      </div>
    </div>
  </>
};


export default CheckBoxForm;
