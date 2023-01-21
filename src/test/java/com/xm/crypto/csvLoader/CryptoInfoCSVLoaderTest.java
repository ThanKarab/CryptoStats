package com.xm.crypto.csvLoader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class CryptoInfoCSVLoaderTest {
    @Test
    public void givenNoFiles_LoadNothing() {
        var csvLoader = new CryptoCSVLoader(null);
        assert (csvLoader.getCryptoInfos().size() == 0);
    }

    @Test
    public void givenEmptyList_LoadNothing() {
        Resource[] resources = {};
        var csvLoader = new CryptoCSVLoader(resources);
        assert (csvLoader.getCryptoInfos().size() == 0);
    }

    @Test
    public void givenCsvWithMissingColumn_whenCsvToCryptoInfoDTO_throwException() {
        Resource res = new ClassPathResource("csvLoader/ValuesWithoutColumn.csv");
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
                    CryptoCSVLoader.csvToCryptoInfoDTO(res);
                }
        );
        Assertions.assertTrue(thrown.getMessage().contains("Error capturing CSV header!"), thrown.getMessage());
    }

    @Test
    public void givenCsvWithoutHeader_whenCsvToCryptoInfoDTO_throwException() {
        Resource res = new ClassPathResource("csvLoader/ValuesWithoutHeader.csv");
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
                    CryptoCSVLoader.csvToCryptoInfoDTO(res);
                }
        );
        Assertions.assertTrue(thrown.getMessage().contains("Error capturing CSV header!"), thrown.getMessage());
    }

    @Test
    public void givenCsvWithWrongPrice_whenCsvToCryptoInfoDTO_throwException() {
        Resource res = new ClassPathResource("csvLoader/ValuesWithWrongPrice.csv");
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
                    CryptoCSVLoader.csvToCryptoInfoDTO(res);
                }
        );
        Assertions.assertTrue(thrown.getMessage().contains("Error parsing CSV line: 2."), thrown.getMessage());
    }

    @Test
    public void givenCsvWithWrongSymbol_whenCsvToCryptoInfoDTO_throwException() {
        Resource res = new ClassPathResource("csvLoader/ValuesWithWrongSymbol.csv");
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
                    CryptoCSVLoader.csvToCryptoInfoDTO(res);
                }
        );
        Assertions.assertTrue(thrown.getMessage().contains("Error parsing CSV line: 2."), thrown.getMessage());
    }

    @Test
    public void givenCsvWithWrongTimestamp_whenCsvToCryptoInfoDTO_throwException() {
        Resource res = new ClassPathResource("csvLoader/ValuesWithWrongTimestamp.csv");
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
                    CryptoCSVLoader.csvToCryptoInfoDTO(res);
                }
        );
        Assertions.assertTrue(thrown.getMessage().contains("java.lang.NumberFormatException"), thrown.getMessage());
    }
}
