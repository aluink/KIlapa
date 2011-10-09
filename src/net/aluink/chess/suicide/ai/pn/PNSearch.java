package net.aluink.chess.suicide.ai.pn;

import java.util.Stack;

import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;

public class PNSearch {
	
	int NODEPOOL;
	PNNode [] NODES;
	
	PNNode root;
	Color rootColor;
	Board board;
	LegalMoveGenerator lmg;
	long nodecount;
	int endIndex;
	
	public static PNNode [] init(){
		return init(1000000);
	}
	
	public static PNNode [] init(int np){
		System.out.print("Initializing " + np + " pn nodes...");
		PNNode [] nodes = new PNNode[np];
		for(int i = 0;i < np;i++){
			nodes[i] = new PNNode();
		}
		System.out.println("done.");
		return nodes;
	}
	
	public static final int INF = 300000;
	
	public int [] search(Board b, long maxNodeCount, LegalMoveGenerator lmg){
		return search(b, maxNodeCount, lmg, 0, 1, init((int)maxNodeCount+2000));
	}
	
	public int[] search(Board b, long maxNodecount, LegalMoveGenerator lmg, int rIndex, int startIndex, PNNode [] nodes){
		this.NODES = nodes;
		board = b;
		this.endIndex = startIndex;
		this.lmg = lmg;
		rootColor = b.getTurn();
		root = NODES[rIndex];
		int rootTmpParent = root.parent;
		root.parent = -1;
		nodecount = 0;
		while(nodecount < maxNodecount && root.proof != 0 && root.disproof != 0){
			int node = findMostProvingNode(rIndex);
			expand(node);
			updateNodes(node);
		}
		
		int n = -1;
		if(root.proof == 0){
			n = root.firstChild;
			while(n != -1){
				if(NODES[n].proof == 0)
					break;
				n = NODES[n].sibling;
			}
		}
		
		root.parent = rootTmpParent;
		
		int ret [] = {root.proof, root.disproof, n-root.firstChild, endIndex, (int)nodecount};
		return ret;
	}

	void updateNodes(int index) {
		PNNode node = NODES[index];
		if(node.firstChild >= 0){
			if(board.getTurn() == rootColor){
				node.proof = INF;
				node.disproof = 0;
				for(int i = node.firstChild;i >= 0;i = NODES[i].sibling){
					PNNode tmp = NODES[i];
					if(tmp.proof < node.proof){
						node.proof = tmp.proof;
					}
					node.disproof += tmp.disproof;
				}
			} else {
				node.proof = 0;
				node.disproof = INF;
				for(int i = node.firstChild;i >= 0;i = NODES[i].sibling){
					PNNode tmp = NODES[i];
					if(tmp.disproof < node.disproof){
						node.disproof = tmp.disproof;
					}
					node.proof += tmp.proof;
				}
			}
		}
			
		if(node.parent != -1){	
			board.unmakeMove();
			updateNodes(node.parent);
		}
	}

	void expand(int index) {
		PNNode node = NODES[index];
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
			int n = NODES[index].firstChild = endIndex;
			endIndex += moves.size();
			PNNode tmp = null;
			while(n < endIndex){
				tmp = NODES[n];
				tmp.parent = index;
				tmp.proof = tmp.disproof = 1;
				tmp.sibling = ++n;
				tmp.firstChild = -1;
				nodecount++;
			}
			tmp.sibling = -1;
		}
	}

	int findMostProvingNode(int index) {
		PNNode node = NODES[index];
		if(node.firstChild < 0){
			return index;
		}
		
		Stack<Move> moves = lmg.getLegalMoves(board);
		
		int n = node.firstChild;
		Move m = moves.get(0);
		
		if(board.getTurn() == rootColor){
			for(int i = 0;n >= 0;n = NODES[n].sibling, i++){
				PNNode tmp = NODES[n];
				if(tmp.proof == node.proof){
					m = moves.get(i);
					break;
				}
			}
		} else {
			for(int i = 0;n >= 0;n = NODES[n].sibling, i++){
				PNNode tmp = NODES[n];
				if(tmp.disproof == node.disproof){
					m = moves.get(i);
					break;
				}
			}
		}
		board.makeMove(m);
		return findMostProvingNode(n);
	}
}