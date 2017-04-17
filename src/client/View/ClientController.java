package client.View;

import client.Client;
import client.Model.Message;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import server.Model.ConnectedClientInfo;

import java.awt.image.BufferedImage;
import java.net.InetAddress;

/**
 * Created by Wojciech on 11.04.2017.
 */
public class ClientController {
    private Client clientInstance;

    @FXML
    private Canvas drawingCanvas;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button clearCanvasButton;

    @FXML
    private Button sendMessageButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private TextField inputTextField;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private TableView<ConnectedClientInfo> connectedUsersTableView;

    @FXML
    private TableColumn<ConnectedClientInfo, String> nicknameCollumn;

    @FXML
    private TableColumn<ConnectedClientInfo, String> IPAddresCollumn;


    @FXML
    public void initialize(){


        nicknameCollumn.setCellValueFactory(cellData->cellData.getValue().nicknameProperty());
        IPAddresCollumn.setCellValueFactory(cellData->cellData.getValue().IPAddressProperty().asString());

        initCanvasHandlers(drawingCanvas);
        clearCanvas(drawingCanvas);


        clearCanvasButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clearCanvas(drawingCanvas);
            }
        });

        sendMessageButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Message messageToSend = new Message(clientInstance.getClientInfo(), inputTextField.getText());
                inputTextField.setText("");
                outputTextArea.appendText("You: "+messageToSend.textMassage);
                clientInstance.send(messageToSend);
            }
        });

        disconnectButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clientInstance.send(new Message(clientInstance.getClientInfo(), "DISCONNECTED", false));
                System.exit(1);
            }
        });
    }

    private void initCanvasHandlers(Canvas canvas) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        drawingCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                graphicsContext.beginPath();
                graphicsContext.moveTo(event.getX(), event.getY());
                graphicsContext.setStroke(colorPicker.getValue());
                graphicsContext.stroke();
            }
        });

        drawingCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>(){

                    @Override
                    public void handle(MouseEvent event) {
                        graphicsContext.lineTo(event.getX(), event.getY());
                        graphicsContext.setStroke(colorPicker.getValue());
                        graphicsContext.stroke();
                    }
                });

        drawingCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        WritableImage writableImage = canvas.snapshot(null, null);
                        BufferedImage image = SwingFXUtils.fromFXImage(writableImage, null);
                        graphicsContext.drawImage(SwingFXUtils.toFXImage(image, null), 0, 0);
                        clientInstance.send(new Message(clientInstance.getClientInfo(), image));
                    }
                });
    }

    private void sendCanvasSnapshot(Canvas canvas){
        WritableImage writableImage = canvas.snapshot(null, null);
        BufferedImage image = SwingFXUtils.fromFXImage(writableImage, null);
        canvas.getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(image, null), 0, 0);
        if(clientInstance!=null)
            clientInstance.send(new Message(clientInstance.getClientInfo(), image));
    }

    private void clearCanvas(Canvas canvas){
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        sendCanvasSnapshot(canvas);
    }

    public void setClientInstance(Client clientInstance) {
        this.clientInstance = clientInstance;
    }

    public TextArea getOutputTextArea() {
        return outputTextArea;
    }
    public Canvas getDrawingCanvas() { return drawingCanvas;}

}
