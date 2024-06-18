import React, {useEffect, useRef, useState} from 'react';
import { Spin } from 'antd';
import { MoreIcon, HistoryIcon } from '@assets/icon';
import knowledgeBase from '@assets/images/knowledge/knowledge-base.png';
import '../styles/referencing-app.scss'
import { getAippList } from "../../../../../shared/http/aipp";
import { useAppSelector } from "../../../../../store/hook";

const ReferencingApp = (props) => {
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const { atItemClick, atClick, searchKey } = props;
  const [ appArr, setAppArr ] = useState([]);
  const [ tableLoading, setTableLoading ] = useState(false);
  const [ total, setTotal ] = useState(1);
  const pageNo = useRef(1);

  useEffect(() => {
    getAppList();
  }, [searchKey])

  // 应用点击回调
  const itemClick = (item) => {
    atItemClick(item);
  }
  // 更多应用
  const moreClick = (e) => {
    e.stopPropagation();
    atClick();
  }
  // 拿取应用列表
  const getAppList = async () => {
    setTableLoading(true);
    try {
      const res = await getAippList(tenantId, {}, 3, (pageNo.current - 1) * 3, searchKey);
      if (res.code === 0) {
        let data = res.data.results;
        setAppArr(data);
        setTotal(res.data.range.total);
      }
    } finally {
      setTableLoading(false);
    }
  }
  return <>{(
    <div className="at-content" onClick={(e) => e.stopPropagation()}>
      <div className="at-head">
        <span className="left">收藏的应用</span>
        <span className="right"  onClick={moreClick}>
          <MoreIcon />
          <span>更多应用</span>
        </span>
      </div>
      <Spin spinning={tableLoading}>
        <div className="at-content-inner">
          {
            appArr.map((item, index) => {
              return (
                <div className="at-list-item" key={index} onClick={() => itemClick(item)}>
                  <div className="left">
                    <span>
                      {item.attributes?.icon ? <img src={item.attributes.icon} /> : <img src={knowledgeBase} />}
                    </span>
                    <span className="name">{item.name}</span>
                    <span className="description">{item.attributes.description}</span>
                  </div>
                  <div className="right">
                    <HistoryIcon />
                  </div>
                </div>
              )
            })
          }
        </div>
      </Spin>
    </div>
  )}</>
};

export default ReferencingApp;
