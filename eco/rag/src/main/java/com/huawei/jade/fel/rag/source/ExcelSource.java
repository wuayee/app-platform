/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.source;

import com.huawei.fitframework.log.Logger;
import com.huawei.jade.fel.engine.operators.sources.Source;
import com.huawei.jade.fel.rag.common.Document;
import com.huawei.jade.fel.rag.common.IdGenerator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * pdf类型的数据源。
 * <p>将pdf类型的数据包装为Document类型。</p>
 *
 * @since 2024-05-013
 */
public class ExcelSource extends Source<List<Document>> {
    private static final Logger logger = Logger.get(ExcelSource.class);

    private List<String> titleName = new ArrayList<>();
    private List<List<String>> contents = new ArrayList<>();

    private boolean isExcelXLS(String path) throws IllegalArgumentException{
        int dotIdx = path.indexOf(".");
        if (dotIdx == -1) {
            logger.error("illegal file path: ", path);
            throw new IllegalArgumentException();
        }
        String fileExtension = path.substring(dotIdx + 1);
        return fileExtension.equalsIgnoreCase("xls");
    }

    private void contentExtract(String path, Integer headRow, Integer dataRow, Integer sheetId) {
        Workbook wb = null;
        try (FileInputStream fs = new FileInputStream(path)) {
            if (isExcelXLS(path)) {
                wb = new HSSFWorkbook(fs);
            } else {
                wb = new XSSFWorkbook(fs);
            }
            Sheet sheet = wb.getSheetAt(sheetId);
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
        } catch (IOException e) {
            logger.debug("Error when extracting from excel, msg:", e.getMessage());
        }
    }

    public void load(String path, Integer headRow, Integer dataRow, Integer sheetId) {
        contentExtract(path, headRow, dataRow, sheetId);
        List<Document> docs = new ArrayList<>();
        docs.add(new Document(IdGenerator.getId(), contents, null));
        emit(docs);
    }
}
