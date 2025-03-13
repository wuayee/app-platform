/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { useTranslation } from 'react-i18next';
import { QuestionCircleOutlined } from '@ant-design/icons';
import { getCookie, setSpaClassName } from '@/shared/utils/common';
import OwnerForm from './components/owner-form';
import './index.scss';


/**
 * 创建表单
 *
 * @return {JSX.Element}
 * @constructor
 */
const IntelligentForm = () => {
  const { t } = useTranslation();

  // 联机帮助
  const onlineHelp = () => {
    window.open(`${window.parent.location.origin}/help${getCookie('locale').toLocaleLowerCase() === 'en-us' ? '/en' : '/zh'}/smart_forms.html`, '_blank');
  }

  return <div className={setSpaClassName('app-fullpage')}>
    <div className='aui-header-1'>
      <div className='aui-title-1'>
        {t('intelligentForm')}
        { process.env.PACKAGE_MODE === 'spa' && <QuestionCircleOutlined onClick={onlineHelp} style={{ marginLeft: '8px', fontSize: '18px' }} />}
      </div>
    </div>
    <div className='aui-block'>
      <OwnerForm />
    </div>
  </div>
};

export default IntelligentForm;