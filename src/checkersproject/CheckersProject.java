/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkersproject;

/**
 *
 * @author AdeizaSama
 * @editor FaroukUmar
 */

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class CheckersProject {

    /**
     * @param args the command line arguments
     */
    
    private static BoardGUI myBoard; //the display object for the board.
    private static Game game;//the object containing the game rules, mechanics and data
    
    public static void main(String[] args) {
        JFrame gameGui = new JFrame();
        
        myBoard = new BoardGUI();
        game = new Game();
        
        gameGui.setContentPane(myBoard);
        gameGui.setSize(800,600);
        gameGui.setVisible(true);
        
        game.startUp();
        myBoard.drawPieces(game.getBoardData());
        
        gameGui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        gameGui.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //method to handle the actions for the click in the game
                
                game.clickCell((e.getX()-7)/70, (e.getY()-30)/70); 
                //the less 7 and 30 in x and y respectively are for adjusting the frame errors when in windowed mode
                System.out.println("(X, Y):  " + (e.getX()-7)/70 + ", " + (e.getY()-30)/70);
                myBoard.selX = game.selX;
                myBoard.selY = game.selY;
                myBoard.drawPieces(game.getBoardData());
                myBoard.repaint();
                //if the game has a selected piece, send it to the board for painting
            }
        });
       
    }
    
}
