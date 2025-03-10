/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.exporter.repository;

import modelengine.jade.service.CarverSpanExporter;
import modelengine.jade.service.SpanExporterRepository;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.inspection.Validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 表示 {@link SpanExporterRepository} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-07-22
 */
@Component
public class DefaultSpanExporterRepository implements SpanExporterRepository {
    private final List<CarverSpanExporter> exporters = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void register(CarverSpanExporter exporter) {
        if (exporter != null) {
            this.exporters.add(exporter);
        }
    }

    @Override
    public void unregister(CarverSpanExporter exporter) {
        if (exporter != null) {
            this.exporters.remove(exporter);
        }
    }

    @Nonnull
    @Override
    public List<CarverSpanExporter> get(Predicate<CarverSpanExporter> predicate) {
        Validation.notNull(predicate, "The filter condition cannot be null.");
        return this.exporters.stream().filter(predicate).collect(Collectors.toList());
    }
}
