package c_minimax;

import java.util.HashMap;

//author: Gary Kalmanovich; rights reserved

public class Connect4Position implements InterfacePosition {
    // This implementation is designed for at most 7 columns by 6 rows
    // It packs the entire position into a single long
    // Though, there is some sparseness to the packing
    
    // Rightmost 21=3*7 bits are for storing column sizes. (3 bits accommodates 0..7)
    // Next, going to the left 42=6*7*1 bits are binary for colors. (Either red or yellow) 
    // Finally, the left most bit is for the player
    

    private long position = 0;
    private int nC = 0;
    private int nR = 0;
    private HashMap<Long,Integer> hashedPositions;

    Connect4Position( int nC, int nR) {
        position = 0;
        this.nC = nC;
        this.nR = nR;
    }

    Connect4Position( InterfacePosition pos ) {
        position = pos.getRawPosition();
        nC       = pos.nC();
        nR       = pos.nR();
    }
    
    Connect4Position(InterfacePosition pos, HashMap<Long,Integer> hashes) {
    	this(pos);
    	hashedPositions = hashes;
    }

    private int getColumnChipCount( InterfaceIterator iPos ) { // Number of chips in column iC
        return getColumnChipCount( iPos.iC() );
    }
    
    private int getColumnChipCount( int iC ) { // Number of chips in column iC
        //TODO fill this in based on:
        // Rightmost 21=3*7 bits are for storing column sizes. (3 bits accommodates 0..7)
        // Next, going to the left 42=6*7*1 bits are binary for colors. (Either red or yellow) 
        // Finally, the left most bit is for the player
        return (int)(position >> (iC*3)) & 7; //cast to int, because we only have the last 3 bits anyway
    }
    
    @Override public int nC() { return nC; }
    @Override public int nR() { return nR; }

    @Override
    public long getRawPosition() { 
        return position;
    }

    @Override
    public int getColor( InterfaceIterator iPos ) { // 0 if transparent, 1 if red, 2 if yellow
        int  iR_ = iPos.nR()-iPos.iR()-1; // This numbers the rows from the bottom up
        return getColor( iPos.iC(), iR_, getColumnChipCount(iPos) );
    }

    private int getColor( int iC, int iR_, int nColumnChipCount ) { // 0 if transparent, 1 if red, 2 if yellow
        //TODO fill this in based on:
        // Rightmost 21=3*7 bits are for storing column sizes. (3 bits accommodates 0..7)
        // Next, going to the left 42=6*7*1 bits are binary for colors. (Either red or yellow) 
        // Finally, the left most bit is for the player
    	//we count from down to up, I believe
    	if ((iR_+1) > nColumnChipCount) //if there aren't enough chips, it must be transparent
    		return 0;
    	//multiply the below iR_ by nC because there are nC chips per row
        return (int)((position >> (21+(nC*iR_)+iC)) & 1) + 1; //add 1 to 0 for red, 1 to 1 for yellow
    }
    
    public boolean isPositionFillable(InterfaceIterator iPos) {
        int  iR_ = iPos.nR()-iPos.iR()-1; // This numbers the rows from the bottom up
        long currentChips = getColumnChipCount(iPos);
        return iR_ == currentChips; // not > and not < means ==
    }

    @Override
    public void setColor( InterfaceIterator iPos, int color ) { // color is 1 if red, 2 if yellow
        int  iC  = iPos.iC();
        int  iR  = iPos.iR();
        int  iR_ = iPos.nR()-iR-1; // This numbers the rows from the bottom up
        long currentChips = getColumnChipCount(iPos);
        //System.out.println("Setting color for: " + iR_ + " " + iC);
        if (        iR_ > currentChips) { 
            System.err.println("Error: This position ("+iC+","+iR+") cannot yet be filled.");
        } else if ( iR_ < currentChips) { 
            System.err.println("Error: This position ("+iC+","+iR+") is already filled.");
        } else {
            // Increment columnSize - this happens in the first 21 bits so we don't need to typecast
        	//make all our shift constants longs because Java will do strange things if we do not
        	long chipSizeShift = 7; //not necessary, but included for safety
        	long colorShift = 1; //is necessary
        	position = (position & ~(chipSizeShift << (iC*3))) | 
        			((currentChips+1) << (iC*3)); //clear the 3 bits we care about
        	//then OR with what we want to set it to 
            // Set the color (default is color==1)
        	long colorSet = color == 1 ? 0 : 1; //0 for red, 1 for yellow
        	//first move 21 to get pass the column sizes, then shift over by row, column
        	long movePos = 21 + (4*iR_) + iC; // 1 bit per square, 4 per row, 1 per column
//        	same as tictactoe, but 1 instead of 3 since there's only 1 bit. 
        	position = (position & ~(colorShift << movePos)) | (colorSet << movePos);
        }
    }

