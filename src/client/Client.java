package client;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Client {

    private static final String MASTER_HOST = "localhost";
    private static final int MASTER_PORT = 9000;

    public static void main(String[] args) throws Exception {

        String filePath = "test.txt";
        String filename = "test.txt";

        byte[] fileData = Files.readAllBytes(Paths.get(filePath));

        List<byte[]> chunks = splitFile(fileData, 1024);

        System.out.println("[CLIENT] Total chunks: " + chunks.size());

        List<Integer> nodes = getStorageNodes();

        List<String> metadata = new ArrayList<>();

        int nodeIndex = 0;

        for (int i = 0; i < chunks.size(); i++) {
            int port = nodes.get(nodeIndex % nodes.size());
            String chunkId = "chunk_" + i;

            sendChunk(port, chunkId, chunks.get(i));

            metadata.add(chunkId + ":" + port);

            nodeIndex++;
        }

        saveMetadata(filename, metadata);

        System.out.println("[CLIENT] Upload complete!");
    }

    private static List<Integer> getStorageNodes() throws Exception {
        Socket socket = new Socket(MASTER_HOST, MASTER_PORT);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        dos.writeUTF("GET_NODES");

        String response = dis.readUTF();
        socket.close();

        List<Integer> nodes = new ArrayList<>();

        for (String p : response.split(",")) {
            nodes.add(Integer.parseInt(p.trim()));
        }

        return nodes;
    }

    private static void sendChunk(int port, String chunkId, byte[] data) throws Exception {
        Socket socket = new Socket("localhost", port);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        dos.writeUTF("STORE " + chunkId);
        dos.writeInt(data.length);
        dos.write(data);
        dos.flush();

        socket.close();

        System.out.println("[CLIENT] Sent " + chunkId + " to port " + port);
    }

    private static void saveMetadata(String filename, List<String> metadata) throws Exception {
        Socket socket = new Socket(MASTER_HOST, MASTER_PORT);

        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        dos.writeUTF("SAVE_META " + filename + " " + metadata.size());

        for (String m : metadata) {
            dos.writeUTF(m);
        }

        System.out.println("[CLIENT] Master response: " + dis.readUTF());

        socket.close();
    }

    private static List<byte[]> splitFile(byte[] fileData, int chunkSize) {
        List<byte[]> chunks = new ArrayList<>();

        for (int i = 0; i < fileData.length; i += chunkSize) {
            int end = Math.min(fileData.length, i + chunkSize);
            chunks.add(Arrays.copyOfRange(fileData, i, end));
        }

        return chunks;
    }
}