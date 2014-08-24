package beothorn.github.com.toroidalgo.go.impl.logging;


public class GoLogger {

	private static final boolean DEBUG = true;
	
	public static void log(Object log){
		if(DEBUG){
			System.out.println(log);
		}
	}
	
}
