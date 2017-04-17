package client.Model;

import client.View.ClientController;
import server.Model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Wojciech on 13.04.2017.
 */
public class ConnectionToServer {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private LinkedBlockingQueue<Object> messageQueue;

    public ConnectionToServer(Socket socket, LinkedBlockingQueue<Object> messageQueue) throws IOException {
        this.socket = socket;
        this.messageQueue = messageQueue;
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

        Thread read = new Thread(() -> {
            while (true) try {
                Object object = inputStream.readObject();
                messageQueue.put(object);
            }catch (SocketException e){
                System.out.println(e.getMessage());
                break;
            }catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        read.setDaemon(true);
        read.start();
    }

    public void write(Object object){
        try{
            outputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
