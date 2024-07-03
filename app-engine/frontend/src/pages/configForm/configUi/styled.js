import styled from 'styled-components';

export const ConfigWrap = styled.div`
    width: 100%;
    height: calc(100% - 70px);
    overflow: auto;
    font-size: 12px;
    padding: 15px 25px;
    padding-top: 0;
    .ant-form-item-control {
      width: 100%;
    }
    .ant-collapse .ant-collapse-item .ant-collapse-header {
      padding-left: 0;
      padding-bottom: 8px;
      padding-top: 0;
    }
    .ant-select {
      font-size: 12px;
      color: #252B3A;
    }
    .ant-input-number .ant-input-number-input {
      color: #252B3A;
    }

    .ant-select-single .ant-select-selector {
      font-size: 12px;
      color: #252B3A;
    }

    .ant-select-outlined.ant-select-multiple .ant-select-selection-item {
      background-color: rgb(248 248 248);
    }

    .ant-input {
      font-size: 12px;
      color: #252B3A;
    }

    .ant-card {
      background: #fcfcfd;
      color: #252B3A;
    }

    .ant-form-item-label > label {
      font-size: 12px;
    }

    .ant-form-item .ant-form-item-explain {
      font-size: 12px;
    }
    .ant-collapse-header {
      padding-left: 0;
    }
    .control-container {
      border-radius: 8px;
      position: relative;
      color: #252B3A;
    }
    .llm-container {
      border-radius: 12px;
      border: 0;
    }

    .control-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
      margin-top: 5px;
      font-size: 12px;
      .control-title {
        display: flex;
        align-items: center;
        font-size: 12px;
        color: #808080;
      }

      &.c-header {
        position: absolute;
        left: 8px;
        top: -16px;
        background: white;
        padding: 0 10px 0 5px;
      }
    }
    .control-inner {
      .item {
        display: flex;
        height: 40px;
        align-items: center;
        padding: 0 16px;
        border-radius: 4px;
        border: 1px solid rgb(230, 230, 230);
        justify-content: space-between;
        margin-bottom: 12px;
        .item-left {
          display: flex;
          align-items: center;
          img {
            margin-right: 8px;
          }
        }
      }
    }
    .plus-icon {
      position:relative;
      margin-top:2px;
      color: #047bfc;
      vertical-align: 0rem ;
    }

    .title-icon {
      margin-right:6px;
    }

    .ant-select:not(.ant-select-customize-input) .ant-select-selector {
      position: relative;
      background-color: #fff;
      border: 1px solid #e4e4e7;
      border-radius: 4px;
      transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    }
    .full-border .ant-select-selector{
      border-width: 1px !important;
    }

    .no-right-radius .ant-select-selector{
      border-radius: 8px 0px 0px 8px !important;
    }

    .ant-input, .ant-input-number {
      border: 1px solid #e4e4e7;
      border-radius: 8px;
      &.no-left-radius {
        border-radius: 0px 8px 8px 0;
      }
      &.no-right-radius {
        border-radius: 8px 0px 0px 8px;
      }
    }
    .ant-form-item {
      margin-bottom: 12px;
      color: #252B3A;
    }
    .ant-select-multiple .ant-select-selection-item {
      border-radius: 8px;
    }
    .empty-container {
      display: flex;
      align-items: center;
      .ant-btn {
        padding: 0;
        margin: 0;
      }
    }
    .ant-card-bordered {
        box-shadow: 0 2px 7px 0px rgba(37, 43, 58, 0.12);
        border: 1px solid #f5f5f5;
    }
    .ant-card-bordered:hover {
        box-shadow: 0px 3px 12px 3px rgba(0, 0, 0, 0.15);
    }
    .conversation-switch {
      margin-left: 10px;
    }
  .ant-switch.ant-switch-checked {
    background-color: #0478fc;
  }
  .ant-switch.ant-switch-checked:hover:not(.ant-switch-disabled) {
    background-color: #0478fc;
  }
`;

export const InspirationWrap = styled.div`
  .plus-icon {
    position:relative;
    color: #047bfc;
    vertical-align: 0rem ;
    margin: 0 0 0 10px;
    font-size: 20px;
  }
  .ant-input {
    font-size: 12px;
  }
  .ant-select-single .ant-select-selector {
    font-size: 12px;
  }
  .ant-table-wrapper .ant-table {
    font-size: 12px;
  }
  .ant-btn-primary {
    background-color: #047bfc;
  }
`;
