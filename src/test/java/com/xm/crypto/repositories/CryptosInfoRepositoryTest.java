package com.xm.crypto.repositories;

import com.xm.crypto.CryptoSymbolEnum;
import com.xm.crypto.csvLoader.CryptoInfoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CryptosInfoRepositoryTest {
    @Test
    public void givenNoCryptoInfo_initializeNoRepos(){
        var cryptosRepo = new CryptosInfoRepository(new ArrayList<>());
        Assertions.assertNull(cryptosRepo.getCryptoInfoRepository(CryptoSymbolEnum.BTC));
    }

    @Test
    public void givenBTCCryptoInfo_initializeBTCRepo(){
        var date = LocalDate.of(2023, 1, 21);
        var cryptoInfos = List.of(
                new CryptoInfoDTO(0, date, CryptoSymbolEnum.BTC, 1)
        );
        var cryptosRepo = new CryptosInfoRepository(cryptoInfos);
        Assertions.assertNotNull(cryptosRepo.getCryptoInfoRepository(CryptoSymbolEnum.BTC));
    }

    @Test
    public void givenBTCCryptoInfo_initializeRepoValuesProperly(){
        var date = LocalDate.of(2023, 1, 21);
        var minValue = new Random().nextDouble();
        var maxValue = new Random().nextDouble() + minValue;
        var cryptoInfos = Arrays.asList(
                new CryptoInfoDTO(0, date, CryptoSymbolEnum.BTC, minValue),
                new CryptoInfoDTO(1, date, CryptoSymbolEnum.BTC, maxValue)
        );
        var BTCRepo = new CryptosInfoRepository(cryptoInfos).getCryptoInfoRepository(CryptoSymbolEnum.BTC);
        Assertions.assertEquals(minValue, BTCRepo.getMinPrice());
        Assertions.assertEquals(maxValue, BTCRepo.getMaxPrice());
        Assertions.assertEquals(minValue, BTCRepo.getOldestPrice());
        Assertions.assertEquals(maxValue, BTCRepo.getNewestPrice());
        Assertions.assertEquals((maxValue - minValue) / minValue, BTCRepo.getNormalizedRange());
    }

    @Test
    public void givenBTCAndETHCryptoInfo_groupInfoProperly(){
        var date = LocalDate.of(2023, 1, 21);
        var minValue = new Random().nextDouble();
        var maxValue = new Random().nextDouble() + minValue;
        var cryptoInfos = Arrays.asList(
                new CryptoInfoDTO(0, date, CryptoSymbolEnum.BTC, minValue),
                new CryptoInfoDTO(0, date, CryptoSymbolEnum.ETH, maxValue)
        );
        var cryptosRepo = new CryptosInfoRepository(cryptoInfos);
        var BTCRepo = cryptosRepo.getCryptoInfoRepository(CryptoSymbolEnum.BTC);
        var ETHRepo = cryptosRepo.getCryptoInfoRepository(CryptoSymbolEnum.ETH);

        Assertions.assertEquals(minValue, BTCRepo.getMinPrice());
        Assertions.assertEquals(minValue, BTCRepo.getMaxPrice());
        Assertions.assertEquals(maxValue, ETHRepo.getMinPrice());
        Assertions.assertEquals(maxValue, ETHRepo.getMaxPrice());
    }

    @Test
    public void givenBTCCryptoInfo_getNormalizedRangePerDateProperly(){
        var date = LocalDate.of(2023, 1, 21);
        var minValue = new Random().nextDouble();
        var maxValue = new Random().nextDouble() + minValue;
        var cryptoInfos = Arrays.asList(
                new CryptoInfoDTO(0, date, CryptoSymbolEnum.BTC, minValue),
                new CryptoInfoDTO(0, date, CryptoSymbolEnum.BTC, maxValue)
        );
        var BTCRepo = new CryptosInfoRepository(cryptoInfos).getCryptoInfoRepository(CryptoSymbolEnum.BTC);

        Assertions.assertEquals((maxValue-minValue)/minValue, BTCRepo.getNormalizedRange());
    }
}
