package net.aluink.chess.pgn;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.aluink.chess.suicide.game.Move;

public class Game {
	Map<String, String> pairs = new HashMap<String,String>();
	List<Move> moves = new LinkedList<Move>();
	
	public void addPair(String k, String v){
		pairs.put(k,v);
	}
	
	public void addMove(Move m){
		moves.add(m);
	}

	public List<Move> getMoves() {
		return moves;
	}
	
	public String getPairData(String key){
		return pairs.get(key);
	}
}
