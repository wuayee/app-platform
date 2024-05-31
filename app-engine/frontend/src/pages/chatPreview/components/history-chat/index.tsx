import React, { useContext, useEffect, useRef, useState } from "react";
import { Drawer, Input, Dropdown } from "antd";
import type { MenuProps } from "antd";
import {
  SearchOutlined,
  EllipsisOutlined,
  ClearOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import "./style.scoped.scss";
import { deleteChat, getChatDetail, getChatList, tenantId } from "../../../../shared/http/chat";
import { AippContext } from "../../../aippIndex/context";
import { aippDebug } from "../../../../shared/http/aipp";
import { getDaysAndHours } from "../../../../common/dataUtil";

interface HistoryChatProps {
  open: boolean;
  setOpen: (val: boolean) => void;
  appInfo: any;
}

const HistoryChat: React.FC<HistoryChatProps> = ({ open, setOpen}) => {
  const currentChat = useRef(null);
  const { aippInfo, appId, tenantId,chatId,setChatList,setChatId,listRef ,setChatRunning} = useContext(AippContext);
  const [data, setData] = useState([]);
  const [requestInfo,setRequestInfo]=useState({
    aipp_id:'', version:'',offset:0, limit:100
    });

    const freshList=async()=>{
        const chatRes= await getChatList(tenantId,requestInfo);
        setData(chatRes?.data);
    };
    useEffect(()=>{
      if(open===true){
        freshList();
      }
     },[open]);

  const getAppId=async(aippInfo)=>{
    if(!aippInfo.id) return;
    const debugRes = await aippDebug(tenantId, aippInfo.id, aippInfo);
    let { aipp_id, version } = debugRes?.data;
    const requestBody={
      app_id: aipp_id,
      app_version:version,
      offset:0,
      limit:100
  };
  setRequestInfo(requestBody);
  const chatRes= await getChatList(tenantId,requestBody);
  setData(chatRes?.data);
}

const items: MenuProps["items"] = [
  {
    key: "1",
    label: <div onClick={async()=>{
     await deleteChat(tenantId,currentChat?.current?.chat_id);
     if(chatId===currentChat?.current?.chat_id)
     {
      setChatId(null);
      setChatList(() => {
        let arr = [];
        listRef.current = arr;
        return arr;
      });
     }
     // 删除成功提示
     getAppId(aippInfo);
    }}>删除</div>,
  },
];

  const continueChat=async(chatItem)=>{
    setChatRunning(false);
    const chatListRes =  await getChatDetail(tenantId,chatItem?.chat_id,requestInfo);
    setChatId(chatListRes?.data?.chat_id);
    const list= chatListRes?.data?.msg_list?.reverse()?.map((item,index)=>{
     return index%2===0? 
     {content: item.content?.[0], type: 'send', checked: false, sendType: 'text'}:
     {content: item.content?.[0], type: 'recieve', checked: false, recieveType: 'text'}
    })

    setChatList(() => {
      let arr = [...list];
      listRef.current = arr;
      return arr;
    });
    setOpen(false);
  }

  useEffect(() => {
    console.log(aippInfo)
    if(aippInfo){
    getAppId(aippInfo);
    }
  }, [aippInfo])

  return (
    <Drawer
      destroyOnClose
      mask={false}
      title={
        <div className="history-title">
          <div className="history-title-left">
            <span>历史聊天</span>
            <div className="history-clear-btn" hidden onClick={() => setData([])}>
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
        <Input placeholder="搜索..." prefix={<SearchOutlined />} disabled/>
      </div>
      <div className="history-wrapper">
        {data.map((item) => (
          <div className="history-item" key={item?.chat_id} onClick={()=>{currentChat.current=item;}}>
            <div className="history-item-content">
              <div className="history-item-header">
                <div className="history-item-title">{item?.chat_name}</div>
                <span
                  style={{ cursor: "pointer", color: "#1677ff" }}
                  onClick={() => {continueChat(item);}}
                >
                  继续聊天
                </span>
              </div>
              <div className="history-item-desc">{item?.msg_list?.[0]}</div>
            </div>
            <div className="history-item-footer">
              <span>{getDaysAndHours( item?.update_time_timestamp,item?.current_time_timestamp)}</span>
              <Dropdown menu={{ items }} trigger={["click"]}>
                <EllipsisOutlined className="history-item-footer-more"/>
              </Dropdown>
            </div>
          </div>
        ))}
      </div>
    </Drawer>
  );
};

export default HistoryChat;
