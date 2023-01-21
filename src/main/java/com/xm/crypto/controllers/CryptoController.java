package com.xm.crypto.controllers;

import com.xm.crypto.CryptoSymbolEnum;
import com.xm.crypto.service.CryptoInfoService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;


@RestController
public class CryptoController {
    CryptoInfoService cryptoInfoService;

    private final Bucket bucket;

    @Autowired
    public CryptoController(CryptoInfoService cryptoInfoService) {
        this.cryptoInfoService = cryptoInfoService;
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @GetMapping("/crypto_normalized_range")
    public List<CryptoNormalizedRangeDTO> crypto_normalized_range() {
        if (! bucket.tryConsume(1)) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS);
        }
        var cryptos = cryptoInfoService.getCryptosInfoNormalizedRange(true);
        Collections.reverse(cryptos);
        return cryptos;
    }

    @GetMapping("/crypto_stats/{symbol}")
    public CryptoStatsDTO crypto_stats(@PathVariable(value = "symbol") CryptoSymbolEnum symbol) {
        if (! bucket.tryConsume(1)) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS);
        }
        return cryptoInfoService.getCryptoStats(symbol);
    }

    @GetMapping("/crypto_normalized_range/{date}")
    public CryptoNormalizedRangeSpecificDateDTO crypto_normalized_range(
            @Parameter(schema = @Schema(type="string" ,format = "date", example = "2022-01-30"))
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        if (! bucket.tryConsume(1)) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS);
        }
        var res = cryptoInfoService.getCryptoHighestNormalizedRange(date);
        if (res == null) {
            throw new ResponseStatusException(NOT_FOUND, "There are no entries on the specified date.");
        }
        return res;
    }
}