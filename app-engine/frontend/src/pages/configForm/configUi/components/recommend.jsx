
import React, { useEffect, useState, useRef } from 'react';
import { Form, Input, Button } from 'antd';
import { PlusCircleOutlined  } from '@ant-design/icons';
import { DeleteIcon } from '@assets/icon';
import '../styles/recommends.scss';

const Recommend = (props) => {
  const { updateData, recommendValues } = props;
  const [ list, setList ] = useState([]);
  const listCrrent = useRef([]);

  useEffect(() => {
    if (recommendValues) {
      setList(() => {
        listCrrent.current = JSON.parse(JSON.stringify(recommendValues));
        return listCrrent.current;
      });
    };
  }, [props.recommendValues]);
  const handleDeleteIns = (index) => {
    listCrrent.current.splice(index, 1);
    setList([ ...listCrrent.current ]);
    saveRecommend();
  }
  const addRecommend = () => {
    listCrrent.current = [ ...listCrrent.current, '' ]
    setList(listCrrent.current);
  }
  const handleChange = (val, index) => {
    listCrrent.current[index] = val;
    setList([ ...listCrrent.current ]);
  }
  const saveRecommend = () => {
    let list = listCrrent.current.map(item => item.trim());
    let arr = list.filter(item => item.length);
    updateData(arr, "recommend");
  }
  return <>{(
    <div className="control-container">
      <div className="control">
        <div className="control-header ">
          <div className="control-title">
            <span>首次与应用对话时的推荐问题，最多创建3个</span>
          </div>
        </div>
        <Form.Item
          name="recommend"
          style={{
            marginTop: "10px",
            display: "block",
          }}
        >
          <div className="recommend-add">
            <Button type="link" disabled={list.length === 3} icon={<PlusCircleOutlined />} onClick={addRecommend}>创建</Button>
          </div>
          {
            list.map((item, index) => {
              return(
                <div className="recommend-container" key={index}>
                  <div className="card-title">
                    <span className="left">
                      问题 {index + 1}
                    </span>
                    <span className="right">
                      <span onClick={() => handleDeleteIns(index)}><DeleteIcon /></span>
                    </span>
                  </div>
                  <div className="card-prompt">
                    <Input placeholder="请输入" 
                      value={item} 
                      onChange={(e) => handleChange(e.target.value, index)}
                      onBlur={saveRecommend}
                    />
                  </div>
                </div>
              )
            })
          }
        </Form.Item>
      </div>
    </div>
  )}</>
};


export default Recommend;
