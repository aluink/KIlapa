package net.aluink.chess.pgn;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.aluink.chess.suicide.game.Move;

public class Game {
	Map<String, String> pairs = new HashMap<String,String>();
	List<Move> moves = new LinkedList<Move>();
	
	public void addPair(String k, String v){
		pairs.put(k,v);
	}
	
	public void addMove(Move m){
		moves.add(m);
	}

	public List<Move> getMoves() {
		return moves;
	}
	
	public String getPairData(String key){
		return pairs.get(key);
	}
	
	public static void main(String[] args) {
		String url = "http://www.ficsgames.com/cgi-bin/search.cgi?" +
				"roperator=1&" +
				"rating=2100&" +
				"rgroup=2&" +
				"variant=13&" +
				"results=1&" +
				"rtimeoperator=1&" +
				"gtime=0&" +
				"rincoperator=1&" +
				"ginc=99&" +
				"date-sel-after-dd=&START_DAY" +
				"date-sel-after-mm=START_MONTH&" +
				"date-sel-after=START_YEAR&" +
				"date-sel-dd=END_DAY&" +
				"date-sel-mm=END_MONTH&" +
				"date-sel=END_YEAR&" +
				"dlgames=download";
		
		int [] monthMax = {31,28,31,30,31,30,31,31,30,31,30,31};
		String [] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
		
		for(int year = 2000;year < 2011;year++){
			for(int month = 0;month < 12;month++){
				for(int day = 1;day < monthMax[month]-1;day++){
					System.out.print("wget ");
					System.out.print(url.
							replace("START_DAY", String.valueOf(day)).
							replace("START_MONTH", String.valueOf(months[month])).
							replace("START_YEAR", String.valueOf(year)).
							replace("END_DAY", String.valueOf(day+1)).
							replace("END_MONTH", String.valueOf(months[month])).
							replace("END_YEAR", String.valueOf(year)));
					System.out.println("-O FICS_" + months[month] + (day<10?"0"+day:day) + year + "_" + months[month] + ((day+1)<10?"0"+(day+1):(day+1)) + year);
				}
			}
		}
	}
}
