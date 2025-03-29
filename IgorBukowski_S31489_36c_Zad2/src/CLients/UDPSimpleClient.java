package CLients;

import java.net.*;

public class UDPSimpleClient {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            String message = "CCS DISCOVER";
            InetAddress serverAddress = InetAddress.getByName("255.255.255.255"); // Broadcast address
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), serverAddress, 1234);

            socket.send(packet);
            System.out.println("Wysłano pakiet DISCOVER.");

            byte[] buffer = new byte[1024];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String response = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Otrzymano odpowiedź: " + response);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
