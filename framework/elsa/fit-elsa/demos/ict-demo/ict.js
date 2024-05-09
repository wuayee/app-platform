/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

import {ELSA} from "../../core/elsaEntry.js";
import {getUrlParam} from "../../common/util.js";

window.onload = () => {
    const btn = document.getElementById('fullScreen');
    const session = getUrlParam("session");
    const WAIT_TIME = 6;
    let countDown = WAIT_TIME;
    let interval;
    let text;

    btn.onclick = () => {
        text = "排队围观中";
        disableBtn();
        loadPresentation();
    }

    const countDownFunc = () => {
        countDown--;
        if (countDown === 0) {
            clearInterval(interval);
            countDown = WAIT_TIME;
            btn.value = "开始围观";
            btn.disabled = false;
        } else {
            btn.value = text + ", 请稍后" + countDown + "s";
        }
    }

    function disableBtn() {
        btn.disabled = true;
        btn.value = text + ", 请稍后" + countDown + "s";
        if (interval) {
            clearInterval(interval);
        }
        interval = setInterval(countDownFunc, 1000);
    }

    const loadPresentation = async () => {
        let container = document.getElementById("ict-elsa-page");
        let viewer = document.getElementById("present");
        if (viewer === null) {
            viewer = document.createElement("div");
            viewer.id = "present";
            viewer.style.background = "red";
            container.appendChild(viewer);
        } else {
            viewer.innerHTML = "";
        }
        const graph = await ELSA.viewGraph(session, "presentation", viewer, error => {
            text = "太火爆啦";
            disableBtn();
        });
        if (graph) {
            graph.dirtied = (data, event) => {
                if (event.action === "exit_present") {
                    location.reload();
                }
            }
        }
    };
}
