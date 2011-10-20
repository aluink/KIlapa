package net.aluink.chess.pgn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
						try {
							b.makeMove(m);
						} catch (Exception e) {
							b.printBoard();
						}
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
		File gameDB = new File("gameDb");
		List<Game> games = new LinkedList<Game>();
		for(String file : gameDB.list()){
			games.addAll(parser.parse("gameDb\\" + file));
		}
		Map<String, Integer[]> fenResults = new HashMap<String, Integer[]>();
		System.out.println(games.size() + " games read.");
		for(Game g : games){
			Board b = new Board();
			b.setToStarting();
			String result = g.getPairData("Result");
			int dif = 0;
			
			if(result.equals("1-0")){
				dif++;
			} else if(result.equals("0-1")){
				dif--;
			}
			for(Move m : g.getMoves()){
				b.makeMove(m);
				String fen = b.getFen();
				Integer [] x = {0,0};
				if(fenResults.containsKey(fen)){
					x = fenResults.get(fen);					
				}
				x[0]++;
				x[1] += dif;
				fenResults.put(fen, x);
			}
		}
		for(String key : fenResults.keySet()){
			Integer [] x = fenResults.get(key);
			if(x[0] > 125){
				System.out.println(key + ": " + x[0] + " " + x[1]);
			}
		}
	}
}
