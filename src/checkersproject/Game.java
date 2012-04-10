/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkersproject;

//import java.util.Vector;
import java.util.ArrayList;

public class Game {
    private int[][] boardData = new int[9][9];
    public String turn = "min";
    public int selX;
    public int selY;
    //public int treeLimit = 0;
    
    public void Game(){
        
    }
        
    public void startUp(){
        int j;
        int i;
        int shifter=0;
        
        for(j=0;j<3;j++){
            for(i=0;i<8;i++){
                if(shifter%2==1) boardData[i][j] = 1;
                else boardData[i][j] = 0;
                shifter++;
            }
            if(j%2==1) shifter--;
            else shifter++;
        }
        //Max and empty pieces, I added the empty pieces 0 here so they can be ovewritten below and not the other way around
        
        shifter=0;
        for(j=7;j>4;j--){
            for(i=0;i<8;i++){
                if(shifter%2==0) boardData[i][j] = -1;
                shifter++;
            }
            if(j%2==0) shifter--;
            else shifter++;
        }
        //Min pieces
    }
    
    public int[][] getBoardData(){
        return cloneData(boardData);
    }
    
    public int[][] cloneData(int[][] bd){
        int[][] newData = new int [9][9];
        
        for(int j=0;j<9;j++){
            for(int i=0;i<9;i++){
                newData[i][j] = bd[i][j];
                //System.out.println(bd[i][j] +" turned to "+ newData[i][j]);
            }
        }
        
        return newData;
    }
    
    public void kingPiece(int x, int y, int[][] bd){
        if(Math.abs(bd[x][y])==1)
            bd[x][y] *= 2;
    }
    
    public int[][] makeMove(int[] moveCommand, int[][] bd){
        int value;
        int[][] newbd = cloneData(bd);
        
        int x = moveCommand[0];
        int y = moveCommand[1];
        int newx = moveCommand[2];
        int newy = moveCommand[3];
        int isEating = moveCommand[4];
        
        newbd[8][6] = 0; newbd[8][7] = 0; newbd[8][8] = 0;
        newbd[0][8] = -1; newbd[1][8] = -1; newbd[2][8] = -1; newbd[3][8] = -1;
        newbd[4][8] = -1; newbd[5][8] = -1; newbd[6][8] = -1; newbd[7][8] = -1;
        
        if(newbd[x][y] != 0 && newbd[newx][newy] == 0){
            
            if(newbd[x][y]==-1 && newy==0) kingPiece(x, y, newbd);
            else if(newbd[x][y]==1 && newy==7) kingPiece(x, y, newbd);
            
            value = newbd[x][y];
            newbd[x][y] = 0;
            newbd[newx][newy] = value;
            
            if(isEating==1){
                newbd[(x+newx)/2][(y+newy)/2] = 0;
                int xAdder,yAdder;

                //if the last piece that moved can still eat anything around it, lock it.
                for(yAdder=-2;yAdder<3;yAdder++){
                for(xAdder=-2;xAdder<3;xAdder++){
                    if(Math.abs(xAdder)==2 && Math.abs(yAdder)==2 && canEat(x,y,x+xAdder,y+yAdder,bd)){
                        newbd[0][8] = newx;
                        newbd[1][8] = newy; //assign ghost slots to find the piece that is locked
                        newbd[8][8] = value; //tells you who is playing next, min or max
                        newbd[8][7] = 1; //is this an eat combo? :true (+1)
                        break;
                    }
                }
                }
                
                if(newbd[0][8]==-1) newbd[8][8] = -value; //pass the turn to the next person
            }
            
            else{
                newbd[8][8] = -value; //pass the turn to the next person
            }
            
        }
        
        return newbd;
    }
    
    public boolean inBounds(int x,int y){
        
        if(x>=0 && x<=7 && y>=0 && y<=7) return true;
        
        return false;
    }
    
    public int getCount(int[][] bd, int player){
        int result = 0;
        
        for(int y=0;y<8;y++){
        for(int x=0;x<8;x++){
            if((bd[x][y]>0 && player==1) || (bd[x][y]<0 && player==-1)){
                
                result+=1;
                
            }
        }
        }
        
        return result;
    }
    
