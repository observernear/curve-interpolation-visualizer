package main.cgvsu.com.model;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Point2DManagerTest {

    private Point2DManager manager;

    @BeforeEach
    void setup() {
        manager = new Point2DManager();
    }

    @Test
    void testAddAndGetPoints() {
        Point2D p = new Point2D(1, 2);
        manager.addPoint(p);

        List<Point2D> points = manager.getPoints();
        assertEquals(1, points.size());
        assertEquals(p, points.get(0));
    }

    @Test
    void testInsertPoint() {
        manager.addPoint(new Point2D(0, 0));
        manager.insertPoint(0, new Point2D(1, 1));

        List<Point2D> points = manager.getPoints();
        assertEquals(new Point2D(1, 1), points.get(0));
        assertEquals(new Point2D(0, 0), points.get(1));
    }

    @Test
    void testUpdatePoint() {
        manager.addPoint(new Point2D(0, 0));
        manager.updatePoint(0, new Point2D(2, 2));

        assertEquals(new Point2D(2, 2), manager.getPoints().get(0));
    }

    @Test
    void testRemovePoint() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(1, 1);
        manager.addPoint(p1);
        manager.addPoint(p2);

        manager.removePoint(p1);
        assertFalse(manager.containsPoint(p1));
        assertTrue(manager.containsPoint(p2));

        manager.removePoint(0);
        assertTrue(manager.getPoints().isEmpty());
    }

    @Test
    void testClearPoints() {
        manager.addPoint(new Point2D(0, 0));
        manager.clearPoints();
        assertTrue(manager.getPoints().isEmpty());
    }

    @Test
    void testHasEnoughPointsForCurve() {
        assertFalse(manager.hasEnoughPointsForCurve());
        manager.addPoint(new Point2D(0, 0));
        assertFalse(manager.hasEnoughPointsForCurve());
        manager.addPoint(new Point2D(1, 1));
        assertTrue(manager.hasEnoughPointsForCurve());
    }

    @Test
    void testFindNearestPoint() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(3, 4);
        manager.addPoint(p1);
        manager.addPoint(p2);

        assertEquals(p1, manager.findNearestPoint(new Point2D(1, 1), 2));
        assertNull(manager.findNearestPoint(new Point2D(10, 10), 1));
    }

    @Test
    void testFindNearestPointIndex() {
        Point2D p1 = new Point2D(0, 0);
        Point2D p2 = new Point2D(3, 4);
        manager.addPoint(p1);
        manager.addPoint(p2);

        assertEquals(0, manager.findNearestPointIndex(new Point2D(1, 1), 2));
        assertEquals(-1, manager.findNearestPointIndex(new Point2D(10, 10), 1));
    }

    @Test
    void testContainsPoint() {
        Point2D p = new Point2D(1, 1);
        manager.addPoint(p);
        assertTrue(manager.containsPoint(p));
        assertFalse(manager.containsPoint(new Point2D(2, 2)));
    }
}
