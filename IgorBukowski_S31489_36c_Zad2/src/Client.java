import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    private final Socket clientSocket;
    private final MetricsAnalyzer stats;

    public Client(Socket socket, MetricsAnalyzer stats) {
        this.clientSocket = socket;
        this.stats = stats;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String command;
            while ((command = in.readLine()) != null) {
                processCommand(command, out);
            }
        } catch (IOException e) {
            System.err.println("Client connection error: " + e.getMessage());
        } finally {
            closeSocket();
        }
    }

    private void processCommand(String command, PrintWriter out) {
        String[] parts = command.split(" ");
        if (parts.length != 3) {
            sendError(out);
            return;
        }

        String operation = parts[0];
        int arg1, arg2;

        try {
            arg1 = Integer.parseInt(parts[1]);
            arg2 = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            sendError(out);
            return;
        }

        int result;
        switch (operation) {
            case "ADD":
                result = arg1 + arg2;
                stats.incrementOperationCount("ADD");
                break;
            case "SUB":
                result = arg1 - arg2;
                stats.incrementOperationCount("SUB");
                break;
            case "MUL":
                result = arg1 * arg2;
                stats.incrementOperationCount("MUL");
                break;
            case "DIV":
                if (arg2 == 0) {
                    sendError(out);
                    return;
                }
                result = arg1 / arg2;
                stats.incrementOperationCount("DIV");
                break;
            default:
                sendError(out);
                return;
        }

        stats.addResult(result);
        out.println(result);
        System.out.println("Processed command: " + command + " -> Result: " + result);
    }

    private void sendError(PrintWriter out) {
        out.println("ERROR");
        stats.incrementErrorCount();
    }

    private void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}
