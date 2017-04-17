package client;

import client.Model.ClientInfo;
import client.Model.ConnectionToServer;
import client.Model.Message;
import client.View.ClientController;
import client.View.LoginDialogController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import server.Model.ConnectedClientInfo;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.System.exit;

/**
 * Created by Wojciech on 11.04.2017.
 */
public class Client extends Application {

    //Core
    private static Client clientInstance = null;
    private ConnectionToServer server;
    private LinkedBlockingQueue<Object> messageQueue;
    private ObservableList<ConnectedClientInfo> clientInfoObservableList = FXCollections.observableArrayList();
    private Socket socket;
    private String IPAdress;
    private int port;
    private String nickname;
    private ClientController clientController;
    Thread messageHandling;
    private ClientInfo clientInfo;

    public Client() {

        synchronized (Client.class) {
            if (clientInstance != null) throw new UnsupportedOperationException(getClass() + " is singleton, but constructor called more than once");
            clientInstance = this;
        }


    }

    public void initConnection(){
        try {
            socket = new Socket(IPAdress, port);
        } catch (ConnectException e) {
            System.out.println("Cannot connect to server");
            exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientInfo = new ClientInfo(nickname, socket);

        messageQueue = new LinkedBlockingQueue<>();
        try {
            server = new ConnectionToServer(socket, messageQueue);
            server.write(new Message(clientInfo, "Connected to server"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageHandling = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) try {
                Object object = messageQueue.take();
                if (object instanceof Message) {
                    Message message = (Message) object;
                    if (!Objects.equals(message.from.getNickname(), nickname)) {
                        if (message.textMassage != null) clientController.getOutputTextArea().appendText("[" + message.from.getNickname() + "]: " + message.textMassage + "\n");
                        if (message.drawingImage != null) {
                            BufferedImage drawingSnapshot = message.drawingImage;
                            clientController.getDrawingCanvas().getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(drawingSnapshot, null), 0, 0);
                        }
                    }
                }
                else if (object instanceof ClientInfo) {
                    ClientInfo clientInfo = (ClientInfo)object;
                    ConnectedClientInfo connectedClientInfo = new ConnectedClientInfo(clientInfo.getNickname(), clientInfo.getIPAddress(), clientInfo.getConnectedAtPort());
                    if(clientInfoObservableList.contains(connectedClientInfo))
                        clientInfoObservableList.remove(connectedClientInfo);
                    else
                        clientInfoObservableList.add(connectedClientInfo);
                }
            }catch(InterruptedException e){
                    e.printStackTrace();
                }
        });

        messageHandling.setDaemon(true);
        messageHandling.start();

    }

    public void send(Object object) {
        server.write(object);
    }

    public ClientController getClientController() {
        return clientController;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setIPAdress(String IPAdress) {
        this.IPAdress = IPAdress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Thread getMessageHandling() {
        return messageHandling;
    }

    //Interface
    private Stage primaryStage;
    private BorderPane rootLayout;


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Puns ");

        initRootLayout();
        initClientLayout();
        if(showLoginDialog());
            initConnection();
    }

    private void initClientLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Client.class.getResource("View/ClientLayout.fxml"));
            AnchorPane clientLayout = loader.load();
            rootLayout.setCenter(clientLayout);

            this.clientController = loader.getController();
            this.clientController.setClientInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(server.Server.class.getResource("View/RootLayout.fxml"));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean showLoginDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Client.class.getResource("View/LoginDialog.fxml"));
            AnchorPane dialog = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Login to Puns");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialog);
            dialogStage.setScene(scene);


            LoginDialogController loginDialogController = loader.getController();
            loginDialogController.setClientInstance(clientInstance);
            loginDialogController.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public String getNickname() {
        return nickname;
    }
}

