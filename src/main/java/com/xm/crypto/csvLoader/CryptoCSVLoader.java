package com.xm.crypto.csvLoader;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CryptoCSVLoader {
    List<CryptoInfoDTO> cryptoInfos = new ArrayList<>();

    public List<CryptoInfoDTO> getCryptoInfos() {
        return this.cryptoInfos;
    }

    public CryptoCSVLoader() {
        String CRYPTO_PRICES_FOLDER = "prices";     // TODO Move to application properties
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

        Resource[] resources;
        try {
            resources = patternResolver.getResources("classpath:" + CRYPTO_PRICES_FOLDER + "/*.csv");
        } catch (IOException exc) {
            System.out.println("Error opening crypto prices folder."); // TODO proper logger
            return;
        }

        for (Resource resource : resources) {
            this.cryptoInfos.addAll(csvToCryptoInfoDTO(resource));
        }
    }

    private List<CryptoInfoDTO> csvToCryptoInfoDTO(Resource resource) {
        List<CryptoInfoDTO> cryptoInfos = new ArrayList<>();
        try {
            cryptoInfos = new CsvToBeanBuilder(new FileReader(resource.getFile()))
                    .withType(CryptoInfoDTO.class)
                    .build()
                    .parse();
        } catch (Exception exc) {
            System.out.println("Could not load file: " + resource.getFilename() + "\n Exception: " + exc); // TODO proper logger
        }
        return cryptoInfos;
    }
}
