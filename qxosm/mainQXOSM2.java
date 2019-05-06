package qxosm.qxosm;

import javax.servlet.annotation.WebServlet;

import org.vaadin.addon.vol3.OLMap;
import org.vaadin.addon.vol3.client.source.OLMapQuestLayerName;
import org.vaadin.addon.vol3.layer.OLTileLayer;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import qxosm.qxosm.openlayer.BasicMap;
import qxosm.qxosm.openlayer.BingMap;
import qxosm.qxosm.openlayer.ClickHandlerTestMap;
import qxosm.qxosm.openlayer.ConstrainedMap;
import qxosm.qxosm.openlayer.ControlTestMap;
import qxosm.qxosm.openlayer.ExtentTestMap;
import qxosm.qxosm.openlayer.GeoJsonMap;
import qxosm.qxosm.openlayer.IconFeatureMap;
import qxosm.qxosm.openlayer.ImageWMSMap;
import qxosm.qxosm.openlayer.InteractionMap;
import qxosm.qxosm.openlayer.KMLTestMap;
import qxosm.qxosm.openlayer.OSMMap;
import qxosm.qxosm.openlayer.Proj4jsMap;
import qxosm.qxosm.openlayer.ResolutionsTestMap;
import qxosm.qxosm.openlayer.StyleTestMap;
import qxosm.qxosm.openlayer.TileWMSMap;
import qxosm.qxosm.openlayer.VectorLayerMap;
import qxosm.qxosm.openlayer.VectorModifyingMap;
import qxosm.qxosm.openlayer.ViewPortMap;
import qxosm.qxosm.openlayer.WFSTestMap;
import qxosm.qxosm.openlayer.WMTSMap;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class mainQXOSM2 extends UI {

	private Navigator navigator;
	private CssLayout buttonContainer;
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        
    	VerticalLayout root=new VerticalLayout();
        root.setSizeFull();
        this.setContent(root);
        buttonContainer = new CssLayout();
        buttonContainer.setWidth("100%");
        root.addComponent(buttonContainer);
        VerticalLayout navigatorContainer=new VerticalLayout();
        navigatorContainer.setSizeFull();
        root.addComponent(navigatorContainer);
        root.setExpandRatio(navigatorContainer, 1.0f);
        navigator=new Navigator(this, navigatorContainer);
        addViews();
        if(Page.getCurrent().getUriFragment()==null || Page.getCurrent().getUriFragment().length()==0){
            navigator.navigateTo("Basic");
    	}
    }
    private void addViews() {
        addView("Basic",BasicMap.class);
        addView("Bing", BingMap.class);
        addView("ViewPort",ViewPortMap.class);
        addView("Constrained (center)", ConstrainedMap.class);
//        addView("Constrained (panning)", PanningConstrainedMap.class);
        addView("OpenStreetMap", OSMMap.class);
        addView("ImageWMSMap",ImageWMSMap.class);
        addView("TileWMSMap", TileWMSMap.class);
        addView("VectorLayer", VectorLayerMap.class);
        addView("VectorLayerWithIcons", IconFeatureMap.class);
        addView("VectorModifying", VectorModifyingMap.class);
        addView("StyleTest", StyleTestMap.class);
        addView("ControlTest", ControlTestMap.class);
        addView("InteractionTest", InteractionMap.class);
        addView("ExtentTest", ExtentTestMap.class);
        addView("WMTSMap", WMTSMap.class);
        addView("ClickHandler", ClickHandlerTestMap.class);
        addView("Proj4jsMap", Proj4jsMap.class);
        addView("Resolutions", ResolutionsTestMap.class);
        addView("GeoJSON", GeoJsonMap.class);
        addView("WFS", WFSTestMap.class);
        addView("KML", KMLTestMap.class);
}
    private void addView(String label, Class<? extends View> implementation){
        navigator.addView(label, implementation);
        Button startView=new Button(label);
        startView.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(event.getButton().getCaption());
            }
        });
        buttonContainer.addComponent(startView);
}

    @WebServlet(urlPatterns = "/*", name = "mainQXOSMServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = mainQXOSM2.class, productionMode = false)
    public static class mainQXOSMServlet extends VaadinServlet {
    }
}
