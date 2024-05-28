import styled from 'styled-components';

export const ConfigFormWrap = styled.div`
  height: 100%;
  width: 500px;
  flex-shrink: 0;
  .config-form {
    height: 100%;
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
      margin-bottom: 12px;
      .config-left {
        span {
          font-size: 14px;
          font-weight: 400;
          color: rgb(77, 77, 77);
          cursor: pointer;
          margin-right: 24px;
        }
        .active {
          color: #2673E5;
        }
      }
      .config-btn {
        cursor: pointer;
        font-size: 12px;
        color: #2673e5;
        display: flex;
        align-item: center;
        svg {
          margin-right: 6px;
        }
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
