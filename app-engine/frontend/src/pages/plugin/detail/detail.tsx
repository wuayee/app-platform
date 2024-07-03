import React, { useEffect, useState } from 'react';
import { Divider, Flex, Table, Tag } from 'antd';
import GoBack from '../../../components/go-back/GoBack';
import { PluginIcons } from '../../../components/icons/plugin';
import { Icons } from '../../../components/icons';
import { useParams } from 'react-router';
import { getPluginDetail } from '../../../shared/http/plugin';
import '../style.scoped.scss';
import { IconMap, outputColumns, paramsColumns } from '../helper';

const tags = ['fit', 'http'];
const PlugeDetail: React.FC = () => {
const { pluginId } = useParams();
const [data,setData]=useState(null);
const [inputParams,setInputParams]=useState([]);
const [outputParams,setOutputParams]=useState([]);
 const refreshDetail=async()=>{
    const res= await getPluginDetail(pluginId);
    setData(res?.data);
    let properties=res?.data?.schema?.parameters?.properties || {};
    const resInput = Object.keys(properties).map((key)=> ({...properties[key],key:key}));
    setInputParams(resInput);
    setOutputParams([res?.data?.schema?.return || {}]);
  }
  useEffect(()=>{
    if(pluginId){
    refreshDetail();
    }
  },[pluginId])
  return(
  <div className='aui-fullpage'>
    <div className='aui-header-1'>
      <div className='aui-title-1'>
        <GoBack path='/plugin' title='插件详情' />
      </div>
    </div>
    <div className='aui-block'>
      <div className='detail-header'>
        <img src='/src/assets/images/knowledge/knowledge-base.png' />
        <div>
          <div style={{ display: 'flex' }}>
            <div className='detail-header-name'>{data?.name}</div>
            <div className='version-div'>
              <Tag className='version'>v1.1.0</Tag>
            </div>
            <div className='icon-display'>
            <PluginIcons.ToolIcon/>  
            </div>
            <Flex className='icon-display' gap={4}>
               {IconMap[data?.tags?.[0]]?.icon}
               <span style={{ fontSize: 12, fontWeight: 700 }}>{IconMap[data?.tags?.[0]]?.name}</span>
            </Flex>
            <div className='header-tag'>
              {data?.tags.map((tag: string, index: number) => (
                <Tag key={index}>{tag}</Tag>
              ))}
            </div>
          </div>
          <div className='user-info'>
            <Icons.user />
            <span>{data?.creator}</span>
            <span className='header-time' hidden>创建于2021</span>
            <span className='header-time' hidden>引用数:250</span>
          </div>
        </div>
      </div>
      <div style={{marginTop:14}}>{data?.description}</div>
      <Divider />
      <div className='param-title'>输入参数</div>
      <Table dataSource={inputParams} columns={paramsColumns} pagination={false} />
      <div className='param-title' style={{marginTop:14}}>输出参数</div>
      <Table dataSource={outputParams} columns={outputColumns} pagination={false} />
    </div>
  </div>
)};

export default PlugeDetail;
