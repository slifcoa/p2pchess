package chess;

import javax.swing.*;

public class ChessGUI {
	public static void main(String[] args){
		ChessGUI game = new ChessGUI();

		game.setLookFeel();
		//game.searchForGame();
		game.createGame();

	}

	//Displays the Search for game prompt, opens chess board if connection is established
	private static void searchForGame(){
		//Create initial Search for Game prompt
		JTextField username = new JTextField();
		JTextField ip = new JTextField();
		JTextField port = new JTextField();
		//Object for input strings
		Object[] connectionInput = {
				"Username: ", username ,
				"IP Address: ", ip,
				"Port: ", port
		};
		//Object for customizing buttons
		Object[] options = {"Host", "Connect"};

		int message = JOptionPane.showConfirmDialog(null, connectionInput, "Search For Game", JOptionPane.OK_CANCEL_OPTION);

		String user = username.getText();
		String ipA = ip.getText();
		int portNum = Integer.parseInt(port.getText());

		System.out.println(user);
		System.out.println(ipA);
		System.out.println(portNum);

		if(message == JOptionPane.CANCEL_OPTION || message == JOptionPane.CLOSED_OPTION){
			System.exit(0);
		}
		return;
	}
	private static void setLookFeel(){

		//customize UI appearance
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createGame(){
		//Set title and register exit button
		JFrame frame = new JFrame("Multiplayer Chess Game By The Gankster Boys");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Instantiate the panel and populate frame with grid
		ChessModel model = new ChessModel();
		ChessPanel panel = new ChessPanel(model);
		frame.getContentPane().add( panel);

		frame.pack();
		frame.setVisible(true);
	}
}
