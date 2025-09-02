package com.algoritmoberkeley.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {
    public static void main(String[] args) {
        int porta = 12345;
        List<Socket> clientesConectados = new CopyOnWriteArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(porta)) {

            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(10000);

                        List<Long> temposClientes = new ArrayList<>();
                        long tempoServidor = System.currentTimeMillis();
                        System.out.println("Relógio servidor: " + new Time(tempoServidor));
                        temposClientes.add(tempoServidor);

                        for (Socket cliente : clientesConectados) {
                            try {
                                DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
                                DataInputStream in = new DataInputStream(cliente.getInputStream());
                                out.writeLong(0);
                                long tempoCliente = in.readLong();
                                temposClientes.add(tempoCliente);
                                System.out.println("Relógio cliente recebido: " + new Time(tempoCliente));
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

                            if (clienteIndex + 2 != temposClientes.size())
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
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}
