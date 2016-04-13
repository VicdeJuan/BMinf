package es.uam.eps.bmi.search.ranking.graph;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MatrixTest {

    /**
     * Test of addRowLast method, of class Matrix.
     */
    @Test
    public void testAddRowLast() {
        System.out.println("addRowLast");

        double[] initial = {1, 2, 3, 4};
        Matrix instance = new Matrix(initial, 2, 2);
        instance.addRowLast(2);
        double[] expected = {1, 2, 3, 4, 0, 0, 0, 0};
        System.out.print(instance);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], instance.getData()[i], 0.000001);
        }
    }

    /**
     * Test of addColLast method, of class Matrix.
     */
    @Test
    public void testAddColLast() {
        double[] initial = {1, 2, 3, 4};
        Matrix instance = new Matrix(initial, 2, 2);
        instance.addColLast(2);
        double[] expected = {1, 2, 0, 0, 3, 4, 0, 0};
        System.out.print(instance);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], instance.getData()[i], 0.000001);
        }
    }

    @Test
    public void testRocchiNormalize() {
        double[] initial = {
            18.0,   12.2,   14.0,   29.5,   40.3,
            106,    163,    74,     54,     155,
            36,     28,     21,     26,     42,
            11,     14,     23,     19,     19
        };
        
        Matrix instance = new Matrix(initial, 4, 5);
        double[] expected = {
            3.6,    2.44,   3.5,    4.916667, 8.05,
            21.2,   32.6,   18.5,   9.0,    31.0,
            7.2,    5.6,    5.25,   4.33,   8.4,
            2.2,    2.8,    5.75,   3.1667, 3.8
        };
        
        double[] users = {
            1,0,4,0,5,0,2,0,0,5,
            0,2,0,3,0,3,5,0,1,0,
            4,0,0,0,4,0,3,0,0,2,
            0,2,1,5,0,0,1,3,0,3,
            3,0,4,0,3,5,0,0,4,0
        };
        
        System.out.println(instance);
        instance.RocchiNormalize(new Matrix(users, 5, 10));
        System.out.println(instance);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], instance.getData()[i], 0.01);
        }
    }

}
