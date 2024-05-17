import InterviewQuestions from "./InterviewQuestions.jsx";
import {v4 as uuidv4} from "uuid";

export const interviewQuestionsComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : [
            {
                id: uuidv4(),
                name: "output",
                type: "Object",
                from: "value",
                value: [
                    {id: uuidv4(), type: "Array", from: "value", value: "interviewResult"},
                ]
            }
        ]
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><InterviewQuestionsComponent /></>);
    };

    /**
     * 必须.
     */
    self.reducers = (data, action) => {

    };

    return self;
};

const InterviewQuestionsComponent = () => {
    return (<>
        <InterviewQuestions />
    </>)
};
