package net.aluink.chess.pgn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import net.aluink.chess.suicide.game.Board;
import net.aluink.chess.suicide.game.Move;
import net.aluink.chess.suicide.game.lmg.LegalMoveGenerator;
import net.aluink.chess.suicide.game.lmg.SuicideLMG;

public class PGNParser {
	public PGNParser(){
		
	}
	
	public List<Game> parse(String fn) throws IOException{
		List<Game> games = new LinkedList<Game>();
		BufferedReader reader = new BufferedReader(new FileReader(fn));
		
		String line;
		Game g = new Game();
		LegalMoveGenerator lmg = new SuicideLMG();
		Board b;
		String result = null;
		while((line = reader.readLine()) != null){
			line = removeComments(line);
			if(line.length() < 1)
				continue;
			if(line.charAt(0) == '['){
				line = line.substring(1, line.length()-1);
				String [] pair = line.split(" ");
				pair[1] = pair[1].substring(1, pair[1].length()-1).trim();
				if(pair[0].trim().equals("Result"))
					result = pair[1].trim();
				g.addPair(pair[0].trim(), pair[1]);
				
			} if(line.startsWith("1.")){
				line = line.substring(2).trim();
				String [] moves = line.split("\\s+\\d{1,4}\\.\\s+");
				
				b = new Board();
				b.setToStarting();
				for(String move : moves){
					if(move.trim().equals(result))
						continue;
					String [] plys = move.split("\\s+");
					int i;
					for(i = 0;i < 2;i++){
						String ply = plys[i];
						if(ply.equals(result)){
							break;
						}
						Stack<Move> legalMoves = lmg.getLegalMoves(b);
						Move m = Move.getAlgebraicMove(b, legalMoves, ply);
						g.addMove(m);
						b.makeMove(m);
					}
					if(plys.length > 2 || (i < plys.length && plys[i].equals(result)))
						break;
				}
				games.add(g);
				g = new Game();				
			}
		}
		
		
		
		return games;
	}
	
	static String removeComments(String s){
		String [] res = Pattern.compile("\\{[^\\}]*\\}").split(s);
		s = "";
		for(String str : res){
			s = s.concat(str);
		}
		return s;
	}
	
	public static void main(String[] args) throws IOException {
		PGNParser parser = new PGNParser();
		parser.parse("game1.pgn");
	}
}
