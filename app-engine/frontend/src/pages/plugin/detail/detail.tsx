import React, { useEffect, useState } from 'react';
import { Divider, Table, Tag } from 'antd';
import { PluginIcons } from '@/components/icons/plugin';
import { Icons } from '@/components/icons';
import { IconMap, outputColumns, paramsColumns } from '../helper';
import { useTranslation } from 'react-i18next';
import '../style.scoped.scss';

const PlugeDetail: React.FC = ({ pluginData }) => {
  const { t } = useTranslation();
  const [data, setData] = useState<any>([]);
  const [inputParams, setInputParams] = useState([]);
  const [outputParams, setOutputParams] = useState([]);
  const refreshDetail = async () => {
    setData(pluginData);
    let properties = pluginData?.schema?.parameters?.properties || {};
    const resInput = Object.keys(properties).map((key) => ({ ...properties[key], key: key }));
    setInputParams(resInput);
    setOutputParams([pluginData?.schema?.return || {}]);
  };
  useEffect(() => {
    if (pluginData) {
      refreshDetail();
    }
  }, [pluginData]);

  return (
    <div className='engine-plugin-detail'>
      <div className='detail-header'>
        <img src='./src/assets/images/knowledge/knowledge-base.png' />
        <div>
          <div style={{ display: 'flex' }}>
            <div className='detail-header-name'>{data?.name}</div>
            <div className='version-div'>
              <Tag className='version'>v1.1.0</Tag>
            </div>
            <div className='icon-display'>
              <PluginIcons.ToolIcon />
            </div>
            <div className='icon-display' style={{ gap: '4px' }}>
              {IconMap[data?.tags?.[0]]?.icon}
              <span style={{ fontSize: 12, fontWeight: 700 }}>
                {IconMap[data?.tags?.[0]]?.name}
              </span>
            </div>
            <div className='header-tag'>
              {data?.tags?.map((tag: string, index: number) => <Tag key={index}>{tag}</Tag>)}
            </div>
          </div>
          <div className='user-info'>
            <Icons.user />
            <span>{data?.creator}</span>
            <span className='header-time' hidden>
              {t('createdAt')}
            </span>
            <span className='header-time' hidden>
              {t('quoteNumber')}
            </span>
          </div>
        </div>
      </div>
      <div style={{ marginTop: 14 }}>{data?.description}</div>
      <Divider />
      <div className='param-title'>{t('inputParam')}</div>
      <Table dataSource={inputParams} columns={paramsColumns} pagination={false} rowKey='key' />
      <div className='param-title' style={{ marginTop: 14 }}>
        {t('outputParam')}
      </div>
      <Table dataSource={outputParams} columns={outputColumns} pagination={false} rowKey='key' />
    </div>
  );
};

export default PlugeDetail;
