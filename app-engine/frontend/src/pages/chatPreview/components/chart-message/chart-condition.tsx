
import React, { useEffect, useState, useContext, useRef } from 'react';
import { Button, Popover } from 'antd';
import { ChatContext } from '@/pages/aippIndex/context';
import { conditionList, compareMap, casecadeMap } from './common/condition';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import ConditionItems from './components/codition-item';
import AddCondition from './components/add-condition';
import './styles/chart-condition.scss';

const ChartCondition = (props) => {
  const { data, confirm } = props;
  const dimension = useAppSelector((state) => state.commonStore.dimension);
  const { showCheck } = useContext(ChatContext);
  const [isDisabled, setIsDisabled] = useState(false);
  const [filter1, setFilter1] = useState([]);
  const [filter2, setFilter2] = useState([]);
  const filters = useRef<any>([]);
  const formData = useRef<any>();
  const hasLv1 = useRef<any>();

  // 初始化溯源表单
  useEffect(() => {
    formData.current = JSON.parse(data.dsl);
    filters.current = conditionList();
    hasLv1.current = data.dsl.includes('lv1_prod_rd_team_cn_name');
    formSetValue();
  }, [data]);

  useEffect(() => {
    setIsDisabled(showCheck);
  }, [showCheck]);

  // 根据产品线设置下拉
  useEffect(() => {
    if (dimension && dimension !== '其他') {
      !hasLv1.current && (formData.current.lv1_prod_rd_team_cn_name = [dimension]);
      formSetValue();
    }
  }, [dimension]);
  
  // 设置filter值
  const setFiltersVal = () => {
    const filterCopy = JSON.parse(JSON.stringify(filters.current));
    filterCopy.forEach((item) => {
      if (formData.current[item.prop]) {
        item.value = formData.current[item.prop];
      }
      if (formData.current.conditions && formData.current.conditions[item.prop]) {
        handleFilterValue(item, 'conditions');
      }
      item.hide && (item.hide = !hasLv1.current);
    });
    const filter1 = filterCopy.filter((item) => {
      if (item.value && item.value.constructor === Object) {
        return Object.keys(item.value).length;
      }
      return item.value;
    });
    let filter2 = setFiltersVal1();
    setFilter1([...filter1, ...filter2]);
  }
  // 设置filter1
  const setFiltersVal1 = () => {
    const filterCopy = JSON.parse(JSON.stringify(filters.current));
    filterCopy.forEach((item) => {
      item.hide && (item.hide = !hasLv1.current);
      formData.current.condition1 && handleFilterValue(item, 'condition1');
    });
    return filterCopy.filter((item) => {
      if (item.value && item.value.constructor === Object) {
        return Object.keys(item.value).length;
      }
      return item.value;
    });
  }
  // 设置filter2
  const setFiltersVal2 = () => {
    const filterCopy = JSON.parse(JSON.stringify(filters.current));
    filterCopy.forEach((item) => {
      item.hide && (item.hide = !hasLv1.current);
      formData.current.condition2 && handleFilterValue(item, 'condition2');
    });
    let list = filterCopy.filter((item) => {
      if (item.value && item.value.constructor === Object) {
        return Object.keys(item.value).length;
      }
      return item.value;
    });
    setFilter2(list);
  }
  // 表单确定
  const handleSave = (data) => {
    const formatData = data.operator ? { [data.operator]: data.value } : data.value
    if (!data.category) {
      formData.current[data.prop] = formatData;
      formSetValue();
      return;
    }
    formData.current[data.category][data.prop] = JSON.parse(
      JSON.stringify(formatData)
    );
    formSetValue();
    // 重置其他字段
    if (data.belongs) {
      const aimIndex = casecadeMap[data.belongs].findIndex(
        (item) => item.prop === data.prop
      );
      const lowerCascade = casecadeMap[data.belongs].slice(aimIndex + 1);
      const totalFd = [...filter1, ...filter2];
      lowerCascade.forEach((fd) => {
        const aimItem = totalFd.find((it) => it.prop === fd.prop);
        aimItem && (formData.current[aimItem.category][aimItem.prop] = []);
      });
    }
  }
  // 是否包含
  const handleFilterValue = (item, condition) => {
    item.category = condition;
    if (formData.current[condition][item.prop].hasOwnProperty('nin')) {
      item.value = formData.current[condition][item.prop].nin;
      item.operator = 'nin';
    } else if (formData.current[condition][item.prop].hasOwnProperty('in')) {
      item.operator = 'in';
      item.value = formData.current[condition][item.prop].in;
    } else {
      item.value = formData.current[condition][item.prop];
    }
  }
  // 确定表单回显
  const formSetValue = () => {
    setFiltersVal();
    setFiltersVal2();
  }
  const handleConfirm = () => {
    !hasLv1.current && delete formData.current.lv1_prod_rd_team_cn_name;
    confirm(formData.current);
  }
  const handleReset = () => {
    formData.current = JSON.parse(data.dsl);
    formSetValue();
  }
  const handleCancel = () => {
    formSetValue();
  }
  const handleRemove = (item) => {
    if (item.category) {
      delete formData.current[item.category][item.prop]
    } else {
      delete formData.current[item.prop]
    }
    formSetValue();
  }
  const getFormData = () => {
    return formData.current;
  }
  return <>
    <div className='condition-ctn'>
      <div className='cdt-title'>条件和维度</div>
      <div className='cdt-filters'>
        { filter1.length > 0 && filter1.map(item => {
            return <div className='cdt-tag' key={item.label}>
              { !item.hide && 
                <ConditionItems 
                  filterItem={item} 
                  formData={data.dsl} 
                  disabled={isDisabled} 
                  save={handleSave}
                  remove={handleRemove}
                />
              }
            </div>
          })
        }
        { filter1.length > 0 && <AddCondition getFormData={getFormData} conditions={filter1} save={handleSave}/> }
        { filter2.length > 0 &&  <div className='divider'></div> }
        { filter2.length > 0 && filter2.map(item => {
            return <div className='cdt-tag second-tag' key={item.label}>
              { !item.hide && 
                <ConditionItems 
                  filterItem={item} 
                  formData={data.dsl} 
                  disabled={isDisabled} 
                  save={handleSave}
                  remove={handleRemove}
                /> }
            </div>
        }) }
        { filter2.length > 0 && <AddCondition getFormData={getFormData} conditions={filter2} save={handleSave}/> }
      </div>
      { !showCheck && 
        <div className='cdt-toolbar'>
          <Button onClick={handleReset}>重置</Button>
          <Button type='primary' onClick={handleConfirm}>确定</Button>
        </div> 
      }
    </div>
  </>
};

export default ChartCondition;
