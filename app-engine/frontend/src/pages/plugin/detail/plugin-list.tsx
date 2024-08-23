import React, { useEffect, useState } from 'react';
import { Drawer, Button } from 'antd';
import { CloseOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import PluginCard from '@/components/plugin-card';
import EmptyItem from '@/components/empty/empty-item';
import DeployMent from '../deployment';
import { getPluginDetail } from '@/shared/http/plugin';
import { PluginCardTypeE } from '../helper';
import { useTranslation } from 'react-i18next';
import '../styles/plugin.scss';

const PliginList = (props) => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [pluginData, setPluginData] = useState([]);
  const [data, setData] = useState([]);
  const getPluginList = () => {
    getPluginDetail(props.match.params.pluginId).then(({ data }) => {
      setPluginData(data.pluginToolDataList);
      setData(data);
    });
  };
  // 联机帮助
  const onlineHelp = () => {
    if (window.self !== window.top) {
      window.open(`${window.parent.location.origin}/help/toctopics/application_plug-in.html`, '_blank');
    }
  }
  useEffect(() => {
    getPluginList();
  }, []);
  return (
    <>
      {
        <div className='plugin-detail'>
          <div className='aui-header-1 '>
            <div className='aui-title-1'>
              {t('pluginManagement')}
              <QuestionCircleOutlined onClick={onlineHelp} style={{ marginLeft: '8px', fontSize: '18px' }} />
            </div>
            <Button size='small' onClick={() => setOpen(true)}>{t('deploying')}</Button>
          </div>
          <div className='plugin-detail-list'>
            <div className='list-head'>
              <div className='list-back-icon flex'>
                <img src='./src/assets/images/ai/left-arrow.png' onClick={() => window.history.back()} />
              </div>
              <div className='list-detail-img flex'>
                <img src='./src/assets/images/knowledge/knowledge-base.png' />
              </div>
              <div className='list-detail-desc'>
                <div className='desc-top'>
                  <span className='name'>{data?.pluginName}</span>
                </div>
                <div className='desc-middle'>
                  <span className='user'>{t('creator')}{data?.creator}</span>
                </div>
              </div>
            </div>
            {pluginData.length > 0 ? (
              <div className='list-content'>
                {pluginData.map((card: any) => (
                  <PluginCard
                    key={card.uniqueName}
                    pluginData={card}
                    cardType={PluginCardTypeE.MARKET}
                  />
                ))}
              </div>
            ) : (
                <div className='empty-box'>
                  <EmptyItem />
                </div>
              )}
          </div>
          <Drawer
            title={t('deployPlugin')}
            width={1000}
            onClose={() => setOpen(false)}
            closeIcon={false}
            open={open}
            destroyOnClose
            extra={<CloseOutlined onClick={() => setOpen(false)} />}
            footer={null}
          >
            <DeployMent cancle={() => setOpen(false)} confirm={confirm} />
          </Drawer>
        </div>
      }
    </>
  );
};

export default PliginList;
