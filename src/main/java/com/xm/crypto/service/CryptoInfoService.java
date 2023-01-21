package com.xm.crypto.service;

import com.xm.crypto.CryptoSymbolEnum;
import com.xm.crypto.controllers.CryptoNormalizedRangeDTO;
import com.xm.crypto.controllers.CryptoNormalizedRangeSpecificDateDTO;
import com.xm.crypto.controllers.CryptoStatsDTO;
import com.xm.crypto.repositories.CryptosInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CryptoInfoService {
    CryptosInfoRepository cryptosInfoRepository;

    @Autowired
    public CryptoInfoService(CryptosInfoRepository cryptosInfoRepository) {
        this.cryptosInfoRepository = cryptosInfoRepository;
    }

    public List<CryptoNormalizedRangeDTO> getCryptosInfoNormalizedRange(boolean ordered) {
        List<CryptoNormalizedRangeDTO> ranges = new ArrayList<>();
        for (CryptoSymbolEnum symbol : CryptoSymbolEnum.values()) {
            var repo = cryptosInfoRepository.getCryptoInfoRepository(symbol);
            if (repo == null) {
                continue;
            }
            ranges.add(new CryptoNormalizedRangeDTO(symbol.name(), repo.getNormalizedRange()));
        }
        var normRangeDTOComparator = Comparator.comparing(CryptoNormalizedRangeDTO::normalized_range);
        if (ordered) {
            ranges.sort(normRangeDTOComparator);
        }
        return ranges;
    }

    public CryptoStatsDTO getCryptoStats(CryptoSymbolEnum symbol) {
        var crypto = cryptosInfoRepository.getCryptoInfoRepository(symbol);
        return new CryptoStatsDTO(
                symbol.name(),
                crypto.getOldestPrice(),
                crypto.getNewestPrice(),
                crypto.getMinPrice(),
                crypto.getMaxPrice()
        );
    }

    public CryptoNormalizedRangeSpecificDateDTO getCryptoHighestNormalizedRange(LocalDate date) {
        Double maxNormalizedRange = null;
        CryptoSymbolEnum maxNormalizedRangeSymbol = null;
        for (CryptoSymbolEnum symbol : CryptoSymbolEnum.values()) {
            var repo = cryptosInfoRepository.getCryptoInfoRepository(symbol);
            if (repo == null) {  // Repo for that symbol is not initialized
                continue;
            }
            var range = repo.getNormalizedRange(date);
            if (range != null) {  // Normalized range exists on that date
                if (maxNormalizedRange == null || range > maxNormalizedRange) {
                    maxNormalizedRange = range;
                    maxNormalizedRangeSymbol = symbol;
                }
            }
        }
        if (maxNormalizedRange == null) {
            return null;
        }
        return new CryptoNormalizedRangeSpecificDateDTO(
                maxNormalizedRangeSymbol.name(),
                maxNormalizedRange,
                date
        );
    }
}
