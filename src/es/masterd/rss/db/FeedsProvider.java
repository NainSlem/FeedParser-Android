package es.masterd.rss.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import es.masterd.rss.db.FeedsDB.Posts;

public class FeedsProvider extends ContentProvider {
	/*
	 * Espacio de nombres (se debe usar el mismo para definir el content provider
	 * en el Manifest
	 */
	
	public static final Uri CONTENT_URI = Uri.parse("content://es.masterd.blog");

	private static final int POST = 1;
	private static final int POST_ID = 2;

	private static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("es.masterd.blog", "post", POST);
		uriMatcher.addURI("es.masterd.blog", "post/#", POST_ID);
	}

	private SQLiteDatabase feedsDB;
	
	
	@Override
	public boolean onCreate() {
		Context context = getContext();
		FeedsSQLHelper dbHelper = new FeedsSQLHelper(context);
		feedsDB = dbHelper.getWritableDatabase();
		return (feedsDB == null) ? false : true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		// para conjunto de posts
		case POST:
			return "vnd.android.cursor.dir/vnd.masterd.post";
			// para un solo post
		case POST_ID:
			return "vnd.android.cursor.item/vnd.masterd.post";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereargs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case POST:
			count = feedsDB.delete(Posts.NOMBRE_TABLA, where, whereargs);
			break;
		case POST_ID:
			String id = uri.getPathSegments().get(1);
			count = feedsDB.delete(Posts.NOMBRE_TABLA, Posts._ID + " = " + id
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
					whereargs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	
	/**
	 * insert
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = feedsDB.replace(Posts.NOMBRE_TABLA, "", values);
		
		// si todo ha ido ok devolvemos su Uri
		if (rowID > 0) {
			Uri baseUri = Uri.parse("content://es.masterd.blog/post");
			Uri _uri = ContentUris.withAppendedId(baseUri, rowID);
			
			getContext().getContentResolver().notifyChange(_uri, null);
			getContext().getContentResolver().notifyChange(baseUri, null);
			
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}



	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(Posts.NOMBRE_TABLA);

		if (uriMatcher.match(uri) == POST_ID) {
			sqlBuilder.appendWhere(Posts._ID + " = " + uri.getPathSegments().get(1));
		}

		if (sortOrder == null || sortOrder == "") {
			sortOrder = Posts.DEFAULT_SORT_ORDER;
		}

		Cursor c = sqlBuilder.query(feedsDB, projection, selection,
				selectionArgs, null, null, sortOrder);

		// Registramos los cambios para que se enteren nuestros observers
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case POST:
			count = feedsDB.update(Posts.NOMBRE_TABLA, values, selection,
					selectionArgs);
			break;
		case POST_ID:
			count = feedsDB.update(Posts.NOMBRE_TABLA, values, Posts._ID
					+ " = "
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
					+ ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
