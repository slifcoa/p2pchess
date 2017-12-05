package chess;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
class HostConnection implements Runnable{
    /* socket the sever uses */
    protected int           connSockNum     =   8415;
    protected ServerSocket  myServer;
    protected Socket        clientSocket;
    protected boolean       isRunning       =   true;
    protected Thread        runningThread;
    JTextArea myOuput;
    ChessPanel myBoard;
    ClientHandler meClient;

    public HostConnection(JTextArea pointerToOutput, ChessPanel pointerBoard) {
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
        try{
            myServer = new ServerSocket(this.connSockNum);
        } catch (IOException e){
            throw new RuntimeException("Cannot Open Port 8415", e);
        }
        outputMessage("Hosting Game on \n " + myServer.getInetAddress() + ":8415");
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

            if(fromClient.startsWith("Server Started")) {
                if (meClient == null) {
                    meClient = new ClientHandler("localhost", connSockNum + 2);
                    meClient.connect();
                } else {
                    outputMessage("Already Connected");
                    meClient.sendToServer("You there?");
                }
            }

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
            if(fromClient.startsWith("turn")){
                this.myBoard.setTurn();
                this.myBoard.yourTurn = true;
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
