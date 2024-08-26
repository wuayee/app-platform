/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.schedule.cron.support;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.schedule.cron.CronField;
import modelengine.fitframework.util.TimeUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;

/**
 * 表示每天中的小时数的字段。
 *
 * @author 季聿阶
 * @since 2023-01-02
 */
public class HourOfDayCronField extends AbstractCronField {
    private static final int MAX = 23;

    @Override
    public Optional<ZonedDateTime> findCurrentOrNextTime(@Nonnull ZonedDateTime dateTime) {
        int hourValue = this.getBitSet().nextSetBit(dateTime.getHour());
        if (hourValue < 0) {
            return Optional.empty();
        }
        if (hourValue == dateTime.getHour()) {
            return Optional.of(dateTime);
        }
        ZonedDateTime target = dateTime.with(ChronoField.HOUR_OF_DAY, hourValue).with(TimeUtils.firstTimeOfHour());
        return Optional.of(target);
    }

    /**
     * 表示 {@link HourOfDayCronField} 的字段解析器。
     */
    public static class Parser extends AbstractBitCronFieldParser {
        @Override
        protected CronField initialCronField() {
            return new HourOfDayCronField();
        }

        @Override
        protected int getMaxValidValue() {
            return MAX;
        }
    }
}
