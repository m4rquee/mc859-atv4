package problems.kqbf;

import problems.qbf.QBF_Inverse;
import solutions.KSolution;
import solutions.Solution;

import java.io.*;

public class KQBF extends QBF_Inverse {

    /**
     * The vector W of weights for the KQBF
     */
    public Double[] W;

    /**
     * The maximum capacity for the KQBF
     */
    public Double W_max;

    /**
     * Constructor for the KQBF class.
     *
     * @param filename Name of the file for which the objective function parameters
     *                 should be read.
     * @throws IOException Necessary for I/O operations.
     */
    public KQBF(String filename) throws IOException {
        super(filename);
    }

    @Override
    protected Integer readInput(String filename) throws IOException {
        Reader fileInst = new BufferedReader(new FileReader(filename));
        StreamTokenizer stok = new StreamTokenizer(fileInst);

        stok.nextToken();
        int _size = (int) stok.nval;
        stok.nextToken();
        W_max = stok.nval;
        W = new Double[_size];

        for (int i = 0; i < _size; i++) {
            stok.nextToken();
            W[i] = stok.nval;
        }

        read_coefs(stok, _size);
        return _size;
    }

    @Override
    public Double evaluate(Solution<Integer> sol) {
        setVariables(sol);
        KSolution<Integer> aux = (KSolution<Integer>) sol;
        aux.weigth = 0.0;
        for (int i = 0; i < size; i++)
            aux.weigth += variables[i] * W[i];
        return sol.cost = evaluateQBF();
    }
}
