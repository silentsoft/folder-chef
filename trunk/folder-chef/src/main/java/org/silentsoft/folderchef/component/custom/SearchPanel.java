package org.silentsoft.folderchef.component.custom;

import java.io.FileInputStream;

import javax.swing.ImageIcon;

import org.silentsoft.folderchef.component.model.Category.Property;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class SearchPanel extends Pane {

	private ImageView propertyImage;
	private HBox pane;
	
	public SearchPanel(Property property) {
		pane = new HBox();
		
		try {
			switch (property) {
			case Directory:
				propertyImage = new ImageView(new Image(new FileInputStream("icon/directory.png")));
				break;
			case File:
				propertyImage = new ImageView(new Image(new FileInputStream("icon/file.png")));
				break;
			case KeywordSet:
				propertyImage = new ImageView(new Image(new FileInputStream("icon/keyword_set.png")));
				break;
			case Keyword:
				propertyImage = new ImageView(new Image(new FileInputStream("icon/keyword.png")));
				break;
			default :
				propertyImage = new ImageView(new Image(new FileInputStream("icon/none_property.png")));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pane.getChildren().add(propertyImage);
		
		this.getChildren().add(pane);
		
	}
}
