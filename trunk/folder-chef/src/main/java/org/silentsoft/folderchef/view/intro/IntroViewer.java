package org.silentsoft.folderchef.view.intro;

import java.io.FileInputStream;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.silentsoft.core.event.EventHandler;
import org.silentsoft.folderchef.core.BizConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntroViewer extends Application {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IntroViewer.class);

	private Stage stage;
	private Parent introViewer;
	
	private final int INTRO_TIME = 3500;


	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("IntroViewer.fxml"));
			introViewer = fxmlLoader.load();
			
			stage.getIcons().add(new Image(new FileInputStream("icon/folder-chef.png")));
			stage.initStyle(StageStyle.UNDECORATED);
        	stage.setScene(new Scene(introViewer, 454, 128));
        	stage.centerOnScreen();
        	stage.show();
        	
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(INTRO_TIME), introViewer);
			fadeTransition.setFromValue(0.0);
			fadeTransition.setToValue(1.0);
			fadeTransition.play();
		
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						Thread.sleep(INTRO_TIME);
						
						EventHandler.callEvent(IntroViewer.class, BizConst.EVENT_VIEW_INTRO_FINISH);
					} catch (Exception e) {
						;
					}
				}
			}).start();
		} catch (Exception e) {
			LOGGER.error("Failed initialize intro viewer !", e);
		}
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		stage.close();
	}
}
