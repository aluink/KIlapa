package net.aluink.chess.suicide.ai;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import net.aluink.chess.board.Piece;
import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;
import net.aluink.chess.suicide.game.lmg.SuicideLMG;

public class SuicidePlayer implements ChessPlayer {
	
	Board b;
	Color c;
	static final int INF = 300000;
	
	long starttime;
	long PLAY_TIME = 5000L;
	
	long nodecount;
//	long cutoffs, hashHits, hashCuts, hashBMCuts, killerCuts;
	boolean timeupflag;
	
	Hashtable table = new Hashtable(20000);
	PriorityQueue<KillerMove> killerMoves [];
	public static final int KILLER_COUNT = 4;
	
	public SuicidePlayer(Board b, Color c){
		this.b = b;
		this.c = c;
		resetKillers();
	}

	@SuppressWarnings("unchecked")
	private void resetKillers() {
		killerMoves = new PriorityQueue[100];
		for(int i = 0;i < 100;i++){
			killerMoves[i] = new PriorityQueue<KillerMove>(3, new KillerMove());
		}
	}
	
	private void addKiller(KillerMove move, int depth){
		killerMoves[depth].add(move);
		if(killerMoves[depth].size() > KILLER_COUNT)
			killerMoves[depth].remove();
	}
	
	@Override
	public Move getMove() {
		int depth = 3;
		Stack<Move> moves = new SuicideLMG().getLegalMoves(b);
		if(moves.size() == 1)
			return moves.get(0);
		Move cbestmove = null, bestmove = null;
		int best = -INF,value;
		timeupflag = false;
		starttime = System.currentTimeMillis();
		nodecount = 0;
		while(!timeupflag){
			bestmove = cbestmove;
//			hashBMCuts = cutoffs = hashHits = hashCuts = killerCuts = 0;
			for(Move m : moves){
				b.makeMove(m);
				if(depth == 8)
					b.setDebug();
				value = -search(depth,-INF,INF);
				if(value > best){
					cbestmove = m;
					best = value;
				}
				b.unmakeMove();
				if(timeup())
					break;
			}
			if(!timeup()){
//				System.out.println("Depth: " + depth);
//				System.out.println("Value: " + best);
//				System.out.println("Bestmove: " + cbestmove);
//				System.out.println("\tNodecount: " + nodecount);
//				System.out.println("\tCutoffs : " + cutoffs);
//				System.out.println("\tHashHits : " + hashHits);
//				System.out.println("\tHashCuts : " + hashCuts);
//				System.out.println("\tHashBMCuts : " + hashBMCuts);
//				System.out.println("\tKillerCuts : " + killerCuts);
			} else {
				break;
			}
			
			depth++;
		}
		
		if(bestmove == null)
			bestmove = moves.get(0);
		
		System.out.println("NPS: " + (1000 * nodecount) / (System.currentTimeMillis() - starttime));
		System.out.println("Bestmove: " + bestmove);
		return bestmove;
		
	}

	private int search(int depth, int alpha, int beta) {
		int value = -INF;
		int best = -INF;
		Stack<Move> moves = new SuicideLMG().getLegalMoves(b);
		Move bestmove = null;
		
		nodecount++;
		
		for(KillerMove km : killerMoves[depth]){
			bumpMove(moves, km.m);
		}
		
		HashEntry entry = table.lookup(b);
		if(entry != null){
//			hashHits++;
			if(entry.depth >= depth){
//				hashCuts++;
				return entry.value;
			}
			if(moves.contains(entry.bestmove)){
				bumpMove(moves, entry.bestmove);
			}
		}
		
		if(depth == 0){
			return eval();
		}
		
		for(Move m : moves){
			b.makeMove(m);
			value = -search(depth-1,-beta,-alpha);
			b.unmakeMove();
			if(timeup())
				return -INF;
			if(value > best){
				bestmove = m;
				alpha = best = value;
			}
			addKiller(new KillerMove(m, value), depth);
			
			if(beta < alpha) {
//				cutoffs++;
//				if(entry != null && entry.bestmove.equals(m)){
//					hashBMCuts++;
//				}
//				if(isKiller(m, depth)){
//					killerCuts++;
//				}
				return alpha;
			}
		}
		
		table.put(b, value, bestmove, depth);
		
		return alpha;
		
	}
	
	private boolean isKiller(Move m, int depth){
		for(KillerMove km : killerMoves[depth]){
			if(km.m.equals(m))
				return true;
		}
		return false;
			
	}

	private void bumpMove(Stack<Move> moves, Move move) {
		if(moves.contains(move))
			moves.push(moves.remove(moves.indexOf(move)));
	}

	private boolean timeup() {
		if(timeupflag)
			return true;
		if((System.currentTimeMillis() > (starttime + PLAY_TIME))){
			timeupflag = true;
		}
		return timeupflag;
		
	}

	private int eval() {
		List<Piece> pieces = b.getPieces();
		Color c = b.getTurn();
		int score = 0;
		for(Piece p : pieces){
			if(p.getColor() == c)
				score--;
			else
				score++;
		}
		return score;
	}

	public Color getSide() {
		return c;
	}
}
