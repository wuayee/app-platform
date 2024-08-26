import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { Form, Collapse, theme, Switch } from 'antd';
import { CaretRightOutlined } from '@ant-design/icons';
import { debounce } from '@/shared/utils/common';
import LLM from './components/llm';
import Skill from './components/skill';
import Knowledge from './components/knowledge';
import Inspiration from './components/inspiration';
import Recommend from './components/recommend';
import { setHistorySwitch } from '@/store/common/common';
import { MultiConversationContent } from '@fit-elsa/elsa-react';
import { useTranslation } from 'react-i18next';
import './index.scoped.scss';

function ConfigUI(props) {
  const { t } = useTranslation();
  const { formData, handleConfigDataChange, inspirationChange, status, activeKey } = props;
  const [form] = Form.useForm();
  const [inspirationValues, setInspirationValues] = useState(null);
  const [recommendValues, setRecommendValues] = useState([]);
  const [memoryValues, setMemoryValues] = useState(null);
  const [pluginValue, setPluginValue] = useState([]);
  const [knowledge, setKnowledge] = useState(null);
  const [isDisabled, setIsDisabled] = useState(false);
  const { token } = theme.useToken();
  const renderRef = useRef(false);
  const dispatch = useAppDispatch();
  const historySwitch = useAppSelector((state) => state.commonStore.historySwitch);
  const panelStyle = {
    border: 'none',
  };
  // 更新Memory
  const updateMemory = (data) => {
    updateConfig(data, 'memory');
    setMemoryValues(data);
  }
  // 更新是否展示多轮对话开关
  const historySwitchChange = (checked, event) => {
    event.stopPropagation();
    dispatch(setHistorySwitch(checked));
    const data = { ...memoryValues, memorySwitch: checked };
    updateMemory(data);
  }
  // 更新多轮对话type
  const onTypeChange = (e, memoryValueType, memoryValue) => {
    const data = { memorySwitch: true, type: e, value: memoryValue };
    updateMemory(data);
  }
  // 更新多轮对话value
  const onValueChange = (valueType, newValue) => {
    const data = { ...memoryValues, value: newValue };
    updateMemory(data);
  }
  const getItems = (panelStyle) => [
    {
      key: '1',
      label: t('LLM'),
      children: <LLM updateData={updateConfig} />,
      style: panelStyle,
    },
    {
      key: '2',
      label: t('plugin'),
      children: <Skill waterflowChange={waterflowChange} pluginData={pluginValue} updateData={updateConfig} />,
      style: panelStyle,
    },
    {
      key: '3',
      label: t('knowledgeBase'),
      children: <Knowledge knowledge={knowledge} updateData={updateConfig} />,
      style: panelStyle,
    },
  ];
  const getItems2 = (panelStyle) => [
    {
      key: '4',
      label: t('guessAsk'),
      children: <Recommend updateData={updateConfig} recommendValues={recommendValues} />,
      style: panelStyle,
    },
    {
      key: '5',
      label: (
        <div>
          {t('multipleRoundsOfConversation')}
          <Switch
            className='conversation-switch'
            onChange={(checked, event) => historySwitchChange(checked, event)}
            value={memoryValues.memorySwitch ? memoryValues.memorySwitch : false}
          />
        </div>
      ),
      children: <MultiConversationContent
        itemId='memory'
        disabled={!historySwitch}
        props={{
          type: {
            value: memoryValues.type,
            onChange: onTypeChange
          },
          value: {
            value: memoryValues.value,
            onChange: onValueChange
          }
        }}
      />,
      style: panelStyle,
    },
    {
      key: '6',
      label: t('creativeInspiration'),
      children: <Inspiration inspirationValues={inspirationValues} updateData={updateConfig} />,
      style: panelStyle,
    }
  ];
  useEffect(() => {
    setIsDisabled(status === 'published');
  }, [status])

  useEffect(() => {
    if (!formData) return;
    const newData = formData.properties.reduce((acc, item) => {
      acc[item.name] = item.defaultValue;
      return acc;
    }, {});
    form.setFieldsValue(newData);
    dispatch(setHistorySwitch(newData?.memory?.memorySwitch));
    handleSet();
  }, [formData]);
  const handleSet = useCallback(debounce(() => setFormInitData(), 200), []);
  // 设置初始值
  const setFormInitData = () => {
    setInspirationValues(form.getFieldValue('inspiration'));
    setKnowledge(form.getFieldValue('knowledge'));
    setRecommendValues(form.getFieldValue('recommend'));
    setMemoryValues(form.getFieldValue('memory'));
    if (form.getFieldValue('workflows') && form.getFieldValue('tools')) {
      let list = [...form.getFieldValue('workflows'), ...form.getFieldValue('tools')];
      setPluginValue(list);
    };
  }
  // 自动保存参数构建
  const buildSaveData = (key, value, saveData) => {
    for (let prop of saveData.properties) {
      if (prop.name === key) {
        prop.defaultValue = value;
      }
    }
    if (key === 'recommend') {
      let item = saveData.properties.filter(item => item.name === key);
      item.length ? item[0].defaultValue = value : saveData.properties.push({
        'id': 'prop9',
        'name': 'recommend',
        'dataType': 'List<String>',
        'defaultValue': value
      })
    }
  }
  // 数据变化回调
  const handleValuesChange = (changedValues) => {
    const entries = Object.entries(changedValues);
    const saveData = { ...formData };
    entries.forEach(([key, value]) => {
      buildSaveData(key, value, saveData);
    });
    handleConfigDataChange(saveData);
  }
  // 监听数据变化
  const updateConfig = (value, key, map = undefined) => {
    if (['tools', 'workflows'].includes(key)) {
      saveConfig(value, key, map);
    } else {
      form.validateFields().then(() => {
        saveConfig(value, key, map);
      }).catch((errorInfo) => { })
    }

  }
  // 触发保存
  const saveConfig = (value, key, map) => {
    const saveData = { ...formData };
    buildSaveData(key, value, saveData, map);
    handleConfigDataChange(saveData);
    if (key === 'inspiration') {
      inspirationChange();
    }
  }
  // 工具流自动选中
  const waterflowChange = () => {
    let uniqueName = sessionStorage.getItem('uniqueName');
    if (uniqueName) {
      let workflows = form?.getFieldValue('workflows');
      let workflwArr = Array.from(new Set([...workflows, uniqueName]))
      form.setFieldValue('workflows', workflwArr);
      handleValuesChange({ workflows: workflwArr }, valueMap);
      sessionStorage.removeItem('uniqueName');
    }
  }

  return (<>
    <div className='config-wrap'>
      <Form
        form={form}
        layout='vertical'
        disabled={isDisabled}
      >
        {
          activeKey === 'application' ?
            (
              <Collapse
                bordered={false}
                defaultActiveKey={['1', '2', '3']}
                expandIcon={({ isActive }) => <CaretRightOutlined rotate={isActive ? 90 : 0} />}
                style={{
                  background: token.colorBgContainer,
                }}
                items={getItems(panelStyle)}
              />
            ) : (
              <Collapse
                bordered={false}
                defaultActiveKey={['4', '5', '6']}
                expandIcon={({ isActive }) => <CaretRightOutlined rotate={isActive ? 90 : 0} />}
                style={{
                  background: token.colorBgContainer,
                }}
                items={getItems2(panelStyle)}
              />
            )
        }
      </Form>
    </div>
  </>);
}
export default ConfigUI;
