package qxosm.qxosm.openlayer;

import org.vaadin.addon.vol3.OLMap;
import org.vaadin.addon.vol3.client.format.OLFeatureFormat;
import org.vaadin.addon.vol3.layer.OLVectorLayer;
import org.vaadin.addon.vol3.source.OLVectorSource;
import org.vaadin.addon.vol3.source.OLVectorSourceOptions;

/**
 * Bing Map view
 * Created by Martin Stypinski 22/03/2016
 */
public class GeoJsonMap extends BasicMap {

	@Override
	protected OLMap createMap() {
		OLMap map=super.createMap();
		// lets add some wfs features
		OLVectorSourceOptions vectorOptions=new OLVectorSourceOptions();
		// this is proxied by the TestServer
		//vectorOptions.setUrl("https://raw.githubusercontent.com/varmais/maakunnat/master/kunnat.geojson");
		//vectorOptions.setUrl("http://xosm.ual.es/xosmapi/Query/minLon/-2.45516/minLat/36.83645/maxLon/-2.45007/maxLat/36.83912?query=xosm_pbd:getLayerByName(.%20,%22Calle%20Calzada%20de%20Castro%22,100)");
		vectorOptions.setUrl("C:\\Users\\Administrator\\eclipse-workspace\\qxosm\\layerCalzada.xml");
		vectorOptions.setFormat(OLFeatureFormat.OSMXML);
		OLVectorSource vectorSource=new OLVectorSource(vectorOptions);
		OLVectorLayer vectorLayer=new OLVectorLayer(vectorSource);
		vectorLayer.setLayerVisible(true);
		map.addLayer(vectorLayer);
		return map;
	}


}
