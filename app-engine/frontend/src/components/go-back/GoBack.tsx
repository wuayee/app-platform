import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Icons } from '../icons';

const GoBack: React.FC<{path,title}> = ({path,title}) => {
  const navigate=useNavigate();
  return(
  <div
    onClick={(): void => {
      navigate(path);
    }}
    style={{display:'flex'}}
  >
    <div ><Icons.Left /></div>
    <span style={{marginLeft:8,marginTop:-3}}>{title}</span>
  </div>
)};

export default GoBack;