import { Button, Divider } from 'antd';
import React, { useEffect, useState } from 'react';
import { getAppInfo, getAppInfoByVersion } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { useHistory, useParams } from 'react-router';
import { AppIcons } from '@/components/icons/app';
import { useAppDispatch } from '@/store/hook';
import { setAppInfo } from "@/store/appInfo/appInfo";
import { useTranslation } from "react-i18next";
import './style.scoped.scss';

const AppOverview: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useHistory().push;
  const { appId, tenantId } = useParams();
  const [detail, setDetail] = useState({});
  const [appIcon, setAppIcon] = useState('');
  const dispatch = useAppDispatch();

  useEffect(() => {
    getAppInfo(tenantId, appId).then(res => {
      if (res.code === 0) {
        setDetail({ ...res.data });
        if (res.data?.attributes?.icon) {
          setAppIcon(res.data?.attributes?.icon);
        }
      } else {
        Message({ type: 'error', content: res.message || t('getDetailFail') });
      }
    });
  }, []);

  const gotoArrange = () => {
    getAppInfoByVersion(tenantId, appId).then(res => {
      if (res.code === 0) {
        dispatch(setAppInfo({}));
        const newAppId = res.data.id;
        navigate(`/app-develop/${tenantId}/app-detail/${newAppId}`);
      }
    })
  }

  return (
    <div className='tab-content'>
      <div className='content-inner'>
        <div className='inner-box'>
          <div className='details-content'>
            {appIcon ?
              <img width={100} height={100} src={appIcon} />
              :
              <img width={100} height={100} src='./src/assets/images/knowledge/knowledge-base.png' />
            }

            <div className='details-content' style={{ flexDirection: 'column' }}>
              <div className='detail-name'>
                <span className='text'>{detail?.name || ''}</span>
                {
                  (detail.attributes?.latest_version || detail.state === 'active') ?
                    (
                      <div className="status-tag">
                        <img src='./src/assets/images/ai/complate.png' />
                        <span>{t('published')}</span>
                      </div>
                    ) :
                    (
                      <div className="status-tag">
                        <img src='./src/assets/images/ai/publish.png' />
                        <span>{t('unPublished')}</span>
                      </div>
                    )
                }
              </div>
              <div className='detail-footer'>
                <div className='icon'>
                  <AppIcons.UserIcon />
                  <span>{detail?.createBy || 'Admin'}</span>
                </div>
                <div className='create'>
                  <span>{t('createAt')}</span>
                  <span>{detail?.createAt}</span>
                </div>
              </div>
              <div className='detail-footer'>
                <div className='app-id'>
                  <span>{t('app')}ID：</span>
                  <span>{detail?.id}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className='app-desc' title={detail?.attributes?.description}>
          {detail?.attributes?.description}
        </div>
        <Button type='primary' onClick={gotoArrange} style={{
          width: '96px',
          height: '32px',
        }}>{t('toArrange')}</Button>
        <Divider style={{ margin: 0, backgroundColor: '#D7D8DA' }} />
        <div>
          <div className='remarks-content'>
            <div className='remarks'>
              <span className='left'>{t('prologue')}：</span>
              <span className='right'>{detail?.attributes?.greeting || '-'}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default AppOverview;
