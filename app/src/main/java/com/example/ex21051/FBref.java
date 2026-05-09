/**
 * @author shaked hazan shaked1246@gmail.com
 * @version 1.0
 * @since 9/05/2026
 * Utility class to hold Firebase Database references.
 */
package com.example.ex21051;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBref {
    /**
     * Instance of the FirebaseDatabase.
     */
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();

    /**
     * Reference to the "Expenses" node in the database.
     */
    public static DatabaseReference refExpenses = FBDB.getReference("Expenses");
}
