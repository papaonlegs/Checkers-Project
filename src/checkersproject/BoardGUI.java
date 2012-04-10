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
import java.awt.*;
import javax.swing.*;

public class BoardGUI extends JPanel {
    static final int WIDTH = 800, HEIGHT = 600;
    int[][] boardData = new int[8][8];
    
    int selX=-1;
    int selY=-1;
    
    public int getWitdh(){
        return WIDTH;
    }
    
    public int getHeight(){
        return HEIGHT;
    }
    
    public void paint(Graphics g1) {
     
        Graphics2D g = (Graphics2D) g1;
        
        g.setColor(Color.black);
        g.fillRect(0, 0, 560, 560);
        g.setColor(Color.white);
        int whiter=0;
        for(int j=0;j<8;j++){
            for(int i=0;i<8;i++){
                if(whiter%2==1){
                    g.fillRect(i*70, j*70, 70, 70);
                }
                whiter++;
                
                if(boardData[i][j]<=-1){
                    g.setColor(Color.red);
                    g.fillOval(i*70, j*70, 70, 70);
                    if(boardData[i][j]==-2){
                        g.setColor(Color.WHITE);
                        g.fillRect(i*70+30, j*70, 10, 70);
                        g.fillRect(i*70, j*70+30, 70, 10);
                    }
                    if(selX==i && selY==j){
                        g.setColor(Color.PINK);
                        g.fillOval(i*70+10, j*70+10, 50, 50);
                    }
                    g.setColor(Color.white);
                }
                else if(boardData[i][j]>=1){
                    g.setColor(Color.orange);
                    g.fillOval(i*70, j*70, 70, 70);
                    if(boardData[i][j]==2){
                        g.setColor(Color.WHITE);
                        g.fillRect(i*70+30, j*70, 10, 70);
                        g.fillRect(i*70, j*70+30, 70, 10);
                    }
                    /*if(selX==i && selY==j){
                        g.setColor(Color.yellow);
                        g.fillOval(i*70+10, j*70+10, 50, 50);
                    }*/
                    g.setColor(Color.white);
                }
            }
            if(j%2==1) whiter--;
            else whiter++;
        }
        
    }
    
    public void drawPieces(int[][] bd){
        boardData = bd;
    }
}
