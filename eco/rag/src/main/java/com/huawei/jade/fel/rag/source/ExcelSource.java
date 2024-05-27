/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.source;

import com.huawei.fitframework.log.Logger;
import com.huawei.jade.fel.engine.operators.sources.Source;
import com.huawei.jade.fel.rag.common.Document;
import com.huawei.jade.fel.rag.common.IdGenerator;

import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * pdf类型的数据源。
 * <p>将pdf类型的数据包装为Document类型。</p>
 *
 * @since 2024-05-013
 */
public class ExcelSource extends Source<List<Document>> {
    private static final Logger logger = Logger.get(ExcelSource.class);

    @Getter
    private List<String> titleName = new ArrayList<>();
    @Getter
    private List<List<String>> contents = new ArrayList<>();

    private enum FileType {
        NORMAL_FILE,
        SYNONYMS_FILE,
        RELATIONAL_ENUM_FILE,
    }

    private boolean isExcelXLS(String path) throws IllegalArgumentException{
        int dotIdx = path.indexOf(".");
        if (dotIdx == -1) {
            logger.error("illegal file path: ", path);
            throw new IllegalArgumentException();
        }
        String fileExtension = path.substring(dotIdx + 1);
        return fileExtension.equalsIgnoreCase("xls");
    }

    private FileType getFileType(String path) {
        int dotIdx = path.indexOf("近义词");
        if (dotIdx != -1) {
            return FileType.SYNONYMS_FILE;
        }
        dotIdx = path.indexOf("从属枚举");
        if (dotIdx != -1) {
            return FileType.RELATIONAL_ENUM_FILE;
        }
        return FileType.NORMAL_FILE;
    }

    private void normalExtract(Integer headRow, Integer dataRow, Sheet sheet) {
        Integer rowNum = sheet.getPhysicalNumberOfRows();
        int colNum = sheet.getRow(0).getPhysicalNumberOfCells();

        sheet.getRow(headRow).forEach((cell) -> {
            titleName.add(cell.getStringCellValue());
        });
        for (Integer rowNo = dataRow; rowNo < rowNum; rowNo++) {
            List<String> rowContent = new ArrayList<>();

            Row row = sheet.getRow(rowNo);
            for (int col = 0; col < colNum; col++) {
                Cell cell = row.getCell(col);
                switch(cell.getCellType()) {
                    case NUMERIC:
                        rowContent.add(Double.toString(cell.getNumericCellValue()));
                        break;
                    case STRING:
                        rowContent.add(cell.getStringCellValue());
                        break;
                    default:
                        logger.error("Unsupported datatype:", cell.getCellType());
                }
            }
            contents.add(rowContent);
        }
    }

    private void synonymsExtract(Sheet sheet) {
        titleName.add("近义词");
        titleName.add("标准词");
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            for (int j = 1; j < row.getPhysicalNumberOfCells(); j++) {
                if (row.getCell(j) == null || row.getCell(j).getStringCellValue().isEmpty()) {
                    continue;
                }
                contents.add(
                        Arrays.asList(row.getCell(j).getStringCellValue(), row.getCell(0).getStringCellValue()));
            }
        }
    }

    private void relationalEnumExtract(Sheet sheet) {
        Map<String, List<Integer>> relations = new HashMap<>();
        List<String> tags = new ArrayList<>();

        titleName.add("从属主体");
        titleName.add("关系");

        sheet.getRow(0).forEach(cell -> {
            tags.add(cell.getStringCellValue());
        });

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < tags.size(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                String cellVal = cell.getStringCellValue();
                if (cellVal == null) {
                    continue;
                }
                if (relations.get(cellVal) == null) {
                    List<Integer> list = new ArrayList<>();
                    list.add(j);
                    relations.put(cellVal, list);
                } else {
                    relations.get(cellVal).add(j);
                }
            }
        }

        relations.forEach((key, value) -> {
            StringBuilder sb = new StringBuilder();
            value.forEach(tagIdx -> {
                sb.append(tags.get(tagIdx));
                sb.append(",");
            });
            if (!value.isEmpty()) {
                sb.setLength(sb.length() - 1);
            }
            contents.add(Arrays.asList(key, sb.toString()));
        });
    }

    /**
     * 解析提取excel中的表头及内容信息。
     *
     * @param path 表示文件所在路径 {@link String}。
     * @param headRow 表示表头位于的行数id（从0开始） {@link Integer}
     * @param dataRow 表示数据的起始行数id {@link Integer}
     * @param sheetId 表示要提取的excel工作簿id {@link Integer}
     */
    public void parseContent(String path, Integer headRow, Integer dataRow, Integer sheetId) {
        Workbook wb = null;
        try (FileInputStream fs = new FileInputStream(path)) {
            if (isExcelXLS(path)) {
                wb = new HSSFWorkbook(fs);
            } else {
                wb = new XSSFWorkbook(fs);
            }
            Sheet sheet = wb.getSheetAt(sheetId);

            switch (getFileType(path)) {
                case NORMAL_FILE:
                    normalExtract(headRow, dataRow, sheet);
                    break;
                case RELATIONAL_ENUM_FILE:
                    relationalEnumExtract(sheet);
                    break;
                case SYNONYMS_FILE:
                    synonymsExtract(sheet);
                    break;
            }
        } catch (IOException e) {
            logger.debug("Error when extracting from excel, msg:", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * 解析提取excel中的表头及内容信息,用于在aiflow中作为数据源输入触发流程。
     *
     * @param path 表示文件所在路径 {@link String}。
     * @param headRow 表示表头位于的行数id（从0开始） {@link Integer}
     * @param dataRow 表示数据的起始行数id {@link Integer}
     * @param sheetId 表示要提取的excel工作簿id {@link Integer}
     */
    public void load(String path, Integer headRow, Integer dataRow, Integer sheetId) {
        parseContent(path, headRow, dataRow, sheetId);
        List<Document> docs = new ArrayList<>();
        docs.add(new Document(IdGenerator.getId(), contents, null));
        emit(docs);
    }
}
