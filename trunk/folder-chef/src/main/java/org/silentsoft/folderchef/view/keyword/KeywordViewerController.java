package org.silentsoft.folderchef.view.keyword;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.apache.commons.lang.StringUtils;
import org.silentsoft.core.component.messagebox.MessageBox;
import org.silentsoft.core.event.EventHandler;
import org.silentsoft.core.util.ObjectUtil;
import org.silentsoft.folderchef.component.model.Keyword;
import org.silentsoft.folderchef.core.BizConst;
import org.silentsoft.folderchef.core.SharedMemory;
import org.silentsoft.folderchef.main.FolderChef;

public class KeywordViewerController {

	@FXML
	private TableView table;
	
	@FXML
	private TableColumn colKeyword;
	
	@FXML
	private TableColumn colCount;
	
	@FXML
	private TextField txtUpperCount;
	
	@FXML
	private Button btnPrev;
	
	@FXML
	private Button btnNext;
	
	public void settingDataPropertiesToColumns() {
		colKeyword.setCellValueFactory(new PropertyValueFactory<Keyword, Object>("keyword"));
		colCount.setCellValueFactory(new PropertyValueFactory<Keyword, Object>("count"));
	}
	
	public void displayKeywordToGrid() {
		ObservableList<Keyword> data = FXCollections.observableArrayList();
		
		Set<Entry<String, Object>> entrySet = SharedMemory.getExtractorMap().entrySet();
		for (Map.Entry<String, Object> entry : entrySet) {
			data.add(new Keyword(entry.getKey(), entry.getValue()));
		}
		
		table.setItems(data);
	}
	
	@FXML
	private void txtUpperCount_OnKeyAction() {
		btnNext_OnMouseReleased();
	}
	
	@FXML
	private void btnPrev_OnMouseReleased() {
		SharedMemory.getExtractorMap().clear();
		EventHandler.callEvent(KeywordViewer.class, BizConst.EVENT_VIEW_OPTION, false);
//		keywordViewer.dispose();
	}
	
	@FXML
	private void btnNext_OnMouseReleased() {
		if (isValidated()) {
			saveOptionToSharedMemory();
			EventHandler.callEvent(KeywordViewer.class, BizConst.EVENT_VIEW_LEVEL, false);
//			keywordViewer.dispose();
		} else {
			MessageBox.showErrorTypeVaildationFailure(FolderChef.getStage(), ObjectUtil.toString(SharedMemory.getDataMap().get(BizConst.KEY_MESSAGE)));
		}
		
		//TODO : Call Event "EVENT_VIEW_LEVEL"
		//       1. <User acts> do leveling the keywords.
		//		 2. Call Event "EVENT_TRANSFORM_EXECUTE"
		//       3.  => make relation between the keywords.
		//       4. Call Event "EVENT_VIEW_CATEGORY"
		//       5. <User acts> click next button.
		//		 6. Call Event "EVENT_LOAD_EXECUTE"
		//	     7.  => loading to destination by keywords relation.
		
	}
	
	private boolean isValidated() {
		if ("".equals(txtUpperCount.getText())) {
			SharedMemory.getDataMap().put(BizConst.KEY_MESSAGE, "Upper count cannot be null !");
			return false;
		}
		
		if (StringUtils.isNumeric(txtUpperCount.getText())) {
			if (Integer.parseInt(txtUpperCount.getText()) < 0) {
				SharedMemory.getDataMap().put(BizConst.KEY_MESSAGE, "Upper count cannot be under then 0 !");
				return false;
			}
		} else {
			SharedMemory.getDataMap().put(BizConst.KEY_MESSAGE, "Upper count only need number !");
			return false;
		}
		
		return true;
	}
	
	private void saveOptionToSharedMemory(){
		int upperCount = Integer.parseInt(txtUpperCount.getText());
		SharedMemory.getDataMap().put(BizConst.KEY_TRANSFORM_UPPER_COUNT, upperCount);
	}
}
