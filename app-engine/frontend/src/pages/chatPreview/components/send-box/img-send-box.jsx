
import React from 'react';
import { useParams } from 'react-router-dom';
import { httpUrlMap } from '@shared/http/httpConfig';
import fileImg from '@assets/images/ai/file2.png';

const { AIPP_URL } = httpUrlMap[process.env.NODE_ENV];
const ImgSendBox = (props) => {
  const { content, sendType } = props;
  const { tenantId } = useParams();
  let { file_name, file_path } = JSON.parse(content);

  function setFileDom(type) {
    switch (type) {
      case 'img':
        return <img className="img-send-item" src={`${AIPP_URL}/${tenantId}/file?filePath=${file_path}&fileName=${file_name}`}/>
        break;
      case 'audio':
        return <audio className="audio-file" src={`${AIPP_URL}/${tenantId}/file?filePath=${file_path}&fileName=${file_name}`} autoPlay></audio>
        break;
      case 'video':
        return <video src={`${AIPP_URL}/${tenantId}/file?filePath=${file_path}&fileName=${file_name}`} autoPlay></video>
        break;
      default:
        return  (<div className="file-div-item">
                  <img className="file-item" src={fileImg}/>
                  <span className="file-text" title={file_name}>{file_name}</span>
                </div>)
    }
  }
  return <>{(
    <div className="img-send-box">
      { setFileDom(sendType) }
    </div>
  )}</>
}

export default ImgSendBox;
