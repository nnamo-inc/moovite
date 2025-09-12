package com.nnamo.utils;

import junit.framework.TestCase;

import java.sql.SQLException;

public class FuzzyMatchTest extends TestCase {
    public FuzzyMatchTest() throws SQLException {
    }

    public void testFuzzyMatchExact() {
        String input = "Main Street";
        String target = "Main Street";
        assertEquals(1.0, FuzzyMatch.fuzzyMatch(input, target));
    }

    public void testFuzzyMatchCaseInsensitive() {
        String input = "main street";
        String target = "Main Street";
        assertEquals(1.0, FuzzyMatch.fuzzyMatch(input, target));
    }

    public void testFuzzyMatchPartial() {
        String input = "Main St";
        String target = "Main Street";
        assertTrue(FuzzyMatch.fuzzyMatch(input, target) > 0.5);
    }

    public void testFuzzyMatchNoMatch() {
        String input = "Broadway";
        String target = "Main Street";
        assertEquals(0.0, FuzzyMatch.fuzzyMatch(input, target));
    }

    public void testFuzzyMatchEmptyInput() {
        String input = "";
        String target = "Main Street";
        assertEquals(0.0, FuzzyMatch.fuzzyMatch(input, target));
    }

    public void testFuzzyMatchEmptyTarget() {
        String input = "Main Street";
        String target = "";
        assertEquals(0.0, FuzzyMatch.fuzzyMatch(input, target));
    }

    public void testFuzzyMatchBothEmpty() {
        String input = "";
        String target = "";
        assertEquals(1.0, FuzzyMatch.fuzzyMatch(input, target));
    }

    public void testFuzzyMatchNullInput() {
        String input = null;
        String target = "Main Street";
        assertEquals(0.0, FuzzyMatch.fuzzyMatch(input, target));
    }

    public void testScoreWithinRange() {
        String input = "Main St";
        String target = "Main Street";
        double score = FuzzyMatch.fuzzyMatch(input, target);
        assertTrue(score >= 0.0 && score <= 1.0);
    }
}
