
import React, { useEffect, useState } from 'react';
import { Form, Select, InputNumber, Input } from 'antd';
import { getModels } from '@/shared/http/appBuilder';
import { useTranslation } from 'react-i18next';

const LLM = (props) => {
  const { t } = useTranslation();
  const { updateData } = props;
  const [showControl, setShowControl] = useState(true);
  const [models, setModels] = useState([]);
  const { TextArea } = Input;

  const handleGetModels = (open) => {
    if (!open) return;
    getModels().then((res) => {
      setModels(res.data);
    })
  }

  // 温度输入框formatter
  const formatter = (newValue) => {
    const value = parseFloat(newValue);
    if (value === 0.0) {
      return 0;
    }
    if (value >= 1) {
      return 1;
    }
    return value;
  }

  // 温度输入框失焦回调
  const handleTemperBlur = (e) => {
    if (e.target.value === '') {
      return;
    }
    let originValue = parseFloat(e.target.value);
    if (isNaN(originValue)) {
      originValue = 0;
    }
    let changeValue;
    if (originValue <= 0.0) {
      changeValue = 0;
    } else if (originValue >= 1.0) {
      changeValue = 1;
    } else {
      // 保留一位小数
      changeValue = Math.round(originValue * 10) / 10;
    }
    setTimeout(() => updateData(changeValue, 'temperature'), 100);
  }

  useEffect(() => {
    handleGetModels(true);
  }, [])

  return (
    <>
      <div className='control-container llm-container'>
        <div className='control'>
          <div style={{ display: showControl ? 'block' : 'none' }}>
            <div style={{ display: 'flex' }}>
              <Form.Item
                style={{ flex: 2 }}
                name='model'
                label={t('LLM')}
                rules={[{ required: true }]}
              >
                <Select
                  className={'no-right-radius full-border'}
                  placeholder={t('selectLlm')}
                  style={{ width: '300px' }}
                  options={models}
                  onDropdownVisibleChange={(open) => handleGetModels(open)}
                  onChange={(value) => { updateData(value, 'model') }}
                  fieldNames={{
                    label: 'id',
                    value: 'id'
                  }}
                >
                </Select>
              </Form.Item>
              <Form.Item
                style={{
                  flex: 1
                }}
                name='temperature'
                label={t('temperature')}
                rules={[{ required: true }]}
              >
                <InputNumber
                  className={'no-left-radius'}
                  style={{
                    width: '100%',
                    borderLeft: 'none'
                  }}
                  min={0}
                  max={1}
                  controls={true}
                  keyboard={true}
                  precision={1}
                  onBlur={(e) => { handleTemperBlur(e); }}
                  step={0.1}
                  type="number"
                  formatter={formatter}
                />
              </Form.Item>
            </div>
            <Form.Item name='systemPrompt' label={t('promptName')}>
              <TextArea
                placeholder={t('promptHolder')}
                rows={6}
                onBlur={(e) => { updateData(e.target.value, 'systemPrompt') }}
                autoSize={{
                  minRows: 6,
                }}
              />
            </Form.Item>
          </div>
        </div>
      </div>
    </>
  )
};
export default LLM;
