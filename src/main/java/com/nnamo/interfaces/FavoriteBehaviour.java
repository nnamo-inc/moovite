package com.nnamo.interfaces;

import com.nnamo.enums.DataType;

public interface FavoriteBehaviour {
    void addFavorite(String string, DataType mode);
    void removeFavorite(String string, DataType mode);
}
