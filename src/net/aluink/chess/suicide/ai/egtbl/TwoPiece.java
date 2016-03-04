package net.aluink.chess.suicide.ai.egtbl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

import net.aluink.chess.board.Piece;
import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.board.Piece.Type;
import net.aluink.chess.suicide.Kilapa.Logger;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;
import net.aluink.chess.suicide.game.lmg.SuicideLMG;

public class TwoPiece {
	
	static byte [][][][] table;
	static Board [][][][] boards;
	static final byte INF = 127;
	
	static public void initializeTableGenerator() throws IOException{
		
		boards = new Board[6][6][64][64];
		table = new byte[6][6][64][64];
		
		
		for(int i = 0;i < 6;i++){
			for(int j = 0;j < 6;j++){
				for(int l = 0;l < 64;l++){
					for(int m = 0;m < 64;m++){
						if(l == m)
							continue;
						table[i][j][l][m] = 0;
						boards[i][j][l][m] = setupBoard(i,j,l,m);
					}
				}
			}			
		}
		
		
		
	}
	
	private static Board setupBoard(int i, int j, int l, int m) {
		Board b = new Board();
		b.setPos(l, new Piece(Color.WHITE, Type.fromIndex(i)));
		b.setPos(m, new Piece(Color.BLACK, Type.fromIndex(j)));
		b.setTurn(Color.WHITE);
		return b;
	}

	public static void runTable(int i, int j){
		LegalMoveGenerator lmg = new SuicideLMG();
		boolean anotherPassNeeded = true;
		boolean xvx = false;
		if(i == j)
			xvx = true;
		while(anotherPassNeeded){
			anotherPassNeeded = false;
			for(int n = 0;n < 2;n++){
				for(int l = 0;l < 64;l++){
					for(int m = 0;m < 64;m++){
						if(l == m || (!xvx && table[i][j][l][m] != 0))
							continue;
						Board b = boards[i][j][l][m];
						Stack<Move> moves = lmg.getLegalMoves(b);
						byte max = -INF;
						for(Move move : moves){
							b.makeMove(move);
							int [] index = getBoardIndex(b);
							if(xvx && index[2] == -1 || index[3] == -1)
								continue;
							// If this move make the next guy lose...I win
							if(max < -table[index[0]][index[1]][index[2]][index[3]]){
								max = (byte) -table[index[0]][index[1]][index[2]][index[3]];
							}						
							b.unmakeMove();
						}
						 
						if(max < 0 && table[i][j][l][m] != (byte) (max + 1)){
							table[i][j][l][m] = (byte) (max + 1);
							anotherPassNeeded = true;
						} else if(max > 0 && table[i][j][l][m] != (byte) (max - 1)){
							table[i][j][l][m] = (byte) (max - 1);
							anotherPassNeeded = true;
						}
					}
				}
				if(xvx)
					break;
				j ^= i;
				i ^= j;
				j ^= i;
			}
		}
	}
	
	public static void playWithTable(){
		LegalMoveGenerator lmg = new SuicideLMG();
		Logger.Singleton.logn("Load a board: ");
		Scanner scanner = new Scanner(System.in);
		int x = scanner.nextInt();
		int y = scanner.nextInt();
		int k = scanner.nextInt();
		int l = scanner.nextInt();
		scanner.close();
		Board b = setupBoard(x,y,k,l);
		boolean eog = false;
		while(true){
			int [] index = getBoardIndex(b);
			
			b.printBoard();
			int tmp = table[index[0]][index[1]][index[2]][index[3]];
			Logger.Singleton.logn("EGTBL value: " + tmp);
			Stack<Move> moves = lmg.getLegalMoves(b);
			Move move = null;
			for(Move m : moves){
				b.makeMove(m);
				index = getBoardIndex(b);
				if(index[2] == -1 || index[3] == -1){
					Logger.Singleton.logn("EOG");
					eog = true;
					break;
				}
				if((table[index[0]][index[1]][index[2]][index[3]] < 0 && -table[index[0]][index[1]][index[2]][index[3]]-1 == tmp) ||
					(table[index[0]][index[1]][index[2]][index[3]] > 0 && -table[index[0]][index[1]][index[2]][index[3]]+1 == tmp)){
					move = m;
					b.unmakeMove();
					break;
				}
					
				b.unmakeMove();
			}
			if(eog)
				break;
			b.makeMove(move);
		}
	}
	
