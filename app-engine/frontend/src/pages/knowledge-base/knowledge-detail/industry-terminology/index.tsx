import React, { useEffect, useState } from 'react';
import { Form }from 'antd';
import { Button, Table } from 'antd';
import type { TableProps } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useSearchParams } from "react-router-dom";

import BreadcrumbSelf from '../../../../components/breadcrumb';
import { KnowledgeIcons } from '../../../../components/icons';
import { getKnowledgeTableById } from '../../../../shared/http/knowledge';
import './index.scoped.scss';
import KnowLedgeTable from '../../../../components/knowledge-detail-table/table-table';


const IndustryTerminology = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const rowid = searchParams.get("rowid");
  const id = searchParams.get("id");

  // 行信息
  const [rowInfo, setRowInfo] = useState<any>(null)

  // 获取知识表信息
  const getTableInfo = async ()=> {
    try {
      if(!rowid) return;
      let res = await getKnowledgeTableById(rowid);
      setRowInfo(res)
    } catch (error) {
      
    }
  }

  useEffect(()=> {
    if(rowid) {
      getTableInfo();
    }
  }, []);

  const importClick = () => {
    navigate(`/knowledge-base/knowledge-detail/import-data?id=${id}&tableid=${rowid}&tabletype=${(rowInfo?.format || '').toLowerCase()}`);
  }


  return (
    <>
    <div className='aui-fullpage'>
    <div className='aui-header-1'>
      <div className='aui-title-1'>
        <BreadcrumbSelf searchFlag={true} currentLabel={ rowInfo?.name }></BreadcrumbSelf>
      </div>
    </div>
      <div className='aui-block' style={{
        display: 'flex',
        flexDirection: 'column',
        gap: 8
      }}>
        <div className='knowledge-detail-header' style={{
          display: 'flex', 
          gap: 30,
          alignItems: 'center',
          paddingBottom: 30,
          borderBottom: '1px solid #CDD7E6'
        }}>
            <div className='detail-header-return' style={{
              cursor: 'pointer'
            }} >
              {<KnowledgeIcons.leftArrow/>}
            </div>
            <div className='detail-header-info' style={{
                flex: 1,
                display: 'flex'
              }}>
                {rowInfo?.format === 'TEXT'? <KnowledgeIcons.tableText width={57} height={57}/> : <KnowledgeIcons.tableXlsx width={57} height={57}/>}
              <div className='detail-info-title' style={{
                fontSize: 20,
                height: 30,
                color: 'rgba(5, 5, 5, .96)',
                display:'flex',
                flexDirection: 'column',
                gap: 10
              }}>
                <span>{rowInfo?.name || ''}</span>
                
                  <div className='detail-info-userinfo' style={{
                    fontSize: 14,
                    color: 'rgba(105, 105, 105, .96)',
                    display: 'flex',
                    gap: 10
                  }}>
                      <div className='tag'>{rowInfo?.serviceType || ''}</div>
                      <div className='tag' style={{
                        backgroundColor: '#E6F3FA'
                      }}>{rowInfo?.recordNum ?? 0 }</div>
                  </div>
              </div>
              
            </div>
            <div className='detail-header-add'>
              <Button type="primary" onClick={importClick}  style={{
                borderRadius: 4,
                backgroundColor: '#2673E5',
                display: 'flex',
                alignItems: 'center'
              }}>{<KnowledgeIcons.import/>} 导入</Button>
            </div>
        </div>
        <div className='knowledge-table'>
            {rowInfo && <KnowLedgeTable type={(rowInfo?.format || '').toLowerCase()} reposId={rowInfo.repositoryId} id={rowInfo.id}/>}
            
        </div>
      <div />
    </div>
    </div>
    </>
  )
}
export default IndustryTerminology;