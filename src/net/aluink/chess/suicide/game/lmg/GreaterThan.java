package net.aluink.chess.suicide.game.lmg;

public class GreaterThan implements BoundChecker {

	int end;
	
	public GreaterThan(int end){
		this.end = end;
	}
	
	@Override
	public boolean inBounds(int pos) {
		return pos > end;
	}
	
};
