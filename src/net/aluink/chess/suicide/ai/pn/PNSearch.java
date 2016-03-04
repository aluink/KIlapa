package net.aluink.chess.suicide.ai.pn;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.suicide.Kilapa.Logger;
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
	
	public int getProof(){
		return root.proof;
	}
	
	public int getWinningChild(){
		int itr = root.firstChild;
		int i = 0;
		while(itr != -1){
			if(NODES[itr].proof == 0)
				return i;
			i++;
			itr = NODES[itr].sibling;
		}
		return i;
	}
	
	public static PNNode [] init(){
		return init(1000000);
	}
	
	public static PNNode [] init(int np){
		PNNode [] nodes = new PNNode[np];
		for(int i = 0;i < np;i++){
			nodes[i] = new PNNode();
		}
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
			int i = 0;
			while(n < endIndex){
				tmp = NODES[n];
				tmp.parent = index;
				tmp.proof = tmp.disproof = 1;
				tmp.sibling = ++n;
				Move m = moves.get(i++);
				tmp.move = m.getCompressed();
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
		
		int n = node.firstChild;
		
		if(board.getTurn() == rootColor){
			for(;n >= 0;n = NODES[n].sibling){
				PNNode tmp = NODES[n];
				if(tmp.proof == node.proof){
					break;
				}
			}
		} else {
			for(;n >= 0;n = NODES[n].sibling){
				PNNode tmp = NODES[n];
				if(tmp.disproof == node.disproof){
					break;
				}
			}
		}
		if(n == -1){
			Logger.Singleton.logn("Error in FMP, printing state");
			PNNode tmpNode = node;
			Queue<PNNode> q = new LinkedList<PNNode>();
			q.add(tmpNode);
			while(tmpNode.parent != -1){
				tmpNode = NODES[tmpNode.parent];
				q.add(tmpNode);
			}
			while(!q.isEmpty()){
				tmpNode = q.remove();
				Logger.Singleton.logn(tmpNode);
			}
			Logger.Singleton.logn();
			while(!board.atBeginning()){
				board.printBoard();
				board.unmakeMove();
			}
			Logger.Singleton.logn();
		}
//		try {
			PNNode t = NODES[n];
			Move m = new Move(t.move);
			board.makeMove(m);
			return findMostProvingNode(n);
//		} catch (Exception e) {
//			Kilapa.Log("Error in FMP, printing state");
//			PNNode tmpNode = node;
//			Queue<PNNode> q = new LinkedList<PNNode>();
//			q.add(tmpNode);
//			while(tmpNode.parent != -1){
//				tmpNode = NODES[tmpNode.parent];
//				q.add(tmpNode);
//			}
//			while(!q.isEmpty()){
//				tmpNode = q.remove();
//				Kilapa.Log(tmpNode);
//			}
//			Kilapa.Log();
//			while(!board.atBeginning()){
//				board.printBoard();
//				board.unmakeMove();
//			}
//			Kilapa.Log();
//			e.printStackTrace();
//			Kilapa.Log("Exiting");
//			System.exit(1);
//			return -1;
//		}
	}
}