package CLients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class TCPSimpleClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String[] operations = {"ADD", "SUB", "MUL", "DIV"};
            Random random = new Random();
            for (int i = 0; i < 5; i++) {
                int arg1 = random.nextInt(10) + 1;
                int arg2 = random.nextInt(10) + 1;
                String oper = operations[random.nextInt(4)];

                String request = oper + " " + arg1 + " " + arg2;
                System.out.println("Wysłano zapytanie: " + request);
                out.println(request);

                String response = in.readLine();
                System.out.println("Odpowiedź serwera: " + response);
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
