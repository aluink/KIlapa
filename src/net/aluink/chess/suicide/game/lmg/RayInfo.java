package net.aluink.chess.suicide.game.lmg;

public class RayInfo {
	BoundChecker bc;
	int dir;
	public RayInfo(BoundChecker bc, int dir) {
		super();
		this.bc = bc;
		this.dir = dir;
	}
	public BoundChecker getBc() {
		return bc;
	}
	public void setBc(BoundChecker bc) {
		this.bc = bc;
	}
	public int getDir() {
		return dir;
	}
	public void setDir(int dir) {
		this.dir = dir;
	}
}