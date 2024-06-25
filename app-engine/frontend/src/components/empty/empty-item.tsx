import { Flex } from "antd";
import React from "react";
import { Icons } from "../icons";

const EmptyItem = ({ text = '暂无数据' }) => {
  return (
    <>
      <Flex vertical align={"center"}>
        <Icons.emptyIcon  />
        <div style={{ margin :'12px 0' }}>{text}</div>
      </Flex>
    </>
  );
}

export default EmptyItem;
