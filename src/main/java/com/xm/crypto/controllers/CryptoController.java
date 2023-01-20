package com.xm.crypto.controllers;

import com.xm.crypto.CryptoSymbolEnum;
import com.xm.crypto.service.CryptoInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@RestController
public class CryptoController {
    CryptoInfoService cryptoInfoService;

    @Autowired
    public CryptoController(CryptoInfoService cryptoInfoService) {
        this.cryptoInfoService = cryptoInfoService;
    }

    // TODO Add proper exception handlers

    @GetMapping("/crypto_normalized_range")
    public List<CryptoNormalizedRangeDTO> crypto_normalized_range() {
        var cryptos = cryptoInfoService.getCryptosInfoNormalizedRange(true);
        Collections.reverse(cryptos);
        return cryptos;
    }

    @GetMapping("/crypto_stats/{symbol}")
    public CryptoStatsDTO crypto_stats(@PathVariable(value = "symbol") CryptoSymbolEnum symbol) {
        return cryptoInfoService.getCryptoStats(symbol);
    }

    @GetMapping("/crypto_normalized_range/{date}")
    public ResponseEntity<?> crypto_normalized_range(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        var res = cryptoInfoService.getCryptoHighestNormalizedRange(date);
        if (res == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no entries on the specified date.");
        }
        return ResponseEntity.ok(res);
    }
}