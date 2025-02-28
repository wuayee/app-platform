/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Drawer, Timeline, Empty } from 'antd';
import { useParams } from 'react-router-dom';
import { CloseOutlined } from '@ant-design/icons';
import { getVersion } from '@/shared/http/aipp';
import { useTranslation } from "react-i18next";

const TimeLineFc = (props) => {
  const { open, setOpen } = props;
  const [timeList, setTimeList] = useState([]);
  const { tenantId, appId } = useParams();
  const { t } = useTranslation();

  useEffect(() => {
    open && getVersion(tenantId, appId).then(res => {
      if (res.code === 0) {
        setTimeList(res.data);
      }
    })
  }, [open]);
  const descProcess = (str) => {
    if (!str || str === 'null') {
      return '';
    }
    return str;
  }
  return <>
    <Drawer
      title={t('publishHistory')}
      placement='right'
      width='420px'
      closeIcon={false}
      onClose={() => setOpen(false)}
      open={open}
      footer={null}
      extra={
        <CloseOutlined onClick={() => setOpen(false)} />
      }>
      <div>
        <div style={{ marginBottom: '18px', display: 'flex', alignItems: 'center' }}>
          <img src='./src/assets/images/ai/tag.png' />
          <span style={{ marginLeft: '12px' }}>{t('cannotRevertVersion')}</span>
        </div>
        {timeList.length > 0 ? 
          <Timeline>
            { timeList.map(timeItem => (
              <Timeline.Item color='#000000'>
                <div className="time-line-inner" style={{ color: 'rgb(77, 77, 77)' }}>
                  <div style={{ fontWeight: '700' }}>{timeItem.appVersion}</div>
                  <div style={{ margin: '8px 0' }}>{descProcess(timeItem.publishedDescription)}</div>
                  <div>{timeItem.publishedBy}</div>
                  <div>{timeItem.publishedAt}</div>
                </div>
              </Timeline.Item>
            )) }
          </Timeline> :
          <div style={{ marginTop: '300px' }}><Empty description={t('noData')} /></div>
        }
      </div>
    </Drawer>
  </>
};


export default TimeLineFc;
