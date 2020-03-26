package com.asdtechlabs.whatshack.entities;

import android.content.ContentValues;

public interface Persistable {

    public void writeToProvider(ContentValues out);

}
