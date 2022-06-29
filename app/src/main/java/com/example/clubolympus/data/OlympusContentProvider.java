package com.example.clubolympus.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.clubolympus.data.ClubOlympusContract.*;

public class OlympusContentProvider extends ContentProvider {

    private static final int MEMBERS = 111;
    private static final int MEMBER_ID = 222;

    OlympusDbHelper dbHelper;

    private static  final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(ClubOlympusContract.AUTHORITY, ClubOlympusContract.PATH_MEMBERS, MEMBERS);
        uriMatcher.addURI(ClubOlympusContract.AUTHORITY, ClubOlympusContract.PATH_MEMBERS + "/#", MEMBER_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new OlympusDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = uriMatcher.match(uri);

        switch(match) {
            case MEMBERS:
                cursor = db.query(MemberEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MEMBER_ID:
                selection = MemberEntry.KEY_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(MemberEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                //Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't query incorrect URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String firstName = values.getAsString(MemberEntry.KEY_FIRST_NAME);
        String lastName = values.getAsString(MemberEntry.KEY_LAST_NAME);
        Integer gender = values.getAsInteger(MemberEntry.KEY_GENDER);
        String sport = values.getAsString(MemberEntry.KEY_SPORT);

        if ((firstName == null) || (lastName == null) || (sport == null)) {
            throw new IllegalArgumentException("NULL can't be inserted to a table");
        }

        if (gender == null || !(gender == MemberEntry.GENDER_FEMALE || gender == MemberEntry.GENDER_MALE || gender == MemberEntry.GENDER_UNKNOWN)) {
            throw new IllegalArgumentException("Can't insert - problem with gender");
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        switch(match) {
            case MEMBERS:
                long id = db.insert(MemberEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    //Toast.makeText(getContext(), "New raw was not inserted", Toast.LENGTH_LONG).show();
                    Log.e("insertMethod", "Insertion is failed for " + uri);
                    return null;
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return ContentUris.withAppendedId(uri, id);
            default:
                //Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Insertion is failed for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch(match) {
            case MEMBERS:
                rowsDeleted =  db.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEMBER_ID:
                selection = MemberEntry.KEY_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted =  db.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                //Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't delete incorrect URI " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(MemberEntry.KEY_FIRST_NAME)) {
            String firstName = values.getAsString(MemberEntry.KEY_FIRST_NAME);
            if (firstName == null)  {
                throw new IllegalArgumentException("NULL can't be inserted to a table");
            }
        }

        if (values.containsKey(MemberEntry.KEY_LAST_NAME)) {
            String lastName = values.getAsString(MemberEntry.KEY_LAST_NAME);
            if (lastName == null)  {
                throw new IllegalArgumentException("NULL can't be inserted to a table");
            }
        }

        if (values.containsKey(MemberEntry.KEY_SPORT)) {
            String sport = values.getAsString(MemberEntry.KEY_SPORT);
            if (sport == null)  {
                throw new IllegalArgumentException("NULL can't be inserted to a table");
            }
        }

        if (values.containsKey(MemberEntry.KEY_GENDER)) {
            Integer gender = values.getAsInteger(MemberEntry.KEY_GENDER);
            if (gender == null || !(gender == MemberEntry.GENDER_FEMALE || gender == MemberEntry.GENDER_MALE || gender == MemberEntry.GENDER_UNKNOWN)) {
                throw new IllegalArgumentException("Can't insert - problem with gender");
            }
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch(match) {
            case MEMBERS:
                rowsUpdated = db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MEMBER_ID:
                selection = MemberEntry.KEY_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated =  db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                //Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't update incorrect URI " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return  rowsUpdated;
    }


    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);

        switch(match) {
            case MEMBERS:
                return  MemberEntry.CONTENT_MULTIPLE_ITEMS;
            case MEMBER_ID:
                return  MemberEntry.CONTENT_SINGLE_ITEM;
            default:
                //Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

    }
}
