import React, {useEffect, useState } from 'react';
import { Form, Collapse, theme } from 'antd';
import { ConfigWrap } from './styled';
import { CaretRightOutlined } from '@ant-design/icons';
import LLM from './components/llm';
import Skill from './components/skill';
import Knowledge from './components/knowledge';
import Inspiration from './components/inspiration';
import Recommend from './components/recommend';

function ConfigUI(props) {
    const { formData, handleConfigDataChange, inspirationChange, status, activeKey } = props;
    const [ form ] = Form.useForm();
    const [ inspirationValues, setInspirationValues ] = useState(null);
    const [ recommendValues, setRecommendValues ] = useState([]);
    const [ knowledge, setKnowledge ] = useState(null);
    const [ isDisabled, setIsDisabled ] = useState(false);
    const { token } = theme.useToken();
    const panelStyle = {
      border: 'none',
    };
    const getItems = (panelStyle) => [
      {
        key: '1',
        label: '大模型',
        children: <LLM updateData={updateConfig} />,
        style: panelStyle,
      },
      {
        key: '2',
        label: '插件',
        children: <Skill waterflowChange={waterflowChange} updateData={updateConfig} />,
        style: panelStyle,
      },
      {
        key: '3',
        label: '知识库',
        children: <Knowledge knowledge={knowledge} updateData={updateConfig} />,
        style: panelStyle,
      },
    ];
    const getItems2 = (panelStyle) => [
      {
        key: '4',
        label: '猜你想问',
        children: <Recommend updateData={updateConfig} recommendValues={recommendValues}/>,
        style: panelStyle,
      },
      {
        key: '5',
        label: '创意灵感',
        children: <Inspiration inspirationValues={inspirationValues} updateData={updateConfig}/>,
        style: panelStyle,
      }
    ];
    useEffect(() => {
      setIsDisabled(status === "published");
    }, [status])

    useEffect(() => {
      if (!formData) return;
      const newData = formData.properties.reduce((acc, item) => {
        acc[item.name] = item.defaultValue;
        return acc;
      }, {});
      form.setFieldsValue(newData);
    }, [formData])

    const buildSaveData = (key, value, saveData) => {
      for (let prop of saveData.properties) {
        if (prop.name === key) {
          prop.defaultValue = value;
        }
      }
    }

    const handleValuesChange = (changedValues, allValues) => {
      const entries = Object.entries(changedValues);
      const saveData = {...formData};
      entries.forEach(([key, value]) => {
        buildSaveData(key, value, saveData);
      });
      handleConfigDataChange(saveData);
    }

    useEffect(() => {
      setInspirationValues(form.getFieldValue("inspiration"));
      setKnowledge(form.getFieldValue("knowledge"));
    }, [form.getFieldsValue()])

    const updateConfig = (value, key) => {
      const saveData = {...formData};
      buildSaveData(key, value, saveData);
      handleConfigDataChange(saveData);
      if (key === "inspiration") {
        inspirationChange();
      }
    }

    const waterflowChange = () => {
      let uniqueName = sessionStorage.getItem('uniqueName');
      if (uniqueName) {
        let workflows = form?.getFieldValue('workflows');
        let workflwArr =  Array.from(new Set([...workflows, uniqueName]))
        form.setFieldValue('workflows', workflwArr);
        handleValuesChange({ workflows: workflwArr });
        sessionStorage.removeItem('uniqueName');
      }
    }

    return (
      <>
        <ConfigWrap>
          <Form
            form={form}
            layout="vertical"
            disabled={isDisabled}
          > 
            {
              activeKey === 'application' ? 
              (
                <Collapse
                  bordered={false}
                  activeKey={['1', '2', '3']}
                  expandIcon={({ isActive }) => <CaretRightOutlined rotate={isActive ? 90 : 0} />}
                  style={{
                    background: token.colorBgContainer,
                  }}
                  items={getItems(panelStyle)}
                /> 
              ) : (
                <Collapse
                  bordered={false}
                  activeKey={['4', '5']}
                  expandIcon={({ isActive }) => <CaretRightOutlined rotate={isActive ? 90 : 0} />}
                  style={{
                    background: token.colorBgContainer,
                  }}
                  items={getItems2(panelStyle)}
                /> 
              )
            }
          </Form>
        </ConfigWrap>
      </>
    )
}
export default ConfigUI;
