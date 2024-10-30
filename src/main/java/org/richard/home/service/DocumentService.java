package org.richard.home.service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.nio.channels.ClosedChannelException;

public interface DocumentService {

    String createFile(byte[] file, String contentType, String fileName) throws ConnectException, IOException, ClosedChannelException, URISyntaxException, InterruptedException;

    byte[] obtainFile(String fileName);
    byte[] obtainFileByObjectId(String objectId);
}
