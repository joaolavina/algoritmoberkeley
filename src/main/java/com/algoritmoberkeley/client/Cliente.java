package com.algoritmoberkeley.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Time;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int porta = 12345;

        long relógio = System.currentTimeMillis() + (long) (Math.random() * 86400000) - 43200000;
        System.out.println("Relógio inicial: " + relógio + " " + new Time(relógio));
        
        while (true) {
            try (Socket socket = new Socket(host, porta);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream())) {

                socket.setKeepAlive(true);

                while (!socket.isClosed() && socket.isConnected()) {
                    in.readLong();

                    out.writeLong(relógio);
                    out.flush();
                    long correcaoRelogio = in.readLong();

                    relógio += correcaoRelogio;
                    System.out.println("Relógio ajustado para: " + new Time(relógio));
                }
            } catch (IOException e) {
                System.err.println("Erro de conexão com o servidor: " + e.getMessage());
            }
        }
    }
}
