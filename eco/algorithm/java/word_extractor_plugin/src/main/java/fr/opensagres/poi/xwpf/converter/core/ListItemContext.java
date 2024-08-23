/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package fr.opensagres.poi.xwpf.converter.core;

import fr.opensagres.poi.xwpf.converter.core.utils.RomanAlphabetFactory;
import fr.opensagres.poi.xwpf.converter.core.utils.RomanNumberFactory;
import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * ListItemContext
 *
 * @since 2024-02-28
 */
public class ListItemContext {
    private int startI = 0;
    private int nbx = 0;
    private final int number;
    private final CTLvl lvl;
    private final ListItemContext parent;
    private final String numberText;

    /**
     * word抽取算子实现
     *
     * @param lvl ListItemContext的level值
     * @param number 文本 number 值
     * @param parent 文本的 ListItemContext
     *
     * @return ListItemContext
     */
    public ListItemContext(CTLvl lvl, int number, ListItemContext parent) {
        this.lvl = lvl;
        this.parent = parent;
        this.startI = 0;
        if (lvl != null) {
            CTDecimalNumber start = lvl.getStart();
            if (start != null) {
                BigInteger val = start.getVal();
                if (val != null) {
                    this.startI = val.intValue();
                }
            }
        }

        this.nbx = 0;
        this.number = number + this.startI;
        if (lvl != null) {
            CTNumFmt numFmt = lvl.getNumFmt();
            this.numberText = computeNumberText(this.number, numFmt != null ? numFmt.getVal() : null);
        } else {
            this.numberText = null;
        }
    }

    public ListItemContext getParent() {
        return this.parent;
    }

    public CTLvl getLvl() {
        return this.lvl;
    }

    public int getNumber() {
        return this.number;
    }

    public String getNumberText() {
        return this.numberText;
    }

    private static String computeNumberText(int num, STNumberFormat.Enum numberFormat) {
        switch (numberFormat.toString()) {
            case "lowerLetter":
                return RomanAlphabetFactory.getLowerCaseString(num);
            case "upperLetter":
                return RomanAlphabetFactory.getUpperCaseString(num);
            case "lowerRoman":
                return RomanNumberFactory.getLowerCaseString(num);
            case "upperRoman":
                return RomanNumberFactory.getUpperCaseString(num);
            case "decimalZero":
                return num < 10 ? "0" + num : String.valueOf(num);
            case "none":
                return "";
            default:
                return String.valueOf(num);
        }
    }

    /**
     * word抽取算子实现
     *
     * @param lvl ListItemContext的level值
     *
     * @return ListItemContext
     */
    public ListItemContext createAndAddItem(CTLvl lvl) {
        return new ListItemContext(lvl, this.nbx++, this);
    }

    public boolean isRoot() {
        return false;
    }

    /**
     * word抽取算子实现
     *
     * @return String text 提取的文本
     */
    public String getText() {
        if (this.lvl == null || this.lvl.getLvlText() == null) {
            return "";
        }
        String text = this.lvl.getLvlText().getVal();
        CTNumFmt numFmt = this.lvl.getNumFmt();
        if (!STNumberFormat.BULLET.equals(numFmt)) {
            List<String> numbers = new ArrayList();

            for (ListItemContext item = this; !item.isRoot(); item = item.getParent()) {
                numbers.add(0, item.getNumberText());
            }

            String num = null;

            for (int i = 0; i < numbers.size(); ++i) {
                if (numbers.get(i) instanceof String) {
                    num = (String) numbers.get(i);
                }
                text = StringUtils.replaceAll(text, "%" + (i + 1), num);
            }
        }

        return text;
    }
}
