/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link AppData} 的测试类。
 *
 * @author 李金绪
 * @since 2024-09-19
 */
public class AppDataTest {
    @Test
    @DisplayName("")
    void shouldOkWhenGet() {
        AppData appData = new AppData();
        appData.setModifier("modifier");
        appData.setCreator("creator");
        appData.setSource("source");
        appData.setIcon("icon");

        assertThat(appData.getIcon()).isEqualTo("icon");
        assertThat(appData.getCreator()).isEqualTo("creator");
        assertThat(appData.getModifier()).isEqualTo("modifier");
        assertThat(appData.getSource()).isEqualTo("source");
    }
}
