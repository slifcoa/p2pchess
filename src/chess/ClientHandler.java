package chess;

import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.Security;

/**
 * Created by phil on 12/3/17.
 */
public class ClientHandler {
    protected boolean           connected       =   false;
    protected SSLSocket clientSocket;
    protected DataInputStream   myInput;
    protected DataOutputStream  myOutput;


    public boolean connect(){
        {
            // Registering the JSSE provider
            Security.addProvider(new Provider());

            //Specifying the Keystore details
            System.setProperty("javax.net.ssl.trustStore","myKey.ks");
            System.setProperty("javax.net.ssl.trustStorePassword","baseball");

            // Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
            // System.setProperty("javax.net.debug","all");
        }
        //Lets pretend they typed in 127.0.0.1:8415
        try {
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            clientSocket = (SSLSocket) sslSocketFactory.createSocket("127.0.0.1",8415);
            myOutput = new DataOutputStream(clientSocket.getOutputStream());
            myInput = new DataInputStream( new BufferedInputStream( clientSocket.getInputStream()));
            myOutput.writeUTF("CONNECTED YO\n");
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

    public String moveFromServer(){
        String returnString = "";
        if(connected){
            try{
                returnString = myInput.readUTF();
            } catch (IOException e){
                throw new RuntimeException("Can't receive",e);
            }
        }
        return returnString;
    }
}
