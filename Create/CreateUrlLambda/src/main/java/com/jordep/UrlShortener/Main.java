package com.jordep.UrlShortener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> { // RequestHandler é uma interface que você implementa para criar um manipulador para funções AWS Lambda (handleRequest).

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Client s3Client = S3Client.builder().build();

    @Override // sobrescreve o método handleRequest da interface RequestHandler
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) { //  Map<Chave, Valor>  ->  Map<body, Map<originalUrl, https://algumaCoisa.com.br>>

        if (input.get("body") == null) {
            throw new IllegalArgumentException("Invalid input: 'body' is required");
        }

        String body = input.get("body").toString();

//        input {
//            body : {\"originalUrl\":\"https://algumaCoisa.com.br\"}
//              headers: {...}
//        }

        Map<String, String> bodyMap;
        try {
            bodyMap = objectMapper.readValue(body, Map.class); // tranforma o body em um Map <String, String>
        } catch (JsonProcessingException exception) { // JsonProcessingException or Exception
            throw new RuntimeException("Error parsing JSON body" + exception.getMessage(), exception);
        }
        String originalUrl = bodyMap.get("originalUrl");
        String expirationTime = bodyMap.get("expirationTime");
        long expirationTimeInSeconds = Long.parseLong(expirationTime);

        String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);

        UrlData urlData = new UrlData(originalUrl, expirationTimeInSeconds);

        try {
            String urlDataJson = objectMapper.writeValueAsString(urlData);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket("bucket-urlshortner")
                    .key(shortUrlCode + ".json")
                    .build();

            s3Client.putObject(request, RequestBody.fromString(urlDataJson));
        } catch (Exception exception) {
            throw new RuntimeException("Erro saving data to S3: " + exception.getMessage(), exception);
        }

        Map<String, String> response = new HashMap<>();
        response.put("code", shortUrlCode);

        return response;
    }
}
