package org.spring;

import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NioSocketClient {
    private final static int BUFFER_SIZE = 1024;

    private InetSocketAddress inetSocketAddress;

    public NioSocketClient(int port) {
        try {
            inetSocketAddress = new InetSocketAddress("localhost", port);
            init();
        } catch (Exception _) {
        }
    }

    private void init() {
        try (SocketChannel socketChannel = SocketChannel.open(inetSocketAddress)) {
            socketChannel.configureBlocking(true);

            Scanner in = new Scanner(System.in);
            String fileName = in.nextLine();
            sendRequest(socketChannel, fileName);

            receiveFile(socketChannel, fileName);
        } catch (Exception _) {
        }
    }

    private void sendRequest(SocketChannel client, String fileName) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        buffer.put(fileName.getBytes());
        buffer.flip();
        client.write(buffer);
    }

    private void receiveFile(SocketChannel client, String fileName) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        try (FileOutputStream fos = new FileOutputStream("D:\\test\\" + fileName)) {
            int bytesRead;
            while ((bytesRead = client.read(buffer)) > 0) {
                buffer.flip();
                fos.write(buffer.array(), 0, bytesRead);
                buffer.clear();
            }
            System.out.println("Файл получен: D:\\test\\" + fileName);
        }
    }

    public static void main(String[] args) {
        new NioSocketClient(12345);
    }
}
