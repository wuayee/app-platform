import React, {useState, useEffect, useMemo, useContext} from 'react';
import styled from 'styled-components';
import { throttle } from 'lodash';
import { Message } from '@shared/utils/message';
import {Button, DatePicker, Select, TreeSelect} from 'antd';
import {
  products,
  aidProducts,
  calcProducts,
  indicators,
  cascIndicators,
  FinanceGroupType,
  groupTypeOption,
} from './options.js';
import { formatYYYYMM } from './question-util.js';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { resumeInstance } from '@shared/http/aipp';
import { ChatContext } from '../../../../aippIndex/context';
dayjs.extend(customParseFormat);
const QuestionClarWrap = styled.div`
  .question_clar_root {
    .title {
      font-weight: 700;
      margin-bottom: 16px;
    }
    .question_item {
      margin-bottom: 8px;
      display: flex;
      align-items: center;
      .select_box {
        width: 160px;
        .date_picker {
          width: 100%;
        }
      }
      .text {
        display: inline-block;
        background-color: rgb(214, 238, 255);
        border-radius: 4px;
        padding: 4px 15px;
        margin: 0 6px;
      }
      .text_value {
        background-color: rgb(217, 252, 230);
        border-radius: 4px;
        padding: 4px 15px;
        margin: 0 6px;
      }
    }
    .footer {
      padding: 10px 0;
      padding-left: 20px;
    }
    .mr10 {
      margin-right: 10px;
    }
    /deep/.dv-select__selection {
      height: 30px;
    }
    .cascader_box {
      display: flex;
      .cascader {
        width: 250px;
      }
      /deep/.tiny-input__inner {
        min-height: 30px !important;
      }
    }
    .ambiguous_box {
      .ambiguous {
        margin-top: 8px;
        display: flex;
        align-items: center;
        .label {
          min-width: 50px;
        }
      }
    }
  }
`;
const QuestionClar = (props) => {
  const id = 'questionClarResult';
  const { data, mode } = props;
  const [ questionInfo, setQuestionInfo ] = useState(null);
  const { RangePicker } = DatePicker;
  const { handleRejectClar, dataDimension, tenantId } = useContext(ChatContext);
  useEffect(() => {
    if (!data?.formData) return;
    if (data.formData[id]) {
      typeof (data.formData[id]) === 'string' ? setQuestionInfo(JSON.parse(data?.formData[id])) : setQuestionInfo(data?.formData[id]);
    }
  }, [data?.formData])
  // 产品
  const [ proType, setProType ] = useState('主产品');
  const proTypeOptions = [{value: '主产品', label: '主产品'}, {value: '辅产品', label: '辅产品'}];

  /**
   * 判断是否需要展示选项的前两层
   * @returns {boolean} 是否
   */
  const whetherShowTheFirstTwoFloors = () => {
    if (!questionInfo) return false;
    return questionInfo.isBudget || questionInfo.type === 'OPEN_QUERY';
  }

  /**
   * 获取级联数据前两层
   * @param data 级联数据
   * @param level 层数
   * @returns {*} 返回级联数据的前两层
   */
  const getFirstTwoLevels = (data, level) => {
    const dataList = data.map((item) => {
      if (item.children) {
        if (level < 1) {
          return {...item, children: getFirstTwoLevels(item.children, level + 1)};
        } else {
          return {...item, children: []};
        }
      } else {
        return item;
      }
    })
    return dataList;
  }
  const productOptions = useMemo(() => {
    if (proType === '辅产品') {
      return aidProducts;
    }
    let options = products;
    if (dataDimension === 'CPL') {
      return calcProducts;
    }
    // 如果是“预算预测”则只取前两层
    if (whetherShowTheFirstTwoFloors()) {
      return getFirstTwoLevels(options, 0);
    }
    return options;
  }, [proType, dataDimension, questionInfo]);
  // 类别
  const [ groupProType, setGroupProType ] = useState('主产品');
  const getTypeOptions = useMemo(() => {
    if (!questionInfo) return;
    const { groupBy } = questionInfo;
    let groupByType = groupBy.type;
    // 辅产品 前端自己定义枚举
    if (groupByType === 'PRODUCT' && groupProType === '辅产品') {
      groupByType = 'AID_PRODUCT';
    }
    let options = groupTypeOption[groupByType] || [];
    // 如果是“预算预测”则只取前两项
    if (whetherShowTheFirstTwoFloors() && groupProType !== '辅产品') {
      options = options.slice(0, 2);
    }
    return options;
  }, [questionInfo, groupProType]);

  // 歧义字段处理
  const [ ambiguousList, setAmbiguousList ] = useState([]);

  const [formData, setFormData] = useState({
    timeInterval: [],
    product: [],
    indicator: [],
    groupBy: {
      type: 'PRODUCT',
      value: '',
    },
  })

  // 是否展示表单项
  function isShow(type) {
    const obj = questionInfo;
    if (!obj) return false;
    if (type === 'groupBy') {
      const groupByType = obj['groupBy'].type;
      return FinanceGroupType[groupByType];
    }
    return obj[type];
  }

  // 更新formData
  const updateFormData = (type, value) => {
    let data = {...formData};
    data[type] = value;
    setFormData(data);
  }

  //修改会计期时间区间回调
  const onTimeIntervalChange = (value, dateString) => {
    updateFormData('timeInterval', value);
  }

  //会计期
 const rangeDate = useMemo(() => {
   return formData.timeInterval.map((v) => {
     return formatYYYYMM(v);
   }, [formData.timeInterval]);
 });

  //产品类型修改回调
  const onProTypeChange = (value) => {
    setProType(value);
  }

  //产品修改回调
  const onProductChange = (value) => {
    updateFormData('product', value)
  }

  //指标
  const indicatorOptions = useMemo(() => {
    let options = indicators;
    if (dataDimension === 'CPL') {
      options =  cascIndicators;
    }
    if (whetherShowTheFirstTwoFloors()) {
      return getFirstTwoLevels(options, 0);
    }
    return options;
  }, [dataDimension, questionInfo]);

  //指标修改回调
  const onIndicatorChange = (value) => {
    updateFormData('indicator', value);
  }

  //经营分组修改
  const onGroupProTypeChange = (value) => {
    setGroupProType(value);
  }

  //经营分组选项修改
  const onGroupByValueChange = (value) => {
    let data = {...formData};
    data.groupBy.value = value;
    setFormData(data);
  }

  // 构造歧义词列表
  const initAmbList = (obj) => {
    const keys = Object.keys(obj);
    let result = keys.map((k) => {
      let item = obj[k];
      const itemKeys = Object.keys(item);
      return {
        label: k,
        value: item[itemKeys[0]],
        options: itemKeys.map((itemKey) => {return {label: itemKey, value: item[itemKey]}}),
      };
    });
    return result;
  };
  useEffect(() => {
    if (!questionInfo) return;
    const { otherElements } = questionInfo;
    setAmbiguousList(initAmbList(otherElements));
  }, [questionInfo]);

  //歧义词修改回调
  const onAmbiguousChange = (value, item) => {
    let dataList = [...ambiguousList];
    setAmbiguousList(dataList.map((data) => {
      if (data.label === item.label) {
        data.value = value;
      }
      return data;
    }));
  }

  // 拒绝
  const rejectClar = throttle(() => handleRejectClar(), 500, { trailing: false });
  // 出参使用，处理数据格式
  const processData = (indicator) => {
    let data = {};
    for (const item of indicator) {
      const value = item.value.split('-');
      const itemValue = value[value.length - 1];
      const itemKey = value[value.length - 2]
      if (data[itemKey]) {
        data[itemKey].add(itemValue);
      } else {
        data[itemKey] = new Set();
        data[itemKey].add(itemValue);
      }
    }
    Object.entries(data).forEach(([key, value]) => {
      data[key] = [...value];
    });
    return data;
  }
  // 确定
  const confirmClar = throttle(handleConfirm, 500, { trailing: false });
  async function handleConfirm() {
    const { product, timeInterval, indicator, groupBy } = formData;
    // 校验展示的表单中，是否有空值
    if (
      (isShow('needProduct') && !product.length) ||
      (isShow('needIndicator') && !indicator.length) ||
      (isShow('needTime') && (!rangeDate[0] || !rangeDate[1])) ||
      (isShow('groupBy') && !groupBy)
    ) {
      Message({
        type: 'info',
        content: '表单项为必填',
      });
      return;
    }
    if (rangeDate[0]?.slice(0, 4) !== rangeDate[1]?.slice(0, 4)) {
      Message({
        type: 'info',
        content: '会计期范围需在同一年',
      });
      return;
    }
    // 产品和指标级联只取所选中的每一项的最后一项,打平去重得到所多选的选项
    let otherElements = {};
    ambiguousList.forEach((v) => {
      otherElements[v.label] = v.value;
    });
    const info = {
      timeInterval: {
        start: rangeDate[0],
        end: rangeDate[1],
      },
      product: processData(product),
      indicator: processData(indicator),
      groupBy,
      otherElements,
      type: questionInfo.type,
      explicitInfo: questionInfo.explicitInfo,
    };
    const params = {
      formAppearance: JSON.stringify(data.formAppearance),
      formData: JSON.stringify({[id]: info}),
      businessData: {
        parentInstanceId: data.parentInstanceId,
        [id]: JSON.stringify(info),
      }
    }
    const res = await resumeInstance(tenantId, data?.formData?.instanceId, params);
  }

  return (
    <QuestionClarWrap>
      <div className='question_clar_root'>
        <div className='title'>
          为了更精确回答这个问题，小魔方希望向您确认几个细节：</div>
        {/*会计期*/}
        { isShow('needTime') && <div className='question_item'>
          <div>
            需要按什么
            <span className='text'>会计期</span>
            进行统计：
          </div>
          <RangePicker picker='month'
                       value={formData.timeInterval}
                       format='YYYY-MM'
                       minDate={dayjs('2023-01-01', 'YYYY-MM-DD')}
                       maxDate={dayjs('2024-12-31', 'YYYY-MM-DD')}
                       className='data_picker'
                       onChange={(value, dateString) => {onTimeIntervalChange(value, dateString)}}
                       disabled={mode === 'history'}
          />
        </div>
        }
        {/*产品*/}
        { isShow('needProduct') && <div className='question_item'>
          <div>
            需要按什么
            <span className='text'>产品</span>
            进行计算：
          </div>
          <div>
            <Select
              className='select_box mr10'
              value={proType}
              options={whetherShowTheFirstTwoFloors() ? [proTypeOptions[0]]: proTypeOptions}
              onChange={onProTypeChange}
              disabled={mode === 'history'}
            />
          </div>
          <div className='cascader_box'>
            <TreeSelect
              value={formData.product}
              placeholder='请选择'
              className='cascader mr10'
              multiple
              fieldNames={{label: 'label', value: 'value', children: 'children'}}
              onChange={onProductChange}
              treeData={productOptions}
              maxTagCount={3}
              treeCheckStrictly={true}
              treeCheckable={true}
              disabled={mode === 'history'}
            />
          </div>
        </div>
        }
        {/*财务指标*/}
        { isShow('needIndicator') && <div className='question_item'>
          <div>
            需要按什么
            <span className='text'>财务指标</span>
            进行计算：
          </div>
          <div className='cascader_box'>
            <TreeSelect
              value={formData.indicator}
              placeholder='指标'
              className='cascader'
              multiple
              fieldNames={{label: 'label', value: 'value', children: 'children'}}
              onChange={onIndicatorChange}
              treeData={indicatorOptions}
              maxTagCount={3}
              treeCheckStrictly={true}
              treeCheckable={true}
              disabled={mode === 'history'}
            />
          </div>
        </div>
        }
        {/*类别*/}
        { isShow('groupBy') && <div className='question_item'>
          <div>
            需要按什么
            <span className='text'>{
              FinanceGroupType[questionInfo.groupBy.type]
            }</span>
            进行划分：
          </div>
          { questionInfo.groupBy.type === 'PRODUCT' &&
            <Select
              className='select_box mr10'
              value={groupProType}
              options={whetherShowTheFirstTwoFloors() ? [proTypeOptions[0]] : proTypeOptions}
              onChange={onGroupProTypeChange}
              disabled={mode === 'history'}
          />
          }
          <Select
            className='select_box mr10'
            value={formData.groupBy.value}
            options={getTypeOptions}
            onChange={onGroupByValueChange}
            disabled={mode === 'history'}
          />
          </div>
        }
        {/*歧义词选择*/}
        <div className='ambiguous_box'>
          <div className='amb_title'>歧义字段处理：</div>
          {ambiguousList.map((item, index) => {
            return (
              <div className='ambiguous' key={item.label}>
                <span className='label'>{ item.label }：</span>
                <Select
                  className='select_box mr10'
                  value={item.value}
                  options={item.options}
                  onChange={(value) => onAmbiguousChange(value, item)}
                  disabled={mode === 'history'}
                />
              </div>
            )
          })}
      </div>
      { mode !== 'history' &&
        <div className='footer'>
          <Button className='mr10' type='primary' onClick={confirmClar}>确定</Button>
          <Button onClick={rejectClar}>拒绝澄清</Button>
        </div> }
</div>
    </QuestionClarWrap>
  )
}

export default QuestionClar;
