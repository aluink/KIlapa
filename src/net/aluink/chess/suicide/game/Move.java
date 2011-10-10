package net.aluink.chess.suicide.game;

import net.aluink.chess.board.Piece;
import net.aluink.chess.board.Piece.Color;

public class Move {
	int start;
	int end;
	Piece promo;
	boolean ep;
	
	public Piece getPromo() {
		return promo;
	}

	public void setPromo(Piece promo) {
		this.promo = promo;
	}

	public Move(int start, int end){
		this(start,end, null, false);
	}
	
	public Move(int start, int end, boolean ep){
		this(start,end, null, ep);
	}
	
	public Move(int start, int end, Piece promo, boolean ep){
		this.start = start;
		this.end = end;
		this.promo = promo;
		this.ep = ep;
	}
	
	public Move(Move m) {
		this(m.start, m.end, m.promo, m.ep);
	}

	public Move(int move) {
		this.start = move >> 26 & 0x3F;
		this.end = move >> 20 & 0x3F;
		this.ep = (move >> 5 & 1) == 1; 
		promo = ((move >> 4 & 1) == 1) ? Piece.fromCompressed(move & 0xF) : null;		
	}

	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	public String toString(){
		return "" + (char)((start%8) + 'a') + (start/8 + 1) + (char)((end%8)+'a') + (end/8+1) + (promo != null ? (promo.getColor() == Color.WHITE ? Character.toUpperCase(promo.getFen()) + "" : promo.getFen() + "") : "");
	}
	
	public static Move [] promoSet(int start, int end, Color c){
		Move [] moves = new Move[5];
		for(int i = 0;i < 5;i++)
			moves[i] = new Move(start, end);
		if(c == Color.BLACK){
			moves[0].promo = Piece.BKING;
			moves[1].promo = Piece.BQUEEN;
			moves[2].promo = Piece.BBISHOP;
			moves[3].promo = Piece.BKNIGHT;
			moves[4].promo = Piece.BROOK;
		} else {
			moves[0].promo = Piece.WKING;
			moves[1].promo = Piece.WQUEEN;
			moves[2].promo = Piece.WBISHOP;
			moves[3].promo = Piece.WKNIGHT;
			moves[4].promo = Piece.WROOK;
		}
		return moves;
	}
	
	@Override
	public boolean equals(Object rhs){
		Move m = (Move) rhs;
		return start == m.start && end == m.end && promo == m.promo && ep == m.ep;
	}

	public int getCompressed() {
		return ((start << 26) | (end << 20) | ((ep?1:0) << 5) | (promo == null ? 0 : promo.getByte()));  
	}
	
	public static void main(String[] args) {
		short x = -30208;
		Move m = new Move(x);
		System.out.println(m);
	}
	
}
