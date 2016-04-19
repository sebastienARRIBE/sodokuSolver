package sudokuSolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

public class Grid implements Observer {
	private ArrayList<Cell> cells;
	ArrayList<NineCell> lines;
	ArrayList<NineCell> rows;
	ArrayList<NineCell> squares;
	boolean isConsistent;

	public Grid(String file) {

		this.cells = new ArrayList<Cell>();
		this.readFile(file);
		this.instantiateLines();
		this.instantiateRows();
		this.instantiateSquares();
		this.initializePossibilities();
		this.isConsistent = true;
	}

	private Grid(Grid grid) {
		this.cells = new ArrayList<Cell>();

		for (Cell cell1 : grid.cells) {
			Cell copyOfCell1 = new Cell(cell1);
			copyOfCell1.addObserver(this);
			this.cells.add(copyOfCell1);

		}

		this.isConsistent = true;
		this.instantiateLines();
		this.instantiateRows();
		this.instantiateSquares();
		this.initializePossibilities();

	}
	
	//TODO make lines rows and squares more generic! (code duplication)

	private void instantiateLines() {
		// TODO make something cleaner and faster
		this.lines = new ArrayList<NineCell>();
		for (int i = 1; i <= 9; i++) {
			NineCell line = new NineCell();
			for (Iterator<Cell> iterator = cells.iterator(); iterator.hasNext();) {
				Cell cell = (Cell) iterator.next();
				if (cell.getLine() == i) {
					line.addCell(cell);
					cell.addObserver(line);
				}
			}

			this.lines.add(line);
		}

	}

	private void instantiateRows() {
		// TODO make something cleaner and faster
		this.rows = new ArrayList<NineCell>();
		for (int i = 1; i <= 9; i++) {
			NineCell row = new NineCell();
			for (Iterator<Cell> iterator = cells.iterator(); iterator.hasNext();) {
				Cell cell = (Cell) iterator.next();
				if (cell.getRow() == i) {
					row.addCell(cell);
					cell.addObserver(row);
				}
			}

			this.rows.add(row);
		}
	}

	private void instantiateSquares() {
		// TODO make something cleaner and faster
		this.squares = new ArrayList<NineCell>();
		NineCell square = new NineCell();
		for (int squareLine = 1; squareLine <= 7; squareLine = squareLine + 3) {
			for (int squareRow = 1; squareRow <= 7; squareRow = squareRow + 3) {
				for (int i = 0; i <= 2; i++) {
					for (int j = 0; j <= 2; j++) {
						Cell cell = this.getCell((i + squareLine), (j + squareRow));
						square.addCell(cell);
						cell.addObserver(square);
					}

				}
				this.squares.add(square);
				square = new NineCell();
			}
		}

	}

	private Cell getCell(int line, int row) throws NullPointerException {
		if (line <= 9 && row <= 9) {
			for (Cell cell : cells) {
				if (cell.getLine() == line && cell.getRow() == row) {
					return cell;
				}
			}
		}
		throw new NullPointerException("cell at line: " + line + " row: " + row + "does not exists");
	}

	private Cell getCell(Cell cell) {
		// /!\ we get the cell at the same coordinates than the parameter
		// but it can be IN A DIFFERENT GRID!!!!
		return this.getCell(cell.getLine(), cell.getRow());
	}

	private void readFile(String filePath) {
		File file = new File("sudoku.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			for (int i = 1; i <= 9; i++) {
				String line = reader.readLine();
				for (int j = 1; j <= line.length(); j++) {
					String character = line.substring(j - 1, j);
					if (character.equals(" ") || character.equals("0")) {
						Cell cell = new Cell(i, j);
						cell.addObserver(this);
						this.cells.add(cell);
					} else
						try {
							this.cells.add(new Cell(i, j, Integer.parseInt(character)));
						} catch (NumberFormatException nfe) {
							System.err.println(
									"the file contains a non appropriate number at line: " + i + " character: " + j);
						}

				}

			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void initializePossibilities() {
		for (Cell cell : this.cells) {
			if (cell.getPossibilities().size() == 1) {
				cell.setChanged();
				cell.notifyObservers(cell.getPossibilities().iterator().next());
			}
		}
	}

	public void solve() {
		this.initializePossibilities();
		if (!this.backTracking()){
			System.out.println("this sudoku has no solution");
		}
	}

	private boolean backTracking() {
		if (!this.isConsistent) {
			return false;
		} else if (this.findEmptyCell() == null) {// null means no empty cell

			System.out.println("SOLVED!!!");
			this.display();
			
			return true;
		} else {
			Cell emptyCell = this.findEmptyCell();
			ArrayList<Grid> node = this.spawnNode(emptyCell);
			for (Grid assumptionGrid : node) {
				if (assumptionGrid.backTracking()) {
					return true;
				}
			}

			return false;
		}

	}

	private synchronized ArrayList<Grid> spawnNode(Cell emptyCell) {
		Set<Integer> possibilities = new TreeSet<Integer>(emptyCell.getPossibilities());
		ArrayList<Grid> grids = new ArrayList<Grid>();
		for (int possibility : possibilities) {
			Grid assumptionGrid = new Grid(this);
			assumptionGrid.getCell(emptyCell).setOnePossibility(possibility);
			grids.add(assumptionGrid);
		}
		return grids;
	}

	private Cell findEmptyCell() {
		// We keep the return null in the case where there is no empty cell because
		// 1) that is the purpose of null
		// 2) no better alternative has been found (null Object DP, returning an empty collection, annotations

		Cell emptyCell = null;
		for (Cell cell : this.cells) {
			if (cell.getPossibilities().size() > 1) {
				emptyCell = cell;
				break;
			}
		}
		return emptyCell;
	}

	private void display() {
		for (NineCell line : this.lines) {
			for (Cell cell : line.getCells()) {
				if (cell.getPossibilities().size() == 1) {
					System.out.print(cell.getPossibilities().iterator().next());
				} else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Cell cell = (Cell) arg0;
		if (cell.getPossibilities().size() == 0) {
			this.isConsistent = false;
		}

	}

}