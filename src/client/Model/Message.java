package client.Model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Wojciech on 14.04.2017.
 */
public class Message implements Serializable {
    public ClientInfo from;
    public String textMassage;
    public boolean connected = true;

    transient public BufferedImage drawingImage;

    public Message(ClientInfo clientInfo, String textMassage, boolean connected){
        this.from=clientInfo;
        this.textMassage=textMassage;
        this.connected=connected;
    }

      public Message(ClientInfo from, String message){
          this.from=from;
          this.textMassage=message;
          drawingImage=null;
      }

      public Message(ClientInfo from, BufferedImage drawingImage){
          this.from=from;
          this.textMassage=null;
          this.drawingImage=drawingImage;
      }

      private void readObject(ObjectInputStream objectInputStream)throws IOException, ClassNotFoundException{
          objectInputStream.defaultReadObject();
          drawingImage= ImageIO.read(objectInputStream);
      }

      private void writeObject(ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException{
         objectOutputStream.defaultWriteObject();
         if(drawingImage!=null)
            ImageIO.write(drawingImage, "png", objectOutputStream);
      }
}
