package chess;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class ChessPanel extends JPanel {
	//todo maybe do something with this
	HostConnection hostConn;
	FindConnection findConn;
	Boolean isHost = false;
	String username;
	Boolean yourTurn;


	//menu items for connecting to games
	private JMenuBar menuBar;
	private JMenu connectMenu;
	private JMenuItem hostGameItem;
	private JMenuItem findGameItem;

	private JButton[][] board;
	private ChessModel model;

	private ImageIcon[] bIcons;
	private ImageIcon[] wIcons;

	private JButton reset;
	private JButton undo;
	protected JButton disconnect;
	//todo test button replace later with letting user host game on specific port
	private JButton hostTest;
	private JLabel turn;
	private int messageCode;

	private JPanel boardpanel;
	private JPanel buttonpanel;
	private JPanel eastPanel;

	private JPanel chatPanel;
    private JTextArea output;
    private JTextField input;

	private ArrayList<Move> moveHistory;

	/*server connection handler*/
	private HostConnection serverConnHandler;
	private ServerHandler myServer;
	private ClientHandler meClient;

	public ChessPanel(ChessModel model) {
		menuBar = new JMenuBar();
		//Build the first menu.
		connectMenu = new JMenu("Play Online");
		menuBar.add(connectMenu);

		//a group of JMenuItems
		hostGameItem = new JMenuItem("Host Game");
		findGameItem = new JMenuItem("Find Game");
		connectMenu.add(hostGameItem);
		connectMenu.add(findGameItem);

		this.model = model;
		this.yourTurn = false;

		ButtonListener buttonListener = new ButtonListener();
		//set listeners for menu items
		hostGameItem.addActionListener(buttonListener);
		findGameItem.addActionListener(buttonListener);

		//set ChessPanel layout
		this.setLayout(new BorderLayout());
		//add menu to panel
		this.add(menuBar, BorderLayout.NORTH);

		//Create JPanel for chess board
		this.boardpanel = new JPanel(new BorderLayout());
		this.boardpanel.setPreferredSize(new Dimension(600, 600));
		this.boardpanel.setLayout(new GridLayout(8, 8, 1, 1));
		this.createBoard(this.boardpanel, buttonListener);
		this.add(boardpanel, BorderLayout.WEST);

		//Create JPanel for Buttons
		this.buttonpanel = new JPanel(new GridLayout(4,1,16,16));
		this.buttonpanel.setPreferredSize(new Dimension(200, 300));

//		//todo replace this functionality with joptionpane that hosts on specified port
//		hostTest = new JButton("Host on port 8415");
//		this.hostTest.addActionListener(buttonListener);
//		this.buttonpanel.add(this.hostTest);

		//Create and add Reset Button
		reset = new JButton("Reset Game");
		this.reset.addActionListener(buttonListener);
		this.buttonpanel.add(this.reset);

		//Create and add Undo Button
		undo = new JButton("Undo Move");
		this.undo.addActionListener(buttonListener);
		this.buttonpanel.add(this.undo);

		//Create and add disconnect button
		disconnect = new JButton("Disconnect");
		this.disconnect.addActionListener(buttonListener);
		this.buttonpanel.add(this.disconnect);

		//Create and add Turn Label
		turn = new JLabel("Turn: ");
		this.turn.setForeground(Color.WHITE);
		this.buttonpanel.add(this.turn);

		//Create panel for Chat messenger
		this.chatPanel = new JPanel(new BorderLayout());
		this.chatPanel.setPreferredSize(new Dimension(200,300));

		this.output = new JTextArea();
		this.input = new JTextField();
		this.input.addActionListener(buttonListener);

		//this.output.set
		this.output.setEditable(false);
		this.chatPanel.add(this.output);
		this.chatPanel.add(this.input, BorderLayout.SOUTH);

		//Create parent panel for Button and Chat Panel
		this.eastPanel = new JPanel(new BorderLayout());
		this.eastPanel.add(this.buttonpanel, BorderLayout.NORTH);
		this.eastPanel.add(this.chatPanel, BorderLayout.SOUTH);
		this.add(this.eastPanel, BorderLayout.EAST);

		this.createChessIcons();
		this.reset();

	}

	//Populates the GUI grid and add's listener's to each square
	private void createBoard(JPanel panel, ButtonListener listener) {
		Color[] colors = { Color.RED, Color.LIGHT_GRAY};
		JButton b;

		this.board = new JButton[8][8];
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				b = new JButton();
				b.setBackground(colors[(r + c) % 2]);
				b.addActionListener(listener);
				panel.add(b);
				this.board[r][c] = b;
			}
		}
	}

	private JButton buttonAt(Square s) {
		return this.board[s.row][s.column];
	}

	//Load all of the images for the pieces
	private void createChessIcons() {
		// Sets the ImageIcon for the Black Player Pieces
		ImageIcon bRook = new ImageIcon("./src/images/bRook.png");
		ImageIcon bKnight = new ImageIcon("./src/images/bKnight.png");
		ImageIcon bBishop = new ImageIcon("./src/images/bBishop.png");
		ImageIcon bQueen = new ImageIcon("./src/images/bQueen.png");
		ImageIcon bKing = new ImageIcon("./src/images/bKing.png");
		ImageIcon bPawn = new ImageIcon("./src/images/bPawn.png");
		ImageIcon wRook = new ImageIcon("./src/images/wRook.png");
		ImageIcon wKnight = new ImageIcon("./src/images/wKnight.png");
		ImageIcon wBishop = new ImageIcon("./src/images/wBishop.png");
		ImageIcon wQueen = new ImageIcon("./src/images/wQueen.png");
		ImageIcon wKing = new ImageIcon("./src/images/wKing.png");
		ImageIcon wPawn = new ImageIcon("./src/images/wPawn.png");

		ImageIcon[] blackIcons = { bRook, bKnight, bBishop, bQueen, bKing, bBishop, bKnight, bRook, bPawn };
		this.bIcons = blackIcons;

		ImageIcon[] whiteIcons = { wRook, wKnight, wBishop, wQueen, wKing, wBishop, wKnight, wRook, wPawn };
		this.wIcons = whiteIcons;
	}

	//Resets the Icon pieces back to starting positions
	private void reset() {
		model = new ChessModel();
		moveHistory = new ArrayList<Move>();

		turn.setText("Turn: " + model.currentPlayer());
		for (int c = 0; c < 8; c++) {
			this.board[0][c].setIcon(bIcons[c]);
		}

		for (int c = 0; c < 8; c++) {
			this.board[1][c].setIcon(bIcons[8]);
		}

		for (int r = 2; r < 6; r++) {
			for (int c = 0; c < 8; c++) {
				this.board[r][c].setIcon(null);
			}
		}
		for (int c = 0; c < 8; c++) {
			this.board[7][c].setIcon(wIcons[c]);
		}

		for (int c = 0; c < 8; c++) {
			this.board[6][c].setIcon(wIcons[8]);
		}
	}

	//Updates turn JLabel when invoked
	public void setTurn(){
		//turn.setText("Turn: " + model.currentPlayer());
		if(isHost){
			if(model.currentPlayer() == Player.WHITE){
				turn.setText("It's Your Turn");
			}
			else{turn.setText("Opponent's turn");}
		} else{
			if(model.currentPlayer() == Player.BLACK){
				turn.setText("It's Your Turn");
			}
			else{turn.setText("Opponent's turn");}
		}

	}

	//Promotes the pawn Icon to a queen Icon
	private void promote(Square s) {
		if (model.pieceAt(s).player().equals(Player.WHITE)) {
			buttonAt(s).setIcon(wIcons[3]);
			System.out.println("white");
		} else if (model.pieceAt(s).player().equals(Player.BLACK)) {
			buttonAt(s).setIcon(bIcons[3]);
		}
	}

	//Moves the Icon piece
	protected void move(Move m) {
		//Move the piece Icon
		m.fromPieceIcon = (ImageIcon) buttonAt(m.from).getIcon();
		m.toPieceIcon = (ImageIcon) buttonAt(m.to).getIcon();

		this.model.move(m);
		
		if (model.promote(m.to)) {
			promote(m.to);
		} else {
			buttonAt(m.to).setIcon(m.fromPieceIcon);
		}

		//set previous location of piece to null
		this.buttonAt(m.from).setIcon(null);
		//add the move to history
        this.moveHistory.add(m);
        this.model.setNextPlayer();
        this.setTurn();
		if(this.yourTurn){
			this.yourTurn = false;
		} else{
			this.yourTurn = true;
		}
	}

	//Erases the last move
	private void undoMove() {
		if (moveHistory.size() > 0) {
			int n = moveHistory.size() - 1;

			Move m = moveHistory.get(n);
			model.undo(m);

			buttonAt(m.from).setIcon(m.fromPieceIcon);
			buttonAt(m.to).setIcon(m.toPieceIcon);

			model.setNextPlayer();
			this.setTurn();
			moveHistory.remove(m);
		}
	}

	//Sets the messageCode variable to proper prompt when invoked
	private void displayMessage(IChessPiece piece) {
		if (messageCode == 1) {
			JOptionPane.showMessageDialog(null, "It is not your turn.");
		}
		else if (messageCode == 2) {
			if (model.currentPlayer() == Player.WHITE) {
				JOptionPane.showMessageDialog(null, "White is in check.");
			}
			else if (model.currentPlayer() == Player.BLACK) {
				JOptionPane.showMessageDialog(null, "Black is in check.");
			}
		}
		else if (messageCode == 3) {
			if (model.currentPlayer() == Player.WHITE) {
				JOptionPane.showMessageDialog(null, "CheckMate: Black wins");
			}
			else if (model.currentPlayer() == Player.BLACK) {
				JOptionPane.showMessageDialog(null, "CheckMate: White wins");
			}
			this.gameOverDialog();
		}
		else if (model.getMessage() == 1) {
			JOptionPane.showMessageDialog(null, "Invalid move; the King is placed in check.");
		}
		else if (model.getMessage() == 2) {
			JOptionPane.showMessageDialog(null, "Invalid move; the King remains in check.");
		}
		else if (model.getMessage() == 3) {
			JOptionPane.showMessageDialog(null, "" + piece.type() + ":  Invalid move.");
		}
	}

	//Handles all of the prompting and logic when the game is over
	private void gameOverDialog() {

		int confirm = JOptionPane.showOptionDialog(null, "Would you like to play again?", "Game Over",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

		if (confirm == JOptionPane.YES_OPTION) {
			this.reset();
		}
		if (confirm == JOptionPane.NO_OPTION || confirm == JOptionPane.CLOSED_OPTION) {
			System.exit(1);
		}
	}

	//Returns the square on the grid of selected piece
	private Square getEventSquare(ActionEvent event) {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				if (board[r][c] == event.getSource()) {
					return new Square(r, c);
				}
			}
		}
		return null;
	}

	//Checks to see if there is an opponent piece at new position
	private boolean moveToOpponentPiece( Move move)
	{
		if(move.toPiece != null){
			playerSound(this.model.currentPlayer());
			return true;
		}
		return false;
	}

	//Method that handles playing correct audio when invoked
	private void playerSound(Player p){
		String file = "";
		 if(p == Player.WHITE){
		 	file = "audio/mario_here_we_go.wav";
		 }

		 if(p == Player.BLACK){
		 	file = "audio/mparty8_luigi_02.wav";
		 }
		 
		 if(this.model.inCheck(p) && this.model.isCheckMate()){
			 file = "audio/smb_gameover.wav";
		 }
		
		try {
         // Open an audio input stream.
         URL url = this.getClass().getClassLoader().getResource(file);
         AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
         // Get a sound clip resource.
         Clip clip = AudioSystem.getClip();
         // Open audio clip and load from the audio input stream.
         clip.open(audioIn);
         clip.start();
      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      }
      catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
		
	}

	/*sets server connection handler for panel*/
	public void setServerConnHandler(HostConnection serverConnHandler){
		this.serverConnHandler = serverConnHandler;
	}

	/*Call this method after player selects host game and enters in port number to host on*/
	public void hostGame(int port) throws Exception {

		hostConn = new HostConnection(this.output, this);
		hostConn.connSockNum = port;
		Thread newThread = new Thread(hostConn);
		newThread.start();
		isHost = true;

	}

	public void connectGame(String IP, int port){

		findConn = new FindConnection(IP, port, this.output, this);
		Thread newThread2 = new Thread(findConn);
		newThread2.start();
	}

	// inner class that represents action listener for buttons
	private class ButtonListener implements ActionListener {

		Square fromSquare;
		Square eventSquare;
		Color backgroundColor;
		boolean fromTo;
		//String username;

		public ButtonListener() {
			fromTo = false;
		}

		public void actionPerformed(ActionEvent event) {
			Move thisMove;
			Square toSquare;

			if(hostGameItem == event.getSource()){
				//create custom joptionpane
				JTextField userName = new JTextField();
				JTextField portNumber = new JTextField();
				final JComponent[] inputs = new JComponent[] {
						new JLabel("Username"),
						userName,
						new JLabel("Port Number"),
						portNumber
				};
				int result = JOptionPane.showConfirmDialog(null, inputs, "Enter username and port number to host on.", JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					try {
						hostGame(Integer.parseInt(portNumber.getText()));
						hostGameItem.setEnabled(false);
						findGameItem.setEnabled(false);
						username = userName.getText();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			if(findGameItem == event.getSource()){
				//create custom joptionpane
				JTextField userName = new JTextField();
				JTextField portNumber = new JTextField();
				JTextField serverAddress = new JTextField();
				final JComponent[] inputs = new JComponent[] {
						new JLabel("Username"),
						userName,
						new JLabel("Server IP (localhost is 127.0.0.1)"),
						serverAddress,
						new JLabel("Port Number"),
						portNumber
				};
				int result = JOptionPane.showConfirmDialog(null, inputs, "Find Game", JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					try {
						//todo create findGame() method like hostGame implementation
						connectGame(serverAddress.getText(), Integer.parseInt(portNumber.getText()));
						hostGameItem.setEnabled(false);
						findGameItem.setEnabled(false);
						username = userName.getText();
						setTurn();
						findConn.sendTurn();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			//Reset's game when new game is clicked
			if (reset == event.getSource()) {
//				movePiece();
				/*
					fromTo = false;

				//Show Prompt for starting a new game
				int confirm = JOptionPane.showOptionDialog(null, "Are you sure you want to start a new game?",
						"Reset Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null,
						null);

				//Reset's game if yes was clicked
				if (confirm == JOptionPane.YES_OPTION) {
					reset();
				}*/
			}

			//perform Logic for Undo Button
			if (undo == event.getSource()) {
				if (fromTo == true) {
					fromTo = false;
					buttonAt(eventSquare).setBackground(backgroundColor);
				}
				undoMove();
			}
			if (input == event.getSource()){
				outputMessage("You: " +  input.getText());
				if(isHost)
					hostConn.sendChat(username + ": " + input.getText());
				else
					findConn.sendChat(username + ": " + input.getText());

				input.setText("");
			}

			//perform Logic for disconnect button
			if(disconnect == event.getSource()){
				if(isHost) {
					hostConn.meClient.sendToServer("Move");
				}
				else
					findConn.meClient.sendToServer("Move");

			}

			//If there's a piece on the cell clicked
			eventSquare = getEventSquare(event);
			if (eventSquare != null) {
				if (!fromTo) {
					fromSquare = eventSquare;

					if (model.pieceAt(fromSquare) != null) {

						if (model.pieceAt(fromSquare).player() == Player.WHITE && yourTurn) {
//						if (model.pieceAt(fromSquare).player() == model.currentPlayer()) {
							backgroundColor = buttonAt(fromSquare).getBackground();
							buttonAt(fromSquare).setBackground(Color.BLUE);
							messageCode = 0;
							fromTo = true;
						}
						else {
							messageCode = 1;
							displayMessage(model.pieceAt(fromSquare));
						}
					}
				}
				else if (fromTo) {

					buttonAt(fromSquare).setBackground(backgroundColor);
					toSquare = eventSquare;

					thisMove = new Move(fromSquare, toSquare);
					if (model.isValidMove(thisMove)) {

						if(moveToOpponentPiece(thisMove)){
							playerSound(model.currentPlayer());
						}

						move(thisMove);
						//send move over connection
						if(isHost){
							hostConn.sendMove(7 - fromSquare.row,  7 - fromSquare.column, 7 - toSquare.row, 7 - toSquare.column);
						}
						else {
							findConn.sendMove(7 - fromSquare.row, 7 - fromSquare.column, 7 - toSquare.row, 7 - toSquare.column);
						}


                       // model.setNextPlayer();

						if (model.inCheck(model.currentPlayer())) {
							messageCode = 2;

							if (model.isCheckMate()) {
								messageCode = 3;
								playerSound(model.currentPlayer());
							}
						}

					}
					fromTo = false;
					displayMessage(model.pieceAt(fromSquare));

				}
			}
		}
	}

	//Method that puts text into the output Console
	public void outputMessage(String myMessage){
		output.append(myMessage + "\n");
	}

	public void movePiece(int x1, int y1, int x2, int y2){
		board[x1][y1].doClick();
		board[x2][y2].doClick();
	}

}