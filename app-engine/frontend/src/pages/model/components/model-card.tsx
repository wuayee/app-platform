import React, { ReactElement } from 'react';
import { Card } from 'antd';
import type { MenuProps } from 'antd';
import { Button, Dropdown, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { Icons } from '../../../components/icons';
import { ModelItem } from '../cards-tab';
import { deleteModelByName, getModelList } from '../../../shared/http/model';

const ModelCard = ({ modelItem, setModelItems, openModify }: { modelItem: ModelItem, setModelItems: (val: Array<any>) => void, openModify: Function }) => {
  const operatorItems: MenuProps['items'] = [
    {
      key: 'delete',
      label: (
        <a style={{ width: 50 }} onClick={() => deleteModel()}>
          删除
        </a>
      ),
      disabled: modelItem.status === 'undeployed'
    },
    {
      key: 'edit',
      label: (
        <a style={{ width: 50 }} onClick={() => modifyModel()}>
          修改
        </a>
      ),
      disabled: modelItem.status === 'undeployed'
    },
  ];
  // 路由
  const navigate = useNavigate();

  const toModelDetail = (id: string) => {
    navigate('/model/detail', { state: { modelId: id } });
  };
  const deleteModel = () => {
    const deleteParams = {
      data: {
        name: modelItem.name,
      },
    };
    deleteModelByName(deleteParams).then((res) => {
      if (res && res.code === 200) {
        message.success('模型删除成功');
        getModelList().then((res) => {
          if (res) {
            setModelItems(res.llms);
          }
        });
      } else {
        message.error('模型删除失败');
      }
    });
  };

  const modifyModel = () => {
    openModify(modelItem);
  }

  return (
    <Card
      style={{
        width: 376,
        background: 'url(/src/assets/images/knowledge/knowledge-background.png)',
        height: 240,
      }}
    >
      {/* 头部区域 */}
      <div
        style={{
          height: 40,
        }}
      >
        <div>
          <div
            style={{
              fontSize: 20,
              color: 'rgba(5, 5, 5, .96)',
              cursor: 'pointer',
              width: 200,
            }}
            onClick={() => toModelDetail(modelItem.id)}
          >
            {modelItem.name}
          </div>
        </div>
        <div
          style={{
            marginLeft: 150,
          }}
        ></div>
        <div
          style={{
            display: 'flex',
            flexDirection: 'row-reverse',
          }}
        >
          <div
            style={{
              fontSize: 14,
              color: 'rgba(5, 5, 5, .96)',
              marginTop: -28,
            }}
          >
            {modelItem.status}
          </div>
          <div style={{ marginTop: -25 }}>
            {modelItem.status === 'healthy' && <img src='/src/assets/images/model/healthy.svg' />}
            {modelItem.status === 'unhealthy' && (
              <img src='/src/assets/images/model/unhealthy.svg' />
            )}
            {modelItem.status === 'undeployed' && (
              <img src='/src/assets/images/model/undeployed.svg' />
            )}
          </div>
        </div>
      </div>
      <div
        style={{
          display: 'flex',
          gap: '16px',
          height: 30,
        }}
      >
        <div
          style={{
            fontSize: 14,
            color: ' rgb(26, 26, 26);',
            background: 'rgb(242, 242, 242)',
            borderWidth: 1,
            borderStyle: 'dashed',
            borderColor: 'rgb(221, 221, 221)',
            borderRadius: '4px',
            padding: '1px 8px 1px 8px',
          }}
        >
          {modelItem.orgnization}
        </div>
        <div
          style={{
            fontSize: 14,
            color: ' rgb(26, 26, 26);',
            background: 'rgb(242, 242, 242)',
            borderWidth: 1,
            borderStyle: 'dashed',
            borderColor: 'rgb(221, 221, 221)',
            borderRadius: '4px',
            padding: '1px 8px 1px 8px',
          }}
        >
          {modelItem.model}
        </div>
      </div>

      {/* 描述 */}
      <div
        title={modelItem.description}
        style={{
          display: '-webkit-box',
          textOverflow: 'ellipsis',
          overflow: 'hidden',
          WebkitLineClamp: 3,
          WebkitBoxOrient: 'vertical',
          fontSize: '14px',
          lineHeight: '22px',
          textAlign: 'justify',
          height: 66,
          marginTop: 16,
        }}
      >
        {modelItem.description}
      </div>
      <div
        style={{
          marginTop: 16,
          width: '330px',
          border: '1px solid rgb(229, 239, 252)',
        }}
      ></div>
      {/* 底部 */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'flex-end',
          marginTop: 16,
        }}
      >
        <div>
          <Dropdown
            menu={{
              items: operatorItems,
            }}
            placement='bottomLeft'
            trigger={['click']}
          >
            <div
              style={{
                cursor: 'pointer',
              }}
            >
              <Icons.more width={20} />
            </div>
          </Dropdown>
        </div>
      </div>
    </Card>
  );
};

export default ModelCard;
