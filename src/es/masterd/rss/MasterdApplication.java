package es.masterd.rss;

import android.app.Application;

/**
 * Clase que contiene la informaci�n general de la aplicaci�n as� como algunos
 * datos de configuraci�n
 */

public class MasterdApplication extends Application {

	public String getRssUrl() {
		return "http://blog.exitae.es/rss";
	}
}
