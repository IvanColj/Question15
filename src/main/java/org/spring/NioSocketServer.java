package org.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;

public class NioSocketServer {
    private final int port;

    public NioSocketServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("Сервер запущен на порту: " + port);

            // Обрабатываем одного клиента
            try (SocketChannel clientChannel = serverSocketChannel.accept()) {
                System.out.println("Клиент подключился");

                handleClient(clientChannel);
                // Завершаем работу сервера после обработки клиента
                System.out.println("Клиент отключился. Завершаем сервер.");
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при обработке клиента", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка работы сервера", e);
        }
    }

    private void handleClient(SocketChannel clientChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // Читаем запрос от клиента
        clientChannel.read(buffer);
        buffer.flip();
        String fileName = new String(buffer.array(), 0, buffer.limit()).trim();
        buffer.clear();

        System.out.println("Клиент запросил файл: " + fileName);

        File file = new File("D:\\" + fileName);
        if (file.exists()) {
            sendFile(clientChannel, file);
        } else {
            System.out.println("Файл не найден: " + fileName);
        }
    }

    private void sendFile(SocketChannel clientChannel, File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             FileChannel fileChannel = fis.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (fileChannel.read(buffer) > 0) {
                buffer.flip();
                clientChannel.write(buffer);
                buffer.clear();
            }
            System.out.println("Файл отправлен: " + file.getName());
        }
    }

    public static void main(String[] args) {
        new NioSocketServer(12345).start();
    }
}
