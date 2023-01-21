package com.xm.crypto.csvLoader;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.xm.crypto.CryptoSymbolEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoInfoDTO{
    @CsvBindByName(column = "timestamp", required = true)
    private long timestamp;

    @CsvCustomBindByName(column = "timestamp", required = true, converter = MilisToDateConverter.class)
    private LocalDate date;

    @CsvBindByName(column = "symbol", required = true)
    private CryptoSymbolEnum symbol;

    @CsvBindByName(column = "price", required = true)
    private double price;

    @Override
    public String toString() {
        return "{" + date + "::" + symbol + "::" + price + "}";
    }

    @NoArgsConstructor
    public static class MilisToDateConverter extends AbstractBeanField {
        private final String timezone = "Europe/Athens";    // TODO Move to application properties

        @Override
        protected LocalDate convert(String millis_str) {
            long millis = Long.parseLong(millis_str);
            var instance = java.time.Instant.ofEpochMilli(millis);
            return java.time.LocalDate.ofInstant(instance, java.time.ZoneId.of(timezone));
        }
    }
}