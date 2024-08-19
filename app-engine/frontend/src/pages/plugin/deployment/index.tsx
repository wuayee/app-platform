
import React, { useEffect, useState, useRef } from 'react';
import { Modal, Button } from 'antd';
import DeployTable from './deploy-table';
import { getDeployTool, setDeployTool } from '@shared/http/plugin';
import { Message } from '@/shared/utils/message';
import '../styles/deployment.scss';

const DeployMent = ({ cancle, confirm }) => {
  const [disabled, setDisabled] = useState(true);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [deployedNum, setDeployedNum] = useState(0);
  const [pluginNum, setPluginNum] = useState(0);
  const pluginRef = useRef(null);
  // 获取所有部署中的插件
  const getData = async () => {
    const res = await getDeployTool('deploying');
    if (res.code === 0 && res.total === 0) {
      setDisabled(false)
    }
  }
  const confirmSunmit = () => {
    const list = pluginRef.current?.getCheckedList();
    const deployedList = pluginRef.current?.getDeployedList();
    let uninstallNum = deployedList.length;
    const deployedIdList = deployedList.map(item => item.pluginId);
    list.forEach(item => {
      if (deployedIdList.includes(item.pluginId)) {
        uninstallNum -= 1;
      }
    });
    setPluginNum(list.length);
    setDeployedNum(uninstallNum);
    if (list.length === 0) {
      Message({ type: 'warning', content: '未选择部署插件' });
      return;
    }
    setLoading(false);
    setOpen(true);
  }
  // 确定部署
  const handleOk = async () => {
    const list = pluginRef.current?.getCheckedList();
    let idList = list.map(item => item.pluginId);
    try {
      setLoading(true);
      const res = await setDeployTool({ pluginIds: idList });
      if (res.code === 0) {
        Message({ type: 'success', content: '操作成功' });
        setOpen(false);
        confirm();
      }
    } finally {
      setLoading(false);
    }
  };
  const handleCancel = () => {
    if (loading) return;
    setOpen(false);
  }
  useEffect(() => {
    getData();
  }, []);
  return <>
    <div className='engine-deployment'>
      <div className='upload-info-head'>
        <img src='/src/assets/images/ai/info-upload.png' />
        <span>部署可能需要一定时长，请关注部署状态，部署成功后插件内的工具将可以被使用</span>
      </div>
      <DeployTable pluginRef={pluginRef} />
      <div className='deploy-info-btn'>
        <Button onClick={() => cancle()}>取消</Button>
        <Button type='primary' onClick={confirmSunmit} disabled={disabled}>确定</Button>
      </div>
    </div>
    <Modal
      open={open}
      title='确认部署？'
      centered
      onCancel={handleCancel}
      footer={[
        <Button onClick={handleCancel}>
          取消
        </Button>,
        <Button type='primary' loading={loading} onClick={handleOk}>
          确定
        </Button>
      ]}
    >
      <p>你将部署 <b>{pluginNum}个</b> 插件</p>
      {deployedNum > 0 && <p>你取消了 <b>{deployedNum}个</b> 已部署的插件，会导致正在运行的应用不可用</p>}
    </Modal>
  </>
};


export default DeployMent;
