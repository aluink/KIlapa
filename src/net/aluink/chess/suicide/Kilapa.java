package net.aluink.chess.suicide;

import java.util.Scanner;
import java.util.Stack;

import net.aluink.chess.suicide.ai.SuicidePlayer;
import net.aluink.chess.suicide.ai.pn.PN2;
import net.aluink.chess.suicide.ai.pn.PNNode;
import net.aluink.chess.suicide.ai.pn.PNSearch;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;
import net.aluink.chess.suicide.game.lmg.SuicideLMG;

public class Kilapa {
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		Board b = null;
		LegalMoveGenerator lgm = new SuicideLMG();
		SuicidePlayer sp = null;
		Stack<Move> moves = new Stack<Move>();
		while(true){
			if(b != null){
				b.printBoard();
				moves = lgm.getLegalMoves(b);
			}
			if(sp != null && sp.getSide() == b.getTurn()){
				Move m = sp.getMove();
				System.out.println("Engine plays: " + m);
				b.makeMove(m);
				continue;
			}
			String command = s.next();		
			if(command.equals("quit")){
				break;
			} else if(command.equals("go")){
				if(sp == null)
					sp = new SuicidePlayer(b, b.getTurn());
				b.makeMove(sp.getMove());
			} else if(command.equals("new")){
				b = new Board();
				b.setToStarting();
//				try {
//					b.setFen("1nbk1bnr/rpp1pppp/8/8/6b1/8/1PPPPPBP/2BQKBNR b");
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				sp = null;
				continue;
			} else if(command.equals("undo")){
				b.unmakeMove();
			} else if(command.equals("test")){
				for(Move m : moves){
					System.out.println(m);
				}
			} else if(command.equals("pn")){
				long start = System.currentTimeMillis();
				int pn [] = new PNSearch().search(b, 400000, new SuicideLMG());				
				System.out.println(pn[0] + " " + pn[1] + " " + (pn[2] >= 0 ? moves.get(pn[2]) : "") + " " + pn[3]);
				System.out.println((pn[3]*1000) / (System.currentTimeMillis() - start));
				b.makeMove(moves.get(pn[2]));
			} else if(command.equals("pn2")){
				int pn [] = new PN2().pn2Search(b, 400000, new SuicideLMG());
				System.out.println(pn[0] + " " + pn[1] + " " + (pn[2] >= 0 ? moves.get(pn[2]) : ""));
			} else {
				try {
					int scol = command.charAt(0)-'a';
					int srow = command.charAt(1)-'1';
					int ecol = command.charAt(2)-'a';
					int erow = command.charAt(3)-'1';
					int start = srow*8+scol;
					int end = erow*8+ecol;
					Move m;
					if(validMove(start, end) && moves.contains(m = new Move(start,end))){
					 	m = moves.get(moves.indexOf(m));
					 	int t = m.getByte();
					 	System.out.println(t);
						b.makeMove(m);
					} else {
						throw new Exception();
					}
					
				} catch (Exception e) {
					System.out.println("Illegal move");
				}
			}
		}
	}

	private static boolean validMove(int start, int end) {
		return start >= 0 && start < 64 && end >= 0 && end < 64;
	}
	
	
}
