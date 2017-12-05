package chess;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

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
class FindConnection implements Runnable {
    /* socket the sever uses */
    protected int           connSockNum     =   8415;
    protected ServerSocket  myServer;
    protected Socket        clientSocket;
    protected boolean       isRunning       =   true;
    protected Thread        runningThread;
    JTextArea myOuput;
    ChessPanel myBoard;
    ClientHandler meClient;
    String IP;
    int port;

    public FindConnection(String IP, int port, JTextArea pointerToOutput, ChessPanel pointerBoard) {
        this.myOuput = pointerToOutput;
        myBoard = pointerBoard;

        this.IP = IP;
        this.port = port;
        //todo assuming port is 8417, it would be 8415 for client connection
        //change later
        meClient = new ClientHandler(IP, port - 2);
        meClient.connect();
    }

    /******************************************************************
     * Main method for running program based on commands.
     ******************************************************************/
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        try{
            myServer = new ServerSocket(port);
            meClient.sendToServer("Server Started");
        } catch (IOException e){
            throw new RuntimeException("Cannot Open Port 8415", e);
        }
        outputMessage("Hosting Game on \n " + myServer.getInetAddress() + ":8417");
        while(isRunning){
            try {
                clientSocket = this.myServer.accept();
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
            String fromClient;
            fromClient = inFromClient.readUTF();
            DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());

            if(fromClient.startsWith("move")){
                String moveStr = fromClient.substring(4);
                String[] coords = moveStr.split("");
                Move move = new Move(new Square(Integer.parseInt(coords[0]),
                        Integer.parseInt(coords[1])),
                        new Square(Integer.parseInt(coords[2]),
                                Integer.parseInt(coords[3])));
                myBoard.move(move);
            }
            if(fromClient.startsWith("chat")){
                outputMessage(fromClient.substring(4));
            }



            outToClient.writeUTF("Server 404OK");
        }
    }


    private void outputMessage(String myMessage){
        myOuput.append((myMessage + "\n"));
    }

    public void sendChat(String message){
        meClient.sendToServer("chat" + message);
    }

    public void sendMove(int x1, int y1, int x2, int y2){
        String message = "move" + x1 + "" + y1 + "" + x2 + "" + y2;
        meClient.sendToServer(message);
    }

}
