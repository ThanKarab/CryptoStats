package com.xm.crypto.controllers;

public record CryptoStatsDTO(
    String symbol,
    double oldest_price,
    double newest_price,
    double min_price,
    double max_price
) { }
