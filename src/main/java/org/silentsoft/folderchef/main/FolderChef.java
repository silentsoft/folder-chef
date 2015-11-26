package org.silentsoft.folderchef.main;

import java.io.FileInputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.silentsoft.folderchef.extract.Extractor;
import org.silentsoft.folderchef.extract.KeywordExtractor;
import org.silentsoft.folderchef.load.CategoryLoader;
import org.silentsoft.folderchef.load.Loader;
import org.silentsoft.folderchef.transform.LevelTransformer;
import org.silentsoft.folderchef.transform.Transformer;
import org.silentsoft.folderchef.view.category.CategoryViewer;
import org.silentsoft.folderchef.view.infinity.InfinityViewer;
import org.silentsoft.folderchef.view.intro.IntroViewer;
import org.silentsoft.folderchef.view.keyword.KeywordViewer;
import org.silentsoft.folderchef.view.level.LevelViewer;
import org.silentsoft.folderchef.view.load.LoadViewer;
import org.silentsoft.folderchef.view.option.OptionViewer;
import org.silentsoft.folderchef.view.option.OptionViewer.ConfigLoadOption;
import org.silentsoft.folderchef.view.search.SearchResultViewer;
import org.silentsoft.io.data.DataMap;
import org.silentsoft.io.event.EventHandler;
import org.silentsoft.io.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderChef extends Application implements EventListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(FolderChef.class);

	private static Stage stage;
	
	private static Extractor   extractor;
	private static Transformer transformer;
	private static Loader      loader;
	
	private static IntroViewer introViewer;
	private static InfinityViewer infinityViewer;
	private static SearchResultViewer searchResultViewer;
	private static OptionViewer optionViewer;
	private static KeywordViewer keywordViewer;
	private static LevelViewer levelViewer;
	private static CategoryViewer categoryViewer;
	private static LoadViewer loadViewer;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;

		stage.setTitle("Folder Chef");
		stage.getIcons().add(new Image(new FileInputStream("icon/folder-chef.png")));
//		stage.setResizable(false);
		
		initialize();
	}
	
	private void initialize() {
		EventHandler.addListener(this);
		
		initializeSharedMemory();
		
		onEventViewIntro();
	}
	
	private void initializeSharedMemory() {
		SharedMemory.setExtractorMap(new DataMap());
		SharedMemory.setTransformerMap(new DataMap());
	}

	@Override
	public void onEvent(String event) {
		try {
			if (event.equals(BizConst.EVENT_PROGRAM_EXIT)) {
				onEventProgramExit();
			} else if (event.equals(BizConst.EVENT_VIEW_INTRO_FINISH)) {
				onEventViewIntroFinish();
			} else if (event.equals(BizConst.EVENT_VIEW_INFINITY)) {
				onEventViewInfinity();
			} else if (event.equals(BizConst.EVENT_VIEW_SEARCH_RESULT)) {
				onEventViewSearchResult();
			} else if (event.equals(BizConst.EVENT_VIEW_SEARCH_RESULT_CLOSE)) {
				onEventViewSearchResultClose();
			} else if (event.equals(BizConst.EVENT_VIEW_OPTION)) {
				onEventViewOption(ConfigLoadOption.FromSharedMemory);
			} else if (event.equals(BizConst.EVENT_VIEW_KEYWORD)) {
				onEventViewKeyword();
			} else if (event.equals(BizConst.EVENT_VIEW_LEVEL)) {
				onEventViewLevel();
			} else if (event.equals(BizConst.EVENT_VIEW_CATEGORY)) {
				onEventViewCategory();
			} else if (event.equals(BizConst.EVENT_VIEW_LOAD)) {
				onEventViewLoad();
			} else if (event.equals(BizConst.EVENT_EXTRACT_EXECUTE)) {
				onEventExtractExecute();
			} else if (event.equals(BizConst.EVENT_TRANSFORM_EXECUTE)) {
				onEventTransformExecute();
			} else if (event.equals(BizConst.EVENT_LOAD_EXECUTE)) {
				onEventLoadExecute();
			} 
		} catch (Exception e) {
			LOGGER.error("I got catch error.", e);
		}
	}
	
	private synchronized void onEventProgramExit() {
		System.exit(0);
	}
	
	private synchronized void onEventViewIntro() {
		if (introViewer != null) {
			introViewer = null;
		}
		
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	try {
		        	if (introViewer != null) {
		        		introViewer.stop();
		        	}
		        	
		        	introViewer = new IntroViewer();
		        	introViewer.start(new Stage());
	        	} catch (Exception e) {
	        		LOGGER.error("Cannot start the intro viewer !", e);
	        	}
	        }
	   });
	}
	
	private synchronized void onEventViewIntroFinish() {
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	try {
	        		if (introViewer != null) {
		        		introViewer.stop();
		        	}
	        		
	        		onEventViewOption(ConfigLoadOption.FromINI);
	        	} catch (Exception e) {
					LOGGER.error("Cannot stop the intro viewer !", e);
				}
	        }
	   });
	}
	
	private synchronized void onEventViewInfinity() {
		if (infinityViewer != null) {
			infinityViewer = null;
		}
		
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	infinityViewer = new InfinityViewer();
	        	
	        	stage.setScene(new Scene(infinityViewer.getInfinityViewer(),230, 70));
	    		stage.centerOnScreen();
	        	stage.show();
	        }
	   });
	}
	
	private synchronized void onEventViewSearchResult() {
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	try {
	        		if (searchResultViewer != null) {
		        		searchResultViewer.stop();
		        	}
		        	
		        	searchResultViewer = new SearchResultViewer();
		        	searchResultViewer.start(new Stage());
	        	} catch (Exception e) {
					LOGGER.error("Cannot start the search result viewer !", e);
				}
	        }
	   });
