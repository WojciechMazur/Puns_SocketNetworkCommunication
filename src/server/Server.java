package server;

import client.Model.ClientInfo;
import client.Model.Message;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import server.Model.ConnectedClientInfo;
import server.Model.ConnectionToClient;
import server.View.ServerController;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wojciech on 11.04.2017.
 */
public class Server extends Application {

    //Server core
    private ObservableList<ConnectionToClient> clientList = FXCollections.observableArrayList();
    private ObservableList<ConnectedClientInfo> clientInfos = FXCollections.observableArrayList();

    private LinkedBlockingQueue<Object> messageQueue;
    private ServerSocket serverSocket;
    private static Server serverInstance = null;
    private ServerController serverController;
    private Thread accept;
    private Thread messageHandling;


    public Server() {
        synchronized (Server.class) {
            if (serverInstance != null) throw new UnsupportedOperationException(getClass() + "is singleton, but constructor called more than once");
            serverInstance = this;

            messageQueue = new LinkedBlockingQueue<>();

            int serverPort = 8080;
            try {
                serverSocket = new ServerSocket(serverPort);
            } catch (BindException e) {
                System.err.println("Port " + serverPort + " already in use");
                System.exit(-1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            accept = new Thread(() -> {
                while (true) try {
                    Socket socket = serverSocket.accept();
                    clientList.add(new ConnectionToClient(socket, messageQueue));
                    serverController.getLoggerTextArea().appendText("New user : " + socket.getInetAddress() + ":" + socket.getPort() + "\n");
                    socket.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            accept.setDaemon(true);
            accept.start();

            messageHandling = new Thread(() -> {
                while (true) try {
                    Message message = (Message) messageQueue.take();

                    if (!clientInDatabase(message.from)) {
                        clientInfos.add(new ConnectedClientInfo(message.from.getNickname(), message.from.getIPAddress(), message.from.getConnectedAtPort()));
                        sendToAll(message.from);
                    }
                    if (message.drawingImage != null) {
                        BufferedImage bufferedImage = message.drawingImage;
                        serverController.clearCanvas(serverInstance.serverController.getDrawningCanvas());
                        serverController.getDrawningCanvas().getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(bufferedImage, null), 0, 0);
                        serverController.getLoggerTextArea().appendText("DrawLog::" + message.from.getIPAddress().toString().substring(1) + "[" + message.from.getNickname() + "]\n");
                    }

                    if (message.textMassage != null)
                        serverController.getLoggerTextArea().appendText("ChatLog::" + message.from.getIPAddress().toString().substring(1) + "[" + message.from.getNickname() + "]::" + message.textMassage + "\n");

                    if (message.connected == false) {
                        removeConnectionToClient(findConnectionToClient(message.from.getConnectedAtPort()));
                        removeConnectedClientInfo(findConnectedClientInfo(message.from.getNickname()));
                        sendToAll(message.from);
                    }

                    sendToAll(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            messageHandling.setDaemon(true);
            messageHandling.start();

        }
    }

    private boolean clientInDatabase(ClientInfo from) {
        for (ConnectedClientInfo clientInfo : clientInfos){
            if(clientInfo.getNickname().equals(from.getNickname()))
                return true;
        }
        return false;
    }


    public void sendToOne(int index, ConnectionToClient from,  Object message) {
        try {
            clientList.get(index).write(message);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private ConnectionToClient findConnectionToClient(int port){
        for(ConnectionToClient connection : clientList){
            if(connection.getSocket().getLocalPort()==port)
                return connection;
        }
        return null;
    }

    private ConnectedClientInfo findConnectedClientInfo(String nickname){
        for(ConnectedClientInfo client : clientInfos){
            if(client.getNickname().equals(nickname))
                return client;
        }
        return null;
    }


    private void removeConnectionToClient(ConnectionToClient connection){
        if(connection!=null)
            clientList.remove(connection);
    }

    private void removeConnectedClientInfo(ConnectedClientInfo client){
        if(client!=null) {
            clientInfos.remove(client);
            sendToAll(client);
        }
    }

    public void sendToAll(Object message){
        for(ConnectionToClient client : clientList)
            client.write(message);
    }

    //Getters and setters


    public ObservableList<ConnectionToClient> getClientList() {
        return clientList;
    }

    public ObservableList<ConnectedClientInfo> getClientInfos() {
        return clientInfos;
    }

    public LinkedBlockingQueue<Object> getMessageQueue() {
        return messageQueue;
    }

    //GUI
    private Stage primaryStage;
    private BorderPane rootLayout;


    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Puns Server");

        initRootLayout();
        showServerLayout();

        serverInstance.serverController.getLoggerTextArea().appendText("Server is running.\nListening for clients.\n");

    }

    private void showServerLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Server.class.getResource("View/ServerLayout.fxml"));
            AnchorPane serverLayout = loader.load();
            rootLayout.setCenter(serverLayout);
            this.serverController=loader.getController();
            serverController.getConnectedClientInfoTableView().setItems(clientInfos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Server.class.getResource("View/RootLayout.fxml"));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
