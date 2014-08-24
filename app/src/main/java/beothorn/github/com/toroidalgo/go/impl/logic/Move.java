package beothorn.github.com.toroidalgo.go.impl.logic;

import java.util.Random;

public class Move {
	
	private static int idGen = new Random().nextInt();
	
	public final boolean isResign;
	public final boolean isPass;
	public final boolean isMark;
	public final int xCoordinate;
	public final int yCoordinate;
	public final int id;
	public final int gameId;
	
	public Move(boolean resign_, boolean pass_, int x_, int y_, boolean mark_, int gameId) {
		isResign = resign_;
		isPass = pass_;
		isMark=mark_;
		xCoordinate = x_;
		yCoordinate = y_;
		this.gameId = gameId;
		id = idGen++;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)                     return false;
		if(!(obj instanceof Move))         return false;
		
		Move other = (Move) obj;
		
		if(other.isResign != isResign)       return false;
		if(other.isPass != isPass)           return false;
		if(other.isMark != isMark)           return false;
		if(other.xCoordinate != xCoordinate) return false;
		if(other.yCoordinate != yCoordinate) return false;
		if(other.id != id)                   return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		String toString = "";
		toString += "isResign:"+isResign;
		toString += " isPass:"+isPass;
		toString += " isMark:"+isMark;
		toString += " xCoordinate:"+xCoordinate;
		toString += " yCoordinate:"+yCoordinate;
		toString += " id:"+id;
		return toString;
	}
}
