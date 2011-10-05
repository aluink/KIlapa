package net.aluink.chess.suicide.game.lmg;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import net.aluink.chess.board.Piece;
import net.aluink.chess.board.Piece.Color;
import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;

public class SuicideLMG implements LegalMoveGenerator {

	public static final int UP = 8;
	public static final int DOWN = -8;
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	
	Board b;
	
	/* (non-Javadoc)
	 * @see net.aluink.chess.suicide.game.BoundChecker#getLegalMoves(net.aluink.chess.suicide.game.Board)
	 */
	@Override
	public Stack<Move> getLegalMoves(Board b) {
		this.b = b;
		Stack<Move> moves = new Stack<Move>();
		AttackingStatus attacking = new AttackingStatus();
		for(int i = 0;i < 64;i++){
			Piece p = b.getPos(i);
			if(p == null || p.getColor() != b.getTurn())
				continue;
			
			switch(p.getType()){
				case KING:
					getKingMoves(i, attacking, moves);
					break;
				case PAWN:
					getPawnMoves(i, attacking, moves);
					break;
				case ROOK:
					getRookMoves(i, attacking, moves);
					break;
				case KNIGHT:
					getKnightMoves(i, attacking, moves);
					break;
				case BISHOP:
					getBishopMoves(i, attacking, moves);
					break;
				case QUEEN:
					getBishopMoves(i, attacking, moves);
					getRookMoves(i, attacking, moves);
					break;
			}
		}
		return moves;
	}

	private void getBishopMoves(int start, AttackingStatus attacking, List<Move> moves) {
		getRayMoves(start, attacking, moves, Arrays.asList(
				new RayInfo(new CompoundBoundChecker(Arrays.asList(new LessThan(64),new ModNotEqual(8,7))),UP+LEFT),
				new RayInfo(new CompoundBoundChecker(Arrays.asList(new GreaterThan(0),new ModNotEqual(8,7))),DOWN+LEFT),
				new RayInfo(new CompoundBoundChecker(Arrays.asList(new LessThan(64),new ModNotEqual(8,0))),UP+RIGHT),
				new RayInfo(new CompoundBoundChecker(Arrays.asList(new GreaterThan(0),new ModNotEqual(8,0))),DOWN+RIGHT))
		);
	}

	private void getKnightMoves(int start, AttackingStatus attacking, List<Move> moves) {
		boolean d_up = start/8 < 6; //d_ means double. so d_up means up-up
		boolean d_down = start/8 > 1;
		boolean d_right = start%8 < 6;
		boolean d_left = start%8 > 1;
		boolean up = start/8 < 7;
		boolean down = start/8 > 0;
		boolean right = start%8 < 7;
		boolean left = start%8 > 0;
		
		if(!attacking.b){
			if(d_left && up)
				getRayNotAttacking(start, start+LEFT+LEFT+UP, attacking, moves);
			if(left && d_up)
				getRayNotAttacking(start, start+LEFT+UP+UP, attacking, moves);
			if(right && d_up)
				getRayNotAttacking(start, start+RIGHT+UP+UP, attacking, moves);
			if(d_right && up)
				getRayNotAttacking(start, start+RIGHT+RIGHT+UP, attacking, moves);
			if(d_right && down)
				getRayNotAttacking(start, start+RIGHT+RIGHT+DOWN, attacking, moves);
			if(right && d_down)
				getRayNotAttacking(start, start+RIGHT+DOWN+DOWN, attacking, moves);
			if(left && d_down)
				getRayNotAttacking(start, start+LEFT+DOWN+DOWN, attacking, moves);
			if(d_left && down)
				getRayNotAttacking(start, start+LEFT+LEFT+DOWN, attacking, moves);
		} else {
			if(d_left && up)
				getRayAttacking(start, start+LEFT+LEFT+UP, moves);
			if(left && d_up)
				getRayAttacking(start, start+LEFT+UP+UP, moves);
			if(right && d_up)
				getRayAttacking(start, start+RIGHT+UP+UP, moves);
			if(d_right && up)
				getRayAttacking(start, start+RIGHT+RIGHT+UP, moves);
			if(d_right && down)
				getRayAttacking(start, start+RIGHT+RIGHT+DOWN, moves);
			if(right && d_down)
				getRayAttacking(start, start+RIGHT+DOWN+DOWN, moves);
			if(left && d_down)
				getRayAttacking(start, start+LEFT+DOWN+DOWN, moves);
			if(d_left && down)
				getRayAttacking(start, start+LEFT+LEFT+DOWN, moves);
		}	
	}

	private void getRayMoves(int start, AttackingStatus attacking, List<Move> moves, List<RayInfo> rayInfos){
		int tmp;
		if(!attacking.b){
			for(RayInfo ri : rayInfos){
				tmp = start + ri.dir;
				while (ri.bc.inBounds(tmp)){
					if(getRayNotAttacking(start, tmp, attacking, moves))
						break;
					tmp += ri.dir;
				}
			}
		} else {
			for(RayInfo ri : rayInfos){
				tmp = start + ri.dir;
				while (ri.bc.inBounds(tmp)){
					if(getRayAttacking(start, tmp, moves))
						break;
					tmp += ri.dir;
				}
			}
		}
	}
	
