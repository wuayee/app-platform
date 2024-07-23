import React, { useEffect, useRef, useState } from 'react';
import { Drawer, Input, Dropdown, Tooltip, Modal } from 'antd';
import type { MenuProps } from 'antd';
import {
  SearchOutlined,
  EllipsisOutlined,
  ClearOutlined,
  CloseOutlined,
} from '@ant-design/icons';
import { clearChatHistory, deleteChat, getChatList } from '@/shared/http/chat';
import { aippDebug, getChatRecentLog } from '@/shared/http/aipp';
import { getDaysAndHours } from '@/common/dataUtil';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setChatList, setChatRunning, setChatId, setOpenStar } from '@/store/chatStore/chatStore';
import { updateChatId } from "@/shared/utils/common";
import './style.scoped.scss';
import { historyChatProcess } from "../../utils/chat-process";
interface HistoryChatProps {
  openHistorySignal: number;
}

const HistoryChatDrawer: React.FC<HistoryChatProps> = ({ openHistorySignal }) => {
  const currentChat = useRef(null);
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatId = useAppSelector((state) => state.chatCommonStore.chatId);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const openStar = useAppSelector((state) => state.chatCommonStore.openStar);
  const [open, setOpen] = useState(false);
  const [data, setData] = useState([]);
  const [isClearOpen,setClearOpen]=useState(false);
  const [requestInfo, setRequestInfo] = useState({
    aipp_id: '', aipp_version: '', offset: 0, limit: 100
  });

  const refreshList = async () => {
    const chatRes = await getChatList(tenantId, requestInfo);
    setData(chatRes?.data);
  }

  const getAippId = async () => {
    if (!appInfo.id) return;
    const debugRes = await aippDebug(tenantId, appInfo?.id, appInfo, appInfo.state);
    let { aipp_id, version } = debugRes?.data;
    const requestBody = {
      aipp_id: aipp_id,
      aipp_version: version,
      offset: 0,
      limit: 100
    };
    setRequestInfo(requestBody);
  }

  const items: MenuProps['items'] = [
    {
      key: '1',
      label: <div onClick={async () => {
        await deleteChat(tenantId, currentChat?.current?.chat_id);
        if (chatId === currentChat?.current?.chat_id) {
          dispatch(setChatId(null));
          updateChatId(null, appId);
          dispatch(setChatList([]));
        }
        refreshList();
        // 删除成功提示
      }}>删除</div>,
    },
  ];

  const continueChat = async (chat_id, current_instance_id) => {
    dispatch(setChatRunning(false));
    const chatListRes = await getChatRecentLog(tenantId, chat_id, appId);
    let chatArr = historyChatProcess(chatListRes);
    await dispatch(setChatList(chatArr));
    setOpen(false);
    dispatch(setChatId(chat_id));
    updateChatId(chat_id, appId);
  }

  useEffect(() => {
    if (appInfo?.id) {
      getAippId();
    }
  }, [appInfo.id])

  const onClearList = async()=>{
    await clearChatHistory(tenantId,appId);
    refreshList();
    dispatch(setChatList([]));
    dispatch(setChatId(null));
    updateChatId(null, appId);
    setClearOpen(false);
    setOpen(false);
  }

  useEffect(() => {
    if (openHistorySignal > 0) {
      setOpen(true);
      dispatch(setOpenStar(false));
      refreshList();
    }
  }, [openHistorySignal]);

  useEffect(() => {
    if (openStar === true) {
      setOpen(false);
    }
  }, [openStar]);

  return (
    <Drawer
      destroyOnClose
      mask={false}
      title={
        <div className='history-title'>
          <div className='history-title-left'>
            <span>历史聊天</span>
            <div className='history-clear-btn' onClick={() => setClearOpen(true)}>
              <ClearOutlined style={{ fontSize: 14, marginLeft: 8 }} />
              <span className='history-clear-btn-text' >清空</span>
            </div>
          </div>
          <CloseOutlined
            style={{ fontSize: 20 }}
            onClick={() => setOpen(false)}
          />
        </div>
      }
      onClose={() => setOpen(false)}
      open={open}
      closeIcon={false}
      bodyStyle={{ padding: 0 }}
    >
      <div style={{ padding: 24 }}>
        <Input placeholder='搜索...' prefix={<SearchOutlined />} disabled />
      </div>
      <div className='history-wrapper'>
        {data?.slice(0, 30).map((item) => (
          <div className='history-item' key={item?.chat_id} onClick={() => { currentChat.current = item; }}>
            <div className='history-item-content'>
              <div className='history-item-header'>
              <Tooltip placement='top' title={<span style={{color:'#4d4d4d'}}>{item?.chat_name}</span>} color='#ffffff'>
              <div className='history-item-title'>{item?.chat_name?.length>10?item?.chat_name?.substring(0,10)+'...':item?.chat_name}</div>
              </Tooltip>
                <span
                  style={{ cursor: "pointer", color: "#1677ff" }}
                  onClick={() => { continueChat(item?.chat_id, item?.current_instance_id);}}
                >
                  继续聊天
                </span>
              </div>
              <div className='history-item-desc'>{item?.msg_list?.[0]}</div>
            </div>
            <div className='history-item-footer'>
              <span>{getDaysAndHours(item?.update_time_timestamp, item?.current_time_timestamp)}</span>
              <Dropdown menu={{ items }} trigger={['click']}>
                <EllipsisOutlined className='history-item-footer-more' />
              </Dropdown>
            </div>
          </div>
        ))}
      </div>
      <Modal title='警告' open={isClearOpen} onOk={onClearList} onCancel={()=>setClearOpen(false)}>
        <p>确认要清空所有聊天记录？删除后该数据无法恢复。</p>
      </Modal>
    </Drawer>
  );
};

export default HistoryChatDrawer;
