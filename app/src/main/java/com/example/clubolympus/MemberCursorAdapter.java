package com.example.clubolympus;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.clubolympus.data.ClubOlympusContract.*;

public class MemberCursorAdapter extends CursorAdapter {

    TextView firstNameTextView;
    TextView lastNameTextView;
    TextView sportTextView;

    public MemberCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        firstNameTextView = view.findViewById(R.id.itemFirstName);
        lastNameTextView = view.findViewById(R.id.itemLastName);
        sportTextView = view.findViewById(R.id.itemSport);

        // Extract properties from cursor
        String firstNameText = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.KEY_FIRST_NAME));
        String lastNameText = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.KEY_LAST_NAME));
        String sportText = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.KEY_SPORT));

        // Populate fields with extracted properties
        firstNameTextView.setText(firstNameText);
        lastNameTextView.setText(lastNameText);
        sportTextView.setText(sportText);
    }
}
