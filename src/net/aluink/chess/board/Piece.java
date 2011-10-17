package net.aluink.chess.board;

import java.util.Arrays;
import java.util.List;

public class Piece {
	public enum Color {
		WHITE(0),	BLACK(1);
		int index;
		private Color(int i){
			index = i;
		}
		public int getIndex(){
			return index;
		}
		public Color other() {
			switch(this){
				case WHITE: return BLACK;
				default: return WHITE;
			}
		}
	};
	
	public enum Type {
		KING(0), QUEEN(1), BISHOP(2), KNIGHT(3), ROOK(4), PAWN(5);
		
		int index;
		private Type(int i){
			index = i;
		}
		
		public static Type fromIndex(int i){
			switch(i){
				case 0:return KING;
				case 1:return QUEEN;
				case 2:return BISHOP;
				case 3:return KNIGHT;
				case 4:return ROOK;
				case 5:return PAWN;
			}
			return null;
		}
		
		public int getIndex(){
			return index;
		}
		
		
		
	};
	
	public static List<Piece> getPieceSet(Color c){
		switch(c){
			case WHITE: return Arrays.asList(WKING,WQUEEN,WBISHOP,WKNIGHT,WROOK,WPAWN);
			case BLACK: return Arrays.asList(BKING,BQUEEN,BBISHOP,BKNIGHT,BROOK,BPAWN);
			default: return null;
		}
		
	}
	
	public static Piece WKING = new Piece(Color.WHITE, Type.KING);
	public static Piece WQUEEN = new Piece(Color.WHITE, Type.QUEEN);
	public static Piece WBISHOP = new Piece(Color.WHITE, Type.BISHOP);
	public static Piece WKNIGHT = new Piece(Color.WHITE, Type.KNIGHT);
	public static Piece WROOK = new Piece(Color.WHITE, Type.ROOK);
	public static Piece WPAWN = new Piece(Color.WHITE, Type.PAWN);
	
	public static Piece BKING = new Piece(Color.BLACK, Type.KING);
	public static Piece BQUEEN = new Piece(Color.BLACK, Type.QUEEN);
	public static Piece BBISHOP = new Piece(Color.BLACK, Type.BISHOP);
	public static Piece BKNIGHT = new Piece(Color.BLACK, Type.KNIGHT);
	public static Piece BROOK = new Piece(Color.BLACK, Type.ROOK);
	
	

	public static Piece BPAWN = new Piece(Color.BLACK, Type.PAWN);

	
	private Color color;
	private Type type;
	
	public Piece(Color c, Type t){
		this.color = c;
		this.type = t;
	}
	
	public Color getColor(){
		return this.color;
	}
	
	public boolean isType(Type t){
		return getType() == t;
	}
	
	public boolean isColor(Color c){
		return getColor() == c;
	}
	
	public Type getType(){
		return this.type;
	}
	
	@SuppressWarnings("unused")
	private Piece(){}
	
	
	public char getFen(){
		Character c = null;
		switch(type){
			case KING:   c ='K'; break;
			case QUEEN:  c = 'Q'; break;
			case BISHOP: c = 'B'; break;
			case KNIGHT: c = 'N'; break;
			case ROOK:   c = 'R'; break;
			case PAWN:   c = 'P'; break;		
		}
		if(color == Color.BLACK){
			return Character.toLowerCase(c);
		}
		return c;
	}
	
	public static Piece fromFen(char c){
		switch(c){
			case 'k': return BKING;
			case 'q': return BQUEEN;
			case 'b': return BBISHOP;
			case 'n': return BKNIGHT;
			case 'r': return BROOK;
			case 'p': return BPAWN;
			case 'K': return WKING;
			case 'Q': return WQUEEN;
			case 'B': return WBISHOP;
			case 'N': return WKNIGHT;
			case 'R': return WROOK;
			case 'P': return WPAWN;
			default: return null;
		}
	}

	public byte getByte() {
		return (byte) ((1 << 4) | (getColor() == Color.WHITE ? (1 << 3) : 0) | (getType().getIndex()));
	}

	public static Piece fromCompressed(int i) {
		return new Piece((i >> 3 & 1) == 1 ? Color.WHITE : Color.BLACK, Type.fromIndex(i & 0x7));
	}
	
	@Override
	public boolean equals(Object rhs){
		return getByte() == ((Piece)rhs).getByte();
	}
	
}