    public int valueMove(int[][] oldbd, int[] moveCommand){
        //the format for int[] move is [x, y, new x, new y,isEatMove(1 or -1)]
        int x = moveCommand[0];
        int y = moveCommand[1];
        int newx = moveCommand[2];
        int newy = moveCommand[3];
        int isEat = moveCommand[4];
        
        int result = 0;
        int player = 0;
        int[][] newbd = makeMove(moveCommand,oldbd);
        
        if(oldbd[x][y]>0) player = 1; //max player
        else if(oldbd[x][y]<0) player = -1; //min player
        
        //if true then this was an eat move, refer to the format above
        if(isEat==1) {
            result += 5*player;

            //if the piece that was eaten was a king, give it more points
            if(oldbd[(x+newx)/2][(y+newy)/2] == -2*player){
                result += 2*player;
            }
        }
        
        //if the player is just a pawn trying to get to the end
        if((newy>y && oldbd[x][y]==1) || (newy<y && oldbd[x][y]==-1)) result += 2*player;

        //if true then the piece was moved next to a wall at a critical time (if the enemy has least 4 more pieces than the player)
        if((newx==0 || newx==7) && getCount(oldbd,-player)-getCount(oldbd,player)>4) result += 2*player;
        
        //else it's better to move in towards the middle
        else if(newx>0 && newx<7) result += 1*player;

        //if true then the piece was kinged
        if(oldbd[x][y]==1*player && newbd[newx][newy]==2*player) result += 3*player;
        
        //if the piece has to leave the wall undefended
        if((x==0 && player==1) || (x==7 && player==-1)) result -= 4*player;

        /*if 2 spaces forward depending on player is within board and contains an enemy pawn, this is a tactical move because now
        the enemy cannot move closer to him for fear of eating.*/
        if( (inBounds(newx+2,newy+2*player) && newbd[newx+2][newy+2*player]==-1*player)
          || (inBounds(newx-2,newy+2*player) && newbd[newx-2][newy+2*player]==-1*player) ) result += 1*player;
        
        //if the piece moved next to an opponent that can eat it.
        if( (inBounds(newx+1,newy+1*player) && newbd[newx+1][newy+1*player]==-1*player && canEat(newx+1,newy+1*player,newx-1,newy+2*player,newbd))
          || (inBounds(newx-1,newy+1*player) && newbd[newx-1][newy+1*player]==-1*player && canEat(newx-1,newy+1*player,newx+1,newy+2*player,newbd)) )
                    result -= 5*player;
        
        //if none of the above intelligent moves have been made then the value of the move is just 1
        //if(result==0) result += 1*player;
        
        //if the move is infact not a move (judging by the isEat factor which should either be 1 or -1)
        if(moveCommand[4]==0) result = 0;

        //System.out.println(result+" is for a move from "+x+" "+y+" to "+newx+" "+newy);
        return result;
    }
    
    public int[] maxMove(int[][] bd, int depth){
        int[] bestMove = {0,0,0,0,0,0};
        int bestValue = 0;
            
        if(gameEnded(bd) || depth > 8){
            
        }
        else{
            depth+=1;
            ArrayList<int[]> moveList = generateMoves(bd,1);
            
            //the format for int[] move is [x, y, new x, new y,isEatMove(1 or -1)].
            int x, y, newx, newy;
            
            for(int i=0;i<moveList.size();i++){
                
                x = moveList.get(i)[0];
                y = moveList.get(i)[1];
                newx = moveList.get(i)[2];
                newy = moveList.get(i)[3];
                
                int[] move = moveList.get(i);
                int moveValue = valueMove(bd,move);
                int[][] resultbd = makeMove(move,bd);
                
                int[] minsMove = minMove(resultbd,depth);
                int minsValue = minsMove[5];
                
                if(move!=null && minsMove!=null){
                    if(bestMove[4]==0) bestMove = move;
                    if(bestValue<moveValue+minsValue){
                        bestMove = move;
                        bestValue = moveValue+minsValue;
                        System.out.println("A best move for max was found: "+x+" "+y+" "+newx+" "+newy);
                        System.out.println(" of value "+bestValue+" at depth "+depth);
                    }
                }
            }
        }
        
        return bestMove;
    }
    
    public int[] minMove(int[][] bd, int depth){
        int[] bestMove = {0,0,0,0,0,0};
        int bestValue = 0;
        
        if(gameEnded(bd) || depth > 4){
            
        }
        else{
            depth+=1;
            ArrayList<int[]> moveList = generateMoves(bd,-1);
            
            //the format for int[] move is [x, y, new x, new y,isEatMove(1 or -1)].
            int x, y, newx, newy;
            
            for(int i=0;i<moveList.size();i++){
                
                x = moveList.get(i)[0];
                y = moveList.get(i)[1];
                newx = moveList.get(i)[2];
                newy = moveList.get(i)[3];
                
                int[] move = moveList.get(i);
                int moveValue = valueMove(bd,move);
                int[][] resultbd = makeMove(move,bd);
                
                int[] maxsMove = maxMove(resultbd,depth);
                int maxsValue = maxsMove[5];
                
                if(move!=null && maxsMove!=null){
                    if(bestMove[4]==0) bestMove = move;
                    if(bestValue>moveValue+maxsValue){
                            bestMove = move;
                            bestValue = moveValue+maxsValue;
                            System.out.println("A best move for min was found: "+x+" "+y+" "+newx+" "+newy);
                            System.out.println(" of value "+bestValue+" at depth "+depth);
                    }
                }
            }
        }
        return bestMove;
    }
    
    public boolean gameEnded(int[][] bd){
        boolean maxPresent = false;
        boolean minPresent = false;
        
        for(int y=0;y<8;y++){
        for(int x=0;x<8;x++){
            
            if(bd[x][y]>0) maxPresent = true;
            if(bd[x][y]<0) minPresent = true;
            
            if(maxPresent && minPresent) return false;
            
        }
        }
        
        return true;
    }
    
