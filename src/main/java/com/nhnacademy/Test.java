package com.nhnacademy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {
    public static void main(String[] args) {
        System.out.println(args);
        ServerSocket serverSocket = null;
        JsonData jsonData = new JsonData(args);
        ObjectMapper mapper = new ObjectMapper();

        try {
            serverSocket = new ServerSocket(80);

            while (true) {
                Socket socket = serverSocket.accept();
                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();

                byte[] bytes = new byte[10000];
                InputStream is = socket.getInputStream();
                int readByteCount = is.read(bytes); // blocking
                String message = new String(bytes, 0, readByteCount, "UTF-8");

                String responseBody = jsonData.responseBody(message);
                String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData.parseJson(message));

                OutputStream os = socket.getOutputStream();
                bytes = responseBody.getBytes("UTF-8");
                os.write(bytes);
                bytes = jsonString.getBytes("UTF-8");
                os.write(bytes);

                os.flush();
                System.out.println(responseBody);
                System.out.println(jsonString);
                System.out.println("[데이터 보내기 성공]");

                is.close();
                os.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
