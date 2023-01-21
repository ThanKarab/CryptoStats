# CryptoStats
Fun project for statistics on cryptos with Java, Spring, Maven and JUnit.

### Installation

1. Install `docker` and `java17`.

1. Build the repo, run the tests and build a docker image:
    ```
    ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=cryptos
    ```

1. Start the container:
   ```
   docker run -d -p 8080:8080 --name app cryptos
   ```

1. Happy reviewing at http://localhost:8080/swagger-ui.html !


### Extra things that were NOT done:
1. Documentation in the code.
1. Better repository logic.
   1. CryptoInfoRepository store in database.
   1. Background service or db triggers to load the stats.
1. Better logs.
1. More tests.
1. More detailed swagger documentation.
