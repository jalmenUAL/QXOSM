package qxosm.qxosm.openlayer;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.vaadin.addon.vol3.OLMap;
import org.vaadin.addon.vol3.VaadinOverlay;
import org.vaadin.addon.vol3.client.OLCoordinate;
import org.vaadin.addon.vol3.client.format.OLFeatureFormat;
import org.vaadin.addon.vol3.client.style.OLCircleStyle;
import org.vaadin.addon.vol3.client.style.OLFillStyle;
import org.vaadin.addon.vol3.client.style.OLIconStyle;
import org.vaadin.addon.vol3.client.style.OLStrokeStyle;
import org.vaadin.addon.vol3.client.style.OLStyle;
import org.vaadin.addon.vol3.client.style.OLTextStyle;
import org.vaadin.addon.vol3.feature.OLFeature;
import org.vaadin.addon.vol3.feature.OLLineString;
import org.vaadin.addon.vol3.feature.OLPoint;
import org.vaadin.addon.vol3.layer.OLLayer;
import org.vaadin.addon.vol3.layer.OLVectorLayer;
import org.vaadin.addon.vol3.source.OLVectorSource;
import org.vaadin.addon.vol3.source.OLVectorSourceOptions;
import org.vaadin.addon.vol3.util.StyleUtils;
import org.w3c.dom.NodeList;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;

import qxosm.qxosm.DefaultParser;

/**
 * Map for testing vector layer
 */
public class XOSM extends OSMMap {

	protected OLVectorLayer vectorLayer;

	@Override
	protected OLMap createMap() {
		OLMap map = super.createMap();

		File f = new File("layerCalzada.xml");
		DefaultParser df = new DefaultParser(f);
		NodeList id = df.getFirstLevelNodeList("/osm/node/@id");
		NodeList lat = df.getFirstLevelNodeList("/osm/node/@lat");
		NodeList lon = df.getFirstLevelNodeList("/osm/node/@lon");

		OLVectorSourceOptions vectorOptions = new OLVectorSourceOptions();

		OLVectorSource vectorSource = new OLVectorSource(vectorOptions);

		for (int i = 0; i < id.getLength(); i++) {

			vectorSource.addFeature(createPointFeatureWithLabel((String) id.item(i).getNodeValue(),
					Double.parseDouble(lon.item(i).getNodeValue()), Double.parseDouble(lat.item(i).getNodeValue())));

		}

		// vectorSource.addFeature(createRectangleFeature("rect",-50,0,100,50));

		map.getView().setCenter(Double.parseDouble(lon.item(0).getNodeValue()),
				Double.parseDouble(lat.item(0).getNodeValue()));
		map.getView().setZoom(18);
		vectorLayer = new OLVectorLayer(vectorSource);

		map.addLayer(vectorLayer);
		
		
	

		return map;
	}

	public OLStrokeStyle createDefaultStrokeStyle() {
		OLStrokeStyle stroke = new OLStrokeStyle();
		stroke.color = "#3399CC";
		stroke.width = 1.25;
		return stroke;
	}

	public static OLFillStyle createDefaultFillStyle() {
		return new OLFillStyle("rgba(255,255,255,0.4");
	}

	public OLCircleStyle createDefaultCircleStyle() {
		OLCircleStyle circle = new OLCircleStyle();
		circle.fill = createDefaultFillStyle();
		circle.stroke = createDefaultStrokeStyle();
		circle.radius = 5.0;
		return circle;
	}

	public OLStyle createDefaultStyle() {
		OLStyle defaultStyle = new OLStyle();
		defaultStyle.fillStyle = createDefaultFillStyle();
		// defaultStyle.strokeStyle=createDefaultStrokeStyle();
		// defaultStyle.circleStyle=createDefaultCircleStyle();
		return defaultStyle;
	}

	private OLFeature createPointFeatureWithLabel(String id, double x, double y) {
		OLFeature testFeature = new OLFeature(id);
		testFeature.setGeometry(new OLPoint(x, y));

		OLStyle style = createDefaultStyle();

		style.textStyle = new OLTextStyle();
		style.textStyle.text = "Quality Issue";
		testFeature.setStyle(style);
		return testFeature;
	}

	protected OLFeature createPointFeature(String id, double x, double y) {
		OLFeature testFeature = new OLFeature(id);
		OLStyle style = new OLStyle();
		style.iconStyle = new OLIconStyle();
		style.iconStyle.color = "blue";
		style.iconStyle.size = new double[] { 32.0, 32.0 };
		testFeature.setStyle(style);
		testFeature.setGeometry(new OLPoint(x, y));
		return testFeature;
	}

	protected OLFeature createPointFeatureIcon(String id, double x, double y) {
		OLFeature pointFeature = createPointFeature(id, x, y);
		OLStyle style = new OLStyle();
		style.iconStyle = new OLIconStyle();
		style.iconStyle.crossOrigin = "anonymous";
		style.iconStyle.color = "blue";
		style.iconStyle.size = new double[] { 32.0, 32.0 };
		style.iconStyle.src = "icon.png";
		pointFeature.setStyle(style);
		return pointFeature;
	}

	protected OLFeature createRectangleFeature(String id, double x, double y, double width, double height) {
		OLFeature testFeature = new OLFeature(id);
		OLLineString lineString = new OLLineString();

		lineString.add(new OLCoordinate(x, y));
		lineString.add(new OLCoordinate(x + width, y));
		lineString.add(new OLCoordinate(x + width, y + height));
		lineString.add(new OLCoordinate(x, y + height));
		lineString.add(new OLCoordinate(x, y));

		testFeature.setGeometry(lineString);
		return testFeature;
	}

	@Override
	protected AbstractLayout createControls() {
		AbstractLayout controls = super.createControls();

		/*
		 * Button toggleVectorLayerVisibility=new Button("Toggle vector layer");
		 * 
		 * toggleVectorLayerVisibility.setHeight("100%");
		 * toggleVectorLayerVisibility.setWidth("100%");
		 * 
		 * toggleVectorLayerVisibility.addClickListener(new Button.ClickListener() {
		 * 
		 * @Override public void buttonClick(Button.ClickEvent event) {
		 * vectorLayer.setLayerVisible(!vectorLayer.isLayerVisible().booleanValue()); }
		 * }); controls.addComponent(toggleVectorLayerVisibility); Button
		 * swapLayerOrdering=new Button("Swap layer order");
		 * 
		 * swapLayerOrdering.setHeight("100%"); swapLayerOrdering.setWidth("100%");
		 * 
		 * swapLayerOrdering.addClickListener(new Button.ClickListener() {
		 * 
		 * @Override public void buttonClick(Button.ClickEvent event) { List<OLLayer>
		 * layers = map.getLayers(); for(OLLayer layer : layers){
		 * map.removeLayer(layer); } Collections.reverse(layers); for(OLLayer layer :
		 * layers){ map.addLayer(layer); } } });
		 * controls.addComponent(swapLayerOrdering);
		 */
		return controls;
	}
}
