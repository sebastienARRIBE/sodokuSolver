package sudokuSolver;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class NineCell implements Observer {
	private ArrayList<Cell> Cells;

	public NineCell() {
		this.Cells = new ArrayList<Cell>();
	}

	public ArrayList<Cell> getCells() {
		return this.Cells;
	}

	
	public void addCell(Cell cell) {
		this.Cells.add(cell);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Cell cell1 = (Cell) arg0;
		if (cell1.getPossibilities().size() == 1) {
			int possibility = cell1.getPossibilities().iterator().next();
			for (Cell cell2 : this.Cells) {
				if (!cell2.equals(cell1)) {
					cell2.takeOutCellPossibility(possibility);
				}

			}
		}
	}

}
