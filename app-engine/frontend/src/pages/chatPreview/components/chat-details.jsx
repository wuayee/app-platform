
import React, { useContext, useState, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { AippContext } from '../../aippIndex/context';
import { TabLeftIcon, TabRightIcon } from '@assets/icon';
import EditModal from '../../components/edit-modal';
import knowledgeBase from '@assets/images/knowledge/knowledge-base.png';
import robot2 from '@assets/images/ai/xiaohai.png';
import '../styles/chat-details.scss';
import StarApps from "./star-apps";
import { AppBoxIcon, CreateAppIcon } from '@assets/icon';

const ChatDetail = () => {
  const { aippInfo, tenantId,openStar, setOpenStar }  = useContext(AippContext);
  const [ modalInfo, setModalInfo ] = useState({});
  const navigate = useNavigate();
  const location = useLocation();
  let modalRef = useRef();
  const isHomepage = aippInfo.name === '小海' && !location.pathname.includes("app-detail");
  const addApp = () => {
    setModalInfo(() => {
      modalRef.current.showModal();
      return {
        name: '',
        attributes: {
          description: '',
          greeting: '',
          icon: '',
          app_type: '编程开发',
        }
      }
    })
  }
  function addAippCallBack(appId) {
    navigate(`/app-develop/${tenantId}/app-detail/${appId}`);
  }
  return <>{(
    <div className='chat-details-content'>
       {aippInfo?.name ? ( isHomepage ? (
        <div className="home-top">
          <div className="head-inner">
            <div className="inner-left">
              <div className="title">APP Engine</div>
              <div className="sub-title">你的专属AI智能编排研发平台</div>
              <div className="desc">AI助力研发，开始创建专属应用吧～</div>
            </div>
            <div className="inner-right">
              <div className="">
                <img src={robot2} />
              </div>
            </div>
            <div className="inner-right-chat">
              Hi~我是APP
              Engine的超级应用 <strong>小海</strong>，我可以<strong>呼唤其他应用</strong>协同工作，也可以<strong>解答存储领域相关任何问题</strong>，试试向我提问吧~
            </div>
          </div>
          <div className="head-nav">
            <div className="nav-left" onClick={addApp}>
              <div className="tag">
                <CreateAppIcon />
              </div>
              <div className="nav-title">创建应用</div>
              <div className="nav-desc">
                通过跟小海聊天轻松定制你的专属应用 -
                上传自有数据，知识库模型训练，为业务带来更多价值。立即开始创建应用吧!
              </div>
            </div>
            <div
              className={`nav-right ${openStar ? "nav-item-active" : ""}`}
              onClick={() => setOpenStar(true)}
            >
              <div className="tag">
                <AppBoxIcon />
              </div>
              <div className="nav-title">应用百宝箱</div>
              <div className="nav-desc">
                我们拥有海量的应用，让您可以轻松获取和部署各种专业的应用,涵盖不同领域和功能，马上开启你的探索应用市场之旅。
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="top">
          <div className="head">
            <Img icon={aippInfo.attributes?.icon} />
          </div>
          <div className="title">{aippInfo.name}</div>
          <div className="text">{aippInfo.attributes?.description}</div>
          <div className="bottom">
            <div className="left">
              <Img icon={aippInfo.attributes?.icon} />
            </div>
            <div className="right">
              {aippInfo.attributes?.greeting || "你好"}
            </div>
          </div>
        </div>
      ) ) : ''}
       <EditModal type="add" modalRef={modalRef} aippInfo={modalInfo} addAippCallBack={addAippCallBack}/>
    </div>
  )}</>;
};

const Img = (props) => {
  const { icon } = props;
  return <>{<span>{icon ? <img src={icon} /> : <img src={knowledgeBase} />}</span>}</>;
};

export default ChatDetail;
