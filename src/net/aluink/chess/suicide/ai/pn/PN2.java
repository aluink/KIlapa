package net.aluink.chess.suicide.ai.pn;

import java.io.File;

import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;

public class PN2 extends PNSearch {

	@Override
	public int[] search(Board b, long startNodeCount, LegalMoveGenerator lmg, File f, boolean prune){
		board = b;
		this.lmg = lmg;
		rootColor = b.getTurn();
		root = new PNNode();
		nodecount = 0;
		long total_nodecount = 0;
		long tmpcount = 0;
		long time = System.currentTimeMillis();
		
		while(root.proof != 0 && root.disproof != 0){
			if(System.currentTimeMillis() - time > 10000){				
				System.out.println( root.proof + " " +
									root.disproof + " " +
									nodecount + " " +
									total_nodecount + " " +
									(((total_nodecount-tmpcount) * 1000)/(System.currentTimeMillis() - time)));
				tmpcount = total_nodecount;
				time = System.currentTimeMillis();
			}
			PNNode node = findMostProvingNode(root);
			PNSearch pn = new PNSearch();
			pn.search(b, subTreeSize(nodecount), lmg, f, false);
			total_nodecount += pn.nodecount;
			copyRoot(pn.root, node);
			updateNodes(node);
		}
		
		int n = -1;
		if(root.proof == 0){
			for(n = 0;n < root.children.size();n++){
				if(root.children.get(n).proof == 0)
					break;
			}
		}
		
		int ret [] = {root.proof, root.disproof, n};
		return ret;
	}
	
	private static long subTreeSize(long nodecount) {
		double A = 600000;
		double B = 80000;
		
		double d =  (1 / (1 + Math.exp((A - nodecount)/B)));
		long n = (long) (nodecount * d);
		return Math.max(n, 100);
	}
	
	void copyRoot(PNNode root, PNNode node){
		node.proof = root.proof;
		node.disproof = root.disproof;
		node.children = root.children;
		nodecount += node.children.size();
		for(PNNode n : node.children){
			n.parent = node;
			n.children = null;
		}
	}
	
}
