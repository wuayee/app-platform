
import React, { useEffect, useState, useRef } from 'react';
import { Button, Popover } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { Message } from '@shared/utils/message';
import { formatterLabel } from '../utils/chart-condition';
import ListFilter from './condition-list-form';
import DateFilter from './condition-date-form';
import CompareFilter from './codition-compare-form';
import CheckFilter from './condition-checkbox-form';
import OpratorFilter from './condition-operator';
import '../styles/condition-item.scss';

const ConditionItems = (props) => {
  const { filterItem, disabled, formData, save, remove } = props;
  const [filterCurrent, setFilterCurrent] = useState<any>({});
  const [filterKey, setFilterKey] = useState('');

  useEffect(() => {
    if (filterItem) {
      let item = JSON.parse(JSON.stringify(filterItem));
      setFilterCurrent(item);
    }
  }, []);
  // 设置表单类型
  const setFormDom = (type) => {
    switch (type) {
      case 'radio':
        return <ListFilter filterCurrent={filterCurrent} setFilterCurrent={setFilterCurrent}/>
        break;
      case 'time':
        return <DateFilter filterCurrent={filterCurrent} setFilterCurrent={setFilterCurrent}/>
        break;
      case 'compare':
        return <CompareFilter filterCurrent={filterCurrent} setFilterCurrent={setFilterCurrent} />
        break;
      default:
        return <CheckFilter filterCurrent={filterCurrent} setFilterCurrent={setFilterCurrent} formData={formData}/>
    }
  };
  const handleCancel = () => {
    
  }
  const tagClick = () => {
    if (disabled) return;
  }
  const handleConfirm = () => {
    if (filterCurrent.value.length === 0) {
      Message({ type: 'warning', content: '选择结果不能为空' });
      return
    }
    save(filterCurrent);
    document.body.click();
    handleCancel();
  }
  const handleRemove = (e, item) => {
    e.stopPropagation();
    remove(item);
  }
  return <>
    <Popover 
      trigger='click'
      placement='bottomLeft'
      arrow={false}
      destroyTooltipOnHide={true}
      // open={filterCurrent.visible}
      content={
        <div>
          { filterItem.operator &&  <OpratorFilter filterCurrent={filterCurrent} setFilterCurrent={setFilterCurrent}/>}
          { setFormDom(filterItem.filterType) }
          <div className='action-menu'>
            <Button size='small' onClick={() => handleCancel()}>取消</Button>
            <Button size='small' type='primary' onClick={handleConfirm}>确定</Button>
          </div>
        </div>
      }>
      <div className='tag-item' onClick={tagClick}>
        <div className='filter-title'> 
          {filterItem.label }
          { filterItem.operator === 'nin' ? '不包含 :' : ' : ' } 
        </div>
        <div className='filter-data' >
          { formatterLabel(filterItem) }
        </div>
        <div className='filter-icon' >
          <CloseOutlined style={{ fontSize: '12px' }} onClick={(e) => handleRemove(e, filterItem)} />
        </div>
      </div>
    </Popover> 
    </>
};
export default ConditionItems;



