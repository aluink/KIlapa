package net.aluink.chess.suicide.ai.pn;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.suicide.ai.SuicidePlayer;
import net.aluink.chess.suicide.ai.book.BookNode;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;
import net.aluink.chess.suicide.game.lmg.SuicideLMG;
import net.aluink.chess.suicide.game.lmg.bitboards.Magic;

public class PN2 extends PNSearch {
	
	double A = 5000000;//130000;
	double B = 1000000;
	
	private static Object [] leadingSpaces(String str){
		int i = 0;
		while(str.charAt(i++) == ' ');
		Object [] ret = {new Integer(--i), str.trim()};
		return ret;
		
	}
	
	public static BookNode parseResults(String file) throws Exception{
		BufferedReader fr = new BufferedReader(new FileReader(file));
		BookNode root;
		try {
			String fen = fr.readLine();
			Board b = new Board();
			b.setFen(fen);
			int score = SuicidePlayer.INF;
			if(b.getTurn() == Color.BLACK)
				score *= -1;
			
			root = new BookNode();
			BookNode itr = root;
			String line;
			int depth = 0;
			while((line = fr.readLine()) != null){
				Object [] split = leadingSpaces(line);
				int d = (Integer) split[0];
				line = (String) split[1];
				if(d < depth){
					while(d < depth){
						b.unmakeMove();
						itr = itr.getParent();
						depth--;
					}
				} else if(d > depth){
					throw new Exception("Error");
				}
				
				depth++;
				Move m = new Move(line);
				b.makeMove(m);
				BookNode tmp = new BookNode();
				
				tmp.setFen(b.getFen());
				itr.addChild(tmp);
				tmp.setMove(m);
				tmp.setScore(score);
				tmp.setParent(itr);
				
				itr = tmp;
				
			}
		} finally {
			fr.close();
		}


		
		return root;
	}
	
	public void pn2Search(Board b, int maxNodecount, LegalMoveGenerator lmg){
		Magic.init();
		NODES = PNSearch.init(maxNodecount);
		board = b;
		this.lmg = lmg;
		rootColor = b.getTurn();
		root = NODES[0];
		long total_nodecount = 0;
		long tmpcount = 0;
		long time = System.currentTimeMillis();
		int arrayOffset = 1;
		long subtreeSize;
		long startTime = System.currentTimeMillis();
		System.out.println("PN2 searching " + maxNodecount);
		while(root.proof != 0 && root.disproof != 0){
			subtreeSize = subTreeSize(arrayOffset);
			if(System.currentTimeMillis() - time > 60000){	
				System.out.println(root.proof + " " + root.disproof + " " + arrayOffset + " " + subtreeSize + " " + total_nodecount + " " + (((total_nodecount-tmpcount) * 1000)/(System.currentTimeMillis() - time)));
				tmpcount = total_nodecount;
				time = System.currentTimeMillis();
			}
			int node = findMostProvingNode(0);
			PNSearch pn = new PNSearch();
			pn.search(b, subtreeSize, lmg, node, arrayOffset, NODES);
			PNNode tmp = NODES[NODES[node].firstChild];
			while(true){
				arrayOffset++;
				tmp.firstChild = -1;
				if(tmp.sibling == -1)
					break;				
				tmp = NODES[tmp.sibling];
			}
			total_nodecount += pn.nodecount;
			
			updateNodes(node);
		}
		System.out.println("Done.");
		System.out.println(root.proof + " " + root.disproof + " " + arrayOffset + " " + " " + total_nodecount + " " + (((total_nodecount-tmpcount) * 1000)/(System.currentTimeMillis() - time)));
		System.out.println("Done in " + (System.currentTimeMillis() - startTime)/1000 + "s");
	}
	
	public static void main(String[] args) throws Exception {
		Board b = new Board();
		b.setFen(args[1]);
		b.printBoard();
		PN2 pn2 = new PN2();
		System.out.println(args[0]);
		int nc = Integer.valueOf(args[0]);
		pn2.pn2Search(b, nc, new SuicideLMG());
		
		String filename;
		try {
			filename = args[2];
		} catch (Exception e) {
			filename = System.currentTimeMillis()%1000 + ".pnr";
			System.out.println("No filename provided, storing to " + filename);
		}
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename));
		pn2.root.store(dos,b,pn2.NODES, pn2.rootColor);
		dos.close();
		
	}
	
//	public static void main(String[] args) throws Exception {
//		BookNode bn = parseResults("e3g6.pnr");
//		DataOutputStream dos = new DataOutputStream(new FileOutputStream("e3g6.spr"));
//		bn.store(dos);
//		dos.close();
//	}

	
	
	private void printResults(FileWriter fr, int index, int offset) throws IOException {
		int itr = NODES[index].firstChild;
		if(itr == -1)
			return;
		PNNode tmp = NODES[itr];
		StringBuilder sb = new StringBuilder("");
		for(int i = 0;i < offset;i++){
			sb.append(" ");
		}
		String padding = sb.toString();
		Stack<Move> moves = lmg.getLegalMoves(board);
		for(Move m : moves){
			if(tmp.proof == 0){
				fr.write(padding + m);
				fr.write("\r\n");
				board.makeMove(m);
				printResults(fr, itr, offset+1);
				board.unmakeMove();
			}
			itr = tmp.sibling;
			if(itr != -1)
				tmp = NODES[itr];
		}
		
	}

	private long subTreeSize(long nodecount) {
		double d =  (1 / (1 + Math.exp((A - nodecount)/B)));
		return Math.max((long) (nodecount * d), 100);
	}
}
