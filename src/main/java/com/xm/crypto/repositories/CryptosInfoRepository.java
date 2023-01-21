package com.xm.crypto.repositories;

import com.xm.crypto.CryptoSymbolEnum;
import com.xm.crypto.csvLoader.CryptoCSVLoader;
import com.xm.crypto.csvLoader.CryptoInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Repository
public class CryptosInfoRepository {
    private static final Logger logger = LoggerFactory.getLogger(CryptosInfoRepository.class);

    private Map<CryptoSymbolEnum, CryptoInfoRepository> cryptoInfoRepos = new HashMap<>();

    public CryptosInfoRepository() {
        this(new CryptoCSVLoader().getCryptoInfos());
    }

    public CryptosInfoRepository(List<CryptoInfoDTO> cryptoInfos) {
        var cryptosInfoPerSymbol = groupCryptoInfoPerSymbol(cryptoInfos);

        // Create one repo per symbol enumeration that exists
        for (CryptoSymbolEnum symbol : CryptoSymbolEnum.values()) {
            try {
                var crypto_repo = new CryptoInfoRepository(cryptosInfoPerSymbol.get(symbol));
                cryptoInfoRepos.put(symbol, crypto_repo);
            }catch (Exception exc){
                logger.error("Could not load information for crypto: " + symbol + "\nException: " + exc);
            }
        }
    }

    private static Map<CryptoSymbolEnum, List<CryptoInfoDTO>> groupCryptoInfoPerSymbol(
            List<CryptoInfoDTO> cryptoInfos
    ) {
        Map<CryptoSymbolEnum, List<CryptoInfoDTO>> cryptosInfoPerSymbol = new HashMap<>();
        for (CryptoSymbolEnum symbol : CryptoSymbolEnum.values()) {
            cryptosInfoPerSymbol.put(symbol, new ArrayList<>());
        }

        for (CryptoInfoDTO cryptoInfo : cryptoInfos) {
            cryptosInfoPerSymbol.get(cryptoInfo.getSymbol()).add(cryptoInfo);
        }
        return cryptosInfoPerSymbol;
    }

    public CryptoInfoRepository getCryptoInfoRepository(CryptoSymbolEnum symbol) {
        return cryptoInfoRepos.get(symbol);
    }

    public static class CryptoInfoRepository {
        private double oldestPrice;
        private long oldestPriceTimestamp;
        private double newestPrice;
        private long newestPriceTimestamp;
        private double minPrice;
        private double maxPrice;
        private double normalizedRange;
        private final Comparator<CryptoInfoDTO> cryptoInfoComparator = Comparator.comparing(CryptoInfoDTO::getDate);
        private List<CryptoInfoDTO> sortedCryptoInfosByDate;
        private Map<LocalDate, Double> normalizedRangePerDate;

        public double getOldestPrice() {
            return oldestPrice;
        }

        public double getNewestPrice() {
            return newestPrice;
        }

        public double getMinPrice() {
            return minPrice;
        }

        public double getMaxPrice() {
            return maxPrice;
        }

        public double getNormalizedRange() {
            return normalizedRange;
        }

        public Double getNormalizedRange(LocalDate date) {
            return normalizedRangePerDate.get(date);
        }

        public CryptoInfoRepository(List<CryptoInfoDTO> cryptoInfos) {
            if (cryptoInfos.size() == 0) {
                throw new RuntimeException("Cannot instantiate repo without any values.");
            }

            cryptoInfos.sort(cryptoInfoComparator);
            sortedCryptoInfosByDate = cryptoInfos;
            initializePriceVariables(cryptoInfos.get(0));
            initializeNormalizedRangePerDate(sortedCryptoInfosByDate);
            initializeMinMaxOldestNewestPrices(sortedCryptoInfosByDate);
            initializeNormalizedRange(minPrice, maxPrice);
        }

        private void initializeNormalizedRange(double minPrice, double maxPrice) {
            normalizedRange = (maxPrice - minPrice) / minPrice;
        }

        private void initializeMinMaxOldestNewestPrices(List<CryptoInfoDTO> cryptoInfos) {
            for (CryptoInfoDTO cryptoInfo : cryptoInfos) {
                minPrice = min(minPrice, cryptoInfo.getPrice());
                maxPrice = max(maxPrice, cryptoInfo.getPrice());
                if (oldestPriceTimestamp > cryptoInfo.getTimestamp()) {
                    oldestPriceTimestamp = cryptoInfo.getTimestamp();
                    oldestPrice = cryptoInfo.getPrice();
                }
                if (newestPriceTimestamp < cryptoInfo.getTimestamp()) {
                    newestPriceTimestamp = cryptoInfo.getTimestamp();
                    newestPrice = cryptoInfo.getPrice();
                }
            }
        }

        private void initializePriceVariables(CryptoInfoDTO cryptoPrice) {
            oldestPrice = cryptoPrice.getPrice();
            oldestPriceTimestamp = cryptoPrice.getTimestamp();
            newestPrice = cryptoPrice.getPrice();
            newestPriceTimestamp = cryptoPrice.getTimestamp();
            minPrice = cryptoPrice.getPrice();
            maxPrice = cryptoPrice.getPrice();
        }

        private void initializeNormalizedRangePerDate(List<CryptoInfoDTO> sortedCryptoInfosByDate) {
            normalizedRangePerDate = new HashMap<>();

            LocalDate curDate = sortedCryptoInfosByDate.get(0).getDate();
            List<CryptoInfoDTO> curDateCryptoInfos = new ArrayList<>();
            for (CryptoInfoDTO cryptoInfo : sortedCryptoInfosByDate) {
                if (!curDate.isEqual(cryptoInfo.getDate()) && curDateCryptoInfos.size() > 0) {
                    normalizedRangePerDate.put(curDate, computeNormalizedRange(curDateCryptoInfos));
                    curDateCryptoInfos = new ArrayList<>();
                }
                curDate = cryptoInfo.getDate();
                curDateCryptoInfos.add(cryptoInfo);
            }
            if (curDateCryptoInfos.size() > 0) {
                normalizedRangePerDate.put(curDate, computeNormalizedRange(curDateCryptoInfos));
            }
        }

        private static double computeNormalizedRange(List<CryptoInfoDTO> cryptoInfos) {
            var minPrice = cryptoInfos.get(0).getPrice();
            var maxPrice = cryptoInfos.get(0).getPrice();
            for (CryptoInfoDTO cryptoInfo : cryptoInfos) {
                minPrice = min(minPrice, cryptoInfo.getPrice());
                maxPrice = max(maxPrice, cryptoInfo.getPrice());
            }
            return (maxPrice - minPrice) / minPrice;
        }
    }
}
