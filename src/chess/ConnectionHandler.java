package chess;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
class ConnectionHandler {
    /* socket the sever uses */
    static final int connSockNum = 8415;

    static ServerConnHandler serverConnHandler;
    static ClientConn clientConn;

    /******************************************************************
     * Main method for running program based on commands.
     ******************************************************************/
    public static void main(String argv[]) throws Exception {
        String option = argv[0];
        System.out.println("main started, option=" + option);

        //if hosting a game
        if(option.equals("host")){
            // socket outside of while loop for listening
//            ServerSocket welcomeSocket = new ServerSocket(connSockNum);
            System.out.println("FTP Server Stared on Port 8415");

            // infinite loop to constantly service clients
//            while (true) {
            // connection socket
            System.out.println("Waiting for connection");
//                Socket connectionSocket = welcomeSocket.accept();
            // handles individual ftp clientsls
//                serverConnHandler = new ServerConnHandler();
            //Output Connection
//                System.out.println("Serving connection " + connectionSocket.getInetAddress() +
//                        " on Port: " + connectionSocket.getPort());
            // start thread
            serverConnHandler.start();
//                clientConn = new ClientConn("localhost", 8416);
//                clientConn.start();
//            }
        }

        //if connecting to a host
        if(option.equals("connect")){

        }
    }

    public void setServerConn(ServerConnHandler serverConn){
        this.serverConnHandler = serverConn;
    }

//    public static void sendTest(){
//        clientConn.setInput();
//        System.out.println("in send test");
//    }
}

/******************************************************************
 * Inner class to handle multiple clients on threads.
 ******************************************************************/
class ServerConnHandler extends Thread {
    /* EOF character*/
    static final String EOF = "!EOF!";

    /* server file path */
    static final String serverFilePath ="./";

    /* data stream to client */
    DataOutputStream outToClient;

    /* reads data from client */
    BufferedReader inFromClient;

    /* string sent from client */
    String fromClient;

    /* tokenizer for fromClient */
    StringTokenizer tokens;

    /* first line of string */
    String firstLine;

    /* sent command from client */
    String clientCommand;

    /* name of specified file*/
    String fileName;

    /* client data string */
    String clientSentence;

    /* boolean for quitting actions */
    boolean quit = false;

    /* connection port */
    int port;

    /* connection socket between server and client */
    Socket connectionSocket;

    ChessPanel panel;

    /******************************************************************
     * Handles each client connection socket.
     *
     //     * @param connectionSocket connection socket between server and client
     ******************************************************************/
    public ServerConnHandler(ChessPanel panel) throws Exception {
        this.panel = panel;

    }

    public void setServer(int port) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(port);
        System.out.println("FTP Server Stared on Port 8415");


