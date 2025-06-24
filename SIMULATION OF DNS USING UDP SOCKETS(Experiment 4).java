import java.io.*;
import java.net.*;
import java.util.*;

public class DnsSimulation {

    private static final int DNS_PORT = 5353; // common DNS port is 53, but 5353 to avoid permission issues
    private static final Map<String, String> dnsRecords = new HashMap<>();

    static {
        // Simulated DNS database
        dnsRecords.put("example.com", "93.184.216.34");
        dnsRecords.put("google.com", "142.250.190.14");
        dnsRecords.put("openai.com", "104.18.7.218");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  To start server: java DnsSimulation server [port]");
            System.out.println("  To start client: java DnsSimulation client <server_host> [port]");
            return;
        }

        String mode = args[0];

        if ("server".equalsIgnoreCase(mode)) {
            int port = DNS_PORT;
            if (args.length >= 2) port = Integer.parseInt(args[1]);
            startServer(port);
        } else if ("client".equalsIgnoreCase(mode)) {
            if (args.length < 2) {
                System.out.println("Client usage: java DnsSimulation client <server_host> [port]");
                return;
            }
            String serverHost = args[1];
            int port = DNS_PORT;
            if (args.length >= 3) port = Integer.parseInt(args[2]);
            startClient(serverHost, port);
        } else {
            System.out.println("Unknown mode: " + mode);
        }
    }

    // DNS Server simulation
    private static void startServer(int port) {
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            System.out.println("DNS Server started on port " + port);

            byte[] buffer = new byte[512];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(request);

                String domain = new String(request.getData(), 0, request.getLength()).trim();
                System.out.println("Query received for domain: " + domain);

                String ip = dnsRecords.getOrDefault(domain.toLowerCase(), "NOT_FOUND");

                byte[] responseData = ip.getBytes();
                DatagramPacket response = new DatagramPacket(
                    responseData,
                    responseData.length,
                    request.getAddress(),
                    request.getPort()
                );

                serverSocket.send(response);
                System.out.println("Responded with IP: " + ip);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // DNS Client simulation
    private static void startClient(String serverHost, int port) {
        try (DatagramSocket clientSocket = new DatagramSocket();
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            InetAddress serverAddress = InetAddress.getByName(serverHost);
            System.out.println("DNS Client started. Type domain names to query:");

            while (true) {
                System.out.print("Enter domain (or 'exit' to quit): ");
                String domain = console.readLine();
                if (domain == null || domain.equalsIgnoreCase("exit")) break;

                byte[] queryData = domain.getBytes();
                DatagramPacket queryPacket = new DatagramPacket(queryData, queryData.length, serverAddress, port);
                clientSocket.send(queryPacket);

                byte[] buffer = new byte[512];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

                clientSocket.receive(responsePacket);

                String ip = new String(responsePacket.getData(), 0, responsePacket.getLength());
                System.out.println("IP address for " + domain + " is: " + ip);
            }
            System.out.println("Client exiting.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
