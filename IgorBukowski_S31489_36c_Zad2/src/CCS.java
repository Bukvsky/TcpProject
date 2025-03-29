import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CCS {
    private static int udpPort;
    private static int tcpPort;
    private static MetricsAnalyzer metricsAnalyzer = new MetricsAnalyzer();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar CCS.jar <port>");
            System.exit(0);
        }

        try{
            udpPort = Integer.parseInt(args[0]);
            tcpPort=udpPort;
            ExecutorService clientPool = Executors.newCachedThreadPool();
            new Thread(()-> launchUDP(udpPort)).start();
            launchTCP(tcpPort,clientPool);

        }catch (NumberFormatException e){
            System.err.println("Invalid port number");
            return;
        }
    }

    public static void launchUDP(int port){
            try(DatagramSocket socket = new DatagramSocket(port)){
                System.out.println("UDP discovery service started on port " + port);
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        socket.receive(packet);
                        String mess = new String(packet.getData(),0, packet.getLength());
                        System.out.println("Received message: " + mess);
                        if(mess.startsWith("CCS DISCOVER")){
                            byte[] response = "CCS FOUND".getBytes();
                            DatagramPacket responsePacket = new DatagramPacket(
                                    response, response.length, packet.getAddress(), packet.getPort());
                            socket.send(responsePacket);
                        }

                    }catch (IOException e) {
                        System.err.println("Error while receiving or sending UDP packet: " + e.getMessage());
                    }

                }

            }catch (SocketException e) {
                System.err.println("Error creating UDP socket: " + e.getMessage());
                throw new RuntimeException(e);
            }
    }
    public static void launchTCP(int port,ExecutorService executorService){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP service started on port " + port);

            new Thread(() -> metricsAnalyzer.startReporting(10000)).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                metricsAnalyzer.incrementClientCount();
                executorService.execute(new Client(clientSocket, metricsAnalyzer));
            }
        } catch (IOException e) {
            System.err.println("Error in TCP service: " + e.getMessage());
        }
    }

}
