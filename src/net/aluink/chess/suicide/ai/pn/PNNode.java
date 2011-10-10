package net.aluink.chess.suicide.ai.pn;


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
	
}
