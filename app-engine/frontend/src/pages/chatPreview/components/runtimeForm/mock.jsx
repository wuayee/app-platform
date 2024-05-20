import React, { useEffect, useState } from 'react';
import ManageCubeCreateReport from './ManageCubeCreateReport.jsx';
import InterviewQuestions from './InterviewQuestions.jsx';

import {getMockChart} from "../../../../shared/http/appBuilder";
import FileContent from "./FileContent.jsx";

const Mock = () => {
    const [chartData, setChartData] = useState(null);

    const questions = [
        {
            question: '分享一下你最近在车联网或者深度学习领域有哪些具有突破性的科研成果。',
        },
        {
            question: '在你的研究生涯中,有没有哪位导师或者家人给予了重大影响?请具体阐述。',
        },
        {
            question: '如 Leonard Cimini Jr.教授,对你的科研方法或思维方式产生了重大影响?',
        },
    ];

    const handleSubmit = (answers) => {
        // 在这里处理提交的答复数据
        console.log(answers);
        // 你可以将答复数据发送到服务器或执行其他操作
    };

    useEffect(() => {
        getMockChart().then((res) => {
            setChartData(res);
        })
    }, [])

    return (
        <>
            <div style={{overflow: "auto", maxHeight: "90vh"}}>
                <ManageCubeCreateReport data={chartData}/>
                {/*<FileContent />*/}
                {/*<InterviewQuestions questions={questions} instanceId={"1"} onSubmit={handleSubmit}/>*/}
                {/*<InterviewQuestions instanceId={"1"} onSubmit={handleSubmit}/>*/}
            </div>
        </>
    );

}

export default Mock;
