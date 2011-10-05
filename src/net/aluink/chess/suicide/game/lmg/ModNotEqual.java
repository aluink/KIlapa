/**
 * 
 */
package net.aluink.chess.suicide.game.lmg;

public class ModNotEqual implements BoundChecker {

	int n;
	int c;

	public ModNotEqual(int n, int c){
		this.n = n;
		this.c = c;
	}
	
	@Override
	public boolean inBounds(int pos) {
		return pos >= 0 && pos % n != c;
	}
	
}