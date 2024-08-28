import React, { useEffect, useState, useRef } from 'react';
import { Checkbox, Input, Button } from 'antd';
import { getFinanceOptions } from '@/shared/http/aipp';
import { useTranslation } from 'react-i18next';
const { Search } = Input;

const CheckBoxForm = (props: any) => {
  const { t } = useTranslation();
  const { filterCurrent, setFilterCurrent } = props;
  const [datasetOptions, setDatasetOptions] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [checkedList, setCheckedList] = useState<any>([]);
  const checkedArr = useRef<any>([]);
  const optionList = useRef<any>([]);
  useEffect(() => {
    dataInit();
  }, [filterCurrent]);

  // 初始化数据
  const dataInit = async () => {
    let options = filterCurrent?.options.filter((item: any) => item.label.includes(searchText));
    setDatasetOptions(() => {
      optionList.current = options;
      return options;
    });
    let checkedData = filterCurrent.value || [];
    checkedArr.current = JSON.parse(JSON.stringify(checkedData));
    setCheckedList([...checkedData]);
  };

  // 选中
  const onChange = (e: any) => {
    const { value } = e.target;
    if (!checkedArr.current.includes(value)) {
      checkedArr.current.push(value);
    } else {
      checkedArr.current = checkedArr.current.filter((item: any) => item !== value);
    }
    let arr = JSON.parse(JSON.stringify(checkedArr.current));
    valueConfirm(arr);
    setCheckedList(arr);
  };

  // 搜索
  const onSearch = (value: any) => {
    if (filterCurrent.filterType === 'backEnd') {
      const param = {
        dimension: 'DSPL',
        field: filterCurrent.prop,
        input: value.trim(),
      };
      let options: any = [];
      getFinanceOptions(param).then((res: any) => {
        if (res.code === 0) {
          options = res?.data?.map((label: any) => {
            return {
              label,
              value: label,
            };
          });
          setDatasetOptions(options);
          let obj = JSON.parse(JSON.stringify(filterCurrent))
          obj.options = options
          setFilterCurrent(obj);
        }
      });
    } else {
      normalSearch(value);
    }
  };
  // 前端搜索
  const normalSearch = (val: string | any[]) => {
    let options: any = optionList.current.map((item: any) => {
      if (val.length) {
        item.hide = item.label.indexOf(val) === -1;
      } else {
        item.hide = false;
      }
      return item;
    });
    setDatasetOptions(options);
  };
  // 全选
  const handleChoseAll = () => {
    let arr: any = [];
    if (checkedList.length !== datasetOptions.length) {
      arr = datasetOptions.map((item: any) => item.value);
    }
    checkedArr.current = [...arr];
    valueConfirm(arr);
    setCheckedList(arr);
  };
  // 确定回显数据
  const valueConfirm = (arr: any) => {
    let obj = JSON.parse(JSON.stringify(filterCurrent));
    obj.value = arr;
    setFilterCurrent(obj);
  };

  return (
    <>
      <div className='check-box-form'>
        <div className='check-search'>
          <Search onSearch={onSearch} placeholder={t('plsEnter')} allowClear />
          {datasetOptions.length > 0 && (
            <Button type='primary' onClick={handleChoseAll}>
              {checkedList.length === datasetOptions.length ? t('unselectAll') : t('selectAll')}
            </Button>
          )}
        </div>
        <div className='check-content'>
          {datasetOptions.map((item: any) => {
            return (
              <div
                key={item.value}
                className={['check-content-item', item.hide ? 'item-hidden' : null].join(' ')}
              >
                <Checkbox
                  value={item.value}
                  checked={checkedList.includes(item.value)}
                  onChange={onChange}
                >
                  <span className='item-text'>{item.label}</span>
                </Checkbox>
              </div>
            );
          })}
        </div>
      </div>
    </>
  );
};

export default CheckBoxForm;
