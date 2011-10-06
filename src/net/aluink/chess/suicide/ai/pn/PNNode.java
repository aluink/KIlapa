package net.aluink.chess.suicide.ai.pn;


public class PNNode {
	int proof;
	int disproof;
	int firstChild;
	int sibling;
	int parent;
	
	public PNNode(){
		proof = disproof = 1;
		parent = -1;
		sibling = -1;
		firstChild = -1;
	}
	
}
