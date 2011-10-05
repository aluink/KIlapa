package net.aluink.chess.suicide.ai.pn;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;

public class PNSearch {
	PNNode root;
	Color rootColor;
	Board board;
	LegalMoveGenerator lmg;
	long nodecount;
	boolean prune;
	
	public static final int INF = 300000;
	
	public int[] search(Board b, long maxNodecount, LegalMoveGenerator lmg, File f, boolean prune){
		board = b;
		this.prune = prune;
		this.lmg = lmg;
		rootColor = b.getTurn();
		root = new PNNode();
		nodecount = 0;
		while(nodecount < maxNodecount && root.proof != 0 && root.disproof != 0){
			PNNode node = findMostProvingNode(root);
			expand(node);
//			System.out.println("=======");
//			board.printBoard();
			updateNodes(node);
//			board.printBoard();
//			System.out.println("*******");
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

	void updateNodes(PNNode node) {
		if(node.children != null){
			if(board.getTurn() == rootColor){
				node.proof = INF;
				node.disproof = 0;
				for(PNNode n : node.children){
					if(n.proof < node.proof){
						node.proof = n.proof;
					}
					node.disproof += n.disproof;
				}
				if(prune && node.proof == 0 && node.parent != null){
					node.disproof = INF;
					nodecount -= node.children.size();
					node.children = null;
				}
			} else {
				node.proof = 0;
				node.disproof = INF;
				for(PNNode n : node.children){
					if(n.disproof < node.disproof){
						node.disproof = n.disproof;
					}
					node.proof += n.proof;
				}
				if(prune && node.disproof == 0 && node.parent != null){
					node.proof = INF;
					nodecount -= node.children.size();
					node.children = null;
				}
			}
		}
			
		if(node.parent != null){	
			board.unmakeMove();
			updateNodes(node.parent);
		}
	}

	void expand(PNNode node) {
		Stack<Move> moves = lmg.getLegalMoves(board);
		if(moves.size() == 0){
			if(board.getTurn() == rootColor){
				node.proof = 0;
				node.disproof = INF;
			} else {
				node.proof = INF;
				node.disproof = 0;
			}
				
		} else {
			node.children = new ArrayList<PNNode>(moves.size());
			nodecount +=  moves.size();
			for(int i = 0;i < moves.size();i++){
				PNNode n = new PNNode();
				n.parent = node;
				node.children.add(n);
			}
		}
	}

	PNNode findMostProvingNode(PNNode node) {
		if(node.children == null){
			return node;
		}
		
		Stack<Move> moves = lmg.getLegalMoves(board);
		
		PNNode n = node.children.get(0);
		if(moves.size() == 0)
			board.printBoard();
		Move m = moves.get(0);
		
		if(board.getTurn() == rootColor){
			for(int i = 1;i < node.children.size();i++){
				PNNode tmp = node.children.get(i);
				if(tmp.proof == node.proof){
					n = tmp;
					m = moves.get(i);
					break;
				}
			}
		} else {
			for(int i = 1;i < node.children.size();i++){
				PNNode tmp = node.children.get(i);
				if(tmp.disproof == node.disproof){
					n = tmp;
					m = moves.get(i);
					break;
				}
			}
		}
		board.makeMove(m);
		return findMostProvingNode(n);
	}
}