package server.Model;

import javafx.beans.property.*;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created by Wojciech on 15.04.2017.
 */
public class ConnectedClientInfo {
    private final StringProperty nickname;
    private final ObjectProperty<InetAddress> IPAddress;
    private final IntegerProperty portNumber;


    public ConnectedClientInfo(){
        this(null, null, 0);
    }

    public ConnectedClientInfo(String nickname, InetAddress IPAddress, int port ){
        this.nickname = new SimpleStringProperty(nickname);
        this.IPAddress = new SimpleObjectProperty<>(IPAddress);
        this.portNumber = new SimpleIntegerProperty(port);
    }


    public String getNickname() {
        return nickname.get();
    }

    public StringProperty nicknameProperty() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public InetAddress getIPAddress() {
        return IPAddress.get();
    }

    public ObjectProperty<InetAddress> IPAddressProperty() {
        return IPAddress;
    }

    public void setIPAddress(InetAddress IPAddress) {
        this.IPAddress.set(IPAddress);
    }

    public int getPortNumber() {
        return portNumber.get();
    }

    public IntegerProperty portNumberProperty() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber.set(portNumber);
    }
}
