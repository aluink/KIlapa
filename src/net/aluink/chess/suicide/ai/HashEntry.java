package net.aluink.chess.suicide.ai;

import net.aluink.chess.suicide.game.Move;

public class HashEntry {

	Move bestmove;
	long check;
	int depth;
	int value;

	public HashEntry(long check, int value, Move bestmove, int depth) {
		super();
		this.check = check;
		this.value = value;
		this.bestmove = bestmove;
		this.depth = depth;
	}

	public Move getBestmove() {
		return bestmove;
	}

	public long getCheck() {
		return check;
	}

	public int getDepth() {
		return depth;
	}

	public int getValue() {
		return value;
	}

	public void setBestmove(Move bestmove) {
		this.bestmove = bestmove;
	}
	public void setCheck(long check) {
		this.check = check;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
