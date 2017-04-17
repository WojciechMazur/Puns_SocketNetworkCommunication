package client.Model;


import client.Client;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Wojciech on 15.04.2017.
 */
public class ClientInfo implements Serializable{
    private String nickname;
    private InetAddress IPAddress;
    private int connectedAtPort;

    public ClientInfo(){
        this(null, null);
    }

    public ClientInfo(String nickname, Socket socket){
        this.nickname = nickname;
        this.IPAddress = socket.getInetAddress();
        this.connectedAtPort = socket.getLocalPort();
    }

    public ClientInfo(String nickname, InetAddress inetAddress, int connectedAtPort){
        this.nickname=nickname;
        this.IPAddress=inetAddress;
        this.connectedAtPort=connectedAtPort;
    }

    public String getNickname() {
        return nickname;
    }

    public InetAddress getIPAddress() {
        return IPAddress;
    }

    public int getConnectedAtPort() {
        return connectedAtPort;
    }
}
