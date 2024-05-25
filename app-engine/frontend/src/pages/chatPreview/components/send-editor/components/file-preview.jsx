
import React, { useEffect, useState } from 'react';
import { ClearFileIcon } from '@assets/icon';
import robot from "@assets/images/ai/robot1.png";
import '../styles/file-preview.scss';

const FilePreview = ({ cancleFile }) => {
  return <>{(
    <div className="file-preview">
      <div className="preview-inner">
        <img src={robot} alt="" />
        <span className="delete-icon">
          <ClearFileIcon onClick={() => cancleFile()}/>
        </span>
      </div>
    </div>
  )}</>
};


export default FilePreview;
