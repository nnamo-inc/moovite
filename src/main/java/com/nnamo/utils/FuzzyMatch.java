package com.nnamo.utils;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.sqlite.Function;

import java.sql.SQLException;

/**
 * SQLite custom function to perform fuzzy matching using Levenshtein distance ({@link LevenshteinDistance}).
 * This function can be used in SQL queries to compare two strings and return a similarity score.
 * <p>
 * Usage in SQL:
 * <code>SELECT FuzzyMatch(column1, 'searchTerm') AS similarityScore FROM tableName;</code>
 * <p>
 * The similarity score is returned as a percentage (0 to 100).
 *
 * @author Davide Galilei
 */
public class FuzzyMatch extends Function {
    private static final LevenshteinDistance LEVENSHTEIN = LevenshteinDistance.getDefaultInstance();

    @Override
    protected void xFunc() throws SQLException {
        // Ensure the function is called with exactly two arguments
        if (args() != 2) {
            throw new SQLException("FuzzyMatch requires 2 arguments");
        }

        String textToSearch = value_text(0);
        String searchTerm = value_text(1);

        double score = fuzzyMatch(textToSearch, searchTerm);

        // Return the score as a percentage (0 to 100)
        result((int) (score * 100));
    }

    public static double fuzzyMatch(String textToSearch, String searchTerm) {
        if (textToSearch == null || searchTerm == null) {
            return 0.0;
        }
        int distance = LEVENSHTEIN.apply(textToSearch.toLowerCase(), searchTerm.toLowerCase());
        int longerLength = Math.max(textToSearch.length(), searchTerm.length());
        if (longerLength == 0) {
            return 1.0; // Both strings are empty
        } else {
            // Return score as a fraction (e.g., 0.855)
            return (longerLength - distance) / (double) longerLength;
        }
    }
}
