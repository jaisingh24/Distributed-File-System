package master;

import java.io.*;
import java.net.*;
import java.util.*;

public class MasterServer {

    private static final int PORT = 9000;

    private static List<Integer> storageNodes = new ArrayList<>();

    // filename → list of (chunkId:port)
    private static Map<String, List<String>> fileChunkMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("[MASTER] Running on port " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            String request = dis.readUTF();
            System.out.println("[MASTER] Received: " + request);

            // Register node
            if (request.startsWith("REGISTER")) {
                int port = Integer.parseInt(request.split(" ")[1]);
                storageNodes.add(port);
                dos.writeUTF("REGISTERED");
            }

            // Send node list
            else if (request.equals("GET_NODES")) {
                String response = String.join(",",
                        storageNodes.stream().map(String::valueOf).toList());
                dos.writeUTF(response);
            }

            // Save metadata
            else if (request.startsWith("SAVE_META")) {
                String[] parts = request.split(" ");
                String filename = parts[1];
                int chunkCount = Integer.parseInt(parts[2]);

                List<String> metadata = new ArrayList<>();

                for (int i = 0; i < chunkCount; i++) {
                    metadata.add(dis.readUTF()); // chunkId:port
                }

                fileChunkMap.put(filename, metadata);
                dos.writeUTF("META_SAVED");

                System.out.println("[MASTER] Metadata saved for " + filename);
            }

            // Get metadata
            else if (request.startsWith("GET_FILE")) {
                String filename = request.split(" ")[1];

                List<String> metadata = fileChunkMap.get(filename);

                if (metadata == null) {
                    dos.writeUTF("NOT_FOUND");
                    return;
                }

                dos.writeUTF("FOUND");
                dos.writeInt(metadata.size());

                for (String m : metadata) {
                    dos.writeUTF(m);
                }
            }

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}