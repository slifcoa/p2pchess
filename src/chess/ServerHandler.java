package chess;

import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;

/****************************************************************
 * Server application that can connect to several clients at a
 * time using multithreading, request a list of the server's
 * files, retrieve a specified file, from the server,
 * and send and store a specified file to the server.
 *
 * @author Mike Ames
 * @author Phil "The Chosen One" Garza aka Dipstick
 * @author Zachary Hern
 * @author Adam Slifco
 *
 * @version October 2017
 ******************************************************************/
class ServerHanlder implements Runnable{
    /* socket the sever uses */
    protected int           connSockNum     =   8415;
    protected ServerSocket  myServer;
    protected SSLSocket     clientSocket;
    protected boolean       isRunning       =   true;
    protected Thread        runningThread;
    JTextArea myOuput;
    ChessPanel myBoard;

    public ServerHanlder(JTextArea pointerToOutput, ChessPanel pointerBoard) {
        this.myOuput = pointerToOutput;
        myBoard = pointerBoard;
    }

    /******************************************************************
     * Main method for running program based on commands.
     ******************************************************************/
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        {
            // Registering the JSSE provider
            Security.addProvider(new Provider());

            //Specifying the Keystore details
            System.setProperty("javax.net.ssl.keyStore","myKey.ks");
            System.setProperty("javax.net.ssl.keyStorePassword","baseball");

            // Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
            // System.setProperty("javax.net.debug","all");
        }
        try{
            ServerSocketFactory socketFactory = SSLServerSocketFactory.getDefault();
            myServer = socketFactory.createServerSocket(this.connSockNum);
        } catch (IOException e){
            throw new RuntimeException("Cannot Open Port 8415", e);
        }
        outputMessage("Hosting Game on \n " + myServer.getInetAddress() + ":8415");
        while(isRunning){
            try {
                clientSocket = (SSLSocket) this.myServer.accept();
            } catch (IOException e){
                throw new RuntimeException("Error Accepting Client", e);
            }
            try{
                processClientRequest(clientSocket);

            } catch (Exception e){
                //DO STUFF
            }
        }
    }

    private void processClientRequest(Socket clientSocket)
            throws Exception {
        boolean ClientConnected = true;
        while(ClientConnected) {
                DataInputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
                String fromClient="";
                try {
                    fromClient = inFromClient.readUTF();
                } catch (Exception e )
                {
                    System.out.print(e.getMessage());
                }
                DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
                outputMessage(fromClient);
                if(fromClient.startsWith("You")){
                    myBoard.movePiece();
                }
                outToClient.writeUTF("Server 404OK");
            }
        }


    private void outputMessage(String myMessage){
        myOuput.append((myMessage + "\n"));
    }
}
