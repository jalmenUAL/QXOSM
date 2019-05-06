package qxosm.qxosm.openlayer;


import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.vaadin.addon.vol3.OLMap;
import org.vaadin.addon.vol3.OLMapOptions;
import org.vaadin.addon.vol3.OLView;
import org.vaadin.addon.vol3.OLViewOptions;
import org.vaadin.addon.vol3.client.OLCoordinate;
import org.vaadin.addon.vol3.client.OLExtent;
import org.vaadin.addon.vol3.client.Projections;
import org.vaadin.addon.vol3.feature.OLFeature;
import org.vaadin.addon.vol3.feature.OLLineString;
import org.vaadin.addon.vol3.feature.OLPoint;
import org.vaadin.addon.vol3.interaction.OLSelectInteraction;
import org.vaadin.addon.vol3.layer.OLLayer;
import org.vaadin.addon.vol3.layer.OLTileLayer;
import org.vaadin.addon.vol3.layer.OLVectorLayer;
import org.vaadin.addon.vol3.source.OLOSMSource;
import org.vaadin.addon.vol3.source.OLSource;
import org.vaadin.addon.vol3.source.OLVectorSource;
import org.vaadin.addon.vol3.util.SimpleContextMenu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Basic map view
 */
public class BasicMap extends VerticalLayout implements View {

	public OLMap map;
	protected OLSource source;
	private static final Logger logger = Logger.getLogger(BasicMap.class.getName());
	public Double minLon, minLat, maxLon, maxLat;
	public Double minLonT, minLatT, maxLonT, maxLatT;
	public Button buttonsa = new Button("Start Analysis!");
	public Button buttonra = new Button("Restart Analysis");
	public Button buttonrv = new Button("Reset View");
	public Button buttonst = new Button("Show state");
	public Button buttontm = new Button("Close map");
	public int size;

	public BasicMap() {

		String basepath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath();
		ThemeResource resource = new ThemeResource(
				"Logo-new.png");

		Image l1 = new Image(null, resource);

		l1.setWidth("100%");
		l1.setHeight("100%");

		this.setMargin(false);
		this.setSpacing(false);

		this.addComponent(l1);

		this.setSizeFull();

		map = createMap();
		
			
		
		createContextMenu();

		this.addViewChangeListener();
		this.addComponent(map);
		this.setExpandRatio(this.iterator().next(), 1.0f);
		this.addComponent(createControls());
		addViewChangeListener();

	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
	}

	protected OLMap createMap() {

		OLMap map = new OLMap(new OLMapOptions().setShowOl3Logo(true).setInputProjection(Projections.EPSG4326));

		source = createSource();
		OLLayer layer = createLayer(source);
		layer.setTitle("MapQuest OSM");

		map.addLayer(layer);
		map.setView(createView());
		map.setSizeFull();

		map.addClickListener(new OLMap.ClickListener() {
			@Override
			public void onClick(OLMap.OLClickEvent clickEvent) {
				System.out.println(clickEvent.toString());
			}
		});
		return map;
	}

	private void addViewChangeListener() {
		map.getView().addViewChangeListener(new OLView.ViewChangeListener() {
			@Override
			public void resolutionChanged(Double newResolution) {
				logger.info("resolution changed " + newResolution);
			}

			@Override
			public void rotationChanged(Double rotation) {
				logger.info("rotation changed " + rotation);
			}

			@Override
			public void centerChanged(OLCoordinate centerPoint) {
				logger.info("center changed " + centerPoint.toString());
			}

			@Override
			public void zoomChanged(Integer zoom) {
				logger.info("zoom changed " + zoom);
			}

			@Override
			public void extentChanged(OLExtent extent) {
				minLonT = extent.minX;
				minLatT = extent.minY;
				maxLonT = extent.maxX;
				maxLatT = extent.maxY;
				logger.info(
						"extent changed " + extent.minX + "," + extent.minY + "," + extent.maxX + "," + extent.maxY);
			}
		});
	}

	protected OLSource createSource() {
		return new OLOSMSource();
	}

	protected OLLayer createLayer(OLSource source) {
		return new OLTileLayer(source);
	}