	private void getRookMoves(int start, AttackingStatus attacking, List<Move> moves) {
		getRayMoves(start, attacking, moves,
			Arrays.asList(
				new RayInfo(new LessThan(64),UP),
				new RayInfo(new GreaterThan(0),DOWN),
				new RayInfo(new ModNotEqual(8,7),LEFT),
				new RayInfo(new ModNotEqual(8,0),RIGHT)
			)
		);		
	}
	private boolean getRayAttacking(int start, int tmp, List<Move> moves){
		if(b.getPos(tmp) != null){
			if(b.getPos(tmp).getColor() != b.getTurn())
				moves.add(new Move(start, tmp));
			return true;
		}
		return false;
	}
	
	class AttackingStatus {
		boolean b;
		
		public AttackingStatus(){
			b = false;
		}
		
		public void set(){
			b = true;
		}
	}
	
	private boolean getRayNotAttacking(int start, int tmp, AttackingStatus attacking, List<Move> moves){
		if(b.getPos(tmp) == null){
			if(!attacking.b)
				moves.add(new Move(start, tmp));
			return false;
		} else if(b.getPos(tmp).getColor() != b.getTurn()){
			if(!attacking.b){
				attacking.set();
				moves.clear();
			}
			moves.add(new Move(start, tmp));			
		}
		return true;
	}
	
	private void getPawnAttack(int start, int tmp, AttackingStatus attacking, List<Move> moves, boolean promo){
		if(tmp == b.getEnpassantPos()){
			if(!attacking.b){
				attacking.set();
				moves.clear();
			}
			moves.add(new Move(start, tmp, true));
		}
		if(b.getPos(tmp) != null && b.getPos(tmp).getColor() != b.getTurn()){
			if(!attacking.b){
				attacking.set();
				moves.clear();
			}
			if(promo){
				moves.addAll(Arrays.asList(Move.promoSet(start, tmp, b.getTurn())));
			} else {
				moves.add(new Move(start, tmp));
			}
		} 
		
	}
	
	private void getPawnMove(int start, int tmp, List<Move> moves, boolean promo){
		if(b.getPos(tmp) == null){
			if(promo){
				moves.addAll(Arrays.asList(Move.promoSet(start, tmp, b.getTurn())));
			} else {
				if(Math.abs(start-tmp) == 16){
					moves.add(new Move(start, tmp));
				} else {
					moves.add(new Move(start, tmp));
				}
			}
		}
	}
	
	private void getPawnMoves(int start, AttackingStatus attacking, List<Move> moves) {
		int row = start/8;
		boolean starting, promo;
		int dir;		
		if(b.getTurn() == Color.WHITE){
			dir = 8;
			starting = row == 1;
			promo = row == 6;
		}
		else{
			dir = -8;
			starting = row == 6;
			promo = row == 1;
		}
		
		if(start%8 != 0)
			getPawnAttack(start, start+dir+LEFT, attacking, moves, promo);
		if(start%8 != 7)
			getPawnAttack(start, start+dir+RIGHT, attacking, moves, promo);
		
		if(!attacking.b){
			getPawnMove(start, start+dir, moves, promo);
			if(starting)
				getPawnMove(start, start+dir+dir, moves, promo);
		}
		//TODO en passant
		
	}

	private void getKingMoves(int p, AttackingStatus attacking, List<Move> moves) {
		boolean up = p/8 < 7;
		boolean down = p/8 > 0;
		boolean left = p%8 > 0;
		boolean right = p%8 < 7;
		if(!attacking.b){
			if(left){
				if(down){
					getRayNotAttacking(p, p+LEFT+DOWN, attacking, moves);
				}
				getRayNotAttacking(p, p+LEFT, attacking, moves);
				if(up){
					getRayNotAttacking(p, p+LEFT+UP, attacking, moves);
				}
			}
			
			if(up){
				getRayNotAttacking(p, p+UP, attacking, moves);
				if(right){
					getRayNotAttacking(p, p+RIGHT+UP, attacking, moves);
				}
			}
			if(right){
				getRayNotAttacking(p, p+RIGHT, attacking, moves);
				if(down){
					getRayNotAttacking(p, p+RIGHT+DOWN, attacking, moves);
				}
			}
			if(down){
				getRayNotAttacking(p, p+DOWN, attacking, moves);
			}
		} else {
			if(left){
				if(down){
					getRayAttacking(p, p+LEFT+DOWN, moves);
				}
				getRayAttacking(p, p+LEFT, moves);
				if(up){
					getRayAttacking(p, p+LEFT+UP, moves);
				}
			}
			
			if(up){
				getRayAttacking(p, p+UP, moves);
				if(right){
					getRayAttacking(p, p+RIGHT+UP, moves);
				}
			}
			if(right){
				getRayAttacking(p, p+RIGHT, moves);
				if(down){
					getRayAttacking(p, p+RIGHT+DOWN, moves);
				}
			}
			if(down){
				getRayAttacking(p, p+DOWN, moves);
			}
		}
	}
	
}
