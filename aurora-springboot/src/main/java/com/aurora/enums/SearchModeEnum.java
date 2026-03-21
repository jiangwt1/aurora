package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchModeEnum {
    // mysql搜索
    MYSQL("mysql", "mySqlSearchStrategyImpl"),
    // es搜索
    ELASTICSEARCH("elasticsearch", "esSearchStrategyImpl");

    private final String mode;

    private final String strategy;

    public static String getStrategy(String mode) {
        for (SearchModeEnum value : SearchModeEnum.values()) {
            if (value.getMode().equals(mode)) {
                return value.getStrategy();
            }
        }
        return null;
    }

}
