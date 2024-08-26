import React, {useState, useEffect, useMemo, useContext} from 'react';
import { throttle } from 'lodash';
import { Message } from '@shared/utils/message';
import { Button, DatePicker, Select, TreeSelect } from 'antd';
import {
  FinanceGroupType,
  groupTypeOption,
  typeMap,
  belongsMap,
} from './options.js';
import { formatYYYYMM } from './question-util.js';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { resumeInstance, getClarifyOptions, getFuClarifyOptions, stopInstance } from '@shared/http/aipp';
import { ChatContext } from '../../../../aippIndex/context';
import './index.scoped.scss';
import { saveContent } from '@shared/http/sse';

dayjs.extend(customParseFormat);

const QuestionClar = (props) => {
  const id = 'questionClarResult';
  const { dataDimension, data, mode, confirmCallBack, tenantId } = props;
  const [questionInfo, setQuestionInfo] = useState(null);
  const { RangePicker } = DatePicker;
  const { handleRejectClar, conditionConfirm } = useContext(ChatContext);
  const [loading, setLoading] = useState(false);
  const [isShowBtn, setIsShowBtn] = useState(false);

  useEffect(() => {
    if (!data?.formData) return;
    if (data.formData[id]) {
      typeof data.formData[id] === 'string'
        ? setQuestionInfo(JSON.parse(data?.formData[id]))
        : setQuestionInfo(data?.formData[id]);
    }
  }, [data?.formData]);
  // 产品
  const [proType, setProType] = useState('主产品');
  const proTypeOptions = [
    { value: '主产品', label: '主产品' },
    { value: '辅产品', label: '辅产品' },
  ];

  /**
   * 判断是否需要展示选项的前两层
   * @returns {boolean} 是否
   */
  const whetherShowTheFirstTwoFloors = () => {
    if (!questionInfo) return false;
    return questionInfo.isBudget || questionInfo.type === 'OPEN_QUERY';
  };

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
          return { ...item, children: getFirstTwoLevels(item.children, level + 1) };
        } else {
          return { ...item, children: [] };
        }
      } else {
        return item;
      }
    });
    return dataList;
  };
  const [productOptions, setProductOptions] = useState(null);
  useEffect(() => {
    const showBtn = isShow('needTime') || isShow('needProduct') || isShow('needIndicator') || isShow('groupBy') || ambiguousList.length > 0;
    setIsShowBtn(showBtn);
    initOptions();
  }, [proType, dataDimension, questionInfo]);
  // 指标和产品调后端接口
  function initOptions() {
    let name = '';
    if (dataDimension?.name) {
      name = dataDimension.name === 'ICT P&S' ? 'IRB' : dataDimension.name;
    }
    const data = { name, type: '' };
    if (isShow('needProduct')) {
      getProOptions(data);
    }
    if (isShow('needIndicator')) {
      getIndicatorOptions(data);
    }
  }
  // 获取产品下拉数据
  async function getProOptions(params) {
    let res;
    if (proType === '主产品') {
      if (dataDimension.name === 'ICT P&S') {
        params.level = 6;
        params.type = '主产品';
        delete params.name;
        res = await getFuClarifyOptions(params);
      } else {
        params.type = typeMap[dataDimension.name];
        res = await getClarifyOptions(params);
      }
    } else {
      params.type = proType;
      delete params.name;
      params.level = 3;
      res = await getFuClarifyOptions(params);
    }
    if (res.code === 0 && res.data) {
      const originData = res.data;
      initSelections(originData);
      let options = originData;
      if (dataDimension.value === 'CPL') {
        options = calcProducts;
      }
      // 如果是“预算预测”产品选项则只取前两层
      if (whetherShowTheFirstTwoFloors()) {
        getFirstTwoLevels(options, 2);
      }
      let currentType = localStorage.getItem('proType');
      if (!currentType || currentType === proType) {
        setProductOptions(options);
      }
    }
  }
  const [indicatorOptions, setIndicatorOptions] = useState(null);
  // 指标
  async function getIndicatorOptions(params) {
    params.type = belongsMap[dataDimension.value];
    delete params.name;
    const res = await getClarifyOptions(params);
    // 过滤出报表项一级的数据
    const originData = res.data.filter((v) => v.label === '报表项1级中文名');
    initSelections(originData);
    // 如果是“预算预测”指标选项则只取前两项
    if (whetherShowTheFirstTwoFloors()) {
      getFirstTwoLevels(indicatorOptions, 2);
    }
    setIndicatorOptions(originData);
  }
  // 初始化下拉数据结构
  function initSelections(options) {
    options.forEach((item) => {
      if (!item) {
        return;
      }
      item.children && item.children[0] === null && delete item.children;
      if (item.children) {
        initSelections(item.children);
      }
      item.value = `${item.labelField === 'report_l1' ? 'report_item_l1_cn_name' : item.labelField}-${item.name}`;
      item.labelCopy = item.label;
      item.label = item.name;
    });
  }
  // 类别
  const [groupProType, setGroupProType] = useState('主产品');
  const getTypeOptions = useMemo(() => {
    if (!questionInfo) return;
    const { groupBy } = questionInfo;
    let groupByType = groupBy?.type || '';
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
  const [ambiguousList, setAmbiguousList] = useState([]);

  const [formData, setFormData] = useState({
    timeInterval: [],
    product: [],
    indicator: [],
    groupBy: {
      type: 'PRODUCT',
      value: '',
    },
  });

  // 是否展示表单项
  function isShow(type) {
    const obj = questionInfo;
    if (!obj) return false;
    if (type === 'groupBy') {
      const groupByType = obj['groupBy']?.type || '';
      return FinanceGroupType[groupByType];
    }
    return obj[type];
  }

  // 更新formData
  const updateFormData = (type, value) => {
    let data = { ...formData };
    data[type] = value;
    setFormData(data);
  };

  //修改会计期时间区间回调
  const onTimeIntervalChange = (value, dateString) => {
    updateFormData('timeInterval', value);
  };

  //会计期
  const rangeDate = useMemo(() => {
    return formData.timeInterval.map(
      (v) => {
        return formatYYYYMM(v);
      },
      [formData.timeInterval]
    );
  });

  //产品类型修改回调
  const onProTypeChange = (value) => {
    localStorage.setItem('proType', value);
    setProType(value);
    updateFormData('product', []);
    setProductOptions(null);
  };

  //产品修改回调
  const onProductChange = (value) => {
    updateFormData('product', value);
  };

  //指标修改回调
  const onIndicatorChange = (value) => {
    updateFormData('indicator', value);
  };

  //经营分组修改
  const onGroupProTypeChange = (value) => {
    setGroupProType(value);
  };

  //经营分组选项修改
  const onGroupByValueChange = (value) => {
    let data = { ...formData };
    data.groupBy.value = value;
    setFormData(data);
  };

  // 构造歧义词列表
  const initAmbList = (obj) => {
    let result = Object.keys(obj).map((k) => {
      let item = obj[k];
      return {
        label: k,
        value: item[0].value,
        options: item,
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
    setAmbiguousList(
      dataList.map((data) => {
        if (data.label === item.label) {
          data.value = value;
        }
        return data;
      })
    );
  };

  // 拒绝
  const rejectClar = throttle(() => confirmCallBack ? rejectQuestion() : handleRejectClar(), 500, { trailing: false });
  const rejectQuestion = () => {
    stopInstance(tenantId, data?.formData?.instanceId, { content: '不好意思，请明确条件后重新提问' }).then(()=>{
      confirmCallBack();
    });
  }
  // 出参使用，处理数据格式
  const processData = (indicator) => {
    let data = {};
    for (const item of indicator) {
      const value = item.value.split('-');
      const itemValue = value[value.length - 1];
      const itemKey = value[value.length - 2];
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
  };
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
      formData: JSON.stringify({ [id]: info }),
      businessData: {
        parentInstanceId: data.parentInstanceId,
        [id]: JSON.stringify(info),
      },
    };
    setLoading(true);
    const res = await saveContent(tenantId, data?.formData?.instanceId, params);
    if (res.status !== 200) {
      Message({ type: 'warning', content: res.msg || '对话失败' });
      return;
    }
    setLoading(false);
    confirmCallBack ? confirmCallBack(res) : conditionConfirm(res);
  }

  return (<>
    <div className='question-clar-wrap'>
      <div className='question_clar_root'>
        <div className='title'>为了更精确回答这个问题，小魔方希望向您确认几个细节：</div>
        {/*会计期*/}
        {isShow('needTime') && (
          <div className='question_item'>
            <div>
              需要按什么
              <span className='text'>会计期</span>
              进行统计：
            </div>
            <RangePicker
              picker='month'
              value={formData.timeInterval}
              format='YYYY-MM'
              minDate={dayjs('2023-01-01', 'YYYY-MM-DD')}
              maxDate={dayjs('2024-12-31', 'YYYY-MM-DD')}
              className='data_picker'
              onChange={(value, dateString) => {
                onTimeIntervalChange(value, dateString);
              }}
              disabled={mode === 'history'}
            />
          </div>
        )}
        {/*产品*/}
        {isShow('needProduct') && (
          <div className='question_item'>
            <div>
              需要按什么
              <span className='text'>产品</span>
              进行计算：
            </div>
            <div>
              <Select
                className='select_box mr10'
                value={proType}
                options={whetherShowTheFirstTwoFloors() ? [proTypeOptions[0]] : proTypeOptions}
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
                fieldNames={{ label: 'label', value: 'value', children: 'children' }}
                onChange={onProductChange}
                treeData={productOptions}
                maxTagCount={3}
                treeCheckStrictly={true}
                treeCheckable={true}
                disabled={mode === 'history'}
              />
            </div>
          </div>
        )}
        {/*财务指标*/}
        {isShow('needIndicator') && (
          <div className='question_item'>
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
                fieldNames={{ label: 'label', value: 'value', children: 'children' }}
                onChange={onIndicatorChange}
                treeData={indicatorOptions}
                maxTagCount={3}
                treeCheckStrictly={true}
                treeCheckable={true}
                disabled={mode === 'history'}
              />
            </div>
          </div>
        )}
        {/*类别*/}
        {isShow('groupBy') && (
          <div className='question_item'>
            <div>
              需要按什么
              <span className='text'>{FinanceGroupType[questionInfo.groupBy.type]}</span>
              进行划分：
            </div>
            {questionInfo.groupBy.type === 'PRODUCT' && (
              <Select
                className='select_box mr10'
                value={groupProType}
                options={whetherShowTheFirstTwoFloors() ? [proTypeOptions[0]] : proTypeOptions}
                onChange={onGroupProTypeChange}
                disabled={mode === 'history'}
              />
            )}
            <Select
              className='select_box mr10'
              value={formData.groupBy.value}
              options={getTypeOptions}
              onChange={onGroupByValueChange}
              disabled={mode === 'history'}
            />
          </div>
        )}
        {/*歧义词选择*/}
        <div className='ambiguous_box'>
          <div className='amb_title'>歧义字段处理：</div>
          {ambiguousList.map((item, index) => {
            return (
              <div className='ambiguous' key={index}>
                <span className='label'>{item.label}：</span>
                <Select
                  className='select_box mr10'
                  value={item.value}
                  options={item.options}
                  onChange={(value) => onAmbiguousChange(value, item)}
                  disabled={mode === 'history'}
                />
              </div>
            );
          })}
      </div>
      { mode !== 'history' && isShowBtn &&
        <div className='footer'>
          <Button className='mr10' type='primary' loading={loading} onClick={confirmClar}>确定</Button>
          <Button onClick={rejectClar}>拒绝澄清</Button>
        </div> }
</div>
    </div>
  </>);
}

export default QuestionClar;