	protected OLView createView() {
		OLViewOptions opts = new OLViewOptions();
		opts.setInputProjection(Projections.EPSG4326);
		OLView view = new OLView(opts);
		view.setZoom(10); // CAMBIAR
		view.setCenter(-3.726459394182147, 40.41626380948196);

		return view;
	}

	protected void saveFile() {

		 

		if (map.getView().getZoom() < 15) {
			Notification.show("Too many elements selected");
		} else {

			buttonra.setEnabled(true);
			buttonsa.setEnabled(false);
			buttonrv.setEnabled(false);

			minLon = minLonT;
			minLat = minLatT;
			maxLon = maxLonT;
			maxLat = maxLatT;

		}

	}

	protected void restart() {
		// map.getView().setCenter(-3.726459394182147, 40.41626380948196);
		// map.getView().setZoom(18);
		buttonsa.setEnabled(true);
		buttonrv.setEnabled(true);
		buttonra.setEnabled(false);
	}

	protected void resetView() {
		map.getView().setCenter(0, 0);
		map.getView().setZoom(1);
	}

	public String size(Double minLon, Double minLat, Double maxLon, Double maxLat) {
		String xml = "";

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			HttpGet request = new HttpGet("http://xosm.ual.es/qxosmapi/Total/minLon/" + minLon + "/minLat/" + minLat
					+ "/maxLon/" + maxLon + "/maxLat/" + maxLat);

			request.addHeader("content-type", "application/text");

			HttpResponse result = httpClient.execute(request);

			xml = EntityUtils.toString(result.getEntity(), "UTF-8");

		} catch (IOException ex) {
		}
		return xml;
	}

	protected AbstractLayout createControls() {
		HorizontalLayout controls = new HorizontalLayout();

		controls.setHeight("100%");
		controls.setWidth("100%");

		controls.setSpacing(true);
		controls.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		buttonsa.setStyleName(ValoTheme.BUTTON_FRIENDLY);

		buttonsa.setIcon(VaadinIcons.PLAY);

		buttonsa.setHeight("100%");
		buttonsa.setWidth("100%");

		buttonsa.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				saveFile();
				// map.getView().fitExtent(createExtent());
			}
		});
		controls.addComponent(buttonsa);

		buttonra.setStyleName(ValoTheme.BUTTON_DANGER);
		buttonra.setIcon(VaadinIcons.BACKWARDS);

		buttonra.setHeight("100%");
		buttonra.setWidth("100%");

		buttonra.setEnabled(false);

		buttonra.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				restart();
				// map.getView().fitExtent(createExtent());
			}
		});
		controls.addComponent(buttonra);

		buttonrv.setStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonrv.setIcon(VaadinIcons.AIRPLANE);

		buttonrv.setHeight("100%");
		buttonrv.setWidth("100%");

		buttonrv.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				resetView();
			}
		});
		controls.addComponent(buttonrv);

		buttonst.setStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonst.setIcon(VaadinIcons.INFO);

		buttonst.setHeight("100%");
		buttonst.setWidth("100%");

		buttonst.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				OLCoordinate center = map.getView().getCenter();
				StringBuilder message = new StringBuilder();
				message.append("center: ").append(center.toString()).append("\n");
				message.append("rotation: ").append(map.getView().getRotation()).append("\n");
				message.append("zoom: ").append(map.getView().getZoom()).append("\n");
				message.append("resolution: ").append(map.getView().getResolution()).append("\n");
				Notification.show(message.toString());
			}
		});
		controls.addComponent(buttonst);

		buttontm.setStyleName(ValoTheme.BUTTON_PRIMARY);
		buttontm.setIcon(VaadinIcons.CLOSE);

		buttontm.setHeight("100%");
		buttontm.setWidth("100%");

		buttontm.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				map.setVisible(!map.isVisible());
			}
		});
		controls.addComponent(buttontm);

		return controls;
	}

	protected void createContextMenu() {
		SimpleContextMenu simpleContextMenu = new SimpleContextMenu(map);
		simpleContextMenu.addItem("Test context menu item", new SimpleContextMenu.Command() {
			@Override
			public void execute() {
				Notification.show("context item clicked");
			}
		});
	}

}
