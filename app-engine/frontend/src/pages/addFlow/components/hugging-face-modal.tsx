
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Modal, Input, Button } from 'antd';
import { getHuggingFaceList } from '@shared/http/appBuilder';
import Pagination from '../../../components/pagination/index';
const { Search } = Input;

const HuggingFaceModal = (props) => {
  const { showModal, setShowModal, onModelSelectCallBack } = props;
  const [ name, setName ] = useState('fill-mask');
  const [ pageNum, setPageNum ] = useState(1);
  const [ pageSize, setPageSize ] = useState(10);
  const [ total, setTotal ] = useState(0);
  const [ pluginData, setPluginData ] = useState([]);
  const [ activeKey, setActiveKey ] = useState('');
  const [ activeName, setActiveName ] = useState('');
  const [ activeUrl, setActiveUrl ] = useState('');
  const { tenantId, appId } = useParams();
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
  }, [props.showModal, name, pageNum, pageSize]);
  // 获取插件列表
  const getPluginList = ()=> {
    getHuggingFaceList(tenantId, {pageNum, pageSize, taskName: name}).then(res => {
      if (res.code === 0) {
        let arr = [];
        res.data.forEach((item, index) => {
          let obj:any = {};
          obj.id = String(index);
          obj.name = item;
          obj.num = 9027;
          obj.desc = 'We are also training larger-scale models and need computational power and data suppor...';
          arr.push(obj);
        })
        setActiveKey(arr[0].id);
        setPluginData(arr);
      }
    });
  };
  // 名称搜索
  const filterByName = (value: string) => {
    if(value !== name) {
      setName(value);
    }
  }
  const itemClick = (item) => {
    setActiveKey(item.id);
    setActiveName(item.name);
    setActiveUrl('');
  }
  const confirm = () => {
    onModelSelectCallBack({ name: activeName });
    setShowModal(false);
  }
  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
    if (pageSize !== curPageSize) {
      setPageSize(curPageSize);
    }
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
          <div className="left-list">
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
                    <div className="item-tag">
                      <span>
                        <img src="/src/assets/images/ai/download.png" alt="" />
                        {card.num}
                      </span>
                      <span>
                        <img src="/src/assets/images/ai/like.png" alt="" />
                        {card.num}
                      </span>
                    </div>
                  </div>
                </div>
                <div className="item-bottom" title={card.desc}>{card.desc }</div>
              </div>
            )}
          </div>
          <div className="left-page">
            <Pagination
              total={total}
              current={pageNum}
              onChange={selectPage}
              pageSize={pageSize}
            /> 
          </div>
        </div>
        <div className="content-right">
          <iframe className="iframe-item" src={activeUrl} frameBorder="0"></iframe>
        </div>
      </div>
    </Modal>
  </>
};


export default HuggingFaceModal;
