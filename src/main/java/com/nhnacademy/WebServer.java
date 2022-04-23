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
    public static void main(String[] args) {
        JsonData jsonData = null;
        ObjectMapper mapper = new ObjectMapper();

        try (ServerSocket serverSocket = new ServerSocket(80)) {
            while (true) {
                Socket socket = serverSocket.accept();
                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
                StringBuilder jsonStr = new StringBuilder();
                byte[] bytes = new byte[4096];
                try (InputStream is = socket.getInputStream()) {
                    var readByteCount = is.read(bytes); // blocking
                    var count = 0;
                    String message = new String(bytes, StandardCharsets.UTF_8).split("\u0000")[0];

                    try (BufferedReader bf = new BufferedReader(new InputStreamReader(is))) {
                        if (message.contains("Content-Type: multipart/form-data")) {
                            String line = null;
                            List<String> ar = new ArrayList<>();
                            while ((line = bf.readLine()) != null) {
                                if (line.contains("--------------------------")) {
                                    if (count > 0 && line.contains(ar.get(0))) {
                                        break;
                                    } else {
                                        ar.add(line);
                                        count++;
                                        jsonStr.append(line).append("\n");
                                    }
                                } else {
                                    jsonStr.append(line).append("\n");
                                }
                            }
                        }

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
                if(isFail){
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}