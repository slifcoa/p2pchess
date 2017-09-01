package chess;

import javax.swing.JFrame;

import javax.swing.UIManager;

public class ChessGUI {
	public static void main(String[] args){
		try { 
			
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			 UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
	            // start application
	        
	} catch (Exception e) {
	    e.printStackTrace();
	}
		
		
		JFrame frame = new JFrame("Chess Game By Adam Slifco");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ChessPanel panel = new ChessPanel();
		frame.getContentPane().add( panel);
		
		frame.pack();
		frame.setVisible(true);
	}
}
