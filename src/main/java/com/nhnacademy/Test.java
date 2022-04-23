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
import org.json.simple.parser.ParseException;

public class Test {
    public static void main(String[] args) {
        JsonData jsonData = null;
        ObjectMapper mapper = new ObjectMapper();

        try (ServerSocket serverSocket = new ServerSocket(80)){
            while (true) {
                Socket socket = serverSocket.accept();
                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
                String jsonStr = "";
                byte[] bytes = new byte[2000];
                try(InputStream is = socket.getInputStream()) {
                    int readByteCount = is.read(bytes); // blocking
                    int count = 0;
                    String message = new String(bytes, StandardCharsets.UTF_8);
                    message = (message.split("\u0000")[0]);
                    System.out.println(message);
                    try (BufferedReader bf = new BufferedReader(new InputStreamReader(is))) {
                        String line = null;
                        List<String> ar = new ArrayList<>();
                        while ((line = bf.readLine()) != null) {
                            if(line.contains("--------------------------")){
                                if (count > 0 && line.contains(ar.get(0))) {
                                    break;
                                }
                                ar.add(line);
                                count++;
                            }
                            jsonStr += line + "\n";
                        }
                        System.out.println("while 끝");
                        jsonData = new JsonData(isa, message, jsonStr);
                        System.out.println(jsonStr);
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
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}