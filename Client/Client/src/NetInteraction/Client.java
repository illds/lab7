package NetInteraction;

import Dependency.Command;

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Client {
    public static void main(String args[]) {
        try {
            String scn = "127.0.0.1 5092";
            String[] strings = scn.trim().split(" +");
            String serverAddress = strings[0];
            int serverPort = Integer.parseInt(strings[1]);
            DatagramSocket sock = new DatagramSocket();
            SocketAddress address = new InetSocketAddress(InetAddress.getByName(serverAddress), serverPort);
            sock.connect(address);
            Ask ask = new Ask(sock, address);
            Answer answer = new Answer(sock, address);
            ClientManager clientManager = new ClientManager(ask, answer);
            ClientEvents clientEvents = new ClientEvents(clientManager);
            Command savedCommand;
            try {
                clientEvents.run();
            } catch (PortUnreachableException e) {
                while (true) {
                    savedCommand = clientManager.getCommandOut();
                    System.out.println("Сервер отключен, через 3 секунды попробуем восстановить соединение.");
                    try {
                        Thread.sleep(1000 * 3);
                    } catch (InterruptedException interruptedException) {
                    }
                    try {
                        if (savedCommand != null) {
                            clientManager.setCommandOut(savedCommand);
                        }
                        clientEvents.run();
                    } catch (PortUnreachableException x) {
                        clientManager.setFirstCommand(false);
                    }
                }
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}