/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useContext, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { Form, InputNumber, Input, Switch, Popover, Empty } from 'antd';
import { isInputEmpty, getConfiguration } from '@/shared/utils/common';
import { AippContext } from '@/pages/aippIndex/context';
import { useAppSelector } from '@/store/hook';
import { setSpaClassName } from '@/shared/utils/common';
import CloseImg from '@/assets/images/close_btn.svg';
import '../styles/configuration.scss';


/**
 * 多输入对话配置弹框组件
 *
 * @param appInfo 应用详情.
 * @param updateUserContext 更新userContext字段方法.
 * @param chatRunning 是否在对话中.
 * @param isChatRunning 在对话中进行提示操作.
 * @param display 是否显示.
 * @return {JSX.Element}
 * @constructor
 */
const ConversationConfiguration = ({ appInfo, updateUserContext, chatRunning, isChatRunning, display }) => {
  const { t } = useTranslation();
  const atAppInfo = useAppSelector((state) => state.appStore.atAppInfo);
  const [open, setOpen] = useState(false);
  const [configAppInfo, setConfigAppInfo] = useState({});
  const [form] = Form.useForm();
  const [configurationList, setConfigurationList] = useState([]);
  const preConfigurationList = useRef([]);
  const { showElsa } = useContext(AippContext);

  // 更新值
  const updateData = () => {
    updateUserContext(form.getFieldsValue());
  };

  const handleNumberChange = (value, isInteger, name) => {
    if (isNaN(value)) {
      form.setFieldValue(name, null);
    } else if (value === '') {
      form.setFieldValue(name, null);
    } else {
      let inputNumber = isInteger ? value : Number(value).toFixed(2);
      if (isInteger) {
        if (value > 999999999) {
          inputNumber = 999999999;
        } else if (value < -999999999) {
          inputNumber = -999999999;
        }
      } else {
        if (value > 999999999.99) {
          inputNumber = 999999999.99;
        } else if (value < -999999999.99) {
          inputNumber = -999999999.99;
        }
      }
      form.setFieldValue(name, Number(inputNumber));
    }
    updateData();
  };

  const handleOpen = (e) => {
    if (e.detail.openInput) {
      setOpen(true);
    }
  };

  useEffect(() => {
    window.addEventListener("mutipleInputRequied", handleOpen);
    return () => {
      window.removeEventListener("mutipleInputRequied", handleOpen);
    }
  }, []);

  // 根据类型获取输入类型
  const getConfigurationItem = ({ name, type }) => {
    switch (type) {
      case 'String':
        return <Input
          style={{ width: 400 }}
          maxLength={500}
          showCount
          onChange={updateData}
        />
      case 'Number':
        return <InputNumber
          style={{ width: 150 }}
          controls={true}
          keyboard={true}
          step={0.01}
          precision={2}
          min={-999999999.99}
          max={999999999.99}
          onChange={(e) => handleNumberChange(e, false, name)}
        />
      case 'Integer':
        return <InputNumber
          style={{ width: 150 }}
          keyboard={true}
          min={-999999999}
          max={999999999}
          parser={(value) => value.replace(/[^\d-]/g, '')} // 仅允许数字和负号
          onChange={(e) => handleNumberChange(e, true, name)}
          formatter={(value) => {
            if (value === '0') {
              return '0';
            }
            return `${Math.floor(value) || ''}`;
          }
          } // 强制显示整数
        />
      case 'Boolean':
        return <Switch onChange={updateData}></Switch>
    }
  };

  // 给表单赋初始值
  useEffect(() => {
    if (configurationList?.length) {
      configurationList.forEach(item => {
        const preItem = preConfigurationList.current.find(it => it.name === item.name);
        const isChangeType = preItem?.type !== item.type;
        if (item.type === 'Boolean') {
          form.setFieldValue(item.name, isChangeType ? false : (form.getFieldValue(item.name) || false));
        } else {
          form.setFieldValue(item.name, isChangeType ? null : ((isInputEmpty(form.getFieldValue(item.name)) ? null : form.getFieldValue(item.name))));
        }
      })
      updateData();
    }
    preConfigurationList.current = configurationList;
  }, [configurationList]);

  useEffect(() => {
    if (open) {
      setConfigurationList(getConfiguration(configAppInfo));
      if (isChatRunning()) {
        setOpen(false);
      }
    }
  }, [open]);

  useEffect(() => {
    if (chatRunning) {
      setOpen(false);
    }
  }, [chatRunning]);

  useEffect(() => {
    setOpen(false);
    setConfigAppInfo(atAppInfo || appInfo || {});
  }, [atAppInfo, appInfo]);

  useEffect(() => {
    const configuration = getConfiguration(configAppInfo);
    setConfigurationList(configuration);
    setOpen(configuration?.length > 0);
  }, [configAppInfo]);

  useEffect(() => {
    if (showElsa) {
      setOpen(false);
    }
  }, [showElsa]);

  const content = (
    <>
      <div className='configuration-header'>
        <span className='configuration-title'>{t('conversationConfiguration')}</span>
        <img src={CloseImg} alt="" onClick={() => setOpen(false)} />
      </div>
      <div className='configuration-content'>
        {
          configurationList?.length > 0 ? <Form form={form} autoComplete='off'>
            {
              configurationList.map(config =>
                <Form.Item
                  key={config.id}
                  name={config.name}
                  label={config.displayName || ' '}
                  className={config.isRequired ? 'is-required' : ''}>
                  {getConfigurationItem(config)}
                </Form.Item>
              )
            }
          </Form> : <Empty description={t('noData')}></Empty>
        }
      </div>
    </>
  );

  return <>
    {
      configurationList.length > 0 &&
      <Popover
        placement="topLeft"
        arrowPointAtCenter
        open={display ? open : false}
        trigger={'click'}
        content={content}
        color='#fff'
        overlayClassName={setSpaClassName('configuration-tooltip')}
      >
        <div className={setSpaClassName('configuration-icon')} onClick={() => setOpen(!open)}></div>
      </Popover>
    }
  </>
};

export default ConversationConfiguration;