        this.connectionSocket = welcomeSocket.accept();
        outToClient =
                new DataOutputStream(connectionSocket.getOutputStream());
        inFromClient = new BufferedReader(new
                InputStreamReader(connectionSocket.getInputStream()));
        System.out.println("FTP Client connected " + connectionSocket.getInetAddress() +
                " Port:" + connectionSocket.getPort());
    }

    /******************************************************************
     * Runs thread.
     *
     * @return void
     ******************************************************************/
    public void run() {
        do {
            try {
                fromClient = inFromClient.readLine();
                System.out.println("fromClient: " + fromClient);
//
                clientCommand = "testing";
                tokens = new StringTokenizer(fromClient);
                //first line is the port number
                firstLine = tokens.nextToken();
                port = Integer.parseInt(firstLine);
                //second line is the command
                clientCommand = tokens.nextToken();

                //handle each different command here
                if (clientCommand.toUpperCase().equals("LIST")) {
                    listCommand(connectionSocket, port);
                }
                if (clientCommand.toUpperCase().equals("RETR")) {
                    retrCommand(connectionSocket, port);
                }
                if (clientCommand.toUpperCase().equals("STOR")) {
                    storCommand(connectionSocket, port);
                }
                if (clientCommand.toUpperCase().equals("QUIT")) {
                    quitCommand(connectionSocket, port);
                    quit = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Connection Lost..");
                //stop loop, might change later
                quit = true;
            }
        } while (quit == false);
    }

    public void setClientCommandTest(String clientCommand){
        this.clientCommand = clientCommand;
    }

    /******************************************************************
     * Lists all files in server directory.
     *
     * @param connectionSocket connection socket to client
     * @param port connection socket port
     *
     * @return void
     ******************************************************************/
    private void listCommand(Socket connectionSocket, int port) throws Exception {
        /* socket for data transfer*/
        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
        DataOutputStream dataOutToClient =
                new DataOutputStream(dataSocket.getOutputStream());

        // read files into file array
//        File folder = new File(serverFilePath);
//        File[] listOfFiles = folder.listFiles();
//
//        // iterate through each file and print name to output stream
//        for (File myFile: listOfFiles){
//            // only shows files, not directories
//            if (myFile.isFile()) {
//                dataOutToClient.writeUTF(myFile.getName());
//            }
//        }
        // end transaction
        dataOutToClient.writeUTF("test file");
        dataOutToClient.writeUTF(EOF);
        dataSocket.close();
        System.out.println("List Data Socket closed");
    }

    /******************************************************************
     * Returns specified file to client.
     *
     * @param connectionSocket connection socket to client
     * @param port connection socket port
     *
     * @return void
     ******************************************************************/
    private void retrCommand(Socket connectionSocket, int port) throws Exception {

        // establish the data socket connection
        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
        DataOutputStream dataOutToClient =
                new DataOutputStream(dataSocket.getOutputStream());

        // get the fileName from the user input
        try {
            fileName = tokens.nextToken().toString();
        } catch (NoSuchElementException e) {
            dataOutToClient.writeUTF("550 ERROR");
            dataSocket.close();
            return;
        }

        // read files into file array
        Path filepath = Paths.get(serverFilePath + fileName);
        File folders = new File(filepath.toString());

        // Checks to see if the file exists in current directory
        if (folders.exists()) {

            // Writes successful status code
            dataOutToClient.writeUTF("200 OK");

            // create fileReader to read the file line by line
            FileReader fileReader = new FileReader(folders);
            BufferedReader buffReader = new BufferedReader(fileReader);

            // Iterates through file line by line until end of file is reached
            try {
                while ((clientSentence = buffReader.readLine()) != null) {
                    dataOutToClient.writeUTF(clientSentence + System.getProperty("line.separator"));
                }

                // write the end of file indicator then close output stream
                dataOutToClient.writeUTF(EOF);
                dataOutToClient.close();

                // Close file reader and acknowledge successful download
                fileReader.close();
                System.out.println("File Downloaded Successfully!");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Writes unsuccessful status code
            dataOutToClient.writeUTF("550 ERROR");
        }

        // terminates the data socket after request
        dataSocket.close();
        System.out.println("Retrieve Data Socket closed");

    }

    /******************************************************************
     * Stores file from client.
     *
     * @param connectionSocket connection socket to client
     * @param port connection socket port
     *
     * @return void
     ******************************************************************/
    private void storCommand(Socket connectionSocket,
                             int port) throws Exception {

        try {
            fileName = tokens.nextToken();
        } catch (Exception e ) {}
        Path filePath = Paths.get(serverFilePath + fileName);

        Files.deleteIfExists(filePath);
        try {
            Files.createFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Creating File");
            outToClient.writeUTF("550 Error");
            return;
        }
        outToClient.writeUTF("200 OK");

        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);

        DataInputStream dataInFromClient =
                new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

        DataOutputStream dataOutToClient =
                new DataOutputStream(dataSocket.getOutputStream());

        StringBuffer stringBuffer = new StringBuffer();

        try {
            String line;
            while (!(line = dataInFromClient.readUTF()).equals(EOF)) {
                stringBuffer.append(line);
            }
            System.out.println("File " + fileName + " recieved from client.");
            Files.write(filePath, stringBuffer.toString().getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(filePath.toString());
        if (file.exists()) {
            dataOutToClient.writeUTF("200 OK");
            System.out.println("File saved at: " + filePath.toString());
        } else {
            dataOutToClient.writeUTF("550 Error");
        }
        dataInFromClient.close();
        dataOutToClient.close();
        dataSocket.close();
        System.out.println("Data socket closed.");

        return;
    }


    /******************************************************************
     * Quits connection to client.
     *
     * @param connectionSocket connection socket to client
     * @param port connection socket port
     *
     * @return void
     ******************************************************************/
    private void quitCommand(Socket connectionSocket, int port) throws Exception {
        try{
            System.out.println("Connection closed to client " + connectionSocket.getInetAddress()
                    + " Port:" + connectionSocket.getPort());
            connectionSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}

/****************************************************************
 * Client application that connects to a server, requests
 * a list of the server's files, retrieve a specified file
 * from the server, and send and store a specified file to
 * the server.
 *
 * @author Mike Ames
 * @author Phil Garza
 * @author Zachary Hern
 * @author Adam Slifco
 *
 * @version October 2017
 ******************************************************************/
class ClientConn extends Thread{

    /* EOF character*/
    static final String EOF = "!EOF!";

    /* filepath for client files */
    static final String clientFilePath = "./";


    /* sentence from user */
    String sentence;

    /* if socket is still open */
    boolean isOpen = true;

    /* if client still connected */
    boolean clientgo = true;

    /* port to service*/
    int port;

    /* socket for passing data */
    Socket dataSocket = null;

    String serverName;

    static String input = "";

    DataOutputStream controlOut;

    public ClientConn(String serverName, int port) throws Exception{
        this.port = port;
        this.serverName = serverName;

//                        Socket ControlSocket;
//                    //Try to Create Control Socket for connection
//                    ControlSocket = new Socket(serverName, port);
//                            controlOut = new DataOutputStream(ControlSocket.getOutputStream());
    }

    @Override
    public void run() {

        // user greeting
        System.out.println("Welcome Team Awesome's Super Cool FTP Server!\n\n" +
                "Please connect to server with: " +
                "CONNECT <server name/serverName address> <server port>" +
                "\nFor Example: CONNECT localhost 8415" +
                "\nThen type command from command list below" +
                "\nLIST (list files on server)" +
                "\nRETR <filename> (retrieves specified file from server)" +
                "\nSTOR <filename> (stores specified file on server)" +
                "\nQUIT (closes connection and quits program)");

        /* User input */
        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));

        // while the socket is open and client wishes to be connected
        while (clientgo) {
            //Get input from user
            try {
                sentence = inFromUser.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* tokenizer for user input */
            StringTokenizer tokens = new StringTokenizer(sentence);
            //Take first token as command
            String myCommand = tokens.nextToken().toUpperCase();
            if (myCommand.equals("CONNECT") && tokens.countTokens() == 2) {

                /* name of server */
                //Secodn Token - Port Third
                String serverName = tokens.nextToken();
                port = Integer.parseInt(tokens.nextToken());


                Socket ControlSocket;
                try {
                    //Try to Create Control Socket for connection
                    ControlSocket = new Socket(this.serverName, port);

                    /* data passed to server */
                    controlOut =
                            new DataOutputStream(ControlSocket.getOutputStream());

                    /* data passed to client */
                    DataInputStream controlIn =
                            new DataInputStream(
                                    new BufferedInputStream(
                                            ControlSocket.getInputStream()));

                    //If you made it this far your connected
                    System.out.println("Connected to Server.");
                    isOpen = true;
                    // line typed by user
                    while (isOpen) {
                        sentence = inFromUser.readLine();
                        tokens = new StringTokenizer(sentence);
                        switch (tokens.nextToken().toUpperCase()) {
                            case "LIST":
                                if (input.equals("list")) {
                                    list(port, "8418 list", controlOut, dataSocket);
                                    System.out.println("input has been changed to " + input);
                                }
                                break;
                            case "LIST:":
                                list(port, sentence, controlOut, dataSocket);
                                break;
                            case "RETR":
                                retr(port, sentence, controlOut, dataSocket);
                                break;
                            case "RETR:":
                                retr(port, sentence, controlOut, dataSocket);
                                break;
                            case "STOR":
                                stor(port, sentence, controlOut, controlIn, dataSocket);
                                break;
                            case "STOR:":
                                stor(port, sentence, controlOut, controlIn, dataSocket);
                                break;
                            case "QUIT":
                                quit(port, sentence, controlOut);
                                isOpen = false;
                                clientgo = false;
                                break;
                            case "QUIT:":
                                quit(port, sentence, controlOut);
                                isOpen = false;
                                clientgo = false;
                                break;
                            default:
                                System.out.println("Invalid Command");
                        }

                    }
                } catch (Exception e) {
                    //If you can't connect its a bad name or port
                    System.out.println("Server not available or Bad Name/Port");
                    System.out.println("You can try to connect again");
                    isOpen = false;
                }
//            } else if (myCommand.equals("QUIT")|| myCommand.equals("QUIT")){
//                //No Connection Made. So nothing todo.
//                System.out.println("GoodBye!");
//                clientgo = false;
//            } else if (myCommand.equals("CONNECT")){
//                System.out.println("Not enough connection parameters");
//            }
//            else{
//                System.out.println("You Need to connect to a server first");
//            }
            }
        }
    }

    protected void setInput() throws Exception{
        controlOut.writeBytes("move " +  port);
        System.out.println("inside setinput");
    }
    /******************************************************************
     * Lists all files in server's directory.
     *
     * @param port connection socket port
     * @param sentence user input
     * @param controlOut output stream to server
     * @param dataSocket socket for sending/receiving data
     *
     * @return void
     ******************************************************************/
    private static void list(int port,
                             String sentence,
                             DataOutputStream controlOut,
                             Socket dataSocket) throws Exception {
        // Create server socket
        int dPort = port + 2;
        ServerSocket welcomeData = new ServerSocket(dPort);
        System.out.println(clientFilePath);

        // write user sentence to server
        controlOut.writeBytes("move " +  port);

        // instantiate dataSocket
        dataSocket = welcomeData.accept();
        DataInputStream inData =
                new DataInputStream(
                        new BufferedInputStream(dataSocket.getInputStream()));
        try {
            /* first UTF line from server */
            String serverData = inData.readUTF();

            // start printing file list
            System.out.println("Files on server: \n" + serverData);

            // while server doesn't pass EOF character
            while (!serverData.equals(EOF)) {

                // continue reading each line and printing file name
                serverData = inData.readUTF();

                // dont print end of file character
                if (!serverData.equals(EOF))
                    System.out.println(serverData);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        welcomeData.close();
        dataSocket.close();
        endOfCommand();
    }

    /******************************************************************
     * Retrieves specified file from server.
     *
     * @param port connection socket port
     * @param sentence user input
     * @param controlOut output stream to server
     * @param dataSocket socket for sending/receiving data
     *
     * @return void
     ******************************************************************/
    private static void retr(int port,
                             String sentence,
                             DataOutputStream controlOut,
                             Socket dataSocket) throws Exception {
        // Create server socket
        int dPort = port + 2;
        ServerSocket welcomeData = new ServerSocket(dPort);
        controlOut.writeBytes(dPort + " " + sentence + " " + '\n');
        String[] getFileName = sentence.split(" ", 2);

        // Establish connection with data socket
        dataSocket = welcomeData.accept();
        // Read user input
        DataInputStream inData =
                new DataInputStream(
                        new BufferedInputStream(dataSocket.getInputStream()));
        StringBuffer stringBuffer = new StringBuffer();

        // get the Filepath
        Path filePath = Paths.get(clientFilePath + getFileName[1]);

        // Writes the file to the client
        try {
            // Retrieves the Status code
            String status = inData.readUTF().toString();
            System.out.println(status);

            // Checks to see if the file was found
            if (status.equals("200 OK")) {
                System.out.println("Downloading File...");

                // Writes/downloads the file line by line
                String line;
                while (!(line = inData.readUTF()).equals(EOF)) {
                    stringBuffer.append(line);
                }
                Files.write(filePath, stringBuffer.toString().getBytes());
                System.out.println("File Downloaded!");
            } else {
                System.out.println("Error. File could not be downloaded.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // closes client side of the data socket after request
        welcomeData.close();
        dataSocket.close();
        endOfCommand();
    }

    /******************************************************************
     * Stores specified file on server.
     *
     * @param port connection socket port
     * @param sentence user input
     * @param controlOut output stream to server
     * @param controlIn input stream from server
     * @param dataSocket socket for sending/receiving data
     *
     * @return void
     ******************************************************************/
    private static void stor(int port,
                             String sentence,
                             DataOutputStream controlOut,
                             DataInputStream controlIn,
                             Socket dataSocket) throws Exception {

        int dPort = port + 2;

        StringTokenizer tokens = new StringTokenizer(sentence);
        String fileName;

        // get the filename from the user input
        try {
            fileName = tokens.nextToken(); // pass the connect command
            fileName = tokens.nextToken();
        } catch (NoSuchElementException e) {
            System.out.println("No filename specified");
            return;
        }

        // Create a file object with the path of the file. Some code from:
        // http://www.avajava.com/tutorials/lessons/
        // how-do-i-read-a-string-from-a-file-line-by-line.html
        File file = new File(clientFilePath + fileName);

        if (!file.exists() || file.isDirectory()) {
            System.out.println("No such file or directory");
            return;
        }

        //Create a socket to listen on
        ServerSocket welcomeData = new ServerSocket(dPort);
        //Pass the server the port the client is listening on, and the command
        controlOut.writeBytes(dPort + " " + sentence + " " + '\n');

        //check to see if the server successfully created a file to save the content to
        String serverFileCreationError = controlIn.readUTF().toString();
        if (serverFileCreationError.equals("550 Error")) {
            System.out.println("Error creating the file on the server side. Exiting");
            endOfCommand();
            welcomeData.close();
            dataSocket.close();
            return;
        }
        //Accept the data socket from the server
        dataSocket = welcomeData.accept();

        DataOutputStream dataControlOut =
                new DataOutputStream(dataSocket.getOutputStream());

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;

        //Read from the file and store in a String Buffer
        try {
            while ((line = bufferedReader.readLine()) != null) {
                dataControlOut.writeUTF(line +
                        System.getProperty("line.separator"));
            }
            dataControlOut.writeUTF(EOF);
            fileReader.close();
            System.out.println("Uploading file . . .");
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        DataInputStream inFromServer = new DataInputStream(dataSocket.getInputStream());
        String status = inFromServer.readUTF().toString();

        if (status.equals("200 OK")) {
            System.out.println("File Uploaded!");
        } else {
            System.out.println("Error. File not uploaded.");
        }
        inFromServer.close();
        welcomeData.close();
        dataControlOut.close();
        dataSocket.close();
        endOfCommand();
        return;
    }

    /******************************************************************
     * Quits the application
     *
     * @param port connection socket port
     * @param sentence user input
     * @param controlOut output stream to server
     *
     * @return void
     ******************************************************************/
    private static void quit(int port,String sentence,DataOutputStream controlOut) throws Exception {
        //Tells the Server to close the connection on the port.
        try{
            controlOut.writeBytes(port + " " + sentence + " " + "\n");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            controlOut.close();
        }
    }
    /*****************************************************************
     * prints command list upon completion of each command
     *
     * @return void
     ******************************************************************/
    private static void endOfCommand() {
        System.out.println("\nWhat would you like to do next?" +
                "\nLIST (list files on server)" +
                "\nRETR <filename> (retrieves specified file from server)" +
                "\nSTOR <filename> (stores specified file on server)" +
                "\nQUIT (closes connection and quits program)");
    }
}

