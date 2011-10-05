package net.aluink.chess.suicide.ai.book;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.aluink.chess.suicide.game.Move;

public class BookNode {
	String fen;
	List<BookNode> children;
	List<Move> moves;
	List<Integer> scores;
	BookNode parent;
	
	public BookNode(){
		children  = new ArrayList<BookNode>();
		moves = new ArrayList<Move>();
		scores = new ArrayList<Integer>();
		parent = null;
	}
	
	public String getFen() {
		return fen;
	}
	public void setFen(String fen) {
		this.fen = fen;
	}
	public BookNode getParent() {
		return parent;
	}
	public void setParent(BookNode parent) {
		this.parent = parent;
	}
	
	public void addChild(BookNode child, Move m, int score){
		children.add(child);
		moves.add(m);
		scores.add(score);
	}
	
	public void removeChild(BookNode child){
		_removeChild(children.indexOf(child));
	}
	
	public void removeChild(Move m){
		_removeChild(moves.indexOf(m));
	}
	
	public void removeChild(Integer score){
		_removeChild(scores.indexOf(score));
	}
	
	
	private void _removeChild(int i) {
		if(i >= 0){
			children.remove(i);
			moves.remove(i);
			scores.remove(i);
		}
	}
	
	public BookNode getChild(Move m){
		if(moves.contains(m)){
			return children.get(moves.indexOf(m));
		}
		return null;
	}
	
	public List<Move> getMoves(int score){
		List<Move> ms = new ArrayList<Move>();
		for(int i = 0;i < moves.size();i++){
			if(scores.get(i) >= score){
				ms.add(moves.get(i));
			}
		}
		return ms;
	}
	
	public Move getBestMove(){
		if(moves.size() == 0){
			return null;
		}
		int i = scores.get(0);
		Move best = moves.get(0);
		for(int j = 1;j < scores.size();j++){
			if(scores.get(j) > i){
				i = scores.get(j);
				best = moves.get(j);
			}				
		}
		return best;
	}
	
	public Move getRandomBestMove(){
		if(moves.size() == 0){
			return null;
		}
		int i = scores.get(0);
		List<Move> best = new ArrayList<Move>();
		best.add(moves.get(0));
		for(int j = 1;j < scores.size();j++){
			int tmp = scores.get(j);
			if(tmp > i){
				i = scores.get(j);
				best = new ArrayList<Move>();
				best.add(moves.get(0));
			} else if(tmp == i){
				best.add(moves.get(j));
			}
		}
		if(best.size() == 1){
			return best.get(0);
		}
		return best.get(new Random().nextInt(best.size()));
	}

	void store(FileOutputStream fos) throws IOException {
		fos.write(children.size());
		for(Move m : moves){
			fos.write(m.getByte());
		}
		for(Integer i : scores){
			fos.write(i);
		}
		for(BookNode n : children){
			n.store(fos);
		}
	}
	
}
