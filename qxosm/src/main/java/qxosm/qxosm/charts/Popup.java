package qxosm.qxosm.charts;


import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class Popup extends Window {
	
	public Popup(Component v,String Height,String Width,String Title){
		
		
		
		super();
		 
		 
		this.setHeightUndefined();
		this.setWidthUndefined();
		
		
		VerticalLayout l = new VerticalLayout();
		l.setHeightUndefined();
		l.setWidthUndefined();
		l.setSpacing(false);
		 
		
		Label title = new Label(Title);
		title.setHeightUndefined();
		title.setWidthUndefined();
		title.setStyleName(ValoTheme.LABEL_BOLD);
		
		VerticalLayout l2 = new VerticalLayout();	
		l2.addComponent(title);
		l2.addComponent(v);
		l2.setSpacing(false);
		 
		l2.setHeightUndefined();
		l2.setWidthUndefined();
		
		l.addComponent(l2);
		
		setContent(l);
		center();
		setModal(true);
		 
		
	}
public Popup(Component v){
		
		super();
		VerticalLayout l = new VerticalLayout();
		 
		l.setHeight("100%");
		l.setWidth("100%");
		setContent(l);
		l.addComponent(v);
		center();
		setModal(true);
		setSizeFull();
		
	}

}
