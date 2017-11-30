package chess;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class ChessGUI {
	public static void main(String[] args){
		//customize UI appearance
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
		} catch (Exception e) {
	    e.printStackTrace();
		}
		//Set title and register exit button
		JFrame frame = new JFrame("Multiplayer Chess Game By The Gankster Boys");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Instantiate the panel and populate frame with grid
		ChessPanel panel = new ChessPanel();
		frame.getContentPane().add( panel);
		
		frame.pack();
		frame.setVisible(true);
	}
}
