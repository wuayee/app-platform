import React, { useContext, useEffect, useRef, useState } from "react";
import { Drawer, Input, Dropdown, Tooltip, Modal } from "antd";
import type { MenuProps } from "antd";
import {
  SearchOutlined,
  EllipsisOutlined,
  ClearOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import "./style.scoped.scss";
import { clearChatHistory, deleteChat, getChatDetail, getChatList, tenantId } from "../../../../shared/http/chat";
import { aippDebug } from "../../../../shared/http/aipp";
import { getDaysAndHours } from "../../../../common/dataUtil";
import { useAppDispatch, useAppSelector } from "../../../../store/hook";
import { setChatList, setChatRunning,setChatId,setOpenStar } from "../../../../store/chatStore/chatStore";

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
  const [lastResSignal, setLastResSignal] = useState(0);
  const [isClearOpen,setClearOpen]=useState(false);
  const [requestInfo, setRequestInfo] = useState({
    aipp_id: '', app_version: '', offset: 0, limit: 100
  });

  const refreshList = async () => {
    const chatRes = await getChatList(tenantId, requestInfo);
    setData(chatRes?.data);
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
  }, [openStar])

  const getAippId = async () => {
    if (!appInfo.id) return;
    const debugRes = await aippDebug(tenantId, appInfo?.id, appInfo);
    let { aipp_id, version } = debugRes?.data;
    const requestBody = {
      aipp_id: aipp_id,
      app_version: version,
      offset: 0,
      limit: 100
    };
    setRequestInfo(requestBody);
    const chatRes = await getChatList(tenantId, requestBody);
    setData(chatRes?.data);
  }

  const items: MenuProps["items"] = [
    {
      key: "1",
      label: <div onClick={async () => {
        await deleteChat(tenantId, currentChat?.current?.chat_id);
        if (chatId === currentChat?.current?.chat_id) {
          dispatch(setChatId(null));
          dispatch(setChatList([]));
        }
        refreshList();
        // 删除成功提示
      }}>删除</div>,
    },
  ];

  const continueChat = async (chat_id, current_instance_id) => {
    dispatch(setChatRunning(false));
    const chatListRes = await getChatDetail(tenantId, chat_id, requestInfo);
    const role = chatListRes?.data?.msg_list?.[0]?.role;
    const list: [] = chatListRes?.data?.msg_list?.reverse()?.map((item, index) => {
      return index % 2 === 0 ?
        { content: item.content?.[0], type: 'send', checked: false, sendType: 'text' } :
        { content: item.content?.[0], type: 'recieve', checked: false, recieveType: 'text', instanceId: current_instance_id }
    });

    if (role === 'USER') {
      list.push({ content: null, type: 'recieve', checked: false, sendType: 'text', loading: true });
      dispatch(setChatRunning(true));
      setLastResSignal(lastResSignal + 1);
    }
    dispatch(setChatList(list));
    setOpen(false);
  }

  const getLastRes = async () => {
    const chatListRes = await getChatDetail(tenantId, chatId, requestInfo);
    const length = chatListRes?.data?.msg_list?.length;
    const role = chatListRes?.data?.msg_list?.[0]?.role;
    if (role === 'SYSTEM') {
      const lastRes = chatListRes?.data?.msg_list?.[0]?.content?.[0]; //最近的聊天在最前面
      const lastItem = { content: lastRes, type: 'recieve', checked: false, sendType: 'text' };
      chatList.pop();
      dispatch(setChatList([...chatList, lastItem]));
      dispatch(setChatRunning(false));
      setLastResSignal(0);
    } else {
      setLastResSignal(lastResSignal + 1);
    }
  }

  useEffect(() => {
    if (lastResSignal > 0) {
      setTimeout(() => {
        getLastRes();
      }, 3000);
    }
  }, [lastResSignal])

  useEffect(() => {
    if (appInfo?.id) {
      getAippId();
    }
  }, [appInfo])

  const getLastContext = async () => {
    const chatListRes = await getChatDetail(tenantId, chatId, requestInfo);
    const length = chatListRes?.data?.msg_list?.length;
    if (length % 2 === 0) {
      const lastItem = chatListRes?.data?.msg_list?.[length - 1];
      chatList.pop();
      chatList.push({ content: lastItem.content?.[0], type: 'recieve', checked: false, recieveType: 'text' });
    }
  }

  const onClearList = async()=>{
   await clearChatHistory(tenantId,appId);
   refreshList();
   dispatch(setChatList([]));
   dispatch(setChatId(null));
   setClearOpen(false);
   setOpen(false);
  }

  return (
    <Drawer
      destroyOnClose
      mask={false}
      title={
        <div className="history-title">
          <div className="history-title-left">
            <span>历史聊天</span>
            <div className="history-clear-btn" onClick={() => setClearOpen(true)}>
              <ClearOutlined style={{ fontSize: 14, marginLeft: 8 }} />
              <span className="history-clear-btn-text" >清空</span>
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
        <Input placeholder="搜索..." prefix={<SearchOutlined />} disabled />
      </div>
      <div className="history-wrapper">
        {data?.map((item) => (
          <div className="history-item" key={item?.chat_id} onClick={() => { currentChat.current = item; }}>
            <div className="history-item-content">
              <div className="history-item-header">
              <Tooltip placement="top" title={<span style={{color:'#4d4d4d'}}>{item?.chat_name}</span>} color='#ffffff'>
              <div className="history-item-title">{item?.chat_name?.length>10?item?.chat_name?.substring(0,10)+'...':item?.chat_name}</div>
              </Tooltip>
                <span
                  style={{ cursor: "pointer", color: "#1677ff" }}
                  onClick={() => { continueChat(item?.chat_id, item?.current_instance_id); dispatch(setChatId(item?.chat_id)); }}
                >
                  继续聊天
                </span>
              </div>
              <div className="history-item-desc">{item?.msg_list?.[0]}</div>
            </div>
            <div className="history-item-footer">
              <span>{getDaysAndHours(item?.update_time_timestamp, item?.current_time_timestamp)}</span>
              <Dropdown menu={{ items }} trigger={["click"]}>
                <EllipsisOutlined className="history-item-footer-more" />
              </Dropdown>
            </div>
          </div>
        ))}
      </div>
      <Modal title="警告" open={isClearOpen} onOk={onClearList} onCancel={()=>setClearOpen(false)}>
        <p>确认要清空所有聊天记录？删除后该数据无法恢复。</p>
      </Modal>
    </Drawer>
  );
};

export default HistoryChatDrawer;
