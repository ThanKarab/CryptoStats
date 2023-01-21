package com.xm.crypto.csvLoader;

import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CryptoCSVLoader {
    private static final Logger logger = LoggerFactory.getLogger(CryptoCSVLoader.class);

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
            logger.error("Error opening crypto prices folder: " + CRYPTO_PRICES_FOLDER + "\n Exception: " + exc);
            return;
        }
        loadCryptoInfos(resources);
    }

    public CryptoCSVLoader(Resource[] resources) {
        loadCryptoInfos(resources);
    }

    private void loadCryptoInfos(Resource[] resources) {
        if (resources == null) {
            return;
        }
        for (Resource resource : resources) {
            try {
                this.cryptoInfos.addAll(csvToCryptoInfoDTO(resource));
            } catch (Exception exc) {
                logger.error("Could not load file: " + resource.getFilename() + "\n Exception: " + exc);
            }
        }
    }

    public static List<CryptoInfoDTO> csvToCryptoInfoDTO(Resource resource) throws IOException {
        return new CsvToBeanBuilder(new FileReader(resource.getFile()))
                .withType(CryptoInfoDTO.class)
                .build()
                .parse();
    }
}
