
import React from 'react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

const TextEditor = ({ text, setText }) => {
  const quillOption = {
    toolbar: {
      container: [
        [{ 'size': ['small', false, 'large', 'huge'] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ list: 'ordered'}, { list: 'bullet'}],
        [{ align: []}],
        [{ color: []}, { background: []}],
        ['image']
      ]
    }
  }
  const handleChange = (content) => {
    setText(content);
  };
  return <>{(
    <div style={{ height: '280px' }}>
      <ReactQuill 
        style={{ height: '240px' }}
        theme='snow' 
        value={text} 
        onChange={handleChange} 
        modules={quillOption}
      />
    </div>
  )}</>
};


export default TextEditor;
