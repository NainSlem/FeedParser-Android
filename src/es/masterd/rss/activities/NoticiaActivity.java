package es.masterd.rss.activities;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;
import es.masterd.rss.R;
import es.masterd.rss.db.FeedsDB.Posts;

public class NoticiaActivity extends Activity {
	private TextView titulo;
	private TextView fecha;
	private WebView contenido;

	/**
	 * OnCreate
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noticia);

		setTitle(R.string.titulo_noticias);

		titulo = (TextView) findViewById(R.id.feedTitulo);
		// Al pulsar sobre el t’tulo se cerrar‡ la ventana
		titulo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		fecha = (TextView) findViewById(R.id.feedFecha);
		contenido = (WebView) findViewById(R.id.feedContenido);
	}

	@Override
	protected void onStart() {
		super.onStart();

		try {
			Bundle extras = getIntent().getExtras();
			long idNoticia = extras.getLong("idNoticia");

			final String[] columnas = new String[] { Posts._ID, // 0
					Posts.TITLE, // 1
					Posts.PUB_DATE, // 2
					Posts.DESCRIPTION // 3
			};

			Uri uri = Uri.parse("content://es.masterd.blog/post");
			uri = ContentUris.withAppendedId(uri, idNoticia);
			
			Log.d("uri",""+uri);

			// Query "managed": la actividad se encargar de cerrar y volver a
			// cargar el cursor cuando sea necesario
			Cursor cursor = managedQuery(uri, columnas, null, null,
					Posts.PUB_DATE + " DESC");

			// Queremos enterarnos si cambian los datos para recargar el cursor
			cursor.setNotificationUri(getContentResolver(), uri);

			// Para que la actividad se encarge de manejar el cursor
			// segœn sus ciclos de vida
			startManagingCursor(cursor);

			// mostramos los datos del cursor en la vista

			if(cursor.moveToFirst()) {
				titulo.setText(cursor.getString(1));
				
				java.text.DateFormat dateFormat = DateFormat.getLongDateFormat(NoticiaActivity.this);
				
				fecha.setText(dateFormat.format(cursor.getLong(2)));
				
				String texto = new String(cursor.getString(3).getBytes(),"utf-8");
				
				// contenido.loadData(texto, "text/html", "utf-8"); // da problemas con utf8
				contenido.loadDataWithBaseURL(null, texto,"text/html", "UTF-8", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}