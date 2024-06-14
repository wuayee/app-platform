
import React, { useEffect, useState } from 'react';
import { Modal, Input, Button } from 'antd';
const { Search } = Input;

const HuggingFaceModal = (props) => {
  const { showModal, setShowModal, onModelSelectCallBack } = props;
  const [ name, setName ] = useState('');
  const [ pluginData, setPluginData ] = useState([]);
  const [ activeKey, setActiveKey ] = useState('');
  const [ activeUrl, setActiveUrl ] = useState('');
  const items = [
    {
      id: '1',
      name: '2Noise/ChatTTS',
      num: '9027',
      desc: 'We are also training larger-scale models and need computational power and data suppor...'
    },
    {
      id: '2',
      name: '2Noise/ChatTTS',
      num: '9027',
      desc: 'We are also training larger-scale models and need computational power and data suppor...'
    },
    {
      id: '3',
      name: '2Noise/ChatTTS',
      num: '9027',
      desc: 'We are also training larger-scale models and need computational power and data suppor...'
    },
    {
      id: '4',
      name: '2Noise/ChatTTS',
      num: '9027',
      desc: 'We are also training larger-scale models and need computational power and data suppor...'
    },
    {
      id: '5',
      name: '2Noise/ChatTTS',
      num: '9027',
      desc: 'We are also training larger-scale models and need computational power and data suppor...'
    },
    {
      id: '6',
      name: '2Noise/ChatTTS',
      num: '9027',
      desc: 'We are also training larger-scale models and need computational power and data suppor...'
    },
    {
      id: '7',
      name: '2Noise/ChatTTS',
      num: '9027',
      desc: 'We are also training larger-scale models and need computational power and data suppor...'
    }
  ]
  useEffect(() => {
    showModal && getPluginList();
  }, [props.showModal, name])
  // 获取插件列表
  const getPluginList = ()=> {
    setActiveKey(items[0].id);
    setPluginData(items);
  };
  // 名称搜索
  const filterByName = (value: string) => {
    if(value !== name) {
      setName(value);
    }
  }
  const itemClick = (item) => {
    setActiveKey(item.id);
  }
  const confirm = () => {
    onModelSelectCallBack({ name: 'zy-model' });
    setShowModal(false);
  }
  return <>
    <Modal 
      title='选择HuggingFace模型' 
      open={showModal} 
      onCancel={() => setShowModal(false)} 
      width='1230px'
      footer={
        <div className="drawer-footer">
          <Button onClick={() => setShowModal(false)}>取消</Button>
          <Button type="primary" onClick={confirm}>确定</Button>
        </div>
      }
    >
      <div className="tool-modal-search">
        <Search size="large" onSearch={filterByName} placeholder="请输入" />
      </div>
      <div className="tool-modal-content">
        <div className="content-left">
          { pluginData.map((card:any) => 
            <div className={ `left-item ${activeKey === card.id ? 'active' : null}` } 
                 key={card.id} 
                 onClick={() => itemClick(card)}>
              <div className="item-top">
                <div className="top-left">
                  <img src="/src/assets/images/ai/hugging-face.png" alt="" />
                </div>
                <div className="top-right">
                  <div className="item-title" title={card.name}>{card.name} </div>
                  <div className="item-tag">{card.num}</div>
                </div>
              </div>
              <div className="item-bottom" title={card.desc}>{card.desc }</div>
            </div>
          )}
        </div>
        <div className="content-right"></div>
      </div>
    </Modal>
  </>
};


export default HuggingFaceModal;
