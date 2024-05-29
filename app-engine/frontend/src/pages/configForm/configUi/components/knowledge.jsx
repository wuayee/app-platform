
import React, { useEffect, useState, useRef, useContext } from 'react';
import { getKnowledges } from "@shared/http/appBuilder";
import { Form, Select, Button } from 'antd';
import { AippContext } from '../../../aippIndex/context';
import AddKnowledge from  './add-knowledge'

const Knowledge = (props) => {
  const { knowledge, updateData } = props;
  const [ showKnowControl, setShowKnowControl ] = useState(true);
  const [ knowledgeOptions, setKnowledgeOptions ] = useState(null);
  const [ knows, setKnows] = useState(null);
  const [ open, setOpen] = useState(false);
  const { tenantId } = useContext(AippContext);
  const searchName = useRef('')
  const onArrowClick = (value, func) => {
    func(!value);
  }

  const handleSearch = (value) => {
    searchName.current = value;
    handleGetKnowledgeOptions();
  }

  const handleClose = (open) => {
    if (!open) {
      searchName.current = '';
      handleGetKnowledgeOptions();
    }
  }

  const handleGetKnowledgeOptions = () => {
    const params = {
      tenantId,
      pageNum: 1,
      pageSize: 10,
      name: searchName.current
    };
    getKnowledges(params).then((res) => {
      if (res.code === 0) {
        setKnowledgeOptions(res.data.items);
      }
    })
  }

  const handleChange = (value, option) => {
    setKnows(value);
    updateData(option, "knowledge");
  }

  useEffect(() => {
    handleGetKnowledgeOptions();
  }, [])

  useEffect(() => {
    setKnows(knowledge?.map(item => item.id));
  }, [knowledge])

  return (
    <>
      <div className="control-container">
        <div className="control">
          <div className="control-header">
            <div className="control-title">
              <Button onClick={ () => setOpen(true) }>添加</Button>
            </div>
          </div>
            <Form.Item
              name="knowledge"
              label=""
              style={{
                marginTop: "10px",
                display: showKnowControl ? "block":"none",
              }}
            >
              <div>
                <Select
                  mode="multiple"
                  showSearch
                  allowClear
                  placeholder="选择合适的知识库"
                  options={knowledgeOptions}
                  onSearch={handleSearch}
                  value={knows}
                  onFocus={handleGetKnowledgeOptions}
                  onDropdownVisibleChange={handleClose}
                  fieldNames={{
                      label: "name",
                      value: "id"
                  }}
                  filterOption={(input, option) => true}
                  onChange={(value, option) => handleChange(value, option)}>
                </Select>
              </div>
            </Form.Item>
        </div>
      </div>
      <AddKnowledge open={open} setOpen={setOpen} />
    </>
  )
};


export default Knowledge;
