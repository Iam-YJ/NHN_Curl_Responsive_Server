package com.nhnacademy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WebServer {
    private static boolean isFail;
    private static boolean exitLoop;
    private static StringBuilder jsonStr;
    private static int count = 0;
    private static List<String> ar;

    public static void main(String[] args) {
        JsonData jsonData = null;
        ObjectMapper mapper = new ObjectMapper();
        jsonStr = new StringBuilder();
        try (ServerSocket serverSocket = new ServerSocket(80)) {
            while (!isFail) {
                Socket socket = serverSocket.accept();
                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
                byte[] bytes = new byte[4096];
                try (InputStream is = socket.getInputStream()) {
                    var readByteCount = is.read(bytes); // blocking
                    String message = new String(bytes, StandardCharsets.UTF_8).split("\u0000")[0];
                    readRequest(mapper, socket, isa, is, message);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void readRequest(ObjectMapper mapper, Socket socket, InetSocketAddress isa,
                                  InputStream is, String message) throws IOException {
        JsonData jsonData;
        byte[] bytes;
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(is))) {
            checkType(message, bf);

            jsonData = new JsonData(isa, message, jsonStr.toString());
            Parser parser = new Parser(jsonData);
            String responseBody = parser.responseBody();
            String jsonString = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(parser.parseJson());
            try (OutputStream os = socket.getOutputStream()) {
                bytes = responseBody.getBytes(StandardCharsets.UTF_8);
                os.write(bytes);
                bytes = jsonString.getBytes(StandardCharsets.UTF_8);
                os.write(bytes);
                os.flush();
            }
        }
    }

    private static void checkType(String message, BufferedReader bf) throws IOException {
        if (message.contains("Content-Type: multipart/form-data")) {
            String line = null;
            ar = new ArrayList<>();
            loopBuffer(bf);
        }
    }

    private static void loopBuffer(BufferedReader bf) throws IOException {
        String line;
        while (!exitLoop &&((line = bf.readLine()) != null)) {
            exitLoop = bodyDataClassification(line);
        }
    }

    private static boolean bodyDataClassification(String line) {
        if (line.contains("--------------------------")) {
            return subClassification(line);
        }
        jsonStr.append(line).append("\n");
        return false;
    }

    private static boolean subClassification(String line) {
        if (count > 0 && line.contains(ar.get(0))) {
            return true;
        }
        ar.add(line);
        count++;
        jsonStr.append(line).append("\n");
        return false;
    }
}
