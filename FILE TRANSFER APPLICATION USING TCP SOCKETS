import java.io.*;
import java.net.*;

public class FileTransferApp {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  To start server: java FileTransferApp server <port> <file_path>");
            System.out.println("  To start client: java FileTransferApp client <host> <port> <save_path>");
            return;
        }

        String mode = args[0];

        try {
            if ("server".equalsIgnoreCase(mode)) {
                if (args.length != 3) {
                    System.out.println("Server usage: java FileTransferApp server <port> <file_path>");
                    return;
                }
                int port = Integer.parseInt(args[1]);
                String filePath = args[2];
                startServer(port, filePath);

            } else if ("client".equalsIgnoreCase(mode)) {
                if (args.length != 4) {
                    System.out.println("Client usage: java FileTransferApp client <host> <port> <save_path>");
                    return;
                }
                String host = args[1];
                int port = Integer.parseInt(args[2]);
                String savePath = args[3];
                startClient(host, port, savePath);

            } else {
                System.out.println("Unknown mode: " + mode);
            }
        } catch (NumberFormatException e) {
            System.out.println("Port must be an integer.");
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        }
    }

    // Server sends the file to the connected client
    private static void startServer(int port, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File does not exist or is not a regular file: " + filePath);
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            System.out.println("Waiting for client to connect to send file: " + file.getName());

            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                try (
                    BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file));
                    BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
                    DataOutputStream dataOut = new DataOutputStream(out);
                ) {
                    // Send file name and length first
                    dataOut.writeUTF(file.getName());
                    dataOut.writeLong(file.length());

                    System.out.println("Sending file...");
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileIn.read(buffer)) != -1) {
                        dataOut.write(buffer, 0, bytesRead);
                    }
                    dataOut.flush();
                    System.out.println("File sent successfully.");
                }
            }
        }
    }

    // Client receives file and saves it
    private static void startClient(String host, int port, String savePath) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server: " + host + ":" + port);

            try (
                BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                DataInputStream dataIn = new DataInputStream(in);
            ) {
                // Receive file name and length
                String fileName = dataIn.readUTF();
                long fileSize = dataIn.readLong();

                System.out.println("Receiving file: " + fileName + " (" + fileSize + " bytes)");

                File saveFile = new File(savePath);
                if (saveFile.isDirectory()) {
                    // Save in this directory with the original filename
                    saveFile = new File(saveFile, fileName);
                }

                try (BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(saveFile))) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalRead = 0;

                    while (totalRead < fileSize && (bytesRead = dataIn.read(buffer, 0,
                            (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
                        fileOut.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                    }
                    fileOut.flush();
                    System.out.println("File received and saved to " + saveFile.getAbsolutePath());
                }
            }
        }
    }
}
