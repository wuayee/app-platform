import QuestionClar from "./index.jsx";
import {v4 as uuidv4} from "uuid";
import React from 'react';

export const questionClarComponent = (jadeConfig) => {
  const self = {};

  /**
   * 必须.
   */
  self.getJadeConfig = () => {
    return jadeConfig ? jadeConfig : {
      "inputParams": [
        {
          id: uuidv4(), name: "instanceId", type: "String", from: "Reference", value: ["instanceId"]
        },
        {
          id: uuidv4(), name: "questionClarResult", type: "String", from: "Reference", value: ["output", "supplement"]
        },
      ],
      "outputParams": [
        {
          id: uuidv4(), name: "questionClarResult", type: "String", from: "Input", value: ""
        }
      ]
    }
  };

  /**
   * 必须.
   */
  self.getReactComponents = () => {
    return (<><QuestionClarComponent/></>);
  };

  /**
   * 必须.
   */
  self.reducers = (data, action) => {

  };

  return self;
};

const QuestionClarComponent = () => {
  return (<>
    <QuestionClar/>
  </>)
};
