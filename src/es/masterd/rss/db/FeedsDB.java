package es.masterd.rss.db;

import android.provider.BaseColumns;

public class FeedsDB {
	
	/*
	 * Nombre de la base de datos
	 */
	public static final String DB_NAME = "feeds.db";
	
	/*
	 * version de la base de datos
	 */
	public static final int DB_VERSION = 1;
	
	
	/**
	 * Esta clase no debe ser instanciada
	 */
	private FeedsDB () {}

	
	/*
	 * Definici—n de la tabla jugadores
	 */
	public static final class Posts implements BaseColumns {
		/**
		 * Esta clase no debe ser instanciada
		 */
		private Posts() {}
			
	    /**
	     * orden por defecto
	     */
	    public static final String DEFAULT_SORT_ORDER = "_ID DESC";
    	
        /**
         * Abstracci—n de los nombres de campos y tabla a constantes
         * para facilitar cambios en la estructura interna de la BD
         */
	    public static final String NOMBRE_TABLA = "feeds";
		
		public static final String _ID = "_id";
		public static final String TITLE = "title";
		public static final String LINK = "link";
		public static final String COMMENTS = "comments";
		public static final String PUB_DATE = "pub_date";
		public static final String CREATOR = "creator";
		public static final String DESCRIPTION = "description";

		public static final String _COUNT = "7";
		
		
	}
}
