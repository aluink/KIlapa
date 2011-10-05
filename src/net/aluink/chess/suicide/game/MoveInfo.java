package net.aluink.chess.suicide.game;

import net.aluink.chess.board.Piece;

public class MoveInfo extends Move {

	Piece capture;
	public Piece getCapture() {
		return capture;
	}

	public void setCapture(Piece capture) {
		this.capture = capture;
	}

	public MoveInfo(int start, int end) {
		super(start, end);
	}
	
	public MoveInfo(Move m, Piece capt){
		super(m);
		this.capture = capt;
	}

}
