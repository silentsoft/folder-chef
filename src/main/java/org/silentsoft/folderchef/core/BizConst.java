package org.silentsoft.folderchef.core;

import java.io.File;

public class BizConst {

	public static final String PATH_CONF_DIRECTORY = File.separator + "conf";
	public static final String PATH_CONFIG = PATH_CONF_DIRECTORY + File.separator + "config.ini";
	public static final String PATH_KEYWORD_SET = PATH_CONF_DIRECTORY + File.separator + "KeywordSet.dat";
	
	public static final int    IDX_KEYSET_KEYWORD = 0;
	public static final int    IDX_KEYSET_COUNT   = 1;
	
	public static final String INI_SECTION_EXTRACT = "extract";
	public static final String INI_EXTRACT_KEYSET = "keyset";
	public static final String INI_EXTRACT_TARGET = "target";
	public static final String INI_EXTRACT_EXTENSIONS = "extensions";
	public static final String INI_SECTION_LOAD = "load";
	public static final String INI_LOAD_DESTINATION = "destination";
	public static final String INI_LOAD_TYPE = "type";
	
	public static final String TYPE_LOAD_COPY = "Copy";
	public static final String TYPE_LOAD_MOVE = "Move";
	
	public static final String TYPE_EXTRACTOR_KEYSET = "TYPE_EXTRACTOR_KEYSET";
	public static final String TYPE_EXTRACTOR_DIRECTORY = "TYPE_EXTRACTOR_DIRECTORY";
	
	//--------------------------------------------------------------------------------
	//SharedMemory.DataMap> Common----------------------------------------------------
	//--------------------------------------------------------------------------------
	public static final String KEY_MESSAGE = "KEY_MESSAGE";
	public static final String KEY_RELATION = "KEY_RELATION";
	public static final String KEY_RELATION_SET = "KEY_RELATION_SET";
	public static final String KEY_SEARCH_RESULT = "KEY_SEARCH_RESULT";
	public static final String KEY_EXTRACT_TYPE = "KEY_EXTRACTOR_TYPE";
	public static final String KEY_EXTRACT_KEYSET = "KEY_EXTRACT_KEYSET";
	public static final String KEY_EXTRACT_TARGET = "KEY_EXTRACT_TARGET";
	public static final String KEY_EXTRACT_EXTENSIONS = "KEY_EXTRACT_EXTENSIONS";
	public static final String KEY_EXTRACT_FILE_COUNT = "KEY_EXTRACT_FILE_COUNT";
	public static final String KEY_TRANSFORM_UPPER_COUNT = "KEY_TRANSFORM_UPPER_COUNT";
	public static final String KEY_TRANSFORM_ROOT = "KEY_TRANSFORM_ROOT";
	public static final String KEY_TRANSFORM_ROOT_LIST = "KEY_TRANSFORM_ROOT_LIST";
	public static final String KEY_TRANSFORM_RESULT = "KEY_TRANSFORM_RESULT";
	public static final String KEY_LOAD_DESTINATION = "KEY_LOAD_DESTINATION";
	public static final String KEY_LOAD_TYPE = "KEY_LOAD_TYPE";
	public static final String KEY_LOAD_EXCLUDE_NONE_ITEM = "KEY_LOAD_EXCLUDE_NONE_ITEM";
	public static final String KEY_LOAD_FILE_COUNT_PRE = "KEY_LOAD_FILE_COUNT_PRE";
	public static final String KEY_LOAD_FILE_COUNT_EXCLUDE = "KEY_LOAD_FILE_COUNT_EXCLUDE";
	public static final String KEY_LOAD_FILE_COUNT_REAL= "KEY_LOAD_FILE_COUNT_REAL";
	public static final String KEY_LOAD_FILE_COUNT_FINISH = "KEY_LOAD_FILE_COUNT_FINISH";
	public static final String KEY_LOAD_ROOT = "KEY_LOAD_ROOT";
	//--------------------------------------------------------------------------------
	//SharedMemory.DataMap> Extractor-------------------------------------------------
	//--------------------------------------------------------------------------------
	public static final String KEY_EXTRACTOR_KEYWORD = "KEY_EXTRACTOR_KEYWORD";
	public static final String KEY_EXTRACTOR_KEYCOUNT = "KEY_EXTRACTOR_KEYCOUNT";
	//--------------------------------------------------------------------------------
	//SharedMemory.DataMap> Transformer-----------------------------------------------
	//--------------------------------------------------------------------------------
	//public static final String KEY_TRANSFORMER_ = "";
	//--------------------------------------------------------------------------------
	//SharedMemory.DataMap> Loader----------------------------------------------------
	//--------------------------------------------------------------------------------	
	//public static final String KEY_LOADER_ = "";
	//--------------------------------------------------------------------------------
	
	public static final String EVENT_PROGRAM_EXIT = "EVENT_PROGRAM_EXIT";
	public static final String EVENT_VIEW_INTRO_FINISH = "EVENT_VIEW_INTRO_FINISH";
	public static final String EVENT_VIEW_INFINITY = "EVENT_VIEW_INFINITY";
	public static final String EVENT_VIEW_SEARCH_RESULT = "EVENT_VIEW_SEARCH_RESULT";
	public static final String EVENT_VIEW_SEARCH_RESULT_CLOSE = "EVENT_VIEW_SEARCH_RESULT_CLOSE";
	public static final String EVENT_VIEW_OPTION = "EVENT_VIEW_OPTION";
	public static final String EVENT_VIEW_KEYWORD = "EVENT_VIEW_KEYWORD";
	public static final String EVENT_VIEW_LEVEL	= "EVENT_VIEW_LEVEL";
	public static final String EVENT_VIEW_CATEGORY = "EVENT_VIEW_CATEGORY";
	public static final String EVENT_VIEW_LOAD = "EVENT_VIEW_LOAD";
	public static final String EVENT_EXTRACT_EXECUTE = "EVENT_EXTRACT_EXECUTE";
	public static final String EVENT_TRANSFORM_EXECUTE = "EVENT_TRANSFORM_EXECUTE";
	public static final String EVENT_TRANSFORM_FINISH = "EVENT_TRANSFORM_FINISH";
	public static final String EVENT_LOAD_EXECUTE = "EVENT_LOAD_EXECUTE";
	public static final String EVENT_UPDATE_LOAD_PROGRESS = "EVENT_UPDATE_LOAD_PROGRESS";
	//--------------------------------------------------------------------------------
}
