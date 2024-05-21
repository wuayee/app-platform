import React from 'react';
import { KnowledgeIcons, Icons } from '../icons';

const HeaderFolderMenu = ({openMenuFunc, style}: {openMenuFunc: any, style: any})=> {
  const openMenu = ()=> {
    openMenuFunc()
  }
  return (
    <>
      <div style={{
        position: 'fixed',
        left: 16,
        top: 20,
        display: 'flex',
        alignItems: 'center',
        gap: 23,
        ...style
      }}>
        <KnowledgeIcons.menuFolder style={{
          cursor: 'pointer',
        }} onClick={openMenu}/>
        <div style={{
          fontSize: 16,
          display: 'flex',
          alignItems: 'center'
        }}>
          <Icons.logo width = {24} height={24} />
          <span style={{
            marginLeft: 8
          }}>APP Engine</span>
        </div>
      </div>
    </>
  )
}

export {
  HeaderFolderMenu
};