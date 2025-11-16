package main.cgvsu.com.model;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BezierStrategyTest {

    private final BezierStrategy bezier = new BezierStrategy();

    @Test
    void testCalculate_ThrowsOnTooFewPoints() {
        List<Point2D> points = List.of(new Point2D(0,0));
        assertThrows(IllegalArgumentException.class, () -> bezier.calculate(points, 10));
    }

    @Test
    void testCalculate_CorrectNumberOfSegments() {
        List<Point2D> points = List.of(new Point2D(0,0), new Point2D(10,10));
        List<Point2D> curve = bezier.calculate(points, 5);
        assertEquals(6, curve.size());
    }

    @Test
    void testCalculate_CurveEndpoints() {
        List<Point2D> points = List.of(new Point2D(0,0), new Point2D(10,10));
        List<Point2D> curve = bezier.calculate(points, 5);
        assertEquals(points.get(0), curve.get(0));
        assertEquals(points.get(1), curve.get(curve.size()-1));
    }

    @Test
    void testCalculate_CasteljauEqualsBernstein() {
        List<Point2D> points = List.of(new Point2D(0,0), new Point2D(5,10), new Point2D(10,0));
        for (double t = 0; t <= 1; t += 0.1) {
            Point2D pC = bezier.calculatePoint(points, t);
            Point2D pB = BezierStrategy.calculatePointBernstein(points, t);
            assertEquals(pC.getX(), pB.getX(), 1e-6);
            assertEquals(pC.getY(), pB.getY(), 1e-6);
        }
    }

    @Test
    void testBinomialCoefficient_BasicCases() {
        assertEquals(1, BezierStrategy.binomialCoefficient(5,0));
        assertEquals(1, BezierStrategy.binomialCoefficient(5,5));
        assertEquals(5, BezierStrategy.binomialCoefficient(5,1));
        assertEquals(10, BezierStrategy.binomialCoefficient(5,2));
        assertEquals(10, BezierStrategy.binomialCoefficient(5,3));
    }

    @Test
    void testBinomialCoefficient_OutOfBounds() {
        assertEquals(0, BezierStrategy.binomialCoefficient(5,-1));
        assertEquals(0, BezierStrategy.binomialCoefficient(5,6));
    }

    @Test
    void testBinomialCoefficient_LargeValue() {
        assertEquals(184756, BezierStrategy.binomialCoefficient(20,10));
    }
}
