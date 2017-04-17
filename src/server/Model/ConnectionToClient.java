package server.Model;

import client.Model.ClientInfo;
import client.Model.Message;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Wojciech on 12.04.2017.
 */
public class ConnectionToClient {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private LinkedBlockingQueue<Object> messageQueue;



    public ConnectionToClient(Socket socket, LinkedBlockingQueue<Object> messageQueue) {
        this.socket = socket;
        this.messageQueue=messageQueue;

        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(new Message(new ClientInfo("Server", socket), "You're connected"));
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread read = new Thread(() -> {
            while (true) try {
                Message object = (Message)inputStream.readObject();
                messageQueue.put(object);
            }catch (SocketException e){
                try{
                    this.socket.close();
                }catch (IOException e1){e.printStackTrace();
            }}
            catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        read.setDaemon(true);
        read.start();
    }

    public void write(Object object) {
        try {
            outputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finalize(){
        try {
            inputStream.close();
            outputStream.close();
        }catch (IOException e){
            System.err.println("Cannot close I/O streams");
            e.printStackTrace();
        }

    }


    public Socket getSocket() {

        return socket;
    }
}
