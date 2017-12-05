package chess;

import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.util.Observable;

import java.security.Security;

/**
 * Created by phil on 12/3/17.
 */
public class ClientHandler extends Observable {
    protected boolean           connected       =   false;

    //protected Socket            clientSocket;

    protected SSLSocket clientSocket;
    protected DataInputStream   myInput;
    protected DataOutputStream  myOutput;
    String IP;
    int port;

    public ClientHandler(String IP, int port){
        this.IP = IP;
        this.port = port;
    }

    public boolean connect(){
            // Registering the JSSE provider
            Security.addProvider(new Provider());

            //Specifying the Keystore details
            System.setProperty("javax.net.ssl.trustStore","myKey.ks");
            System.setProperty("javax.net.ssl.trustStorePassword","baseball");

            // Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
            // System.setProperty("javax.net.debug","all");
        //Lets pretend they typed in 127.0.0.1:8415

        //Lets pretend they typed in 127.0.0.1:8415
        try {
           // clientSocket = new Socket(IP, port);
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            clientSocket = (SSLSocket) sslSocketFactory.createSocket(IP, port);


            myOutput = new DataOutputStream(clientSocket.getOutputStream());
            myInput = new DataInputStream( new BufferedInputStream( clientSocket.getInputStream()));
//            myOutput.writeUTF("Server Started");
            connected = true;

        } catch (IOException e){
            throw new RuntimeException("Can't Connect Server", e);
        }
        return connected;
    }

    public boolean sendToServer(String myMessage){
        boolean returnBool = false;
        if(connected){
            try {
                myOutput.writeUTF(myMessage + "\n");
            } catch (IOException e){
                throw new RuntimeException("Can't send message",e);
            }
            returnBool = true;
        }
        return returnBool;
    }

}
