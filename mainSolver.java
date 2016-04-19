package sudokuSolver;

public class mainSolver {

	public static void main(String[] args) {
		Grid grid = new Grid("sudoku.txt");
		grid.solve();
	}

}