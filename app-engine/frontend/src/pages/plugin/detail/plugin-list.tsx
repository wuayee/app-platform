import React, { useEffect, useState } from 'react';
import { Drawer, Button } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import PluginCard from '@/components/plugin-card';
import EmptyItem from '@/components/empty/empty-item';
import DeployMent from '../deployment';
import { getPluginDetail } from '@/shared/http/plugin';
import { PluginCardTypeE } from '../helper';
import '../styles/plugin.scss';

const PliginList = (props) => {
  const [open, setOpen] = useState(false);
  const [pluginData, setPluginData] = useState([]);
  const [data, setData] = useState([]);
  const getPluginList = () => {
    getPluginDetail(props.match.params.pluginId).then(({ data }) => {
      setPluginData(data.pluginToolDataList);
      setData(data);
    });
  };
  useEffect(() => {
    getPluginList();
  }, []);
  return (
    <>
      {
        <div className='plugin-detail'>
          <div className='aui-header-1 '>
            <div className='aui-title-1'>插件管理</div>
            <Button size='small' onClick={() => setOpen(true)}>部署</Button>
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
                  {/* <span className='icon'>达摩知识插件</span> */}
                  {/* <span className='tag'>已部署</span> */}
                </div>
                <div className='desc-middle'>
                  <span className='user'>创建人： {data?.creator}</span>
                  {/* <span className='plugin-time'>创建于 2024-01-31 17:46:41</span> */}
                </div>
                {/* <div className='desc-bottom'>
              包含XXX，可以帮助用户整理和记录灵感和想法，提高工作和学习效率。这些插件可以根据用户的需求和工作习惯进行选择，提高办公效率。
            </div> */}
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
            title='部署插件'
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
