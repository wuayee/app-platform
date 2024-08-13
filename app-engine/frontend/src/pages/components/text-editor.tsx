
import React, { forwardRef, useImperativeHandle, useEffect } from 'react';
import tinymce from 'tinymce';
import 'tinymce/models/dom/index.js';
import 'tinymce/icons/default/index.js';
import 'tinymce/plugins/image/index.js';
import 'tinymce/plugins/table/index.js';
import 'tinymce/plugins/lists/index.js';
import 'tinymce/skins/ui/oxide/skin.min.css';
import 'tinymce/themes/silver/theme.min.js';

const TextEditor = forwardRef((props, ref) => {

  // 对外暴露方法
  useImperativeHandle(ref, () => {
    return {
      handleChange: () => {
        let content = tinymce.activeEditor?.getContent();
        return content
      }
    }
  });

  useEffect(() => {
    tinymce.init({
      selector: '#publish-editor',
      plugins: 'lists image table',
      language: 'zh_CN',
      language_url: `./src/assets/tinymce/lang/zh-CN.js`,
      skin_url: `./src/assets/tinymce/skins/ui/oxide`,
      content_css: `./src/assets/tinymce/skins/content/default/content.css`,
      height: 260,
      menubar: false,
      statusbar: false,
      toolbar: 'formatselect fontsizeselect | forecolor backcolor bold italic underline strikethrough link | alignleft aligncenter alignright | bullist numlist | table image',
      images_upload_handler: (blobInfo, progress) => {
        return new Promise((resolve, reject) => {
          resolve(`data:${blobInfo.blob().type};base64,${blobInfo.base64()}`);
        });
      }
    })
    return () => {
      tinymce.remove();
    }
  }, [])
  return <>{(
    <div style={{ height: '280px' }}>
      <textarea id='publish-editor'></textarea>
    </div>
  )}</>
});

export default TextEditor;
