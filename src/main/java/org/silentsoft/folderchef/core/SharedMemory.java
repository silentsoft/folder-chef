package org.silentsoft.folderchef.core;

import org.silentsoft.core.data.DataMap;
import org.silentsoft.core.data.DataSet;

public final class SharedMemory {
	private static DataMap dataMap = new DataMap();

	private static DataMap extractorMap;
	private static DataMap transformerMap;
	private static DataSet extractorSet;
	private static DataSet transformerSet;
	private static DataSet loaderSet;
	
	public static synchronized DataMap getDataMap() {
		return dataMap;
	}
	
	public static synchronized void setDataMap(DataMap target) {
		dataMap = target;
	}
	
	/**
	 * WARNING : if change SharedMemory.dataSet, another classes will showing changed dataset.
	 * @return
	 */
	
//	public static synchronized void init(String name, String[] columnName) {
//		siteInfo = new DataSet(columnName);
//	}

	public static synchronized DataMap getExtractorMap() {
		return extractorMap;
	}
	
	public static synchronized void setExtractorMap(DataMap target){
		extractorMap = target;
	}
	
	public static synchronized DataMap getTransformerMap() {
		return transformerMap;
	}
	
	public static synchronized void setTransformerMap(DataMap target) {
		transformerMap = target;
	}
	
	public static synchronized DataSet getExtractorSet() {
		return extractorSet;
	}
	
	public static synchronized void setExtractorSet(DataSet target){
		extractorSet = target;
	}
	
	public static synchronized DataSet getTransformerSet() {
		return transformerSet;
	}
	
	public static synchronized void setTransformerSet(DataSet target) {
		transformerSet = target;
	}
	
	public static synchronized DataSet getLoaderSet() {
		return loaderSet;
	}
	
	public static synchronized void setLoaderSet(DataSet target) {
		loaderSet = target;
	}
}
