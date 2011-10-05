package net.aluink.chess.suicide.ai.pn;

import java.util.List;

public class PNNode {
	int proof;
	int disproof;
	List<PNNode> children;
	PNNode parent;
	
	public PNNode(){
		proof = disproof = 1;
		parent = null;
		children = null;
	}
	
}
