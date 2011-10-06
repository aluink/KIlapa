package net.aluink.chess.suicide.ai.pn;

import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;

public class PN2 extends PNSearch {
	
	public int[] pn2Search(Board b, long maxNodecount, LegalMoveGenerator lmg){
		NODES = PNSearch.init(1000000);
		board = b;
		this.lmg = lmg;
		rootColor = b.getTurn();
		root = NODES[0];
		long total_nodecount = 0;
		long tmpcount = 0;
		long time = System.currentTimeMillis();
		int arrayOffset = 1;
		while(root.proof != 0 && root.disproof != 0){
			if(System.currentTimeMillis() - time > 10000){	
				System.out.println(root.proof + " " + root.disproof + " " + nodecount + " " + total_nodecount + " " + (((total_nodecount-tmpcount) * 1000)/(System.currentTimeMillis() - time)));
				tmpcount = total_nodecount;
				time = System.currentTimeMillis();
			}
			int node = findMostProvingNode(0);
			PNSearch pn = new PNSearch();
			pn.search(b, subTreeSize(arrayOffset), lmg, node, arrayOffset, NODES);
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
		
		int n = -1;
		
		int ret [] = {root.proof, root.disproof, n};
		return ret;
	}
	
	private static long subTreeSize(long nodecount) {
		double A = 130000;
		double B = 30000;
		
		double d =  (1 / (1 + Math.exp((A - nodecount)/B)));
		long n = (long) (nodecount * d);
		return Math.max(n, 100);
	}
}
