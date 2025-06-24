import java.io.*;
import java.net.*;

public class SimpleHttpClient {
    public static void main(String[] args) {
        // Example usage: download http://example.com/
        String host = "example.com";
        String path = "/";

        try (Socket socket = new Socket(host, 80)) {
            // Send HTTP GET request
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("GET " + path + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("Connection: close");
            out.println();  // blank line to end headers

            // Read response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
