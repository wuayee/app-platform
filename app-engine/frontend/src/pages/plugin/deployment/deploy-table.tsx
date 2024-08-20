
import React, { useEffect, useState, useImperativeHandle, useRef } from 'react';
import { Input, Button, Checkbox, Empty, Tooltip, Modal } from 'antd';
import { RightOutlined, CloseOutlined } from '@ant-design/icons';
import { Icons } from '@/components/icons';
import Pagination from '@/components/pagination/index';
import { getPlugins, getDeployTool } from '@shared/http/plugin';
import { debounce } from '@/shared/utils/common';
import { PluginStatusTypeE, PluginCnType } from '../helper';
import '../styles/deploy-table.scss';

const Deploy = ({ pluginRef }) => {
  const [tableData, setTableData] = useState([]);
  const [pluginData, setPluginData] = useState([]);
  const [deployedData, setDeployedData] = useState([]);
  const [pluginLength, setPluginLength] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [total, setTotal] = useState(0);
  const [name, setName] = useState('');
  const [rightName, setRightName] = useState('');
  const pluginList = useRef([]);
  // 获取所有列表
  const getData = async () => {
    const response = await getPlugins({ pageNum, pageSize, excludeTags: 'APP', name });
    if (response.code === 0) {
      let list = response.data || [];
      list.forEach(item => {
        item.checked = item.deployStatus === 'DEPLOYED';
      })
      setTableData(list);
      setTotal(response.total || 0);
    }
  }
  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
  };
  // 获取已部署的列表
  const getDeployData = async () => {
    const response = await getDeployTool('deployed');
    if (response.code === 0) {
      setPluginData(response.data);
      setPluginLength(response.data.length);
      setDeployedData(response.data);
      pluginList.current = JSON.parse(JSON.stringify(response.data));
    }
  };
  // 选中
  const onChange = (checked, item) => {
    tableData.forEach(tableItem => {
      if (item.pluginId === tableItem.pluginId) {
        tableItem.checked = checked;
      }
    });
    if (checked) {
      pluginList.current.push(item);
      searchPluginData(rightName);
    } else {
      let pluginCheckList = pluginData.filter(pluginItem => pluginItem.pluginId !== item.pluginId);
      pluginList.current = pluginList.current.filter(pluginItem => pluginItem.pluginId !== item.pluginId);
      setPluginData(pluginCheckList);
    }
    setPluginLength(pluginList.current.length);
    setTableData(JSON.parse(JSON.stringify(tableData)));
  }
  const filterByName = (value: string, type: string) => {
    if (type === 'table') {
      setName(value);
    } else {
      searchPluginData(value);
      setRightName(value);
    }
  };
  const handleSearch = debounce(filterByName, 1000);
  // 右侧插件列表搜索
  const searchPluginData = (val) => {
    setRightName(val);
    if (val.trim().length) {
      let list = pluginList.current.filter(pluginItem => pluginItem.pluginName.indexOf(val) !== -1);
      setPluginData(list);
      return
    }
    setPluginData(pluginList.current);
  }
  // 对外暴露方法
  useImperativeHandle(pluginRef, () => {
    return {
      getCheckedList: () => {
        return pluginData;
      },
      getDeployedList: () => {
        return deployedData;
      }
    }
  });
  useEffect(() => {
    getDeployData();
  }, []);
  useEffect(() => {
    getData();
  }, [name, pageNum, pageSize]);
  return <>
    <div className='deploy-info-content'>
      <div className='deploy-table'>
        <div className='table-title'>
          <span>插件资源池</span>
          <span className='num'>{total}</span>
        </div>
        <div className='table-search'>
          <div className='table-list'>
            <div className='left'>
              <span>插件名称</span>
              <Input
                placeholder='搜索'
                style={{ width: '140px', height: '25px', marginLeft: '16px' }}
                prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
                onChange={(e) => handleSearch(e.target.value, 'table')}
              />
            </div>
            <div className='right'>插件详情</div>
          </div>
        </div>
        <div className='table-content'>
          {tableData.length > 0 && tableData.map(item => <div className='table-list' key={item.pluginId}>
            <div className='left'>
              <span className='check'><Checkbox checked={item.checked} onChange={(e) => onChange(e.target.checked, item)}></Checkbox></span>
              <span className='name' title={item.pluginName}>{item.pluginName}</span>
              <span className={['plugin-tag', PluginStatusTypeE[item.deployStatus]].join(' ')}>
                {PluginCnType[item.deployStatus]}
              </span>
            </div>
            <div className='right' title={item.extension?.description}>
              <span className='desc'>{item.extension?.description}</span>
            </div>
          </div>)}
          {tableData.length === 0 && <Empty
            imageStyle={{ height: 60 }}
            description={<span>暂无数据</span>} />}
        </div>
        <div style={{ paddingTop: 16 }}>
          <Pagination total={total} current={pageNum} onChange={selectPage} pageSize={pageSize} />
        </div>
      </div>
      <div className='icon'><RightOutlined /></div>
      <div className='deploy-table'>
        <div className='table-title'>
          <span>已选</span>
          <span className='num'>{pluginLength}</span>
          <span className='tips'>最多可选20个</span>
        </div>
        <div className='table-search'>
          <div className='table-list'>
            <div className='left'>
              <span>插件名称</span>
              <Input
                placeholder='搜索'
                style={{ width: '140px', height: '25px', marginLeft: '16px' }}
                prefix={<Icons.search color={'rgb(230, 230, 230)'} />}
                onChange={(e) => handleSearch(e.target.value)}
              />
            </div>
            <div className='right'>插件详情</div>
          </div>
        </div>
        <div className='table-content'>
          {pluginData.length > 0 && pluginData.map(item => <div className='table-list' key={item.pluginId}>
            <div className='left'>
              <span className='name' title={item.pluginName}>{item.pluginName}</span>
              <span className={['plugin-tag', PluginStatusTypeE[item.deployStatus]].join(' ')}>
                {PluginCnType[item.deployStatus]}
              </span>
            </div>
            <div className='right'>
              <span className='desc' title={item.extension?.description}>{item.extension?.description}</span>
              <span className='icon' onClick={() => onChange(false, item)}>
                {item.deployStatus === 'deployed' ? <Tooltip
                  placement='left'
                  title='取消已部署的插件，会导致正在运行的应用不可用，请谨慎操作'
                  color='#ffffff'
                  overlayInnerStyle={{ color: '#333333' }}>
                  <CloseOutlined />
                </Tooltip> : <CloseOutlined />}
              </span>
            </div>
          </div>)}
          {pluginData.length === 0 && <Empty
            imageStyle={{ height: 60 }}
            description={<span>暂无数据</span>} />}
        </div>
      </div>
    </div>
  </>
};
export default Deploy;
