import { Flex } from "antd";
import React from "react";
import { Icons } from "../icons";

const EmptyItem = ({ text = '暂无数据' }) => {
  return (
    <>
      <Flex vertical align={"center"}>
        <Icons.emptyIcon  />
        <div>{text}</div>
      </Flex>
    </>
  );
}

export default EmptyItem;
