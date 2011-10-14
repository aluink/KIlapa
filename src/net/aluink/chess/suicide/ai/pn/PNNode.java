package net.aluink.chess.suicide.ai.pn;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.suicide.ai.SuicidePlayer;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;


public class PNNode {
	int proof;
	int disproof;
	int firstChild;
	int move; // Move from parent to here
	int sibling;
	int parent;
	
	public PNNode(){
		proof = disproof = 1;
		parent = -1;
		sibling = -1;
		move = -1;
		firstChild = -1;
	}
	
	public String toString(){
		return proof + " " + disproof + " " + firstChild + " " + move + " " + sibling + " " + parent;
	}
	
	public void store(DataOutputStream dos, Board b, PNNode [] nodes, Color c) throws IOException{
		store(dos, b, true, nodes, c);
	}
	
	private void store(DataOutputStream dos, Board b, boolean storeFen, PNNode [] nodes, Color c) throws IOException{
		
		
		if(storeFen) dos.writeUTF(b.getFen());
		dos.writeInt(move);
		dos.writeInt(SuicidePlayer.INF * (c == Color.BLACK ? -1 : 1));
		
		int i = 0;
		
		if(this.firstChild == -1) {
			dos.writeInt(0);
			return;
		}
		PNNode tmp = nodes[this.firstChild];
		while(tmp.sibling != -1){
			if(tmp.proof == 0) i++;
			tmp = nodes[tmp.sibling];
		}
		dos.writeInt(i);
		
		tmp = nodes[this.firstChild];
		while(true) {
			if(tmp.proof == 0){
				b.makeMove(new Move(tmp.move));
				tmp.store(dos, b, false, nodes, c);
				b.unmakeMove();
			}
			if(tmp.sibling == -1)
				break;
			tmp = nodes[tmp.sibling];
		} 
	}
	
	public static void main(String[] args) throws IOException {
		int index = 0;
		int [] moves = {
				new Move(48, 40).getCompressed(),
				new Move(48, 32).getCompressed(),
				new Move(49, 41).getCompressed(),
				new Move(49, 33).getCompressed(),
				new Move(50, 42).getCompressed()
		};
		PNNode [] nodes = new PNNode[20];
		for(int i = 0;i < 20;i++){
			nodes[i] = new PNNode();
		}
		PNNode root = nodes[index];
		root.disproof = 20;
		root.proof = 0;
		root.firstChild = 1;
		root.sibling = -1;
		root.move = -1;
		root.parent = -1;
		
		for(int i = 0;i < 5;i++){
			index++;
			PNNode tmp = nodes[index];
			tmp.disproof = 20;
			tmp.proof = i;
			tmp.firstChild = -1;
			tmp.sibling = index+1;
			tmp.move = moves[i];
			tmp.parent = 0;
		}
		DataOutputStream dos = new DataOutputStream(new FileOutputStream("tmpPNStore"));
		Board board = new Board();
		board.setToStarting();
		Move m = new Move("e2e3");
		board.makeMove(m);		
		root.store(dos, board, nodes, Color.BLACK);
		dos.close();
		System.out.println();
	}
	
}