	public static void generateTables() throws IOException{
		initializeTableGenerator();
		LegalMoveGenerator lmg = new SuicideLMG();
		for(int i = 0;i < 5;i++){
			for(int j = 0;j < 5;j++){
				for(int l = 0;l < 64;l++){
					for(int m = 0;m < 64;m++){
						if(l == m)
							continue;
						Board b = boards[i][j][l][m];
						Stack<Move> moves = lmg.getLegalMoves(b);
						for(Move move : moves){
							b.makeMove(move);
							Stack<Move> mvs = lmg.getLegalMoves(b);
							b.unmakeMove();
							if(mvs.size() == 0){
								//TODO this fails for pawn draws
								table[i][j][l][m] = -INF+1;
							}
						}
					}
				}
			}			
		}
		
		
		for(int i = 0;i < 5;i++){
			for(int j = i;j < 5;j++){
				runTable(i, j);
			}
		}
		
		generatePawnTables();
		
		try {
			verifyTables(lmg);
			storeTables("twoPiece.tbl");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boards = null;
		
	}
	
	public static void main(String[] args) throws IOException {
//		generateTables();
//		playWithTable();
		loadTables("twoPiece.tbl");
		playWithTable();
	}
	
	private static void verifyTables(LegalMoveGenerator lmg) throws Exception {
		for(int i = 0;i < 5;i++){
			for(int j = 0;j < 5;j++){
				for(int l = 0;l < 64;l++){
					for(int m = 0;m < 64;m++){
						if(l == m)
							continue;
						Board b = boards[i][j][l][m];
						Stack<Move> moves = lmg.getLegalMoves(b);
						byte max = -INF;
						for(Move move : moves){
							b.makeMove(move);
							int [] index = getBoardIndex(b);
							Stack<Move> mvs = lmg.getLegalMoves(b);
							if(mvs.size() == 0){
								max = -INF;
							} else {
								// If this move make the next guy lose...I win
								if(max < -table[index[0]][index[1]][index[2]][index[3]]){
									max = (byte) -table[index[0]][index[1]][index[2]][index[3]];
								}
								
							}
							b.unmakeMove();
							
						}
						if(max < 0 && table[i][j][l][m] != max+1){
							throw new Exception("Expected " + (max+1) + " " + table[i][j][l][m] + " at (" + i + "," + j + "," + l + "," + m + ")");
						} else if(max > 0 && table[i][j][l][m] != max-1){
							throw new Exception("Expected " + (max-1) + " " + table[i][j][l][m] + " at (" + i + "," + j + "," + l + "," + m + ")");
						} else if(max == 0 && table[i][j][l][m] != 0){
							throw new Exception("Expected 0 " + table[i][j][l][m] + " at (" + i + "," + j + "," + l + "," + m + ")");
						}
							
					}
				}
			}			
		}
		
		
	}

	private static void generatePawnTables() {
		for(int j = 0;j < 5;j++){
			for(int l = 0;l < 64;l++){
				for(int m = 0;m < 64;m++){
					
				}
			}
		}
	}

	public static void loadTables(String fn) throws IOException{
		DataInputStream dis = new DataInputStream(new FileInputStream(fn));
		table = new byte[6][6][64][64];
		for(int i = 0;i < 6;i++){
			for(int j = 0;j < 6;j++){
				for(int k = 0;k < 64;k++){
					for(int l = 0;l < 64;l++){
						table[i][j][k][l] = dis.readByte();
					}
				}
			}
		}
		dis.close();
	}
	
	public static void storeTables(String fn) throws IOException{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(fn));
		for(int i = 0;i < 6;i++){
			for(int j = 0;j < 6;j++){
				for(int k = 0;k < 64;k++){
					for(int l = 0;l < 64;l++){
						 dos.writeByte(table[i][j][k][l]);
					}
				}
			}
		}
		dos.close();
	}
	
	public static Move getBestMove(int p1, int p2, int x, int y){
		return null;
	}
	
	private static int [] getBoardIndex(Board b) {
		long [][] bbs = b.getBitBoards();
		int [] p = new int[2];
		int [] pos = {-1,-1};
		Color c = b.getTurn();
		for(int j = 0;j < 2;j++){
			for(int k = 0;k < 6;k++){
				if(bbs[c.getIndex()][k] != 0){
					pos[j] = Long.numberOfTrailingZeros(bbs[c.getIndex()][k]);
					p[j] = k;
					break;
				}						
			}
			c = c.other();
		}
		
		int [] ret = {p[0],p[1],pos[0],pos[1]};
		return ret;
	}
	
}
