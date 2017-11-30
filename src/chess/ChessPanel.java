package chess;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;


public class ChessPanel extends JPanel {

	private JButton[][] board;
	private ChessModel model;

	private ImageIcon[] bIcons;
	private ImageIcon[] wIcons;

	private JButton reset;
	private JButton undo;
	private JLabel turn;
	private int messageCode;

	private ArrayList<Move> moveHistory;

	public ChessPanel() {

		ButtonListener buttonListener = new ButtonListener();

		JPanel boardpanel = new JPanel();
		boardpanel.setPreferredSize(new Dimension(600, 600));
		boardpanel.setLayout(new GridLayout(8, 8, 1, 1));
		createBoard(boardpanel, buttonListener);
		add(boardpanel, BorderLayout.WEST);

		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.Y_AXIS));
		add(buttonpanel);
		
		reset = new JButton("Reset Game");
		reset.addActionListener(buttonListener);
		buttonpanel.add(reset);
		buttonpanel.add(Box.createRigidArea(new Dimension(0,20)));

		undo = new JButton("Undo Move");
		undo.addActionListener(buttonListener);
		buttonpanel.add(undo);
		buttonpanel.add(Box.createRigidArea(new Dimension(0,20)));
		
		turn = new JLabel("Turn: ");
		turn.setForeground(Color.WHITE);
		buttonpanel.add(turn);

		createChessIcons();
		reset();
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
		return board[s.row][s.column];
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
	
	private void setTurn(){
		turn.setText("Turn: " + model.currentPlayer());
	}


	private void promote(Square s) {
		if (model.pieceAt(s).player().equals(Player.WHITE)) {
			buttonAt(s).setIcon(wIcons[3]);
			System.out.println("white");
		} else if (model.pieceAt(s).player().equals(Player.BLACK)) {
			buttonAt(s).setIcon(bIcons[3]);
		}
	}

	private void move(Move m) {
		//Move the piece Icon
		m.fromPieceIcon = (ImageIcon) buttonAt(m.from).getIcon();
		m.toPieceIcon = (ImageIcon) buttonAt(m.to).getIcon();

		model.move(m);
		
		if (model.promote(m.to)) {
			promote(m.to);
		} else {
			buttonAt(m.to).setIcon(m.fromPieceIcon);
		}
		buttonAt(m.from).setIcon(null);

		moveHistory.add(m);
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
			setTurn();
			moveHistory.remove(m);
		}
	}

	//Sets the messageCode variable to proper prompt when invoked
	private void displayMessage(IChessPiece piece) {
		if (messageCode == 1) {
			JOptionPane.showMessageDialog(null, "It is not your turn.");
		} else if (messageCode == 2) {
			if (model.currentPlayer() == Player.WHITE) {
				JOptionPane.showMessageDialog(null, "White is in check.");
			} else if (model.currentPlayer() == Player.BLACK) {
				JOptionPane.showMessageDialog(null, "Black is in check.");
			}
		} else if (messageCode == 3) {

			if (model.currentPlayer() == Player.WHITE) {
				JOptionPane.showMessageDialog(null, "CheckMate: Black wins");
			} else if (model.currentPlayer() == Player.BLACK) {
				JOptionPane.showMessageDialog(null, "CheckMate: White wins");
			}

			gameOverDialog();
		} else if (model.getMessage() == 1) {
			JOptionPane.showMessageDialog(null, "Invalid move; the King is placed in check.");
		} else if (model.getMessage() == 2) {

			JOptionPane.showMessageDialog(null, "Invalid move; the King remains in check.");
		} else if (model.getMessage() == 3) {
			JOptionPane.showMessageDialog(null, "" + piece.type() + ":  Invalid move.");
		}
	}

	//Handles all of the prompting and logic when the game is over
	private void gameOverDialog() {

		int confirm = JOptionPane.showOptionDialog(null, "Would you like to play again?", "Game Over",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

		if (confirm == JOptionPane.YES_OPTION) {
			reset();
		}
		if (confirm == JOptionPane.NO_OPTION || confirm == JOptionPane.CLOSED_OPTION) {
			System.exit(1);
		}
	}

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
	
	private boolean moveToOpponentPiece( Move move)
	{
		if(move.toPiece != null){
			playerSound(model.currentPlayer());
			return true;
		}
		return false;
	}
	
	private void playerSound(Player player){
		String filename = "";
		 if(player == Player.WHITE){
		 	filename = "audio/mario_here_we_go.wav";
		 	
		 }

		 if(player == Player.BLACK){
		 	filename = "audio/mparty8_luigi_02.wav";
		 	
		 }
		 
		 if(model.inCheck(player) && model.isCheckMate()){
			 filename = "audio/smb_gameover.wav";
		 }
		
		try {
         // Open an audio input stream.
         URL url = this.getClass().getClassLoader().getResource(filename);
         AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
         // Get a sound clip resource.
         Clip clip = AudioSystem.getClip();
         // Open audio clip and load samples from the audio input stream.
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
	
	

	// inner class that represents action listener for buttons
	private class ButtonListener implements ActionListener {
		Square fromSquare;
		Color backgroundColor;
		Square eventSquare;
		boolean fromTo;

		public ButtonListener() {
			fromTo = false;
		}

		public void actionPerformed(ActionEvent event) {
			Move thisMove;
			Square toSquare;

			//Reset's game when new game is clicked
			if (reset == event.getSource()) {

				if (fromTo == true) {
					fromTo = false;
					buttonAt(eventSquare).setBackground(backgroundColor);
				}

				//Show Prompt for starting a new game
				int confirm = JOptionPane.showOptionDialog(null, "Are you sure you want to start a new game?",
						"Reset Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null,
						null);

				//Reset's game if yes was clicked
				if (confirm == JOptionPane.YES_OPTION) {
					reset();
				}
			}

			//perform Logic for Undo Button
			if (undo == event.getSource()) {
				if (fromTo == true) {
					fromTo = false;
					buttonAt(eventSquare).setBackground(backgroundColor);
				}
				undoMove();
			}

			//If there's a piece on the cell clicked
			eventSquare = getEventSquare(event);
			if (eventSquare != null) {
				if (!fromTo) {
					fromSquare = eventSquare;

					if (model.pieceAt(fromSquare) != null) {

						if (model.pieceAt(fromSquare).player() == model.currentPlayer()) {
							backgroundColor = buttonAt(fromSquare).getBackground();
							buttonAt(fromSquare).setBackground(Color.BLUE);
							messageCode = 0;
							fromTo = true;
						} else {
							messageCode = 1;
							displayMessage(model.pieceAt(fromSquare));
						}
					}
				} else if (fromTo) {

					buttonAt(fromSquare).setBackground(backgroundColor);
					toSquare = eventSquare;

					thisMove = new Move(fromSquare, toSquare);
					if (model.isValidMove(thisMove)) {

						if(moveToOpponentPiece(thisMove)){
							playerSound(model.currentPlayer());
						}
						move(thisMove);

						model.setNextPlayer();

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
					setTurn();
					
				}
			}
		}
	}

}