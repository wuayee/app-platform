/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.fortune;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 将易经文档的数据转换为 Map 数据。
 *
 * @author 杭潇
 * @since 2025-03-19
 */
public class ExcelToMapSingleton {
    private static volatile ExcelToMapSingleton instance;
    private static final String FILE_PATH = "data/yijing.xlsx";

    private final Map<String, String> dataMap;

    // 私有构造函数防止外部实例化
    private ExcelToMapSingleton() {
        this.dataMap = new HashMap<>();
    }

    /**
     * 获取单例实例（双重校验锁实现线程安全）
     */
    public static ExcelToMapSingleton getInstance() {
        if (instance == null) {
            synchronized (ExcelToMapSingleton.class) {
                if (instance == null) {
                    instance = new ExcelToMapSingleton();
                }
            }
        }
        return instance;
    }

    /**
     * 读取Excel文件并转换为Map
     */
    public void loadExcelData() {
        this.dataMap.clear();
        ClassLoader classLoader = getClass().getClassLoader();
        URL resourceUrl = classLoader.getResource(FILE_PATH);

        try {
            assert resourceUrl != null;
            try (InputStream fis = resourceUrl.openStream()) {
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        continue;
                    }

                    Cell inputCell = row.getCell(0);
                    Cell outputCell = row.getCell(1);
                    String input = getCellValue(inputCell);
                    String output = getCellValue(outputCell);
                    if (!input.isEmpty() && !output.isEmpty()) {
                        this.dataMap.put(input, output);
                    }
                }
                workbook.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 安全获取单元格内容
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        // 根据单元格类型处理数据
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    /**
     * 获取数据 Map
     */
    public Map<String, String> getDataMap() {
        return new HashMap<>(this.dataMap);
    }
}