//		searchResultViewer.getFrame().setVisible(true);
	}
	
	private synchronized void onEventViewSearchResultClose() {
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	try {
	        		if (searchResultViewer != null) {
		        		searchResultViewer.stop();
		        	}
	        	} catch (Exception e) {
					LOGGER.error("Cannot stop the search result viewer !", e);
				}
	        }
	   });
	}
	
	private synchronized void onEventViewOption(ConfigLoadOption configLoadOption) {
		if (optionViewer != null) {
			optionViewer = null;
		}
		
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	SharedMemory.getDataMap().put("con", configLoadOption);
	        	optionViewer = new OptionViewer();

	        	stage.setScene(new Scene(optionViewer.getOptionViewer(), 282,254));
	        	stage.centerOnScreen();
	    		stage.show();
	        }
	   });
		
		
//		optionViewer.getFrame().setVisible(true);
	}
	
	private synchronized void onEventViewKeyword() {
		if (keywordViewer != null) {
			keywordViewer = null;
		}
		
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	keywordViewer = new KeywordViewer();

	        	stage.setScene(new Scene(keywordViewer.getKeywordViewer(), 331,508));
	    		stage.centerOnScreen();
	        	stage.show();
	        }
	   });
		
//		
//		keywordViewer.getFrame().setVisible(true);
	}
	
	private synchronized void onEventViewLevel() {
		if (levelViewer != null) {
			levelViewer = null;
		}
		
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	levelViewer = new LevelViewer();

	        	stage.setScene(new Scene(levelViewer.getLevelViewer(), 800,688));
	        	stage.centerOnScreen();
	    		stage.show();
	        }
	   });
		
//		
//		levelViewer.getFrame().setVisible(true);
	}
	
	private synchronized void onEventViewCategory() {
		if (categoryViewer != null) {
			categoryViewer = null;
		}
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				categoryViewer = new CategoryViewer();
				
				stage.setScene(new Scene(categoryViewer.getCategoryViewer(), 800, 650));
				stage.centerOnScreen();
				stage.show();
			};
		});
		
//		categoryViewer = new CategoryViewer();
//		categoryViewer.getFrame().setVisible(true);
	}
	
	private synchronized void onEventViewLoad() {
		if (loadViewer != null) {
			loadViewer = null;
		}
		
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				loadViewer = new LoadViewer();
				
				stage.setScene(new Scene(loadViewer.getLoadViewer(), 450, 54));
				stage.centerOnScreen();
				stage.show();
			}
		});
		
//		loadViewer = new LoadViewer();
//		loadViewer.getFrame().setVisible(true);
	}
	
	private synchronized void onEventExtractExecute() {
		extractor = new KeywordExtractor();
	}
	
	private synchronized void onEventTransformExecute(){
		transformer = new LevelTransformer();
	}
	
	private synchronized void onEventLoadExecute() {
		loader = new CategoryLoader();
	}
}
