package com.xm.crypto.controllers;

import com.xm.crypto.CryptoSymbolEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CryptoControllerTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void beforeSetup() {
        baseUrl = baseUrl + ":" + port + "/";
    }

    @Test
    public void getCryptoNormalizedRange_returnsAllCryptoSymbols() {
        var url = baseUrl + "crypto_normalized_range";
        var response = restTemplate.getForEntity(url, CryptoNormalizedRangeDTO[].class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(response.hasBody());
        Assertions.assertEquals(CryptoSymbolEnum.values().length, response.getBody().length);
    }

    @Test
    public void getCryptoStats_whenGivenBTCSymbol_returnsBTCStats() {
        var url = baseUrl + "crypto_stats/BTC";
        var response = restTemplate.getForEntity(url, CryptoStatsDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(response.hasBody());
        Assertions.assertEquals(response.getBody().symbol(), CryptoSymbolEnum.BTC.name());
        Assertions.assertTrue(response.getBody().min_price() > 0);
        Assertions.assertTrue(response.getBody().max_price() > 0);
        Assertions.assertTrue(response.getBody().newest_price() > 0);
        Assertions.assertTrue(response.getBody().oldest_price() > 0);
    }

    @Test
    public void getCryptoStats_whenGivenNonExistingSymbol_returnsBadRequest() {
        var url = baseUrl + "crypto_stats/non_existing";
        HttpClientErrorException thrown = Assertions.assertThrows(HttpClientErrorException.class, () -> {
                    restTemplate.getForEntity(url, ResponseEntity.class);
                }
        );
        Assertions.assertTrue(thrown.getMessage().contains("Bad Request"), thrown.getMessage());
    }

    @Test
    public void getCryptoNormalizedRange_whenGivenProperDate_returnsRangeSpecificDateDTO() {
        var url = baseUrl + "crypto_normalized_range/2022-01-30";
        var response = restTemplate.getForEntity(url, CryptoNormalizedRangeSpecificDateDTO.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(response.hasBody());
        Assertions.assertEquals(response.getBody().date(), LocalDate.of(2022, 01, 30));
        Assertions.assertTrue(response.getBody().normalized_range() > 0);
    }

    @Test
    public void getCryptoNormalizedRange_whenDateWithoutValues_returnsBadRequest() {
        var url = baseUrl + "crypto_normalized_range/2023-01-30";
        HttpClientErrorException thrown = Assertions.assertThrows(HttpClientErrorException.class, () -> {
                    restTemplate.getForEntity(url, ResponseEntity.class);
                }
        );
        Assertions.assertTrue(thrown.getMessage().contains("There are no entries on the specified date."), thrown.getMessage());
    }

    @Test
    public void getCryptoNormalizedRange_whenGivenWrongDateFormat_returnsBadRequest() {
        var url = baseUrl + "crypto_normalized_range/23-01-30";
        HttpClientErrorException thrown = Assertions.assertThrows(HttpClientErrorException.class, () -> {
                    restTemplate.getForEntity(url, ResponseEntity.class);
                }
        );
        Assertions.assertTrue(thrown.getMessage().contains("Bad Request"), thrown.getMessage());
    }
}