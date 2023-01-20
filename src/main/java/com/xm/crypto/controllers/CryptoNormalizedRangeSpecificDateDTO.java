package com.xm.crypto.controllers;

import java.time.LocalDate;

public record CryptoNormalizedRangeSpecificDateDTO(String symbol, double normalized_range, LocalDate date) { }
