package client.View;

import client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Wojciech on 16.04.2017.
 */
public class LoginDialogController {

    @FXML
    private TextField nicknameTextField;

    @FXML
    private TextField IPAddressTextField;

    @FXML
    private TextField portNumberTextField;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    private Client clientInstance;

    private boolean connectClicked =false;
    private Stage dialogStage;



    @FXML
    private void handleConfirmButtonClicked(){
        if(clientInstance==null) throw new NullPointerException("Client instance not initialized");
        clientInstance.setIPAdress(IPAddressTextField.getText().isEmpty() ? "127.0.0.1" : IPAddressTextField.getText());
        clientInstance.setPort(portNumberTextField.getText().isEmpty() ? 8080 : Integer.parseInt(portNumberTextField.getText()));
        clientInstance.setNickname(nicknameTextField.getText().isEmpty() ? "Anonymous" : nicknameTextField.getText());

        connectClicked=true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel(){
        dialogStage.close();
    }

    public Client getClientInstance() {
        return clientInstance;
    }

    public void setClientInstance(Client clientInstance) {
        this.clientInstance = clientInstance;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isConnectClicked() {
        return connectClicked;
    }
}
