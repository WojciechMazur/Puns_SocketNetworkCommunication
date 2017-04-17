package server.View;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import server.Model.ConnectedClientInfo;
import server.Server;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Wojciech on 12.04.2017.
 */
public class ServerController {
    @FXML
    private Canvas drawningCanvas;

    @FXML
    private TextArea loggerTextArea;

    @FXML
    private TableView<ConnectedClientInfo> connectedClientInfoTableView;

    @FXML
    private TableColumn<ConnectedClientInfo, String> nicknameStringTableColumn;

    @FXML
    private TableColumn<ConnectedClientInfo, String> IPAddressStringTableColumn;

    @FXML
    private TableColumn<ConnectedClientInfo, String> portNumberStringTableColumn;

    public TextArea getLoggerTextArea() {
        return loggerTextArea;
    }

    public Canvas getDrawningCanvas() {
        return drawningCanvas;
    }

    public ServerController(){

    }

    @FXML
    private void initialize() {
        nicknameStringTableColumn.setCellValueFactory(cellData -> cellData.getValue().nicknameProperty());
        IPAddressStringTableColumn.setCellValueFactory(cellData -> cellData.getValue().IPAddressProperty().asString());
        portNumberStringTableColumn.setCellValueFactory(cellData->cellData.getValue().portNumberProperty().asString());
    }

    public void clearCanvas(Canvas canvas){
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void save(Canvas canvas){
        WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
        canvas.snapshot(null, writableImage);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);

        try{
            if(ImageIO.write(renderedImage, "png", new File("./output_server_image.png")));
            System.out.println("Image saved");

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public TableView<ConnectedClientInfo> getConnectedClientInfoTableView() {
        return connectedClientInfoTableView;
    }
}
