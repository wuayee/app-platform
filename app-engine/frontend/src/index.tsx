import React, {Suspense} from 'react';
import {createRoot} from 'react-dom/client';
import app from './app.jsx';

const render = Component => {
    const container = document.getElementById('root');
    const root = createRoot(container);
    return root.render(
        <Suspense fallback={<div id="Loading">
            <div className="loader-inner ball-beat">
                <div/>
                <div/>
                <div/>
                <div/>
                <div/>
            </div>
        </div>}>
            <Component/>
        </Suspense>
    );
}

render(app);
