package utiles;

public class Displayer {
	
	public static void display(String msg, boolean debuging) {
		if (debuging) {
			System.out.println(msg);
		}
	}

}