    public ArrayList<int[]> generateMoves(int[][] bd,int player){
        //again the format for int[] moveCommand is [x, y, new x, new y,isEatMove(1 or -1)]
        
        ArrayList<int[]> moveList = new ArrayList();
        
        int[] moveCommand = {0,0,0,0,0,0};
        int xAdder;
        int yAdder;
        
        //check for eat moves first
        for(int y=0;y<8;y++){
        for(int x=0;x<8;x++){
            
            //if the piece is +1 or +2 when the player is 1(MAX) -or- if the piece is -1 or -2 when the player is -1(MIN)
            if( (bd[x][y]>=1 && player==1) || (bd[x][y]<=-1 && player==-1) ){
                    
                for(yAdder=-2;yAdder<3;yAdder++){
                for(xAdder=-2;xAdder<3;xAdder++){
                    if(Math.abs(xAdder)==2 && Math.abs(yAdder)==2 && canEat(x,y,x+xAdder,y+yAdder,bd)){
                        moveCommand[0]=x; 
                        moveCommand[1]=y;
                        moveCommand[2]=x+xAdder; 
                        moveCommand[3]=y+yAdder;
                        moveCommand[4]=1; 
                        
                        moveList.add(moveCommand.clone());
                    }
                }
                }
            }
            
        }
        }
        
        //if the move list is empty then the AI is not obliged to make an eat move. Now look for normal moves.
        if(moveList.isEmpty()){
            for(int y=0;y<8;y++){
            for(int x=0;x<8;x++){

                if( (bd[x][y]>=1 && player==1) || (bd[x][y]<=-1 && player==-1) ){

                    for(yAdder=-1;yAdder<2;yAdder++){
                    for(xAdder=-1;xAdder<2;xAdder++){
                        if(Math.abs(xAdder)==1 && Math.abs(yAdder)==1 && canMove(x,y,x+xAdder,y+yAdder,bd)){
                            moveCommand[0]=x; 
                            moveCommand[1]=y;
                            moveCommand[2]=x+xAdder; 
                            moveCommand[3]=y+yAdder;
                            moveCommand[4]=-1; 

                            moveList.add(moveCommand.clone());
                        }
                    }
                    }
                }

            }
            }
        }
        
        return moveList;
    }
    
    public boolean canMove(int x, int y, int newx, int newy, int[][] bd){
        boolean result = false;
            
        if(inBounds(newx,newy) && bd[newx][newy]==0
           && Math.abs(x-newx)==1){
                
            //System.out.println(bd[x][y]);
            switch(bd[x][y]) {
                case -1: 
                    if(newy-y==-1) result = true;
                    break;
                case -2:
                    if(newy-y==-1 || newy-y==1) result = true;
                    break;
                case 1:
                    if(newy-y==1) result = true;
                    break;
                case 2:
                    if(newy-y==-1 || newy-y==1) result = true;
                    break;
            }
            
        }
        
        return result;
        
    }
    
    public boolean canEat(int x, int y, int newx, int newy, int[][] bd){
        boolean result = false;
            
        if(inBounds(newx,newy) && bd[newx][newy]==0
           && Math.abs(x-newx)==2 && Math.abs(y-newy)==2){

            switch(bd[x][y]) {
                case -1: 
                    if(bd[(x+newx)/2][(y+newy)/2]>0 && (newy<y || bd[7][8]==1)) result = true;
                    break;
                case -2:
                    if(bd[(x+newx)/2][(y+newy)/2]>0) result = true;
                    break;
                case 1:
                    if(bd[(x+newx)/2][(y+newy)/2]<0 && (newy>y || bd[7][8]==1)) result = true;
                    break;
                case 2:
                    if(bd[(x+newx)/2][(y+newy)/2]<0) result = true;
                    break;
            }

        }
        
        return result;
    }
    
    public void clickCell(int x, int y){
        if(inBounds(x,y)){
            if(selX>-1 && boardData[selX][selY]<0){
                int[] moveCommand = {selX,selY,x,y,0};
                
                if(canEat(selX, selY, x, y, boardData) && turn.equals("min")){
                    moveCommand[4] = 1;
                    boardData = makeMove(moveCommand, boardData);
                    //doAI();
                }
                else if(canMove(selX, selY, x, y, boardData) && turn.equals("min")){
                    boardData = makeMove(moveCommand, boardData);
                    //doAI();
                }
                
                if(boardData[8][8]>0){
                    turn = "max";
                    doAI();
                }
                
                selX = -1; selY = -1;
            }
            else selX = x; selY = y;
        }
    }
    
    public void doAI(){
        //treeLimit = 0;
        int[] moveCommand = maxMove(cloneData(boardData),0);
        
        //System.out.println("The piece in "+x+" "+y+" was supposed to move to "+newx+" "+newy);
        
        boardData = makeMove(moveCommand,boardData);
        
        turn = "min";
    }
}
