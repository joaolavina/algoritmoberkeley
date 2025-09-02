package com.algoritmoberkeley.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    public static void main(String[] args) {
        int porta = 12345;
        List<Socket> clientesConectados = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(porta)) {

            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(10000);

                        List<Long> temposClientes = new ArrayList<>();
                        long tempoServidor = System.currentTimeMillis();
                        temposClientes.add(tempoServidor);

                        for (Socket cliente : clientesConectados) {
                            try (
                                    DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
                                    DataInputStream in = new DataInputStream(cliente.getInputStream())) {
                                out.writeLong(0);
                                long tempoCliente = in.readLong();
                                temposClientes.add(tempoCliente);
                            } catch (IOException e) {
                            }
                        }

                        long somaTempos = 0;
                        for (Long tempo : temposClientes) {
                            somaTempos += tempo;
                        }

                        long tempoMedio = somaTempos / temposClientes.size();

                        int clienteIndex = 0;
                        for (Socket cliente : clientesConectados) {
                            long tempoCliente = temposClientes.get(clienteIndex + 1);
                            long ajuste = tempoMedio - tempoCliente;

                            try (DataOutputStream out = new DataOutputStream(cliente.getOutputStream())) {
                                out.writeLong(ajuste);
                                System.out.println("Enviado ajuste de " + ajuste + " ms para o cliente "
                                        + cliente.getInetAddress());
                            } catch (IOException e) {
                            }
                            clienteIndex++;
                        }
                    } catch (InterruptedException e) {
                        System.err.println("Thread de sincronização interrompida.");
                    }
                }
            }).start();

            while (true) {

                Socket clienteSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + clienteSocket.getInetAddress());
                clientesConectados.add(clienteSocket);
            }
        } catch (Exception e) {
        }
    }
}
