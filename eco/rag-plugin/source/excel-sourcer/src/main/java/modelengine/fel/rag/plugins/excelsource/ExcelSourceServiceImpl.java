/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.plugins.excelsource;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fel.rag.source.ExcelSource;
import modelengine.fel.rag.source.ExcelSourceOptions;
import modelengine.fel.rag.source.ExcelSourceService;

import lombok.Getter;

/**
 * Excel数据源服务的实现类
 *
 * @since 2024-06-04
 */
@Component
public class ExcelSourceServiceImpl implements ExcelSourceService {
    @Getter
    private final ExcelSource source = new ExcelSource();

    @Override
    @Fitable("excel-source-load")
    public void load(ExcelSourceOptions options) {
        this.source.load(options.getPath(), options.getHeadRow(), options.getDataRow(), options.getSheetId());
    }

    @Override
    @Fitable("excel-source-parse")
    public void parseContent(ExcelSourceOptions options) {
        this.source.parseContent(options.getPath(), options.getHeadRow(), options.getDataRow(), options.getSheetId());
    }
}
