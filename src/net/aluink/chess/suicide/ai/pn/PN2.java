package net.aluink.chess.suicide.ai.pn;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;
import net.aluink.chess.suicide.game.lmg.SuicideLMG;

public class PN2 extends PNSearch {
	
	double A = 5000000;//130000;
	double B = 1000000;
	
	
	
	public void pn2Search(Board b, int maxNodecount, LegalMoveGenerator lmg){
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
		System.out.println("PN2 searching " + maxNodecount);
		while(root.proof != 0 && root.disproof != 0){
			subtreeSize = subTreeSize(arrayOffset);
			if(System.currentTimeMillis() - time > 10000){	
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
	}
	
	public static void main(String[] args) throws Exception {
		Board b = new Board();
		b.setFen(args[1]);
		b.printBoard();
		PN2 pn2 = new PN2();
		System.out.println(args[0]);
		int nc = Integer.valueOf(args[0]);
		pn2.pn2Search(b, nc, new SuicideLMG());
		
		String filename = System.currentTimeMillis()%1000 + ".pnr";
		FileWriter fr = null;
		try {
			fr = new FileWriter(filename);
			fr.write(args[1] + "\r\n");
			pn2.printResults(fr, 0, 0);			
		} finally{
			if(fr != null){
				fr.close();
			}
		}
		
	}

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
