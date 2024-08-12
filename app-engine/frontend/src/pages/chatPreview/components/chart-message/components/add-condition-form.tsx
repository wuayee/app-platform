import React, { useEffect, useState, useImperativeHandle } from 'react';
import { Select } from 'antd';
import ListFilter from './condition-list-form';
import DateFilter from './condition-date-form';
import CompareFilter from './codition-compare-form';
import CheckFilter from './condition-checkbox-form';
import OpratorFilter from './condition-operator';
import { belongsMap, casecadeMap, conditionMap } from '../common/condition';
import { getOptionNodes } from '@shared/http/aipp';
import { uniqBy } from 'lodash';
const AddConditionForm = (props: any) => {
  const { formData, conditions, addRef } = props;
  const [filterItem, setFilterItem] = useState({
    label: '',
    prop: '',
    filterType: '',
    operator: 'in',
    options: [],
    category: '',
    value: [],
    belongs: '',
    belongsTo: '',
    fullLabel: '',
  });
  const [options, setOptions] = useState([]);
  const excludes = ['currency', 'filter', 'period_id', 'filter'];
  const allFields = Object.values(conditionMap).flat(1);
  const category = conditions.at(-1).category;

  useEffect(() => {
    getSelectOptions();
  }, []);

  // 设置字段下拉
  const getSelectOptions = () => {
    let arr: any = [];
    let data = JSON.parse(formData);
    arr = allFields
      .filter((item) => {
        return (
          !excludes.includes(item.prop) &&
          !data.hasOwnProperty(item.prop) &&
          !data[category].hasOwnProperty(item.prop)
        );
      })
      .map((item) => {
        return { label: item.label, value: item.label };
      });
    setOptions(arr);
  };

  const onChange = async (val: string) => {
    const aimItem = allFields.find((item) => item.label === val);
    const aimCas =
      aimItem?.belongs &&
      JSON.parse(
        JSON.stringify(casecadeMap[aimItem?.belongs].find((val: any) => val.prop === aimItem.prop))
      );
    let currentItem = JSON.parse(JSON.stringify(aimItem));
    currentItem.category = category;
    currentItem.operator = 'in';
    currentItem.value = [];
    currentItem.belongs = aimItem?.belongs;
    currentItem.belongsTo = aimItem?.belongs;
    currentItem.options = aimItem?.options;
    currentItem.fullLabel = aimCas?.label;
    currentItem.prop = aimItem?.prop;
    if(currentItem?.belongs){
      const curIndex = casecadeMap[currentItem.belongs]?.findIndex((cas: any) => {
        return cas.prop === currentItem.prop;
      });
      const parentsField = casecadeMap[currentItem.belongs]?.slice(0, curIndex);
      const arrOptions: any = [];
      const formDataVal = JSON.parse(formData);
      parentsField?.forEach((item: any) => {
        const val = formDataVal[currentItem.category][item.prop];
        if (val) {
          arrOptions.push({
            label: item.label,
            isIn: Object.keys(val)[0] === 'in',
            names: Object.values(val).flat(1),
          });
        }
      });
      let type = currentItem?.belongsTo === '财务指标' ? belongsMap['DSPL'] : currentItem.belongsTo;
      const data = {
        queryLabel: currentItem.fullLabel,
        type: type,
        conditions: arrOptions,
      };
      const res = await getOptionNodes(data);
      if (res.code == 0) {
        currentItem.options = res?.data?.map((val: any) => {
          return {
            label: val?.name,
            value: val?.name,
          };
        });
        currentItem.options = uniqBy(currentItem.options, 'label');
      }
    }
    setFilterItem(currentItem);
  };
  // 设置表单类型
  const setFormDom = (type: string) => {
    switch (type) {
      case 'radio':
        return <ListFilter filterCurrent={filterItem} setFilterCurrent={setFilterItem} />;
        break;
      case 'time':
        return <DateFilter filterCurrent={filterItem} setFilterCurrent={setFilterItem} />;
        break;
      case 'compare':
        return <CompareFilter filterCurrent={filterItem} setFilterCurrent={setFilterItem} />;
        break;
      default:
        return (
          <CheckFilter
            filterCurrent={filterItem}
            setFilterCurrent={setFilterItem}
            formData={formData}
          />
        );
    }
  };
  const getFilterData = () => {
    return filterItem;
  };
  // 给父组件的测试回调
  useImperativeHandle(addRef, () => {
    return {
      getFilterData: getFilterData,
    };
  });

  return (
    <>
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
          {filterItem.operator && (
            <OpratorFilter filterCurrent={filterItem} setFilterCurrent={setFilterItem} />
          )}
          {setFormDom(filterItem.filterType)}
        </div>
      </div>
    </>
  );
};

export default AddConditionForm;
