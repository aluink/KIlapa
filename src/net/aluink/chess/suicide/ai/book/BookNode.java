package net.aluink.chess.suicide.ai.book;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.suicide.ai.SuicidePlayer;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;

public class BookNode {
	
	public static final Map<String, BookNode> bookHash = new HashMap<String, BookNode>();
	public static final Map<Board, BookNode> bookHashHash = new HashMap<Board, BookNode>();
	
	String fen;
	List<BookNode> children;
	Move move;
	int score; //As WHITE, negate for BLACK
	BookNode parent;
	
	public BookNode(){
		children  = new ArrayList<BookNode>();
		move = null;
		score = 0;
		fen = null;
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
	
	public void addChild(BookNode child){
		children.add(child);
	}
	
	public void removeChild(BookNode child){
		children.remove(child);		
	}
	
	public void _removeChild(int i) {
		children.remove(i);
	}
	
	public BookNode getChild(Move m){
		for(BookNode child : children){
			if(child.move.equals(m))
				return child;
		}
		return null;
	}
	
	public Move getMove() {
		return move;
	}

	public void setMove(Move move) {
		this.move = move;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public static BookNode getBookNode(String fen){
		return bookHash.get(fen);
	}
	
	public List<Move> getMoves(int score, Color c){
		List<Move> ms = new ArrayList<Move>();
		for(BookNode child : children){
			if((c == Color.BLACK && child.score <= score) || child.score >= score){
				ms.add(child.move);
			}
		}
		return ms;
	}
	
	public Move getBestMove(Color c){
		if(children.size() == 0){
			return null;
		}
		int score = (c == Color.BLACK ? 1 : -1) * SuicidePlayer.INF;
		Move best = children.get(0).move;
		for(BookNode child : children){
			if((c == Color.BLACK && child.score <= score) || child.score >= score){
				score = child.score;
				best = child.move;
			}				
		}
		return best;
	}
	
//	public Move getRandomBestMove(){
//		if(moves.size() == 0){
//			return null;
//		}
//		int i = scores.get(0);
//		List<Move> best = new ArrayList<Move>();
//		best.add(moves.get(0));
//		for(int j = 1;j < scores.size();j++){
//			int tmp = scores.get(j);
//			if(tmp > i){
//				i = scores.get(j);
//				best = new ArrayList<Move>();
//				best.add(moves.get(0));
//			} else if(tmp == i){
//				best.add(moves.get(j));
//			}
//		}
//		if(best.size() == 1){
//			return best.get(0);
//		}
//		return best.get(new Random().nextInt(best.size()));
//	}

	public void store(DataOutputStream dos) throws IOException{
		store(dos, true);
	}
	
	private void store(DataOutputStream dos, boolean storeFen) throws IOException {
		if(storeFen) dos.writeUTF(fen);
		dos.writeInt(move == null ? -1 : move.getCompressed());
		dos.writeInt(score);
		dos.writeInt(children.size());
		for(BookNode n : children){
			n.store(dos, false);
		}
	}
	
	public void loadAll(String [] filenames) throws IOException {
		for(String fn : filenames){
			DataInputStream dis = null;
			try {
				dis = new DataInputStream(new FileInputStream(fn));
				load(dis);
				
			} catch (Exception e) {
				System.out.println("Unable to open: " + fn);
			} finally {
				if(dis != null) dis.close();
			}
		}
	}
	
	public void load(DataInputStream dis) throws Exception{
		load(dis, null, true);
	}
	
	private void load(DataInputStream dis, Board b, boolean loadFen) throws Exception {
		if(loadFen) {
			this.fen = dis.readUTF();
			b = new Board();
			b.setFen(fen);
		}
		int m = dis.readInt();
		this.move = m == -1 ? null : new Move(m);
		if(move != null){
			b.makeMove(move);
			this.fen = b.getFen();
		}
		this.score = dis.readInt();
		int childrenCount = dis.readInt();
		this.children = new ArrayList<BookNode>(childrenCount);
		for(int i = 0;i < childrenCount;i++){
			BookNode bn = new BookNode();
			bn.load(dis, b, false);
			bn.parent = this;
			this.children.add(bn);
		}
		if(move != null)
			b.unmakeMove();
		bookHash.put(this.fen, this);
		bookHashHash.put(b, this);
	}
	
	public static void main(String[] args) throws Exception {
//		int i = 0;
//		BookNode root = new BookNode();
//		root.fen = "rootFen";
//		root.move = new Move(0,1);
//		root.parent = null;
//		root.score = i++;
//		
//		for(;i < 10;i++){
//			BookNode bn = new BookNode();
//			bn.move = new Move(i,1);
//			bn.parent = root;
//			bn.score = i;
//			root.children.add(bn);
//		}
//		
//		DataOutputStream dos = new DataOutputStream(new FileOutputStream("bookout"));
//		root.store(dos);
//		dos.close();
		
		DataInputStream dis = new DataInputStream(new FileInputStream("e4.pnr"));
		BookNode tmpRoot = new BookNode();
		tmpRoot.load(dis);
		dis.close();
		System.out.println("Done");
		BookNode bn = BookNode.getBookNode("5Fen");
		System.out.println(bn.score);
	}
	
}
