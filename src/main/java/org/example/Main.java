package org.example;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception {

        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

        try (ServerSocket serverSocket = new ServerSocket(8989)) { // стартуем сервер один(!) раз
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ) {
                    System.out.println("подключились");
                    String word = in.readLine();
                    String[] get = java.net.URLDecoder.decode(word, StandardCharsets.UTF_8).split(" ");

                    List<PageEntry> entries = engine.search(get[1].replace("/", ""));
                    String jsonEntries = new Gson().toJson(entries);
                    out.print("HTTP/1.1 200 \r\n"); // Version & status code
                    out.print("Content-Type: text/plain;charset=UTF-8\r\n"); // The type of data
                    out.print("Connection: close\r\n"); // Will close stream
                    out.print("\r\n"); // End of headers
                    out.println(jsonEntries);
                } catch (Exception ex) {

                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }


        // здесь создайте сервер, который отвечал бы на нужные запросы
        // слушать он должен порт 8989
        // отвечать на запросы /{word} -> возвращённое значение метода search(word) в JSON-формате
    }
}