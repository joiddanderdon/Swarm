
public class Tester {
	public static void main(String[] args) {
		ServerBoard myBoard = new ServerBoard();
		myBoard.addZombie();
		String[] boardReturn = myBoard.getBoard().split(",");
		for (String s: boardReturn) {
			System.out.println(s);
		}
		
		
	}
}
