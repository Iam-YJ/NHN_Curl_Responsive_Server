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

public class Test {
    public static void main(String[] args) {
        JsonData jsonData = null;
        ObjectMapper mapper = new ObjectMapper();
        
        try (ServerSocket serverSocket = new ServerSocket(80)){
            while (true) {
                Socket socket = serverSocket.accept();
                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
                String jsonStr = "";
                byte[] bytes = new byte[4096];
                try(InputStream is = socket.getInputStream()) {
                    int readByteCount = is.read(bytes); // blocking
                    String message = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("message : " + message);
                    try (BufferedReader bf = new BufferedReader(new InputStreamReader(is))) {
                        String line = null;
                        while ((line = bf.readLine()) != null) {
                            System.out.println(bf.readLine());
                            jsonStr += line;
                        }
                        jsonData = new JsonData(isa, message);
                        String responseBody = jsonData.responseBody(message);
                        String jsonString = mapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(jsonData.parseJson(message));

                        try (OutputStream os = socket.getOutputStream()) {
                            bytes = responseBody.getBytes(StandardCharsets.UTF_8);
                            os.write(bytes);
                            bytes = jsonString.getBytes(StandardCharsets.UTF_8);
                            os.write(bytes);
                            os.flush();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}