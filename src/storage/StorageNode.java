package storage;

import java.io.*;
import java.net.*;

public class StorageNode {

    private static final String MASTER_HOST = "localhost";
    private static final int MASTER_PORT = 9000;

    public static void main(String[] args) throws IOException {

        int nodePort = (args.length == 0) ? 9001 : Integer.parseInt(args[0]);

        // Register with master
        Socket socket = new Socket(MASTER_HOST, MASTER_PORT);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        dos.writeUTF("REGISTER " + nodePort);
        System.out.println("[STORAGE] Master response: " + dis.readUTF());

        socket.close();

        // Start storage server
        ServerSocket serverSocket = new ServerSocket(nodePort);
        System.out.println("[STORAGE] Node running on port " + nodePort);

        while (true) {
            Socket client = serverSocket.accept();
            new Thread(() -> handleClient(client)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            String request = dis.readUTF();
            System.out.println("[STORAGE] Received: " + request);

            // STORE chunk
            if (request.startsWith("STORE")) {
                String chunkId = request.split(" ")[1];
                int size = dis.readInt();

                FileOutputStream fos = new FileOutputStream("storage_" + chunkId);

                byte[] buffer = new byte[1024];
                int totalRead = 0;

                while (totalRead < size) {
                    int bytesRead = dis.read(buffer, 0, Math.min(buffer.length, size - totalRead));
                    if (bytesRead == -1) break;

                    fos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }

                fos.close();
                System.out.println("[STORAGE] Stored " + chunkId);
            }

            // FETCH chunk
            else if (request.startsWith("FETCH")) {
                String chunkId = request.split(" ")[1];

                File file = new File("storage_" + chunkId);
                byte[] data = new byte[(int) file.length()];

                FileInputStream fis = new FileInputStream(file);
                fis.read(data);
                fis.close();

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(data.length);
                dos.write(data);
                dos.flush();

                System.out.println("[STORAGE] Sent " + chunkId);
            }

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}