package com.xm.crypto.service;

import com.xm.crypto.CryptoSymbolEnum;
import com.xm.crypto.csvLoader.CryptoInfoDTO;
import com.xm.crypto.repositories.CryptosInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

public class CryptoInfoServiceTest {
    private static CryptoInfoService cryptoInfoService;
    private static final LocalDate date1 = LocalDate.of(2023, 1, 21);
    private static final LocalDate date2 = LocalDate.of(2023, 1, 22);
    private static final double minValue = new Random().nextDouble();
    private static final double maxValue = new Random().nextDouble() + minValue;

    @BeforeAll
    public static void init() {
        var cryptoInfos = Arrays.asList(
                new CryptoInfoDTO(0, date1, CryptoSymbolEnum.BTC, minValue),
                new CryptoInfoDTO(1, date1, CryptoSymbolEnum.BTC, maxValue),
                new CryptoInfoDTO(2, date1, CryptoSymbolEnum.BTC, maxValue),
                new CryptoInfoDTO(3, date1, CryptoSymbolEnum.BTC, maxValue),
                new CryptoInfoDTO(4, date1, CryptoSymbolEnum.BTC, maxValue),
                new CryptoInfoDTO(0, date1, CryptoSymbolEnum.ETH, minValue),
                new CryptoInfoDTO(1, date1, CryptoSymbolEnum.ETH, maxValue * 2),
                new CryptoInfoDTO(10, date2, CryptoSymbolEnum.LTC, maxValue * 3)
        );
        cryptoInfoService = new CryptoInfoService(new CryptosInfoRepository(cryptoInfos));
    }

    @Test
    public void givenRepositoryWithStats_whenGetCryptosInfoNormalizedRange_thenReturnOrderedRangeDTO() {
        var dtos = cryptoInfoService.getCryptosInfoNormalizedRange(true);
        var prevRange = dtos.get(0).normalized_range();
        for (var dto : dtos) {
            Assertions.assertTrue(prevRange <= dto.normalized_range());
            prevRange = dto.normalized_range();
        }
    }

    @Test
    public void givenRepositoryWithStats_whenGetCryptoStats_thenReturnProperStatsDTO() {
        var dto = cryptoInfoService.getCryptoStats(CryptoSymbolEnum.BTC);
        Assertions.assertEquals(minValue, dto.min_price());
        Assertions.assertEquals(maxValue, dto.max_price());
        Assertions.assertEquals(minValue, dto.oldest_price());
        Assertions.assertEquals(maxValue, dto.newest_price());
        Assertions.assertEquals(CryptoSymbolEnum.BTC.name(), dto.symbol());
    }

    @Test
    public void givenRepositoryWithStats_whenGetCryptoHighestNormalizedRange_thenReturnRangeDateDTO() {
        var dto = cryptoInfoService.getCryptoHighestNormalizedRange(date1);
        Assertions.assertEquals(dto.normalized_range(), (maxValue * 2 - minValue) / minValue);
        Assertions.assertEquals(dto.date(), date1);
        Assertions.assertEquals(dto.symbol(), CryptoSymbolEnum.ETH.name());
    }
}
