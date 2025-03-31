import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DAS {
    private static int port;
    private static List<Integer> numbers = new ArrayList<>();
    private static DatagramSocket socket;
    private static final String MASTER_PREFIX = "[Master]: ";
    private static final String SLAVE_PREFIX = "[Slave]: ";
    private static int iCounter = 0;
    private static int jCounter = 1;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java DAS <port> <number>");
            System.exit(1);
        }

        try {
            port = Integer.parseInt(args[0]);
            int number = Integer.parseInt(args[1]);

            try {
                socket = new DatagramSocket(port);
                System.out.println(MASTER_PREFIX + "Master mode started.");
                runMaster(number);
            } catch (BindException e) {
                System.out.println(SLAVE_PREFIX + "Port is already occupied. Switching to slave mode.");
                runSlave(port, number);
            }
        } catch (NumberFormatException e) {
            System.err.println("Both <port> and <number> must be valid integers.");
            System.exit(1);
        } catch (SocketException e) {
            System.err.println("Error initializing socket: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void runMaster(int var0) {
        if (var0 != 0 && var0 != -1) {
            numbers.add(var0);

        }

        try {
            byte[] buffer = new byte[4];

            while (true) {
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivedPacket);
                handleMasterMessage(receivedPacket, var0);
            }
        } catch (IOException e) {
            System.err.println("[Master]: Error during communication: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }


    private static void handleMasterMessage(DatagramPacket receivedPacket, int initialNumber) throws IOException {
        int receivedNumber = ByteBuffer.wrap(receivedPacket.getData()).getInt();
        InetAddress senderAddress = receivedPacket.getAddress();
        int senderPort = receivedPacket.getPort();

        if (receivedNumber == 0) {
            ArrayList<Integer> copy = new ArrayList<>(numbers);
            int sum = copy.stream().mapToInt(Integer::intValue).sum();
            int average = (int) Math.floor((double) sum / copy.size());
            System.out.println("[Master]: Calculated average: " + average);
            broadcastMessage(average);
        } else if (receivedNumber == -1) {
            System.out.println("[Master]: Shutdown signal received.");
            broadcastMessage(-1);
            socket.close();
            System.exit(0);
        } else {
            System.out.println("[Master]: Received number: " + receivedNumber);
            iCounter++;
            numbers.add(receivedNumber);
        }
        if (!senderAddress.equals(InetAddress.getLocalHost())) {
            byte[] ackMessage = ByteBuffer.allocate(4).putInt(1).array(); // ACK: 1
            DatagramPacket ackPacket = new DatagramPacket(ackMessage, ackMessage.length, senderAddress, senderPort);
            socket.send(ackPacket);
            System.out.println("[Master]: Sent ACK to " + senderAddress + ":" + senderPort);
        }
//
    }


    private static void runSlave(int port, int number) {
        try (DatagramSocket slaveSocket = new DatagramSocket()) {
            InetAddress masterAddress = InetAddress.getByName("127.0.0.1");
            byte[] numberMessage = ByteBuffer.allocate(4).putInt(number).array();
            DatagramPacket numberPacket = new DatagramPacket(numberMessage, numberMessage.length, masterAddress, port);
            slaveSocket.send(numberPacket);
            System.out.println("[Slave]: Sent number: " + number);

            byte[] ackBuffer = new byte[4];
            DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);

            slaveSocket.setSoTimeout(2000);

            try {
                slaveSocket.receive(ackPacket);
                int ack = ByteBuffer.wrap(ackPacket.getData()).getInt();

                System.out.println("[Slave]: Received acknowledgment ");

            } catch (java.net.SocketTimeoutException e) {
                System.out.println("[Slave]: Exiting.");
            }
        } catch (Exception e) {
            System.err.println("[Slave]: Error during communication: " + e.getMessage());
        }
    }



    private static void broadcastMessage(int message) throws IOException {
        byte[] data = ByteBuffer.allocate(4).putInt(message).array();
        InetAddress broadcastAddress = getBroadcastAddress();
        DatagramPacket packet = new DatagramPacket(data, data.length, broadcastAddress, port);
        socket.send(packet);
        System.out.println(MASTER_PREFIX + "Broadcasted message: " + message);
    }

    private static InetAddress getBroadcastAddress() throws IOException {
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
        if (networkInterface == null) {
            throw new SocketException("Network interface not found.");
        }
        for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            if (address.getBroadcast() != null) {
                return address.getBroadcast();
            }
        }
        throw new SocketException("No broadcast address found.");
    }
}
