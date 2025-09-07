package com.nnamo.utils;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.sqlite.Function;
import java.sql.SQLException;

/**
 * SQLite custom function to perform fuzzy matching using Levenshtein distance ({@link LevenshteinDistance}).
 * This function can be used in SQL queries to compare two strings and return a similarity score.
 *
 * Usage in SQL:
 * <code>SELECT FuzzyMatch(column1, 'searchTerm') AS similarityScore FROM tableName;</code>
 *
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

        if (textToSearch == null || searchTerm == null) {
            result(0); // Return a score of 0 if either string is null
            return;
        }

        // Calculate Levenshtein distance
        int distance = LEVENSHTEIN.apply(textToSearch.toLowerCase(), searchTerm.toLowerCase());

        // Convert distance to a similarity score (0.0 to 1.0)
        int longerLength = Math.max(textToSearch.length(), searchTerm.length());
        if (longerLength == 0) {
            result(1.0); // Both strings are empty
        } else {
            double similarity = (longerLength - distance) / (double) longerLength;
            result(similarity * 100); // Return score as a percentage (e.g., 85.5)
        }
    }
}
