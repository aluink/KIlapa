package net.aluink.chess.suicide.ai;

import java.util.HashMap;
import java.util.Map;

import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;

public class Hashtable {
	
	Map<Board, HashEntry> table;
	long tablesize;
	
	public Hashtable(int size){
		tablesize = size;
		table = new HashMap<Board,HashEntry>(20000);
	}
	
	public HashEntry lookup(Board b){
		HashEntry entry = table.get(b);
		if(entry != null && entry.check == b.getCheckCode()){
			return entry;			
		}
		return null;
	}
	
	public void put(Board b, int value, Move bestmove, int depth){
		table.put(b, new HashEntry(b.getCheckCode(), value, bestmove, depth));
	}
}
