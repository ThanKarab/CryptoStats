package com.xm.crypto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@RestController
public class CryptoController {
    @GetMapping("/crypto_normalized_range")
    public List<CryptoNormalizedRange> crypto_normalized_range() {
        return Arrays.asList(
            new CryptoNormalizedRange("BTC", 10.5),
            new CryptoNormalizedRange("ETH", 1.2)
        );
    }

    @GetMapping("/crypto_stats/{symbol}")
    public CryptoStats crypto_stats(@PathVariable(value="symbol") String symbol) {
        return new CryptoStats("BTC", 10.5, 1,2, 3);
    }

    @GetMapping("/crypto_normalized_range/{date}")
    public CryptoNormalizedRange crypto_normalized_range(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return new CryptoNormalizedRange("BTC", 10.5);
    }
}