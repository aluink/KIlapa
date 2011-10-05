package net.aluink.chess.suicide.game.lmg;

public class LessThan implements BoundChecker {

	int end;
	
	public LessThan(int end){
		this.end = end;
	}
	
	@Override
	public boolean inBounds(int pos) {
		return pos < end;
	}
	
};