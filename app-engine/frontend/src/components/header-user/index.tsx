import React from 'react';
import { KnowledgeIcons } from '../icons';

const HeaderUser = ()=> {
  return (
    <>
      <div style={{
        position: 'fixed',
        right: 30,
        top: 20,
        display: 'flex',
        alignItems: 'center',
        gap: 23
      }}>
        <KnowledgeIcons.dark/>
        <KnowledgeIcons.alarm/>
        <KnowledgeIcons.info/>
        <div style={{
          fontSize: 16,
          display: 'flex',
          alignItems: 'center'
        }}>
          <KnowledgeIcons.user/>
          <span style={{
            marginLeft: 8
          }}>Jasper</span>
        </div>
      </div>
    </>
  )
}

export {
  HeaderUser
};