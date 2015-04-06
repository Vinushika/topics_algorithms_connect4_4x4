package c_minimax;

public class ConnectFourStrategyForThread extends Thread {
	InterfacePosition position;
	InterfaceSearchInfo context;
	Connect4Strategy strat;
	InterfaceIterator iPos;

	public void notifyOfThreadComplete(final Connect4Strategy thread) {
	}

	public ConnectFourStrategyForThread(final InterfaceIterator iPos,
			final InterfacePosition position, final InterfaceSearchInfo context,
			final Connect4Strategy strat)
	{
		this.position = position;
		this.context = context;
		this.strat = strat;
		this.iPos = iPos;
	}

	@Override
	public void run() {
		System.out.println("in run");
		System.out.println(position);
		getBestMoveOld(iPos, position, context);

	}

	public void getBestMoveOld(final InterfaceIterator iPos,
			final InterfacePosition position, final InterfaceSearchInfo context) {
		final int player = position.getPlayer();
		final int opponent = 3 - player; // There are two players, 1 and 2.
		final int isWin = position.isWinner(); // check if it wins
		float score = 0f;

		if (isWin == -1) { // if win is not decided, go down the tree
			// define our stuff
			position.setPlayer(opponent);
			// create a new context so we can get its score from the children
			final InterfaceSearchInfo newContext = new Connect4SearchInfo();
			strat.getBestMove(position, newContext);
			score = -1 * newContext.getBestScoreSoFar();
			position.setPlayer(player);
		} else { // it is decided, so check if it's a draw or not. You can't make a losing move in
			// Connect4, either.
			score = isWin == 0 ? 0 : 1;
		}
		if (score > context.getBestScoreSoFar()) {
			context.setBestMoveSoFar(iPos, score);
		}
		strat.contextMapping.put(position.getRawPosition(), context);
	}

	public static void getBestMoveOld(final InterfaceIterator iPos,
			final InterfacePosition position, final InterfaceSearchInfo context,
			final Connect4Strategy strat) {
		final int player = position.getPlayer();
		final int opponent = 3 - player; // There are two players, 1 and 2.
		final int isWin = position.isWinner(); // check if it wins
		float score = 0f;
		if (isWin == -1) { // if win is not decided, go down the tree
			// define our stuff
			position.setPlayer(opponent);
			// create a new context so we can get its score from the children
			final InterfaceSearchInfo newContext = new Connect4SearchInfo();
			strat.getBestMove(position, newContext);
			score = -1 * newContext.getBestScoreSoFar();
			position.setPlayer(player);
		} else { // it is decided, so check if it's a draw or not. You can't make a losing move in
			// Connect4, either.
			score = isWin == 0 ? 0 : 1;
		}
		if (score > context.getBestScoreSoFar()) {
			context.setBestMoveSoFar(iPos, score);
		}
		strat.contextMapping.put(position.getRawPosition(), context);
	}
}
