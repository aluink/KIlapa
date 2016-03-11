package net.aluink.chess.suicide;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Stack;

import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.suicide.ai.SuicidePlayer;
import net.aluink.chess.suicide.ai.pn.PN2;
import net.aluink.chess.suicide.ai.pn.PNSearch;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;
import net.aluink.chess.suicide.game.lmg.SuicideLMG;
import net.aluink.chess.suicide.game.lmg.bitboards.Magic;

public class Kilapa {

	public static class Logger {
		PrintWriter mWriter = null;
		LoggerType mType;
		
		public LoggerType getType(){
			return mType;
		}

		public static Logger Singleton = new Logger(LoggerType.WBDEBUG);
		
		public static enum LoggerType {
			FILE, ERR, STD, WBDEBUG
		};

		private Logger(LoggerType type) {
			if (type == LoggerType.FILE) {
				try {
					mWriter = new PrintWriter("logFile.txt", "UTF-8");
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			mType = type;
		}	
		
		public void logn() {
			logn("");
		}

		public void logn(String format, Object... objects) {
			logn(String.format(format, objects));
		}

		public void logn(Object str) {
			log(str + "\n");
		}
		
		private void log(Object str) {
			if(mType == LoggerType.ERR) {
				System.err.print(str);
			} else if(mType == LoggerType.FILE) {
				mWriter.print(str);
				mWriter.flush();
			} else if(mType == LoggerType.STD) {
				System.out.print(str);
			} else if(mType == LoggerType.WBDEBUG) {
				System.out.print("# " + str.toString());
			}
		}
		
		public void close(){
			if(mWriter != null) {
				mWriter.close();
				mWriter = null;
			}
		}
	}



	private static boolean isIgnoredCommand(String command) {
		if (command.startsWith("xboard"))
			return true;
		if (command.startsWith("protover"))
			return true;
		if (command.startsWith("random"))
			return true;
		if (command.startsWith("level"))
			return true;
		if (command.startsWith("post"))
			return true;
		if (command.startsWith("hard"))
			return true;
		if (command.startsWith("time"))
			return true;
		if (command.startsWith("result"))
			return true;
		if (command.startsWith("otim"))
			return true;
		if (command.startsWith("force"))
			return true;
		return false;
	}
	
	public Move convertUserMove(String input){
		int scol = input.charAt(0) - 'a';
		int srow = input.charAt(1) - '1';
		int ecol = input.charAt(2) - 'a';
		int erow = input.charAt(3) - '1';
		int start = srow * 8 + scol;
		int end = erow * 8 + ecol;
		return validMove(start, end) ? new Move(start, end) : null;
	}

	private String xboardMode(Scanner s, LegalMoveGenerator lmg, SuicidePlayer sp) {	
		/* COMMAND *
		 * 
		 * **********************
		 * COMMANDS FROM XBOARD *
		 * ********************** 
		 * https://www.gnu.org/software/xboard/engine-intf.html#8
		 * 
		 * xboard - put in xboard mode
		 * protover N - ignored
		 * variant NAME - ignored
		 * quit
		 * random
		 * force
		 * go
		 * playother
		 * white
		 * black
		 * level MPS BASE INC
		 * st TIME
		 * sd DEPTH
		 * nps NODE_RATE
		 * time N
		 * otim N
		 * MOVE
		 * usermove MOVE
		 * ?
		 * ping N
		 * draw
		 * result RESULT {COMMENT}
		 * setboard FEN
		 * edit
		 * hint
		 * bk
		 * undo
		 * remove
		 * hard
		 * easy
		 * post
		 * nopost
		 * analyze
		 * name X
		 * rating 
		 * ics HOSTNAME
		 * computer
		 * pause
		 * resume
		 * memory N
		 * core N
		 * egtpath TYPE PATH
		 * option NAME{=VALUE}
		 * exclude MOVE
		 * include MOVE
		 * exclude all
		 * include all
		 * setscore SCORE DEPTH
		 * life SQUARE
		 * put SQUARE
		 * hover SQUARE
		 * 
		 */
		String command;
		Board b = null;
		while(true) {
			command = s.nextLine();
			if(command.equals("quit")){
				return "quit";
			} else if(command.equals("new")){
				b = new Board();
				b.setTurn(Color.WHITE);
				sp = new SuicidePlayer(b, Color.BLACK);
			} else {
				Move m = convertUserMove(command);
				if(m != null){
					b.makeMove(m);
				}
			}
		}
	}
	
	public Kilapa() throws IOException {
		Board b = null;
		LegalMoveGenerator lmg = new SuicideLMG();
		SuicidePlayer sp = null;
		Stack<Move> moves = new Stack<Move>();
		Magic.init();
		String command;
		Scanner s = new Scanner(System.in);
		try {
			while (true) {
				command = s.nextLine();
				if(command.equals("xboard")) {
					command = xboardMode(s, lmg, sp);
				}
				
				if(command.equals("quit")){
					return;
				}
				
				
					
				
				
				
				/*
				if (b != null) {
					moves = lgm.getLegalMoves(b);
					b.printBoard();
					if (moves.size() == 0) {
						sp = null;
						int count = b.getWhitePieceCount();
						int whites = count / 100;
						int blacks = count % 100;
						if(whites > blacks) System.out.println("0-1 {Black mates}");
						else if(whites < blacks) System.out.println("1-0 {White mates}");
						else if(whites == blacks) System.out.println("1/2-1/2 {Stalemate}");
					}
					if (sp != null && sp.getSide() == b.getTurn()) {
						PNSearch pn = new PNSearch();
						pn.search(b, 400000, new SuicideLMG());
						Move m;
						if (pn.getProof() == 0) {
							m = moves.elementAt(pn.getWinningChild());
						} else {
							m = sp.getMove();
						}
						Logger.Singleton.logn("Engine plays: " + m);
						b.makeMove(m);
						System.out.println("move " + m);
						continue;
					}
				}
				

				command = br.readLine();
				Logger.Singleton.logn("Got command: " + command);
				if (command.equals("quit")) {
					break;
				} else if(command.equals("force")){
					if(sp != null) sp.setSide(null);
				} else if (command.equals("go")) {
					if (sp == null)
						sp = new SuicidePlayer(b, b.getTurn());
					Move m = sp.getMove();
					b.makeMove(m);
					System.out.println("move " + m);
				} else if (command.equals("new")) {
					b = new Board();					
					b.setToStarting();
					sp = new SuicidePlayer(b, Color.BLACK);
					continue;
				} else if (command.equals("undo")) {
					b.unmakeMove();
				} else if (command.equals("printbb")) {
					b.printBitboards();
				} else if (command.startsWith("setFen")) {
					String fen = command.substring(7).trim();
					Logger.Singleton.logn("Setting fen: " + fen);
					try {
						b = new Board();
						b.setFen(fen);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (command.equals("test")) {
					for (Move m : moves) {
						Logger.Singleton.logn(m);
					}
				} else if (command.equals("pn")) {
					long start = System.currentTimeMillis();
					int pn[] = new PNSearch().search(b, 300000, new SuicideLMG());
					Logger.Singleton.logn(pn[0] + " " + pn[1] + " " + (pn[2] >= 0 ? moves.get(pn[2]) : "") + " " + pn[3] + "n");
					Logger.Singleton.logn((pn[3] * 1000) / (System.currentTimeMillis() - start) + "NPS");
					b.makeMove(moves.get(pn[2]));
				} else if (command.equals("pn2")) {
					new PN2().pn2Search(b, 5000000, new SuicideLMG());
				} else if (isIgnoredCommand(command)) {
					continue;
				} else {
					try {
						int scol = command.charAt(0) - 'a';
						int srow = command.charAt(1) - '1';
						int ecol = command.charAt(2) - 'a';
						int erow = command.charAt(3) - '1';
						int start = srow * 8 + scol;
						int end = erow * 8 + ecol;
						Move m;
						if (validMove(start, end) && moves.contains(m = new Move(start, end))) {
							m = moves.get(moves.indexOf(m));
							b.makeMove(m);
						} else {
							throw new Exception();
						}

					} catch (Exception e) {
						Logger.Singleton.logn("Illegal move");
						System.err.println(e.getMessage());
					}
				}
				*/
			}
		} finally {
			Logger.Singleton.close();
		}
	}

	public static void main(String[] args) throws IOException {
		new Kilapa();
	}
	
	private static boolean validMove(int start, int end) {
		return start >= 0 && start < 64 && end >= 0 && end < 64;
	}

}
