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
import { belongsMap, casecadeMap, conditionMap } from '../common/condition';
import { getOptionNodes } from '@shared/http/aipp';
import { uniqBy } from 'lodash';
import '../styles/condition-item.scss';

const ConditionItems = (props: any) => {
  const { filterItem, disabled, formData, save, remove, conditions } = props;
  const [filterCurrent, setFilterCurrent] = useState<any>({});
  const [getFormData, setGetFormData] = useState('');
  const [popoverShow, setPopoverShow] = useState(false);
  const allFields = Object.values(conditionMap).flat(1);
  const category = conditions.at(-1).category;
  useEffect(() => {
    if (filterItem) {
      let item = JSON.parse(JSON.stringify(filterItem));
      setFilterCurrent(item);
    }
  }, [filterItem]);

  // 设置表单类型
  const setFormDom = (type: any) => {
    switch (type) {
      case 'radio':
        return <ListFilter filterCurrent={filterCurrent} setFilterCurrent={setFilterCurrent} />;
        break;
      case 'time':
        return <DateFilter filterCurrent={filterCurrent} setFilterCurrent={setFilterCurrent} />;
        break;
      case 'compare':
        return <CompareFilter filterCurrent={filterCurrent} setFilterCurrent={setFilterCurrent} />;
        break;
      default:
        return (
          <CheckFilter
            filterCurrent={filterCurrent}
            setFilterCurrent={setFilterCurrent}
            formData={getFormData}
          />
        );
    }
  };
  const handleCancel = () => {
    setPopoverShow(false);
  };
  const tagClick = () => {
    if (disabled) return;
  };
  const handleConfirm = () => {
    if (filterCurrent.value.length === 0) {
      Message({ type: 'warning', content: '选择结果不能为空' });
      return;
    }
    save(filterCurrent);
    document.body.click();
    handleCancel();
  };
  const handleRemove = (e: any, item: any) => {
    e.stopPropagation();
    remove(item);
  };

  const onOpenChange = async (open: boolean) => {
    let dataForm = JSON.stringify(formData());
    setGetFormData(dataForm);
    const aimItem = allFields.find((item) => item.label === filterItem.label);
    const aimCas =
      aimItem?.belongs &&
      JSON.parse(
        JSON.stringify(casecadeMap[aimItem?.belongs]?.find((val: any) => val.prop === aimItem.prop))
      );
    let currentItem = JSON.parse(JSON.stringify(aimItem));
    currentItem.category = category;
    currentItem.operator = 'in';
    currentItem.value = filterCurrent.value;
    currentItem.belongs = aimItem?.belongs;
    currentItem.belongsTo = aimItem?.belongs;
    currentItem.options = aimItem?.options;
    currentItem.fullLabel = aimCas?.label;
    currentItem.prop = aimItem?.prop;
    if (currentItem?.belongs) {
      const curIndex = casecadeMap[currentItem.belongs]?.findIndex((cas: any) => {
        return cas.prop === currentItem.prop;
      });
      const parentsField = casecadeMap[currentItem.belongs]?.slice(0, curIndex);
      const arrOptions: any = [];
      const formDataVal = JSON.parse(dataForm);
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
    if (currentItem?.filterType === 'backEnd') {
      currentItem.options = filterCurrent.value.map((val: any) => {
        return {
          label: val,
          value: val,
        };
      });
    }
    setFilterCurrent(currentItem);
    setPopoverShow(open);
  };

  return (
    <>
      <Popover
        trigger='click'
        placement='bottomLeft'
        arrow={false}
        destroyTooltipOnHide={true}
        open={popoverShow}
        onOpenChange={onOpenChange}
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
          <div className='filter-data'>{formatterLabel(filterItem)}</div>
          {!['会计期', '币种', '口径'].includes(filterItem.label) ? (
            <div className='filter-icon'>
              <CloseOutlined
                style={{ fontSize: '12px' }}
                onClick={(e) => handleRemove(e, filterItem)}
              />
            </div>
          ) : (
            ''
          )}
        </div>
      </Popover>
    </>
  );
};
export default ConditionItems;
