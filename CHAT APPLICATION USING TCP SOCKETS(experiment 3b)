import java.io.*;
import java.net.*;
import java.util.*;

public class ChatApp {
    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  To start server: java ChatApp server [port]");
            System.out.println("  To start client: java ChatApp client [host] [port]");
            return;
        }

        String mode = args[0];

        if ("server".equalsIgnoreCase(mode)) {
            int port = DEFAULT_PORT;
            if (args.length >= 2) {
                port = Integer.parseInt(args[1]);
            }
            startServer(port);
        } else if ("client".equalsIgnoreCase(mode)) {
            String host = "localhost";
            int port = DEFAULT_PORT;
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

    // ----------- Server -------------
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    private static void startServer(int port) {
        System.out.println("Chat server started on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clientHandlers.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcast(String message, ClientHandler sender) {
        synchronized(clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientName = "Anonymous";

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void run() {
            try {
                out.println("Enter your name: ");
                clientName = in.readLine();
                out.println("Welcome, " + clientName + "! Type messages to chat.");
                broadcast(clientName + " has joined the chat.", this);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    broadcast(clientName + ": " + message, this);
                }
            } catch (IOException e) {
                System.out.println(clientName + " disconnected.");
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
                clientHandlers.remove(this);
                broadcast(clientName + " has left the chat.", this);
            }
        }
    }

    // ----------- Client -------------
    private static void startClient(String host, int port) {
        try (
            Socket socket = new Socket(host, port);
            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            // Thread to listen for messages from server
            Thread readerThread = new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        System.out.println(fromServer);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            readerThread.start();

            // Main thread sends user input to server
            String userInput;
            while ((userInput = consoleIn.readLine()) != null) {
                out.println(userInput);
            }

        } catch (IOException e) {
            System.err.println("Unable to connect to server: " + e.getMessage());
        }
    }
}
