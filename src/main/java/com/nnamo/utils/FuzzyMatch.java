package com.nnamo.utils;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.sqlite.Function;
import java.sql.SQLException;

public class FuzzyMatch extends Function {

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
        LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();
        int distance = levenshtein.apply(textToSearch.toLowerCase(), searchTerm.toLowerCase());

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
