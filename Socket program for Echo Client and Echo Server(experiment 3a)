import java.io.*;
import java.net.*;

public class EchoApp {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  To start server: java EchoApp server [port]");
            System.out.println("  To start client: java EchoApp client [host] [port]");
            return;
        }

        String mode = args[0];

        if ("server".equalsIgnoreCase(mode)) {
            int port = 12345;
            if (args.length >= 2) {
                port = Integer.parseInt(args[1]);
            }
            startServer(port);

        } else if ("client".equalsIgnoreCase(mode)) {
            String host = "localhost";
            int port = 12345;

            if (args.length >= 2) {
                host = args[1];
            }
            if (args.length >= 3) {
                port = Integer.parseInt(args[2]);
            }
            startClient(host, port);

        } else {
            System.out.println("Unknown mode: " + mode);
        }
    }

    // Echo Server
    public static void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Echo Server listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(() -> {
                    try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    ) {
                        String line;
                        while ((line = in.readLine()) != null) {
                            System.out.println("Received: " + line);
                            out.println(line);
                        }
                    } catch (IOException e) {
                        System.out.println("Client disconnected.");
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Echo Client
    public static void startClient(String host, int port) {
        try (
            Socket socket = new Socket(host, port);
            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            System.out.println("Connected to Echo Server at " + host + ":" + port);
            String userInput;
            while ((userInput = consoleIn.readLine()) != null) {
                out.println(userInput);
                String response = in.readLine();
                System.out.println("Echo: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
