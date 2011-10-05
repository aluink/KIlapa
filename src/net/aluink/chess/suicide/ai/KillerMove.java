package net.aluink.chess.suicide.ai;

import java.util.Comparator;

import net.aluink.chess.suicide.game.Move;

public class KillerMove implements Comparator<KillerMove> {
	Move m;
	int value;
	
	public KillerMove(Move m, int value) {
		this.m = m;
		this.value = value;
	}
	
	public KillerMove() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(KillerMove k1, KillerMove k2) {
		return k1.value - k2.value;
	}
	public Move getM() {
		return m;
	}
	public void setM(Move m) {
		this.m = m;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
