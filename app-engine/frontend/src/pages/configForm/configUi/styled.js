import styled from 'styled-components';

export const ConfigWrap = styled.div`
    width: 100%;
    // background-color: #FBFBFC;
    // min-height: 100%;
    font-size: 12px;
    margin: 20px 0 20px 0;
    border-radius: 8px;
    font-weight: 600;
    .ant-form-item-control {
        width: 100%;
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

    .control-container {
        border: 1px solid rgb(228 228 231);
        border-radius: 8px;
        margin-bottom: 2rem;
        padding: 20px 5px 5px 5px;
        position: relative;
        color: #252B3A;
    }
    .llm-container {
        background: radial-gradient(circle at 100% 100%, #fcfcfd 0, #fcfcfd 10px, transparent 0) 0 0 / 12px 12px no-repeat, radial-gradient(circle at 0 100%, #fcfcfd 0, #fcfcfd 10px, transparent 0) 100% 0 / 12px 12px no-repeat, radial-gradient(circle at 100% 0, #fcfcfd 0, #fcfcfd 10px, transparent 0) 0 100% / 12px 12px no-repeat, radial-gradient(circle at 0 0, #fcfcfd 0, #fcfcfd 10px, transparent 0) 100% 100% / 12px 12px no-repeat, linear-gradient(#fcfcfd, #fcfcfd) 50% 50% / calc(100% - 4px) calc(100% - 24px) no-repeat, linear-gradient(#fcfcfd, #fcfcfd) 50% 50% / calc(100% - 24px) calc(100% - 4px) no-repeat, radial-gradient(at 100% 100%, rgb(22 119 255) 0, transparent 70%), radial-gradient(at 100% 0, rgb(22 119 255) 0, transparent 70%), radial-gradient(at 0 0, rgb(141 188 255) 0, transparent 70%), radial-gradient(at 0 100%, rgb(141 188 255) 0, transparent 70%);
        border-radius: 12px;
        border: 0;
    }
    .control {
        padding: 0 10px 0 10px;
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
        }

        &.c-header {
            position: absolute;
            left: 8px;
            top: -16px;
            background: white;
            padding: 0 10px 0 5px;
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
        border-radius: 0px;
        transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
        border-width: 0 0 1px 0;
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
    .inspiration-container {
        width: 100%;
        font-size: 12px;
        display: flex;
        justify-content: space-between;
    }
    .empty-container {
        display: flex;
        justify-content: center;
        align-items: center;
        .ant-btn {
            padding: 0;
            margin: 0;
        }
    }
    .card-title {
        cursor: pointer;
        font-size: 12px;
        max-width: 100px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }
    .card-title:hover {
        color: #047BFC;
    }
    .ant-card-bordered {
        box-shadow: 0 2px 7px 0px rgba(37, 43, 58, 0.12);
        border: 1px solid #f5f5f5;
    }
    .ant-card-bordered:hover {
        box-shadow: 0px 3px 12px 3px rgba(0, 0, 0, 0.15);
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
