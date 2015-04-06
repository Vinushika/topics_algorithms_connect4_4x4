package c_minimax;

import java.util.HashMap;
import java.util.Map;

//author: Gary Kalmanovich; rights reserved

public class Connect4Strategy implements InterfaceStrategy {
	HashMap<Long,Integer> hashedPositions = new HashMap<>();
	HashMap<Long, InterfaceSearchInfo> contextMapping = new HashMap<Long, InterfaceSearchInfo>();
    @Override
    public void getBestMove(InterfacePosition position, InterfaceSearchInfo context) {
        // Note, return information is embedded in context

        int player   = position.getPlayer();
        int opponent = 3-player; // There are two players, 1 and 2.
        
        //TODO Implement strategy. Note, this is not that different from TicTacToeStrategy that is now visible.
        // Nonetheless, if you want to be efficient, you may want to make it somewhat different.
        for ( InterfaceIterator iPos = new Connect4Iterator(4,4); iPos.isInBounds(); iPos.increment() ) {
            InterfacePosition posNew = new Connect4Position(position);
            if (posNew.getColor(iPos) == 0 && ((Connect4Position)posNew).isPositionFillable(iPos) ) { // This is a free spot that we can fill
            	posNew.setColor(iPos, player); //fill it
            	float score = 0;
            	if (contextMapping.containsKey(posNew.getRawPosition())) {
            	    score = contextMapping.get(posNew.getRawPosition()).getBestScoreSoFar();
            	}
            	else {
                	int isWin = posNew.isWinner(); //check if it wins
                	if(isWin == -1){ //if win is not decided, go down the tree
                		//define our stuff
                		posNew.setPlayer(opponent);
                		//create a new context so we can get its score from the children
                		InterfaceSearchInfo newContext = new Connect4SearchInfo();
                		getBestMove(posNew,newContext);
                		score = -1 * newContext.getBestScoreSoFar();
                	}else{ //it is decided, so check if it's a draw or not. You can't make a losing move in Connect4, either.
                		score = isWin == 0 ? 0 : 1;
                	}
                	contextMapping.put(posNew.getRawPosition(), context);

            	}
            	//we want a max 
            	if(score > context.getBestScoreSoFar()){
            		context.setBestMoveSoFar(iPos,score);

            		if (score > 0)
            			return; //prune after finding the first good move
            	}
            }
        }
    }

    @Override
    public void setContext(InterfaceSearchInfo strategyContext) {
        // Not used in this strategy
    }

    @Override
    public InterfaceSearchInfo getContext() {
        // Not used in this strategy
        return null;
    }
}

class Connect4SearchInfo implements InterfaceSearchInfo {

    InterfaceIterator bestMoveSoFar  = null;
    float             bestScoreSoFar = Float.NEGATIVE_INFINITY;

    @Override
    public InterfaceIterator getBestMoveSoFar() {
        return bestMoveSoFar;
    }

    @Override
    public float getBestScoreSoFar() {
        return bestScoreSoFar;
    }

    @Override
    public void setBestMoveSoFar(InterfaceIterator newMove, float newScore) {
        bestMoveSoFar  = new Connect4Iterator(newMove);
        bestScoreSoFar = newScore;
    }

    @Override
    public int getMinDepthSearchForThisPos() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setMinDepthSearchForThisPos(int minDepth) {
        // Not used in this strategy
    }

    @Override
    public int getMaxDepthSearchForThisPos() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setMaxDepthSearchForThisPos(int minDepth) {
        // Not used in this strategy
    }

    @Override
    public int getMaxSearchTimeForThisPos() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setMaxSearchTimeForThisPos(int maxTime) {
        // Not used in this strategy
    }

    @Override
    public float getOpponentBestScoreOnPreviousMoveSoFar() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setOpponentBestScoreOnPreviousMoveSoFar(float scoreToBeat) {
        // Not used in this strategy
    }

    @Override
    public int getClassStateCompacted() {
        // Not used in this strategy
        return 0/0;
    }

    @Override
    public void setClassStateFromCompacted(int compacted) {
        // Not used in this strategy
    }

}