    @Override
    public int isWinner() {
        //      if winner, determine that and return winner, 
        //      else if draw, return 0
        //      else if neither winner nor draw, return -1
    	//This is identical to tictactoe except we have 4, however because of gravity
    	//it is a little more complicated
    	
    	//first, if we don't have enough chips, quit early
    	if (getChipCount() < 7) {
    		//you need 4 red moves and 3 yellow moves before any win is possible
    		return -1;
    	}
    	if (hashedPositions != null && hashedPositions.containsKey(position))
    		return hashedPositions.get(position);
    	
    	boolean isFull = getChipCount() == nC * nR; //you need 16 in this case to get to a draw state
    	int chipsAtTop = getColumnChipCount(0); //this will be reused, so put it outside the forloop
    	
    	//first, check the rows
    	for(int i=0; i < nR; i++) {
    		//get the leftmost piece
    		int checkRow = getColor(0,i,getColumnChipCount(0));
    		//now check whether we should actually be looking at it
    		if (checkRow > 0){
    			//if it is not transparent, then iterate through the row and check
        		for(int j=1; j <= nC-3; j++) { 
        			//get the three things adjacent to this slot
        			int colorOneAway = getColor(j,i,getColumnChipCount(j));
        			int colorTwoAway = getColor(j+1,i,getColumnChipCount(j+1));
        			int colorThreeAway = getColor(j+2,i,getColumnChipCount(j+2));
        			//if they're equal, we have a win, so quit and return the color
        			if (checkRow > 0 && checkRow == colorOneAway && checkRow == colorTwoAway && checkRow == colorThreeAway) {
        				//while it would be proper form for the below if statement to check whether the position is in the hashmap
        				//before storing it, we save about 80-90ms this way! That's because if the hashmap contains the key,
        				//we would never have reached this code in the first place. As such, the check is NOT necessary,
        				//and we can shave off a little bit of time that way.
        				if (hashedPositions != null)
        					hashedPositions.put(position,checkRow); 
        				return checkRow;
        			}
        			checkRow = colorOneAway; //set it equal to "one away", so we can keep checking down the row
        			//this will become important for 5x5, 6x7.
        		}
        		//we don't have to care about the checkRow value after this - it never gets returned
        		//we still need to check columns
    		}
    	}
    	
    	//check the columns - same code, different indices.
    	for(int i=0; i < nR; i++) {
    		//get the bottom piece (row 0) and check going up
    		int checkColumn = getColor(i,0,getColumnChipCount(i));
    		//now check whether we should actually be looking at it
    		if (checkColumn > 0){
    			//if it is not transparent, then iterate through the row and check
        		for(int j=1; j < nC-2; j++) { 
        			//get the three things adjacent to this slot going UP
        			//columnchipcount should be the same down here
        			int colorOneAway = getColor(i,j,getColumnChipCount(i));
        			int colorTwoAway = getColor(i,j+1,getColumnChipCount(i));
        			int colorThreeAway = getColor(i,j+2,getColumnChipCount(i));
        			//if they're equal, we have a win, so quit and return the color
        			if (checkColumn > 0 && checkColumn == colorOneAway && checkColumn == colorTwoAway && checkColumn == colorThreeAway){
        				if (hashedPositions != null)
        					hashedPositions.put(position,checkColumn);
        				return checkColumn;
        			}
        			checkColumn = colorOneAway; //set it equal to "one away", so we can keep checking down the row
        			//this will become important for 5x5, 6x7.
        		}
    		}
    	}
        // Check both diagonals for four in a row
        //generic is a bit harder, focusing on similar to tictactoe for now
        //check diagonals in separate forloops for readability
        //yes combining them is faster, but it also makes it very difficult to maintain
        int checkDiagA = getColor( 0, 0, chipsAtTop); //reuse this variable
        for(int k = 1; k < 4; k++) {
        	if(checkDiagA > 0 && checkDiagA != getColor(k,k,getColumnChipCount(k)))
        		checkDiagA = 0;
        }
        int checkDiagB = getColor( 0, 3, chipsAtTop);
        for (int k = 3; k > -1; k--) {
        	if(checkDiagB > 0 && checkDiagB != getColor(3-k,k,getColumnChipCount(3-k)))
        		checkDiagB = 0;
        }
        if     (checkDiagA > 0 ) {
        	if (hashedPositions != null)
        		hashedPositions.put(position,checkDiagA);
        	return checkDiagA;
        }
        if     (checkDiagB > 0 )  {
        	if (hashedPositions != null)
        		hashedPositions.put(position,checkDiagB);
        	return checkDiagB;
        }

        if     (isFull         ) {
        	if (hashedPositions != null )
        		hashedPositions.put(position, 0);
        	return          0; // Tie
        }
        else {
        	if (hashedPositions != null)
        		hashedPositions.put(position,-1);
        	return         -1;
        }
    }

    @Override
    public void reset() {
        position = 0;
    }

    @Override
    public void setPlayer(int iPlayer) { // Only 1 or 2 are valid
        if ( !(0<iPlayer && iPlayer<3) ) {
            System.err.println("Error(Connect4Position::setPlayer): iPlayer ("+iPlayer+") out of bounds!!!");
        } else {
            int  currentPlayer = getPlayer();
            if ( currentPlayer != iPlayer ) {
                position ^= 1L << 63;
            }
        }
    }

    @Override
    public int getPlayer() {
        return ((int)(position>>>63))+1;
    }

    @Override
    public int getChipCount() {
        int chipCount = 0;
        for ( int iC = 0; iC < nC(); iC++ ) chipCount += getColumnChipCount(iC);
        return chipCount;
    }

    @Override
    public int isWinner(InterfaceIterator iPos) {
        // Not yet used (You may want to implement/use this for the group assignment)
        return 0/0;
    }

    @Override
    public float valuePosition() {
        // Not yet used
        return 0/0;
    }

    @Override
    public int getChipCount(InterfaceIterator iPos) {
        // Not used yet
        return 0/0;
    }

}
