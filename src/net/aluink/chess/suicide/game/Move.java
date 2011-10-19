package net.aluink.chess.suicide.game;


import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.aluink.chess.board.Piece;
import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.board.Piece.Type;

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

	public Move(String m){
		int scol = m.charAt(0)-'a';
		int srow = m.charAt(1)-'1';
		int ecol = m.charAt(2)-'a';
		int erow = m.charAt(3)-'1';
		this.start = srow*8+scol;
		this.end = erow*8+ecol;
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

	public static Move getAlgebraicMove(Board b, Stack<Move> legalMoves, String aMove){
		Move [] tmp;
		List<Move> s = new LinkedList<Move>();
		if(aMove.equals("Raxa6"))
			System.out.println();
		if(legalMoves.size() == 1)
			return legalMoves.get(0);
		if(Character.isLowerCase(aMove.charAt(0))){ //PAWN
			int col = aMove.charAt(0) - 'a';
			for(Move m : legalMoves){
				if(m.getStart()%8 == col && b.getPos(m.getStart()).getType() == Type.PAWN){
					s.add(m);
				}
			}
			if(s.size() == 1)
				return s.get(0);
			
			int row;
			if(aMove.contains("x")){
				col = aMove.charAt(aMove.indexOf('x')+1) - 'a';
				row = aMove.charAt(aMove.indexOf('x')+2) - '1';
				tmp = s.toArray(new Move[0]);
				for(Move m : tmp){
					if(b.getPos(m.getEnd()) == null || m.getEnd()%8 != col){
						s.remove(m);
					}
				}
			} else {
				row = aMove.charAt(1) - '1'; 
			}
			if(s.size() == 1)
				return s.get(0);
			
			tmp = s.toArray(new Move[0]);
			for(Move m : tmp){
				if(m.getEnd()/8 != row){
					s.remove(m);
				}
			}
			if(s.size() == 1)
				return s.get(0);
			
			if("RNBQK".contains(aMove.substring(aMove.length()-1))){
				char c = aMove.charAt(aMove.length()-1);
				tmp = s.toArray(new Move[0]);
				for(Move m : tmp){
					if(m.getPromo() == null || Character.toUpperCase(m.getPromo().getFen()) != c)
						s.remove(m);					
				}
			}
			if(s.size() == 1)
				return s.get(0);
			
			
		} else {
			Piece p = Piece.fromFen(b.getTurn() == Color.BLACK ? Character.toLowerCase(aMove.charAt(0)) : aMove.charAt(0));
			
			for(Move m : legalMoves){
				if(b.getPos(m.getStart()).equals(p)){
					s.add(m);
				}
			}
			if(s.size() == 1)
				return s.get(0);
			
			int endP, col = -1, row = -1; // Raxa6
			if(aMove.contains("x")){
				int xInd = 1;
				char c = aMove.charAt(1);
				if(c != 'x'){
					xInd++;
					if(Character.isDigit(c)){
						row = c - '1';
					} else {
						col = c - 'a';
					}
				}
				endP = (aMove.charAt(xInd+2) - '1')*8 + (aMove.charAt(xInd+1) - 'a');
				tmp = s.toArray(new Move[0]);
				for(Move m : tmp){
					if(b.getPos(m.getEnd()) == null || (col != -1 && m.getStart()%8 != col) || (row != -1 && m.getStart()/8 != row)){
						s.remove(m);
					}
				}
			} else if(!Character.isDigit(aMove.charAt(2))){
				endP = (aMove.charAt(3) - '1')*8 + (aMove.charAt(2) - 'a');
				char c = aMove.charAt(1);
				if(Character.isDigit(c)){
					row = c - '1';
				} else {
					col = c - 'a';
				}
			} else {
				endP = (aMove.charAt(2) - '1')*8 + (aMove.charAt(1) - 'a');
			}
			
			if(s.size() == 1)
				return s.get(0);
			
			
			tmp = s.toArray(new Move[0]);
			for(Move m : tmp){
				if(m.getEnd() != endP || (col != -1 && m.getStart()%8 != col) || (row != -1 && m.getStart()/8 != row)){
					s.remove(m);
				}
			}
			if(s.size() == 1)
				return s.get(0);
			
			
			if("RNBQK".contains(aMove.substring(aMove.length()-1))){
				char c = aMove.charAt(aMove.length()-1);
				tmp = s.toArray(new Move[0]);
				for(Move m : tmp){
					if(m.getPromo() == null || Character.toUpperCase(m.getPromo().getFen()) != c)
						s.remove(m);			
				}
			}
			if(s.size() == 1)
				return s.get(0);
			
			
		}
		
		System.out.println("Ambiguous algebraic move: " + aMove);
		System.out.println("Narrowed it to ");
		for(Move m : s){
			System.out.println("\t" + m);
		}
		System.out.println("  from");
		for(Move m : legalMoves){
			System.out.println("\t" + m);
		}
		System.out.println("on board");
		b.printBoard();
		
		return null;
	}
	
	
}
