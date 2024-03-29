/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.integration.druid;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.conf.support.MapConfig;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanRegistry;
import com.huawei.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

class DruidBeanContainerInitializedObserverTest {
    @Test
    void should_register_data_source_to_container() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("druid.driver", "org.h2.Driver");
        properties.put("druid.url", "jdbc:h2:mem:test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        properties.put("druid.username", "root");
        properties.put("druid.password", "");
        properties.put("druid.redundant-value", "1");

        Config config = new MapConfig("p", properties);
        DruidPropertiesHandler handler = mock(DruidPropertiesHandler.class);

        BeanContainer.Beans beans = mock(BeanContainer.Beans.class);
        when(beans.get(Config.class)).thenReturn(config);
        when(beans.list(DruidPropertiesHandler.class)).thenReturn(MapBuilder.<String, DruidPropertiesHandler>get()
                .put("my-handler", handler)
                .build());

        BeanContainer container = mock(BeanContainer.class);
        when(container.beans()).thenReturn(beans);

        BeanRegistry registry = mock(BeanRegistry.class);
        when(container.registry()).thenReturn(registry);

        DruidBeanContainerInitializedObserver observer = new DruidBeanContainerInitializedObserver(container);
        observer.onBeanContainerInitialized(container);

        verify(registry, times(1)).register(argThat((Object arg) -> {
            if (arg instanceof DataSource) {
                DataSource dataSource = (DataSource) arg;
                try (Connection connection = dataSource.getConnection()) {
                    return testConnection(connection);
                } catch (SQLException ex) {
                    return false;
                }
            } else {
                return false;
            }
        }));
        verify(handler, times(1)).handleDruidProperties(argThat((Properties values) ->
                values.containsKey("redundantValue") && !values.containsKey("redundant-value")));
    }

    private static boolean testConnection(Connection connection) throws SQLException {
        String sql = "SELECT 1";
        try (Statement statement = connection.createStatement(); ResultSet results = statement.executeQuery(sql)) {
            return results.next() && results.getLong(1) == 1L && !results.next();
        }
    }

    @Test
    void should_not_register_data_source_if_config_not_supplied() {
        MapConfig config = new MapConfig("m", null);
        config.set("druid", null);

        BeanContainer.Beans beans = mock(BeanContainer.Beans.class);
        when(beans.get(Config.class)).thenReturn(config);

        BeanRegistry registry = mock(BeanRegistry.class);

        BeanContainer container = mock(BeanContainer.class);
        when(container.registry()).thenReturn(registry);
        when(container.beans()).thenReturn(beans);

        DruidBeanContainerInitializedObserver observer = new DruidBeanContainerInitializedObserver(container);
        observer.onBeanContainerInitialized(container);

        verify(registry, times(0)).register(any(Object.class));
    }
}
