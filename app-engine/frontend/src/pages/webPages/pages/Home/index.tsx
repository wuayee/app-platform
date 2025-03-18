/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import Title from './components/Title';
import Introduction from './components/Introduction';
import Features from './components/Features';
import Footer from './components/Footer';
import Fit from './components/Fit';
import style from './style.module.scss';

const Home = () => {
    return (
        <>
            <Title/>
            <main className={style['main-container']}>
                <div className={style['main-content']}>
                    <Introduction/>
                    <Features/>
                </div>
                <Fit />
            </main>
            <Footer/>
        </>
    );
}
export default Home;