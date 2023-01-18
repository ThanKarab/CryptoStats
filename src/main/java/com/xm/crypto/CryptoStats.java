package com.xm.crypto;

public record CryptoStats(
    String symbol,
    double oldest_price,
    double newest_price,
    double min_price,
    double max_price
) { }
