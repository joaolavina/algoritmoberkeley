package com.algoritmoberkeley.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int porta = 12345;

        long relógio = System.currentTimeMillis() + (long) (Math.random() * 20000) - 10000;
        System.out.println("Relógio inicial: " + relógio);

        while (true) {
            try(Socket socket = new Socket(host, porta);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream())) {
            
                in.readLong();
                
                out.writeLong(relógio);
                long correçãoRelógio = in.readLong();

                relógio += correçãoRelógio;
                System.out.println("Relógio ajustado para: " + new Date(relógio));

            } catch (IOException e) {
                System.err.println("Erro de conexão com o servidor: " + e.getMessage());
            }
        }
    }
}
