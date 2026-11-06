package ClientUDP;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientUDP {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
             Scanner sc = new Scanner(System.in)) {

            InetAddress serveur = InetAddress.getByName("localhost");
            int port = 1234;

            System.out.print("Nom d'utilisateur : ");
            String nom = sc.nextLine();

            // Thread de réception (concurrent à l'envoi)
            Thread reception = new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (!socket.isClosed()) {
                    try {
                        DatagramPacket p = new DatagramPacket(buffer, buffer.length);
                        socket.receive(p);
                        System.out.println(new String(p.getData(), 0,
                                p.getLength(), StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        break; // socket fermée
                    }
                }
            });
            reception.setDaemon(true);
            reception.start();

            // Message d'inscription : le serveur enregistre ainsi le client
            envoyer(socket, serveur, port, "[" + nom + "] a rejoint le chat");

            // Boucle d'envoi
            while (true) {
                String texte = sc.nextLine();
                if (texte.equalsIgnoreCase("quit")) {
                    envoyer(socket, serveur, port, "[" + nom + "] a quitté le chat");
                    break;
                }
                envoyer(socket, serveur, port, "[" + nom + "] : " + texte);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void envoyer(DatagramSocket s, InetAddress ip, int port, String msg)
            throws Exception {
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        s.send(new DatagramPacket(data, data.length, ip, port));
    }
}