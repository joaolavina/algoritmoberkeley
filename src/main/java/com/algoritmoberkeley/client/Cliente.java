package com.algoritmoberkeley.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int porta = 12345;

        while (true) {
            try(Socket socket = new Socket(host, porta);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream())) {
            
                    
            } catch (IOException e) {
                System.err.println("Erro de conexão com o servidor: " + e.getMessage());
            }
        }
    }
}
