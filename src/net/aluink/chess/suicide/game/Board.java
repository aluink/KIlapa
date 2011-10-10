package net.aluink.chess.suicide.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import net.aluink.chess.board.Piece;
import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.board.Piece.Type;

public class Board {
	private Piece [] pos;
	private Color turn;
	public static final String STARTING_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w"; 
	
	long [] hash = {0L,0L};
	static final long [][][][] hashpieces = initHashPieces();
	static final long [] hashturns = initHashTurns();
	
	long bitboards[][];
	
	Stack<MoveInfo> moves = new Stack<MoveInfo>();
	
	boolean debug;
	
	public void setDebug(){
		debug = true;
	}
	
	public boolean getDebug(){
		return debug;
	}
	
	
	
	private void setPos(int i, Piece p){
		if(pos[i] != null)
			unsetPos(i);
		pos[i] = p;
		bitboards[p.getColor().getIndex()][p.getType().getIndex()] |= 1L << i;
		hash[0] ^= hashpieces[0][p.getColor().getIndex()][p.getType().getIndex()][i];
		hash[1] ^= hashpieces[1][p.getColor().getIndex()][p.getType().getIndex()][i];
	}
	
	public void setToStarting(){
		try {
			setFen(STARTING_POS);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		for(int i = 0;i < 8;i++){
//			setPos(8+i,Piece.WPAWN);
//			setPos(48+i,Piece.BPAWN);
//		}
//
//		setPos(0,Piece.WROOK);
//		setPos(1,Piece.WKNIGHT);
//		setPos(2,Piece.WBISHOP);
//		setPos(3,Piece.WQUEEN);
//		setPos(4,Piece.WKING);
//		setPos(5,Piece.WBISHOP);
//		setPos(6,Piece.WKNIGHT);
//		setPos(7,Piece.WROOK);
//		
//		setPos(56,Piece.BROOK);
//		setPos(57,Piece.BKNIGHT);
//		setPos(58,Piece.BBISHOP);
//		setPos(59,Piece.BQUEEN);
//		setPos(60,Piece.BKING);		
//		setPos(61,Piece.BBISHOP);
//		setPos(62,Piece.BKNIGHT);
//		setPos(63,Piece.BROOK);	
		
//		unsetPos(56);
//		unsetPos(48);
//		unsetPos(2);
//		unsetPos(0);
//		unsetPos(9);
//		unsetPos(54);
//		
//		
//		setPos(8, Piece.BPAWN);
//		setPos(61, Piece.WBISHOP);
//		turn = Color.WHITE;
		
		
	}

	private static long[][][][] initHashPieces() {
		Random rnd = new Random(1);
		long [][][][] hashpieces = new long[2][2][6][64];
		
		for(int i = 0;i < 2;i++){
			for(int l = 0;l < 2;l++){
				for(int j = 0;j < 6;j++){
					for(int m = 0;m < 64;m++){
						hashpieces[i][l][j][m] = rnd.nextLong();
					}
				}
			}
		}
		
		System.out.println("Done init pieces");
		
		return hashpieces;
	}

	private static long[] initHashTurns() {
		Random rnd = new Random(0);
		long [] hashcolors = new long[2];
		
		for(int i = 0;i < 2;i++){
			hashcolors[i] = rnd.nextLong();
		}
		
		System.out.println("Done init turns");
		
		return hashcolors;
	}

	public Board(){
		pos = new Piece[64];
		for(int i = 0;i < 64;i++){
			pos[i] = null;
		}
		
		bitboards = new long[2][6];
		for(int i = 0;i < 2;i++){
			for(int j = 0;j < 6;j++){
				bitboards[i][j] = 0L;
			}
		}
	}
	
	public Board(String fen) throws Exception{
		this();
		setFen(fen);
	}
	
	public int getEnpassantPos(){
		if(enPassantAvailable()){
			return moves.peek().end - (turn == Color.WHITE ? -8 : 8);
		}
		return -1;
	}
	
	public boolean enPassantAvailable(){
		if(moves.empty()) return false;
		MoveInfo mi = moves.peek();
		return Math.abs(mi.start - mi.end) == 16 && pos[mi.end].getType() == Type.PAWN;
	}
	
	
	public void unmakeMove(){
		
		MoveInfo m = moves.pop();
		
		//Reset start
		if(m.promo != null){
			setPos(m.getStart(), m.promo.getColor() == Color.WHITE ? Piece.WPAWN : Piece.BPAWN);
		} else { 
			setPos(m.getStart(), pos[m.getEnd()]);
		}
		
		//If ep, then reset the other pawn.
		if(m.ep){
			boolean b =  pos[m.end].getColor() == Color.WHITE;
			setPos(m.end - (b ? 8 : -8), b ? Piece.BPAWN : Piece.WPAWN);
		}
		
		if(m.getCapture() != null){
			setPos(m.getEnd(), m.getCapture());
		}
		else
			unsetPos(m.getEnd());
		flipTurn();
	}

	/**
	 * @param Move m
	 * @return if this move is an enpassant capture
	 */
//	public boolean isEnPassant(Move m){
//		int dist = Math.abs(m.start - m.end);
//		return pos[m.start].getType() == Type.PAWN &&
//				(dist == 7 || dist == 9) &&
//				pos[m.end] == null;
//	}
	
	public void makeMove(Move m){
		MoveInfo mi = new MoveInfo(m, pos[m.getEnd()]);
		
		if(m.ep){
			setPos(m.getEnd(), pos[m.getStart()]);
			unsetPos(m.getEnd() - (turn == Color.WHITE ? 8 : -8));
		} else {			
			if(m.promo != null){
				setPos(m.getEnd(), m.promo);
			} else {
				setPos(m.getEnd(),pos[m.getStart()]);
			}
		}
		
		unsetPos(m.getStart());
		flipTurn();
		
		moves.push(mi);
		
	}
	
	private void unsetPos(int i) {
		Piece p = pos[i];
		hash[0] ^= hashpieces[0][p.getColor().getIndex()][p.getType().getIndex()][i];
		hash[1] ^= hashpieces[1][p.getColor().getIndex()][p.getType().getIndex()][i];

		bitboards[p.getColor().getIndex()][p.getType().getIndex()] &= ~(1L << i);
		
		pos[i] = null;		
	}

	public Color getTurn(){
		return this.turn;
	}
	
	public void flipTurn(){
		if(turn == Color.WHITE)
			this.turn = Color.BLACK;
		else
			this.turn = Color.WHITE;
		
		hash[0] ^= hashturns[0];
		hash[1] ^= hashturns[1];
	}
	
	public static void printBitboard(long b){
		for(int row = 7;row >= 0;row--){
			for(int col = 0;col < 8;col++){
				System.out.print(b >> (row*8+col) & 1L);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printBitboards(){
		for(int c = 0;c < 2;c++){
			for(int p = 0;p < 6;p++){
				printBitboard(bitboards[c][p]);
				System.out.println("\n");
			}
		}
	}
	
	public void printBoard(){
		for(int row = 7;row >= 0;row--){
			System.out.print("   ");
			for(int col = 0;col < 8;col++){
				System.out.print("+---");
			}
			System.out.print("+\n " + (row+1) + " |");
			
			for(int col = 0;col < 8;col++){
				Piece p = pos[row*8+col];
				if(p == null){
					System.out.print("   |");
					continue;
				}
				
				StringBuilder c = new StringBuilder("");
				switch(p.getColor()){
					case WHITE:
						c.append(" ");break;
					case BLACK:
						c.append("*");break;
				}
				switch(p.getType()){
					case KING:
						c.append("K |");break;
					case ROOK:
						c.append("R |");break;
					case KNIGHT:
						c.append("N |");break;
					case BISHOP:
						c.append("B |");break;
					case QUEEN:
						c.append("Q |");break;
					case PAWN:
						c.append("P |");break;
					default:
						c.append("  |");
					
				}
				System.out.print(c);
			}
			System.out.println("");
		}
		System.out.print("   ");
		for(int col = 0;col < 8;col++){
			System.out.print("+---");
		}
		System.out.print("+\n   ");
		for(int col = 0;col < 8;col++){
			System.out.print("  " + (char)('A'+col) + " ");
		}
		System.out.println("\nTurn: " + (turn == Color.WHITE ? "White" : "Black"));
		System.out.printf("%X %X\n", hash[0], hash[1]);
		System.out.println("Fen: " + getFen());
	}

	public Piece getPos(int i) {
		return pos[i];
	}
	
	public List<Piece> getPieces(){
		List<Piece> pieces = new ArrayList<Piece>(pos.length);
		for(Piece p : pos){
			if(p != null){
				pieces.add(p);
			}				
		}
		return pieces;
	}
	
	public int hashCode(){
		return (int) hash[0];
	}
	
	public long getCheckCode(){
		return hash[1];
	}
	
	public String getFen(){
		StringBuilder sb = new StringBuilder();
		int c;
		for(int row = 7;row >= 0;row--){
			c = 0;
			for(int col = 0;col < 8;col++){
				Piece p = pos[row*8+col];
				if(p != null){
					if(c != 0)
						sb.append(c);
					c = 0;
					sb.append(pos[row*8+col].getFen());
				} else {
					c++;
				}
			}
			if(c != 0)
				sb.append(c);
			if(row != 0)
				sb.append('/');
		}
		sb.append(turn == Color.WHITE ? " w " : " b ");
		return sb.toString();
	}
	
	public void setFen(String fen) throws Exception {
		hash[0] = hash[1] = 0;
		pos = new Piece[64];
		int i = 0;
		fen = fen.trim();
		for(int row = 7;row >= 0;row--){
			int col = 0;
			while(col < 9){
				char c = fen.charAt(i++);
				if(Character.valueOf(c).equals('/')){
					if(col < 8) 
						throw new Exception("Invalid fen notation: " + fen);
					break;
				} else if(Character.isDigit(c)){
					col += c - '0';
				} else if("kqbnrpKQBNRP".contains(Character.valueOf(c).toString())){
					setPos(row*8+col++, Piece.fromFen(c));
				} else if(Character.valueOf(c).equals(' ') && col == 8 && row == 0){
					break;
				}
			}
			if(col == 9 && row != 0)
				throw new Exception("Invalid fen notation: " + fen);
		}
		char c = fen.charAt(i++);
		if(Character.valueOf(c).equals('w')){
			turn = Color.WHITE;
		} else if(Character.valueOf(c).equals('b')){
			turn = Color.BLACK;
			hash[0] = hashturns[0];
			hash[1] = hashturns[1];
		}			
	}
	
	public static void main(String[] args) {
		Board.printBitboard(9115426935197958144L);
	}

	public long[][] getBitBoards() {
		return bitboards;
	}
	
}
