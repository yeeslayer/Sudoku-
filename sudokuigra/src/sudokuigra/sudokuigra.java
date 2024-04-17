package sudokuigra;

import java.util.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;


public class sudokuigra extends JFrame {

    private JTextField[][] celice;
    private static final int prazno = 0;
    private static final int velikost = 9;

    
    public sudokuigra() {
        setTitle("Sudoku igra");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sudokuPanel = new JPanel(new GridLayout(velikost, velikost));
        celice = new JTextField[velikost][velikost];

        //
        
        for (int i = 0; i < velikost; i++) {
            for (int j = 0; j < velikost; j++) {
                JTextField textField = new JTextField();
                textField.setHorizontalAlignment(JTextField.CENTER);
                celice[i][j] = textField;
                sudokuPanel.add(textField);
            }
        }

        //
        
        JButton solveButton = new JButton("Reši");
        solveButton.addActionListener(e -> resisudoku());

        JButton checkButton = new JButton("Preveri");
        checkButton.addActionListener(e -> preveri());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(solveButton);
        buttonPanel.add(checkButton);

        add(sudokuPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        //
        
        nakljucStevilke();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void nakljucStevilke() {
        Sudokudelavec sudokuSolver = new Sudokudelavec();
        sudokuSolver.resi();
        int[][] solvedGrid = sudokuSolver.pridobisudoku();

        // 
        
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= velikost; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        Random random = new Random();
        for (int i = 0; i < velikost; i++) {
            for (int j = 0; j < velikost; j++) {
                if (random.nextDouble() <= 0.4) { 
                    celice[i][j].setText(""); 
                } else {
                    celice[i][j].setText(String.valueOf(numbers.get(solvedGrid[i][j] - 1)));
                    celice[i][j].setEditable(false);
                }
            }
        }
    }
    

//


    private void resisudoku() {
        int[][] currentGrid = pridobisudoku();
        Sudokudelavec sudokuSolver = new Sudokudelavec(currentGrid);
        if (sudokuSolver.resi()) {
            int[][] solvedGrid = sudokuSolver.pridobisudoku();
            posodobisudoku(solvedGrid);
        } 
    }
    
    //
    
    private void preveri() {
        int[][] currentGrid = pridobisudoku();
        Sudokudelavec sudokuSolver = new Sudokudelavec(currentGrid);
        if (sudokuSolver.okpoteza()) {
            JOptionPane.showMessageDialog(this, "Bravo! Sudoku je resen pravilno");
        } else {
            JOptionPane.showMessageDialog(this, "Opala, nekaj je narobe");
        }
    }

    //
    
    private int[][] pridobisudoku() {
        int[][] currentGrid = new int[velikost][velikost];
        for (int i = 0; i < velikost; i++) {
            for (int j = 0; j < velikost; j++) {
                String value = celice[i][j].getText();
                if (value.isEmpty()) {
                    currentGrid[i][j] = prazno;
                } else {
                    currentGrid[i][j] = Integer.parseInt(value);
                }
            }
        }
        return currentGrid;
    }

    private void posodobisudoku(int[][] grid) {
        for (int i = 0; i < velikost; i++) {
            for (int j = 0; j < velikost; j++) {
                celice[i][j].setText(String.valueOf(grid[i][j]));
            }
        }
    }

    private void premecivrste(int[][] grid) {
        Random random = new Random();
        for (int i = 0; i < velikost; i += 3) {
            int rand = random.nextInt(3);
            if (rand != 0) {
                int[] temp = grid[i];
                grid[i] = grid[i + rand];
                grid[i + rand] = temp;
            }
        }
    }

    private void premecistolpce(int[][] grid) {
        Random random = new Random();
        for (int i = 0; i < velikost; i += 3) {
            int rand = random.nextInt(3);
            if (rand != 0) {
                for (int j = 0; j < velikost; j++) {
                    int temp = grid[j][i];
                    grid[j][i] = grid[j][i + rand];
                    grid[j][i + rand] = temp;
                }
            }
        }
    }

    //
    
    public static void main(String[] args) {
        new sudokuigra();
    }
}

	//

class Sudokudelavec {

    private int[][] mreza;

    public Sudokudelavec() {
        this.mreza = new int[9][9];
    }

    public Sudokudelavec(int[][] initialGrid) {
        this.mreza = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(initialGrid[i], 0, this.mreza[i], 0, 9);
        }
    }

    public boolean resi() {
        return resi(0, 0);
    }

    private boolean resi(int row, int col) {
        if (row == 9) {
            row = 0;
            if (++col == 9) {
                return true;
            }
        }

        if (mreza[row][col] != 0) {
            return resi(row + 1, col);
        }

        for (int num = 1; num <= 9; num++) {
            if (okpoteza(row, col, num)) {
                mreza[row][col] = num;
                if (resi(row + 1, col)) {
                    return true;
                }
            }
        }

        mreza[row][col] = 0;
        return false;
    }

    private boolean okpoteza(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (mreza[row][i] == num || mreza[i][col] == num || mreza[row - row % 3 + i / 3][col - col % 3 + i % 3] == num) {
                return false;
            }
        }
        return true;
    }

    public boolean okpoteza() {
        return okpoteza(0, 0, 0);
    }

    public int[][] pridobisudoku() {
        return mreza;
    }
}
