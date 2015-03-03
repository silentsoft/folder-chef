package org.silentsoft.folderchef.view.load;

import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.event.EventListener;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class LoadViewerController implements EventListener {
	
	@FXML
	private ProgressBar loadingBar;
	
	@FXML
	private Label lblCount;
	
	@FXML
	private Button btnDone;
	
	public void initialize() {
		EventHandler.addListener(this);
	}
	
	public void initializeSharedMemory() {
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_FILE_COUNT_REAL, 0);
		SharedMemory.getDataMap().put(BizConst.KEY_LOAD_FILE_COUNT_FINISH, false);
	}
	
	public void loadExecute() {
		EventHandler.callEvent(LoadViewer.class, BizConst.EVENT_LOAD_EXECUTE);
	}
	
	@Override
	public void onEvent(String event) {
		if (event.equals(BizConst.EVENT_UPDATE_LOAD_PROGRESS)) {
			onEventUpdateLoadProgress();
		}
	}
	
	private synchronized void onEventUpdateLoadProgress() {
		boolean loadFinish = ObjectUtil.toBoolean(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_FILE_COUNT_FINISH));
		System.out.println(loadFinish);
		
		int totalFileCount = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_FILE_COUNT_PRE));
		int excludeFileCount = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_FILE_COUNT_EXCLUDE));
		
		int preLoad = totalFileCount - excludeFileCount;
		int realLoad = ObjectUtil.toInt(SharedMemory.getDataMap().get(BizConst.KEY_LOAD_FILE_COUNT_REAL));
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (loadFinish) {
					loadingBar.setProgress(1);
					btnDone.setDisable(false);
				} else {
					if (preLoad > 0 && realLoad > 0) {
						loadingBar.setProgress((double)realLoad / (double)preLoad);
					}
				}
				
				lblCount.setText("(" + realLoad + "/" + preLoad + ")");
			}
		});
	}
	
	@FXML
	private void btnDone_OnMouseReleased() {
		EventHandler.callEvent(LoadViewer.class, BizConst.EVENT_PROGRAM_EXIT);
	}
}
