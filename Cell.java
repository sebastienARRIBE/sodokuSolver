package sudokuSolver;

import java.util.LinkedHashSet;
import java.util.Observable;
import java.util.Set;

public class Cell extends Observable {
	private int line, row;
	private Set<Integer> possibilities;

	public Cell(int line, int row) {
		this.line=line;
		this.row=row;
		this.possibilities = new LinkedHashSet<Integer>();
		for (int i = 1; i <= 9; i++) {
			this.possibilities.add(i);
		}

	}

	public Cell(int line, int row, int possibility) {
		this.line=line;
		this.row=row;
		this.possibilities = new LinkedHashSet<Integer>();
		this.possibilities.add(possibility);
	}

	public Cell(Cell cell) {
		this.possibilities = new LinkedHashSet<Integer>();
		this.line = cell.getLine();
		this.row = cell.getRow();

		for (int possibility : cell.getPossibilities()) {
			this.possibilities.add(possibility);
		}
	}

	public synchronized void takeOutCellPossibility(int possibility) {

		if (this.possibilities.remove(possibility)) {
			if (this.possibilities.size() == 0) {
				//TODO should there be a notifyObservers() ?
				
			} else if (this.possibilities.size() == 1) {
				this.setChanged();
				this.notifyObservers();
			}
		}
		
	}

		public synchronized void setOnePossibility(int possibility) {
		if (this.possibilities.size() <= 1) {
			System.err.println("ERROR @ .setOnePossibility()");
		}

		this.possibilities.clear();
		this.possibilities.add(possibility);
		this.setChanged();
		this.notifyObservers();

	}

	public int getLine() {
		return line;
	}

	public int getRow() {
		return row;
	}
	

	public Set<Integer> getPossibilities() {
		return possibilities;
	}

	
	public void notifyObservers() {
		super.notifyObservers();
	}

	public void setChanged() {
		super.setChanged();
	}
}
