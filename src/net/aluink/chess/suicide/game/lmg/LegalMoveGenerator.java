package net.aluink.chess.suicide.game.lmg;

import java.util.Stack;

import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;

public interface LegalMoveGenerator {
	public Stack<Move> getLegalMoves(Board b);
}
