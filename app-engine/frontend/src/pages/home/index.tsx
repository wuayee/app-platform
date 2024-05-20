import { Button } from "antd";
import React, { useState } from "react";
import StarApps from "./components/star-apps";
import HistoryChat from "./components/history-chat";

const Home: React.FC = () => {
  const [openStar, setOpenStar] = useState(false);
  const [openHistory, setOpenHistory] = useState(false);

  return (
    <div className="aui-fullpage">
      <div className="aui-header-1">
        <div className="aui-title-1">首页</div>
      </div>
      <div className="aui-block">
        <Button onClick={() => setOpenStar(true)}>应用百宝箱</Button>
        <Button onClick={() => setOpenHistory(true)}>历史聊天</Button>
        <div />
      </div>
      <StarApps open={openStar} setOpen={setOpenStar} />
      <HistoryChat open={openHistory} setOpen={setOpenHistory} />
    </div>
  );
};

export default Home;
