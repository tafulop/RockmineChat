/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockminechat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author fulop
 */
public class FXMLDocumentController implements Initializable {
    
    ServerSocket serverSocket;
    Socket clientSocket;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    Thread  listenerThread;
    
    int port = 32457;
    
    @FXML
    private Label connectionStatusLabel;
    
    @FXML
    private CheckBox actAsServerCheckbox;
    
    @FXML
    private TextField userNameTextField;

    @FXML
    private TextField connectIPText;
    
    @FXML
    private TextArea chatHistoryText;
    
    @FXML
    private TextArea messageToSend;
    
    @FXML
    private void handleConnectButtonClick() {
        System.out.println("connect button clicked:" + connectIPText.getText());
        
        if(actAsServerCheckbox.isSelected()){
            connectAsServer();
        }else{
            connectAsClient();
        }
        
    }
    
    @FXML
    private void sendButtonClicked(){
        System.out.println("Send btn clicked: " + messageToSend.getText());
        
        if(oos == null)return;
        
        try {
            oos.writeObject(userNameTextField.getText() + ":\n" + messageToSend.getText());
            messageToSend.clear();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
  
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    void connectAsClient(){
        try {
            clientSocket = new Socket(connectIPText.getText() ,port);
            clientSocket.setKeepAlive(true);
            
            System.out.println("Connected to server.");
            connectionStatusLabel.setText("CONNECTED.");
            
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());
            
            Runnable listener = new Runnable(){
                @Override
                public void run() {
                    
                    while(true){
                        try {
                            Object read = ois.readObject();
                        if(read instanceof String){
                            chatHistoryText.appendText((String)read + "\n");
                        }
                        
                        Thread.sleep(100);
                        
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }   catch (InterruptedException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                    
                }
            };
                    
            
            listenerThread = new Thread(listener);
            listenerThread.start();

            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    void connectAsServer(){
            
            Runnable listener = new Runnable(){
                @Override
                public void run() {
                   
                    try {
                        serverSocket = new ServerSocket(port);
            
                        clientSocket = serverSocket.accept();
                        
                        System.out.println("client connected.");
                        connectionStatusLabel.setText("CONNECTED.");

                        clientSocket.setKeepAlive(true);
                        oos = new ObjectOutputStream(clientSocket.getOutputStream());
                        ois = new ObjectInputStream(clientSocket.getInputStream());

                        while(true){
                            Object read = ois.readObject();
                        if(read instanceof String){
                            chatHistoryText.appendText((String)read + "\n");
                        }
                        Thread.sleep(100);
                        }
                        
                         
                        
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            };
            
            listenerThread = new Thread(listener);
            listenerThread.start();
        
    }
    
}
