import styled from 'styled-components';

export const ConfigFormWrap = styled.div`
  height: 100%;
  border-right: 1px solid #e4e4e7;
  width: 35%;
  .config-form {
    max-height: 100%;
    overflow: auto;
    width: 100%;
    background-color: #ffffff;
    box-sizing: border-box;
    padding: 15px 25px;
    border-top-left-radius: 8px;
    .config-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .config-name {
        font-size: .875rem;
        font-weight: 700;
      }
      .config-btn {
        cursor: pointer;
        font-size: 12px;
        color: #252B3A;
      }
    }
  }
  .config-form-elsa {
    width: 350px;
    box-shadow: 5px 0 10px -10px rgba(0,0,0,.2);
  }
  .icon {
        width: 1.2em;
        height: 1.2em;
        line-height: 1.2rem;
        vertical-align: -.1rem;
        fill: currentColor;
        overflow: hidden;
    }
`;
