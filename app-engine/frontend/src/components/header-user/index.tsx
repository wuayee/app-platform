import React from "react";
import { KnowledgeIcons } from "../icons";

const HeaderUser = () => {
  return (
    <div
      style={{
        display: "flex",
        gap: 16,
        justifyContent: "end",
        alignItems: "center",
        marginRight: 24,
        height: 48,
      }}
    >
      <KnowledgeIcons.dark />
      <KnowledgeIcons.alarm />
      <KnowledgeIcons.info />
      <div
        style={{
          fontSize: 16,
          display: "flex",
          alignItems: "center",
        }}
      >
        <KnowledgeIcons.user />
        <span
          style={{
            marginLeft: 8,
          }}
        >
          Jasper
        </span>
      </div>
    </div>
  );
};

export { HeaderUser };
