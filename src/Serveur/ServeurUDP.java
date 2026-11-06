package Serveur;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServeurUDP {
    public static void main(String[] args) {
        // Liste des clients "connectés" (sans doublons, ordre d'arrivée)
        Set<SocketAddress> clients = new LinkedHashSet<>();

        try (DatagramSocket socket = new DatagramSocket(null)) {
            socket.bind(new InetSocketAddress(1234));
            System.out.println("Serveur de chat UDP démarré sur le port 1234...");

            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                SocketAddress expediteur = packet.getSocketAddress();
                String message = new String(packet.getData(), 0,
                        packet.getLength(), StandardCharsets.UTF_8);

                // 1. Enregistrer l'expéditeur s'il est nouveau
                if (clients.add(expediteur)) {
                    System.out.println("Nouveau client : " + expediteur);
                }
                System.out.println(expediteur + " -> " + message);

                // 2. Diffuser à tous sauf l'expéditeur
                byte[] data = message.getBytes(StandardCharsets.UTF_8);
                for (SocketAddress client : clients) {
                    if (!client.equals(expediteur)) {
                        socket.send(new DatagramPacket(data, data.length, client));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
