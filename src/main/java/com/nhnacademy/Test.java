package com.nhnacademy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.simple.JSONObject;

public class Test {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        JsonData jsonData = new JsonData();
        ObjectMapper mapper = new ObjectMapper();

        try {
            serverSocket = new ServerSocket(80);

            while (true) {
                System.out.println("[연결 기다림]");
                Socket socket = serverSocket.accept();
                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
                System.out.println("[연결 수락함] " + isa.getHostName());

                byte[] bytes = null;
                String message = null;
                InputStream is = socket.getInputStream();
                bytes = new byte[100];
                int readByteCount = is.read(bytes); // blocking
                message = new String(bytes, 0, readByteCount, "UTF-8");

                System.out.println("[데이터 받기 성공] " + message);
                String body = jsonData.body(message);
                JSONObject jsonObject = jsonData.parseJson(message);
                String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData.parseJson(message));

                OutputStream os = socket.getOutputStream();
                bytes = body.getBytes("UTF-8");
                os.write(bytes);
                os.flush();
                System.out.println(jsonString);
                System.out.println("[데이터 보내기 성공]");

//                char[] charArr = jsonObject.toCharArray();
//                for (var i = 0; i < charArr.length; i++) {
//                    System.out.print(charArr[i]);
//                    if (charArr[i] == '}' &&
//                        charArr[i + 1] != ',') {
//                        System.out.print("\n ");
//                    }
//                    if (charArr[i] == '{') {
//                        if (charArr[i + 1] != ',') {
//                            if (charArr[i + 1] != '}') {
//                                System.out.print("\n ");
//                            }
//                        }
//                    }
//                    if (charArr[i] == ',') {
//                        System.out.print("\n ");
//                    }
//                }

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
