package com.algoritmoberkeley.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {
    public static void main(String[] args) {
        int porta = 12345;
        List<Socket> clientesConectados = new CopyOnWriteArrayList<>();
        Map<Socket, Long> temposClientes = new ConcurrentHashMap<>();
        Map<Socket, DataInputStream> ins = new ConcurrentHashMap<>();
        Map<Socket, DataOutputStream> outs = new ConcurrentHashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(20000);

                        if (clientesConectados.isEmpty()) {
                            System.out.println("Nenhum cliente conectado");
                            continue;
                        }

                        long tempoServidor = System.currentTimeMillis();
                        System.out.println("Relógio servidor: " + new Time(tempoServidor));
                        temposClientes.clear();

                        for (Socket cliente : clientesConectados) {
                            try {
                                DataOutputStream out = outs.get(cliente);
                                DataInputStream in = ins.get(cliente);
                                out.writeLong(0);
                                long tempoCliente = in.readLong();
                                temposClientes.put(cliente, tempoCliente);
                                System.out.println("Relógio cliente recebido: " + new Time(tempoCliente));
                            } catch (IOException e) {
                            }
                        }

                        List<Long> tempos = new ArrayList<>(temposClientes.values());
                        tempos.add(tempoServidor);

                        long somaTempos = tempos.stream().mapToLong(Long::longValue).sum();
                        long tempoMedio = somaTempos / tempos.size();

                        for (Socket cliente : clientesConectados) {
                            long tempoCliente = temposClientes.get(cliente);
                            long ajuste = tempoMedio - tempoCliente;

                            try {
                                DataOutputStream out = outs.get(cliente);
                                out.writeLong(ajuste);
                                System.out.println("Enviado ajuste de " + ajuste + " ms para um cliente");
                            } catch (IOException e) {
                            }
                        }
                    } catch (InterruptedException e) {
                        System.err.println("Thread de sincronização interrompida.");
                    }
                }
            }).start();

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + clienteSocket.getInetAddress());
                DataInputStream in = new DataInputStream(clienteSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clienteSocket.getOutputStream());

                ins.put(clienteSocket, in);
                outs.put(clienteSocket, out);
                clientesConectados.add(clienteSocket);
            }
        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}
