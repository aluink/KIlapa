package net.aluink.chess.suicide.game.lmg;

import java.util.List;

public class CompoundBoundChecker implements BoundChecker {

	List<BoundChecker> bcs;
	
	public CompoundBoundChecker(List<BoundChecker> bcs){
		this.bcs = bcs;
	}
	
	@Override
	public boolean inBounds(int pos) {
		for(BoundChecker bc : bcs){
			if(!bc.inBounds(pos)){
				return false;
			}
		}
		return true;
	}
	
}