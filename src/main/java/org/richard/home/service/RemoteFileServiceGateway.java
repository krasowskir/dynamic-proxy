package org.richard.home.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class RemoteFileServiceGateway implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(RemoteFileServiceGateway.class);
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8080;

    private static final String GET_DOCUMENTS_PATH = "/api/documents";
    private static final String HTTP_SCHEME = "http";

    private static HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String createFile(byte[] file, String contentType, String fileName) throws IOException, URISyntaxException, InterruptedException {
        log.info("received request to createFile. FileName: {}, content-type: {}", fileName, contentType);
        HttpRequest httpRequest = HttpRequest.newBuilder(
                        new URI("http", null, HOSTNAME, PORT, "/api/documents/binary", null, null))
                .POST(HttpRequest.BodyPublishers.ofByteArray(file))
                .header("Content-Type", contentType)
                .header("Filename", Objects.requireNonNull(fileName))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String logoObjectId = response.body().substring("objectId: ".length());
        log.info("received response. ObjectId: {}", logoObjectId);
        return logoObjectId;
    }

    @Override
    public byte[] obtainFile(String fileName) {
        log.info("received request to obtainFile. fileName: {}", fileName);
        try {
            var fileNameParameter = "fileName=%s".formatted(fileName);
            HttpRequest httpRequest = HttpRequest.newBuilder(
                            new URI(HTTP_SCHEME, null, HOSTNAME, PORT, GET_DOCUMENTS_PATH, fileNameParameter, null))
                    .GET()
                    .header("Content-Type", "application/octet-stream")
                    .build();

            HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            log.info("finished call to obtain file");
            log.info("download file... {}", response.body().length);
            return response.body();
        } catch (URISyntaxException e) {
            log.error("could not construct uri! path: {}, hostname: {}", GET_DOCUMENTS_PATH, HOSTNAME);
            return null;
        } catch (IOException | InterruptedException e) {
            log.error("something network related went wrong! error: {}", e.getMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public byte[] obtainFileByObjectId(String objectId) {
        log.info("received request to obtainFile. objectId: {}", objectId);
        try {
            var objectIdParameter = "objectId=%s".formatted(objectId);
            HttpRequest httpRequest = HttpRequest.newBuilder(
                            new URI(HTTP_SCHEME, null, HOSTNAME, PORT, GET_DOCUMENTS_PATH, objectIdParameter, null))
                    .GET()
                    .header("Content-Type", "application/octet-stream")
                    .build();

            HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            log.info("finished call to obtain file");
            log.info("download file... {}", response.body().length);
            return response.body();
        } catch (URISyntaxException e) {
            log.error("could not construct uri! path: {}, hostname: {}", GET_DOCUMENTS_PATH, HOSTNAME);
            return null;
        } catch (IOException | InterruptedException e) {
            log.error("something network related went wrong! error: {}", e.getMessage());
            throw new RuntimeException();
        }
    }
}
