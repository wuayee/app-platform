
import React, { useEffect } from 'react';
import { Input, Form, InputNumber, Switch } from 'antd';
import { useTranslation } from 'react-i18next';

const RenderFormItem = (props) => {
  const { t } = useTranslation();
  const { type, name, isRequired } = props;
  const [form] = Form.useForm();

  useEffect(() => {
    const value = form.getFieldValue(name);
    if (value && isNaN(value) && (type === 'Number' || type === 'Integer')) {
      form.setFieldValue(name, null);
    }
  }, []);

  const customLabel = (
    <span className='debug-form-label'>
      <span className='item-name'>{name}</span>
      <span className='item-type'>{type}</span>
    </span>
  );

  const validateNumber = (_, value) => {
    if (value === undefined || value === null || value === '') {
      return Promise.resolve();
    }
    if (isNaN(value)) {
      return Promise.reject(new Error(t('plsEnterValidNumber')));
    }
    return Promise.resolve();
  };

  const handleBlur = (value, isInteger) => {
    if (isNaN(value)) {
      form.setFieldValue(name, null);
      form.validateFields([name]);
    } else {
      form.setFieldValue(name, isInteger ? Math.floor(value) : value);
    }
  }

  return <>
    {type === 'String' &&
      <Form.Item
        name={name}
        label={customLabel}
        rules={[
          { required: isRequired !== false, message: t('plsEnterString') },
        ]}
        className='debug-form-item'
      >
        <Input.TextArea placeholder={`${t('plsEnter')}${name}`} rows={3} />
      </Form.Item>
    }
    {type === 'Integer' &&
      <Form.Item
        name={name}
        label={customLabel}
        initialValue={null}
        rules={[
          { validator: validateNumber },
          { required: isRequired !== false, message: t('plsEnterInt') }
        ]}
        className='debug-form-item'
      >
        <InputNumber
          min={0}
          step={1}
          style={{ width: '100%' }}
          placeholder={`${t('plsEnter')}${name}`}
          onBlur={(e) => handleBlur(e.target.value, true)}
        />
      </Form.Item>
    }
    {type === 'Number' &&
      <Form.Item
        name={name}
        label={customLabel}
        initialValue={null}
        rules={[
          { validator: validateNumber },
          { required: isRequired !== false, message: t('plsEnterNumber') }
        ]}
        className='debug-form-item'
      >
        <InputNumber
          min={0}
          step={1}
          formatter={(value) => value > 1e21 ? 'Infinity' : value}
          style={{ width: '100%' }}
          placeholder={`${t('plsEnter')}${name}`}
          onBlur={(e) => handleBlur(e.target.value, false)}
        />
      </Form.Item>
    }
    {type === 'Boolean' &&
      <Form.Item
        name={name}
        label={customLabel}
        initialValue={true}
        rules={[
          { required: isRequired !== false, message: t('plsEnterInt') },
        ]}
        className='debug-form-item'
      >
        <Switch />
      </Form.Item>
    }
  </>
}


export default RenderFormItem;
