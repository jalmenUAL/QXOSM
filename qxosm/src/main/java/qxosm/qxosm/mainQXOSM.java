package qxosm.qxosm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import com.vaadin.ui.*;
//import com.vaadin.ui.themes.ValoTheme;

import javax.servlet.annotation.WebServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.vaadin.addon.vol3.OLMap;
import org.vaadin.addon.vol3.client.source.OLMapQuestLayerName;
import org.vaadin.addon.vol3.layer.OLTileLayer;
import org.vaadin.addon.vol3.layer.OLVectorLayer;
import org.vaadin.addon.vol3.source.OLVectorSource;
import org.vaadin.addon.vol3.source.OLVectorSourceOptions;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
//import com.vaadin.ui.Label;
//import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ItemClick;
import com.vaadin.ui.Tree.ItemClickListener;
import com.vaadin.ui.Tree.TreeContextClickEvent;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import qxosm.qxosm.charts.ChartQXOSM;
import qxosm.qxosm.charts.Popup;
import qxosm.qxosm.openlayer.BasicMap;
import qxosm.qxosm.openlayer.GeoJsonMap;
import qxosm.qxosm.openlayer.IconFeatureMap;
import qxosm.qxosm.openlayer.InteractionMap;
import qxosm.qxosm.openlayer.KMLTestMap;
import qxosm.qxosm.openlayer.OSMMap;
import qxosm.qxosm.openlayer.StyleTestMap;
import qxosm.qxosm.openlayer.VectorLayerMap;
import qxosm.qxosm.openlayer.XOSM;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class mainQXOSM extends UI {

	private Navigator navigator;
	VerticalLayout right = new VerticalLayout();
	VerticalLayout pop = new VerticalLayout();
	HorizontalSplitPanel split = new HorizontalSplitPanel();
	BasicMap map = new BasicMap();
	List<String> items = new ArrayList<>();
	List<String> items2 = new ArrayList<>();
	List<String> items3 = new ArrayList<>();

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		right.setHeight("100%");
		right.setWidth("100%");
		right.setMargin(false);
		right.setSpacing(false);
		
		 
		pop.setHeight("100%");
		pop.setWidth("100%");
		pop.setMargin(false);
		 

		VerticalLayout nav = new VerticalLayout();
		nav.setStyleName("main");
		
		 

		navigator = new Navigator(this, nav);

		navigator.addView("QXOSM", map);
		navigator.navigateTo("QXOSM");
		nav.setMargin(false);
		nav.setSpacing(false);
		
		VerticalLayout left = new VerticalLayout();
		 

		left.setMargin(false);
		left.setSpacing(false);
		left.addComponent(nav);
		left.setMargin(false);
		left.setSpacing(false);

		Label l2 = new Label("Please Select Area of the Map and click on 'Start Analysis!'");

		l2.setWidth("100%");
		l2.setHeight("100%");

		l2.setVisible(true);

		right.addComponent(l2);

		Tree<String> tree = tree();
		tree.setVisible(false);

		right.addComponent(tree);

		split.addComponent(left);
		split.addComponent(right);
		split.setSplitPosition(75);

		
		this.setContent(split);
		this.setSizeFull();
		//this.setHeight("100%");
		//this.setWidth("100%");

		map.buttonsa.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (map.map.getView().getZoom() >= 15) {
					tree.setVisible(true);
					l2.setVisible(false);
					map.buttonra.setEnabled(true);
				}

			}
		});

		map.buttonra.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {

				tree.setVisible(false);
				l2.setVisible(true);

			}
		});

	}

	public String api(Double minLon, Double minLat, Double maxLon, Double maxLat, String query) {
		String xml = "";

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("query", query));

			HttpGet request = new HttpGet("http://xosm.ual.es/qxosmapi/Query/minLon/" + minLon + "/minLat/" + minLat
					+ "/maxLon/" + maxLon + "/maxLat/" + maxLat + "?" + URLEncodedUtils.format(params, "utf-8"));

			request.addHeader("content-type", "application/xml");

			HttpResponse result = httpClient.execute(request);

			xml = EntityUtils.toString(result.getEntity(), "UTF-8");

		} catch (IOException ex) {
		}
		return xml;
	}

	public String TwinColKeys(Set<String> select) {
		String sel = "(";

		for (String s : select) {
			if ((!(sel == "("))) {
				sel = sel + ",'" + s + "'";
			} else
				sel = sel + "'" + s + "'";
		}

		sel = sel + ")";
		return sel;
	};

	public String[] TwinColCategories(Set<String> select) {

		String[] sel = new String[select.size()];
		int i = 0;

		for (String s : select) {
			sel[i] = s;
			i++;
		}

		return sel;
	};

	public Tree<String> tree() {
		Tree<String> tree = new Tree<>();

		tree.setHeight("100%");
		tree.setWidth("100%");

		TreeData<String> data = new TreeData<>();
		data.addItems(null, "Summary", "Completeness", "Compliance", "Consistency", "Granularity", "Richness",
				"Trust").addItems("Summary", "Summary of Layer", "Summary of Types", "Summary of Versions")
				.addItems("Completeness", "Missing Attributes in an Entity",
						"Missing Attribute in Categories of an Entity",
						"Missing Attribute in Entities")
				.addItems("Compliance", "Commonly Used Keys", "Commonly Used Combinations of Keys")
				.addItems("Commonly Used Keys", "Less Commonly Used Keys", "Less Commonly Used Keys of an Entity",
						"Less Commonly Used Keys in Categories of an Entity")
				.addItems("Commonly Used Combinations of Keys", "Less Commonly Used Combination of an Entity",
						"Less Commonly Used Combination in Categories of an Entity")
				.addItems("Trust", "Versions", "Tags", "Users", "Dates", "Sources", "Local Experience",
						"Global Experience")
				.addItems("Users", "Contributions by User", "Contributions of an Entity by User")
				.addItems("Dates", "Contributions by Date", "Contributions of an Entity by Date")
				.addItems("Sources", "Contributions by Source", "Contributions of an Entity by Source")
				.addItems("Local Experience", "Contributions by Local Experience",
						"Contributions of an Entity by Local Experience")
				.addItems("Global Experience", "Contributions by Global Experience",
						"Contributions of an Entity by Global Experience");

		tree.setDataProvider(new TreeDataProvider<>(data));
		 

		tree.setItemIconGenerator(item -> {
			if (item.equals("Summary")) {
				return VaadinIcons.LIST;
			} else if (item.equals("Completeness")) {
				return VaadinIcons.SUN_RISE;
			} else if (item.equals("Compliance")) {
				return VaadinIcons.MEDAL;
			} else if (item.equals("Consistency")) {
				return VaadinIcons.ADJUST;
			} else if (item.equals("Granularity")) {
				return VaadinIcons.BOOK;
			} else if (item.equals("Richness")) {
				return VaadinIcons.DIAMOND;
			} else if (item.equals("Trust")) {
				return VaadinIcons.USERS;
			}
			return null;
		});
		//tree.setStyleGenerator(item -> {
		//	return "right";
		//});
		tree.asSingleSelect();

		items.clear();
		TagInfoKeys(items);

		pop.setStyleName(ValoTheme.FORMLAYOUT_LIGHT);

		tree.addItemClickListener(new ItemClickListener() {

			@SuppressWarnings("serial")
			@Override
			public void itemClick(ItemClick event) {

				String id = (String) event.getItem();

				if (id == "Summary of Layer") {

					TableQXOSM t = new TableQXOSM();

					Popup p2 = new Popup(
							t.getTable(api(map.minLon, map.minLat, map.maxLon, map.maxLat, "qxosmv3:summary(.)")),
							"450px", "800px","Summary of Number of Elements, Contributors, Contributions and Editions "
									+ "of the Selected Layer");

					addWindow(p2);

				} else if (id == "Summary of Types") {

					pop.removeAllComponents();

					TwinColSelect<String> select = new TwinColSelect<>("Select Entities");
					 
					select.setHeight("100%");
					select.setWidth("100%");
					select.clear();
					select.setItems(items);
					pop.addComponent(select);

					 

					Button button = new Button("Analyze");
					 
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Summary of Number of Elements by Entity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							if (select.getSelectedItems().size() == 0) {
								Notification.show("Please select at least one entity");
							}

							else {
								ChartQXOSM c = new ChartQXOSM();

								String sel = TwinColKeys(select.getSelectedItems());

								Popup p2 = new Popup(c.getChartSimple(
										api(map.minLon, map.minLat, map.maxLon, map.maxLat,
												"qxosmv3:summary_type(.," + sel + ")"),
										ChartType.COLUMN, "Summary of Types",
										"Total Number of Elements: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
												"qxosmv3:elementTotal(.)"),
										
										" elements", "OSM Items", "OSM Types",
										"OSM Items"));

								// pop.removeAllComponents();
								removeWindow(p1);
								addWindow(p2);
							}

						}

					});
				} else if (id == "Summary of Versions") {

					pop.removeAllComponents();

					TwinColSelect<String> select = new TwinColSelect<>("Select Entities");
					select.setHeight("100%");
					select.setWidth("100%");
					select.clear();
					select.setItems(items);
					pop.addComponent(select);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Summary of Number of Versions by Entity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							if (select.getSelectedItems().size() == 0) {
								Notification.show("Please select at least one entity");
							}

							else {
								ChartQXOSM c = new ChartQXOSM();

								String sel = TwinColKeys(select.getSelectedItems());

								Popup p2 = new Popup(c.getChartSimple(
										api(map.minLon, map.minLat, map.maxLon, map.maxLat,
												"qxosmv3:summary_edition_avg(.," + sel + ")"),
										ChartType.COLUMN, "Summary of Versions", 
										"Total Average of Versions: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
												"qxosmv3:editionNumberAvg(.)"),
										" versions",
										"Average of Versions of OSM Elements", "OSM Types",
										"Average of Versions of OSM Elements"));

								// pop.removeAllComponents();
								removeWindow(p1);
								addWindow(p2);
							}
						};
					});

				} else if (id == "Missing Attributes in an Entity") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					TwinColSelect<String> select = new TwinColSelect<>("Select Attributes");
					select.setHeight("100%");
					select.setWidth("100%");
					select.clear();
					pop.addComponent(select);

					comboBox.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							items2.clear();
							TagInfoCombinations(items2, comboBox.getValue());
							select.setItems(items2);
						}

					});

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Completeness: Analysis of Missing Attributes in an Entity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							if (select.getSelectedItems().size() == 0 || comboBox.getValue()==null) {
								Notification.show("Please select at least one entity and an attribute");
							}

							else {
								ChartQXOSM c = new ChartQXOSM();

								String sel = TwinColKeys(select.getSelectedItems());
								String entity = comboBox.getValue();

								Popup p2 = new Popup(c.getChartSimplePercentage(
										api(map.minLon, map.minLat, map.maxLon, map.maxLat,
												"qxosmv3:comp_missing_keys(.," + sel + ",'" + entity + "')"),
										ChartType.COLUMN, "Missing Attributes in " + entity,
										"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
												"qxosmv3:elementNumberByType(.,'"+entity+"')"),
										 " %",
										"Percentage of " + entity + " with Missing Attribute", "OSM Types",
										"Percentage of OSM Items"));

								// pop.removeAllComponents();
								removeWindow(p1);
								addWindow(p2);

							}
						}

					}

					);
				} else if (id == "Missing Attribute in Categories of an Entity") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					TwinColSelect<String> select = new TwinColSelect<>("Select Categories");
					select.setHeight("100%");
					select.setWidth("100%");
					pop.addComponent(select);

					TwinColSelect<String> select2 = new TwinColSelect<>("Select Attributes");
					select2.setHeight("100%");
					select2.setWidth("100%");
					pop.addComponent(select2);

					comboBox.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(ValueChangeEvent event) {

							items2.clear();
							TagInfoValues(items2, comboBox.getValue());
							select.clear();
							select.setItems(items2);

							items3.clear();
							TagInfoCombinations(items3, comboBox.getValue());
							select2.clear();
							select2.setItems(items3);

						}

					});

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "800px", "800px","Completeness: Missing Attributes in Categories of an Entity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							if (select.getSelectedItems().size() == 0 || select2.getSelectedItems().size() == 0 ||  comboBox.getValue()==null) {
								Notification.show("Please select at least one key, one category and one attribute");
							}

							else {

								String sel = TwinColKeys(select.getSelectedItems());
								String sel2 = TwinColKeys(select2.getSelectedItems());
								String entity = comboBox.getValue();

								ChartQXOSM c = new ChartQXOSM();

								String[] categories = TwinColCategories(select.getSelectedItems());
								Popup p2 = new Popup(c.getChartListPercentage(
										api(map.minLon, map.minLat, map.maxLon, map.maxLat,
												"qxosmv3:comp_missing_key_values(.," + sel2 + ",+" + sel + ",'" + entity
														+ "')"),
										ChartType.COLUMN,
										"Missing Attribute in Categories of " + entity,
										"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
												"qxosmv3:elementNumberByType(.,'"+entity+"')"),
										 " %",
										"OSM Types", "Percentage of OSM Items", categories));

								// pop.removeAllComponents();
								removeWindow(p1);
								addWindow(p2);

							}
						}

					}

					);
				} else if (id == "Missing Attribute in Entities") {

					pop.removeAllComponents();

					TwinColSelect<String> select = new TwinColSelect<>("Select Entities");
					select.setHeight("100%");
					select.setWidth("100%");
					pop.addComponent(select);
					select.clear();
					select.setItems(items);

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Attribute");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Completeness: Missing Attribute in Entities");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (select.getSelectedItems().size() == 0 || comboBox.getValue()==null) {
								Notification.show("Please select at least one entity and one attribute");
							}
							else
							{
							ChartQXOSM c = new ChartQXOSM();
							String attribute = comboBox.getValue();
							String sel = TwinColKeys(select.getSelectedItems());

							Popup p2 = new Popup(
									c.getChartSimple(
											api(map.minLon, map.minLat, map.maxLon, map.maxLat,
													"qxosmv3:comp_missing_types_key(.,'" + attribute + "'," + sel
															+ ")"),
											ChartType.COLUMN,
											"Missing " + attribute + " in Entities",
											"",
											" %",
											"Percentage of Items with Missing Attribute", "OSM Types",
											"Percentage of OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}

						}
					});
				} else if (id == "Less Commonly Used Keys") {

					pop.removeAllComponents();

					ChartQXOSM c = new ChartQXOSM();

					Popup p2 = new Popup(c.getChartSimplePercentage(
							api(map.minLon, map.minLat, map.maxLon, map.maxLat,
									"qxosmv3:compli_some_key_unknown(.,(10,20,50,100,200,300,400))"),
							ChartType.COLUMN, "Less Commonly Used Keys", 
							"Total Number of Elements: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
									"qxosmv3:elementTotal(.)"),
							" %", "Percentage of Items with Less Commonly Used Keys",
							"Number of Keys (TagInfo)", "Percentage of OSM Items"));

					addWindow(p2);

				} else if (id == "Less Commonly Used Keys of an Entity") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "200px", "800px", "Compliance: Analysis of Less Commonly Used Keys of an Entity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else {
								
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							
							Popup p2 = new Popup(c.getChartSimplePercentage(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:compli_some_key_unknown_type(.,'" + entity
													+ "',(10,20,50,100,200,300,400))"),
									ChartType.COLUMN, "Less Commonly Used Keys in " + entity,
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')"),
									" %",
									"Percentage of Items with Less Commonly Used Keys", "Number of Keys (TagInfo)",
									"Percentage of Items with Less Commonly Used Keys"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}
						}
					});
				} else if (id == "Less Commonly Used Keys in Categories of an Entity") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					TwinColSelect<String> select = new TwinColSelect<>("Select Categories");
					select.setHeight("100%");
					select.setWidth("100%");
					select.clear();
					pop.addComponent(select);

					comboBox.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							items2.clear();
							TagInfoValues(items2, comboBox.getValue());
							select.setItems(items2);
						}

					});

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Compliance: Analysis of Less Commonly Used Keys in Categories of an Entity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							
							if (select.getSelectedItems().size() == 0 || comboBox.getValue()==null) {
								Notification.show("Please select at least one entity and one category");
							}
							else
							{
							ChartQXOSM c = new ChartQXOSM();

							String entity = comboBox.getValue();
							String sel = TwinColKeys(select.getSelectedItems());
							String[] categories = { "10", "20", "50", "100", "200", "300", "400" };
							
							Popup p2 = new Popup(c.getChartList(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:compli_some_key_unknown_type_values(.,'" + entity + "'," + sel
													+ ",(10,20,50,100,200,300,400))"),
									ChartType.COLUMN, "Less Commonly Used Keys in " + entity, 
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')"),
									
									" Items",
									"Number of Items with Less Commonly Used Keys", "Number of Keys (TagInfo)",
									categories));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);

							}
						}
					});
				} else if (id == "Less Commonly Used Combination of an Entity") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Compliance: Analysis of Less Commonly Used Combination of an Entity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else {
							
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							Popup p2 = new Popup(c.getChartSimplePercentage(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:compli_wrong_combination_keys(.,'" + entity
													+ "',(10,20,50,100,200,300,400))"),
									ChartType.COLUMN, "Less Commonly Used Combination in " + entity,
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')"),
									" %",
									"Percentage of Items with Less Commonly Used Combination", "Number of Keys (TagInfo)",
									"Percentage of OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}
						}
					});
				} else if (id == "Less Commonly Used Combination in Categories of an Entity") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					TwinColSelect<String> select = new TwinColSelect<>("Select Categories");
					select.setHeight("100%");
					select.setWidth("100%");
					select.clear();
					pop.addComponent(select);

					comboBox.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							items2.clear();
							TagInfoValues(items2, comboBox.getValue());
							select.setItems(items2);
						}

					});

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Compliance: Analysis of Less Commonly Used Combination of Categories of an Entity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (select.getSelectedItems().size() == 0 || comboBox.getValue()==null) {
								Notification.show("Please select at least one entity and one category");
							}
							else
							{
							ChartQXOSM c = new ChartQXOSM();
							String[] categories = { "10", "20", "50", "100", "200", "300", "400" };
							String entity = comboBox.getValue();
							String sel = TwinColKeys(select.getSelectedItems());

							Popup p2 = new Popup(c.getChartListPercentage(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:compli_wrong_combination_keys_values(.,'" + entity + "'," + sel
													+ ",(10,20,50,100,200,300,400))"),
									ChartType.COLUMN, "Less Commonly Used Combination in a Category of " + entity,
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')"),
									" %",
									"Number of Keys (TagInfo)", "Percentage of Items with Less Commonly Used Combination",
									categories));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}

						}
					});
				} else if (id == "Granularity") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Analysis of Granularity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else
							{
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							Popup p2 = new Popup(c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:gra_summary_key(.,'" + entity + "')"),
									ChartType.PIE, "Granularity of " + entity, 
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')")+
									" Average Number of Keys: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:average_key(.,'" + entity + "')")+
											
											" Median Number of Keys: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
													"qxosmv3:median(.,'" + entity + "')"
											),
									
									
									" %", "OSM Items", "Keys", "OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}
						}
					});
				} else if (id == "Consistency") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					ComboBox<String> comboBox2 = new ComboBox<>();
					comboBox2.setHeight("100%");
					comboBox2.setWidth("100%");
					comboBox2.setCaption("Select Category");
					comboBox2.clear();
					comboBox2.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox2);

					comboBox.addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChange(ValueChangeEvent event) {
							items2.clear();
							TagInfoValues(items2, comboBox.getValue());
							comboBox2.setItems(items2);
						}

					});

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Analysis of Consistency");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if ( comboBox.getValue()==null || comboBox2.getValue()==null) {
								Notification.show("Please select at least one entity and one category");
							}
							else
							{
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();
							String value = comboBox2.getValue();

							Popup p2 = new Popup(
									c.getChartSimple(
											api(map.minLon, map.minLat, map.maxLon, map.maxLat,
													"qxosmv3:log_summary_key_value(.,'" + entity + "','" + value
															+ "')"),
											ChartType.PIE, "Consistency of " + entity + " in " + value,
											"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
													"qxosmv3:elementNumberByType(.,'"+entity+"')")+
											" Standard Deviation: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
													"qxosmv3:standard_deviation_key(.,'" + entity + "','" + value
															+ "')"),
											
											" %",
											"OSM Items", "Keys", "OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}

						}
					});
				} else if (id == "Richness") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Analysis of Richness");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else {
								
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							Popup p2 = new Popup(c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:rich_summary_values(.,'" + entity + "')"),
									ChartType.PIE, "Richness of " + entity, 
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')")+
									" Number of Distinct Values: "+ api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:richness_type(.,'" + entity + "')"),
									
									" %", "OSM Items", "Keys", "OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);

						}}
					});
				} else if (id == "Versions") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Trust: Analysis of Versions of an Entity");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else {
							
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							Popup p2 = new Popup(c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:trust_summary_versions(.,'" + entity + "')"),
									ChartType.BAR, "Distribution of Versions of " + entity, 
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')"),
									" elements", "OSM Items",
									"Version", "OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}
						}
					});
				} else if (id == "Tags") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Version");
					comboBox.clear();
					comboBox.setItems("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
							"16", "17", "18", "19", "20", "21", "22", "23", "24", "25");
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Trust: Analysis of Tags of a Version");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one version");}
							else {
							ChartQXOSM c = new ChartQXOSM();
							int version = Integer.parseInt(comboBox.getValue());

							Popup p2 = new Popup(c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:trust_summary_tags(.," + version + ")"),
									ChartType.BAR, "Distribution of Tags for Version " + version, 
									"",
									
									" %", "OSM Items",
									"Tags", "OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}
						}
					});
				}

				else if (id == "Contributions by User") {

					ChartQXOSM c = new ChartQXOSM();

					Popup p2 = new Popup(
							c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:trust_summary_users(.)"),
									ChartType.PIE, "Contributions by User", 
									"Total Number of Elements: "+ api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementTotal(.)"),
									" elements", 
									
									"OSM Items",
									"Id of User", "Percentage of OSM Items"));

					addWindow(p2);

				} else if (id == "Contributions of an Entity by User") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Trust: Analysis of Contributions of an Entity by User");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else {
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							Popup p2 = new Popup(c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:trust_summary_users_type(.,'" + entity + "')"),
									ChartType.PIE, "Contributions of " + entity + " by User", 
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')"),
									
									" elements",
									"OSM Items", "Id of User", "Percentage of OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}
						}
					});
				} else if (id == "Contributions by Date") {

					ChartQXOSM c = new ChartQXOSM();

					Popup p2 = new Popup(
							c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:trust_summary_dates(.)"),
									ChartType.AREASPLINE, "Contributions by Date",
									"Total Number of Elements: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementTotal(.)"),
									" elements", "OSM Items", "Period",
									"OSM Items"));

					// pop.removeAllComponents();

					addWindow(p2);

				} else if (id == "Contributions of an Entity by Date") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Trust: Analysis of Contributions of an Entity by Date");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else {
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							Popup p2 = new Popup(c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:trust_summary_dates_type(.,'" + entity + "')"),
									ChartType.AREASPLINE, "Contributions of " + entity + " by Date", 
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')"),
									" elements",
									"OSM Items", "Period", "OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);

						}}
					});
				} else if (id == "Contributions by Source") {

					ChartQXOSM c = new ChartQXOSM();

					Popup p2 = new Popup(
							c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:trust_summary_sources(.)"),
									ChartType.COLUMN, "Contributions by Source", 
									"Percentage of Elements with Source: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:rating_source(.)"),
									" elements", "OSM Items", "Source",
									"OSM Items"));

					// pop.removeAllComponents();

					addWindow(p2);

				} else if (id == "Contributions of an Entity by Source") {

					pop.removeAllComponents();

					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Trust: Analysis of Contributions of an Entity by Source");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else {
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							Popup p2 = new Popup(c.getChartSimplePercentage(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:trust_summary_sources_type(.,'" + entity + "')"),
									ChartType.COLUMN, "Contributions of " + entity + " by Source", 
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')"),
									" %",
									"Percentage of OSM Items", "Source", "Percentage of OSM Items"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}
						}
					});
				} else if (id == "Contributions by Local Experience") {

					ChartQXOSM c = new ChartQXOSM();

					Popup p2 = new Popup(c.getChartSimple(
							api(map.minLon, map.minLat, map.maxLon, map.maxLat,
									"qxosmv3:trust_number_contributions(.,(0,10,100,1000,10000))"),
							ChartType.PIE, "Contributions by Local Experience", 
							"Average of Elements in Area by User: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
									"qxosmv3:rate_users_local(.)"),
							" elements", "OSM Items", "OSM Items",
							"Contributions"));

					// pop.removeAllComponents();

					addWindow(p2);

				} else if (id == "Contributions of an Entity by Local Experience") {

					pop.removeAllComponents();
					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Trust: Analysis of Contributions of an Entity by Local Experience");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else {
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							Popup p2 = new Popup(c.getChartSimple(
									api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:trust_number_contributions_type(.,'" + entity
													+ "',(0,10,100,1000,10000))"),
									ChartType.PIE, "Contributions of " + entity + " by Local Experience", 
									"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
											"qxosmv3:elementNumberByType(.,'"+entity+"')"),
									" elements",
									"OSM Items", "OSM Items", "Contributions"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}
						}
					});
				} else if (id == "Contributions by Global Experience") {

					ChartQXOSM c = new ChartQXOSM();

					Popup p2 = new Popup(c.getChartSimple(
							api(map.minLon, map.minLat, map.maxLon, map.maxLat,
									"qxosmv3:trust_changeset(.,(0,10,100,1000,10000))"),
							ChartType.PIE, "Contributions by Global Experience", 
							"Average of Elements in OSM by User: "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
									"qxosmv3:rate_users_global(.)"),
							
							" elements", "OSM Items", "OSM Items",
							"Changesets"));

					// pop.removeAllComponents();

					addWindow(p2);

				} else if (id == "Contributions of an Entity by Global Experience") {

					pop.removeAllComponents();
					ComboBox<String> comboBox = new ComboBox<>();
					comboBox.setHeight("100%");
					comboBox.setWidth("100%");
					comboBox.setCaption("Select Entity");
					comboBox.clear();
					comboBox.setItems(items);
					comboBox.setEmptySelectionAllowed(false);
					pop.addComponent(comboBox);

					Button button = new Button("Analyze");
					button.setStyleName(ValoTheme.BUTTON_FRIENDLY);
					pop.addComponent(button);

					Popup p1 = new Popup(pop, "420px", "800px","Trust: Analysis of Contributions of an Entity by Global Experience");

					addWindow(p1);

					button.addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							
							if (comboBox.getValue()==null) {Notification.show("Please select at least one entity");}
							else {
							ChartQXOSM c = new ChartQXOSM();
							String entity = comboBox.getValue();

							Popup p2 = new Popup(
									c.getChartSimple(
											api(map.minLon, map.minLat, map.maxLon, map.maxLat,
													"qxosmv3:trust_changeset_type(.,'" + entity
															+ "',(0,10,100,1000,10000))"),
											ChartType.PIE, "Contributions of " + entity + " by Global Experience",
											"Number of "+entity+": "+api(map.minLon, map.minLat, map.maxLon, map.maxLat,
													"qxosmv3:elementNumberByType(.,'"+entity+"')"),
											" elements", "OSM Items", "OSM Items", "Changesets"));

							// pop.removeAllComponents();
							removeWindow(p1);
							addWindow(p2);
							}
						}
					});
				}

			}
		});
		return tree;
	}

	public void TagInfoKeys(List<String> items) {

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("include", "prevalent_values"));
			params.add(new BasicNameValuePair("sortname", "count_all"));
			params.add(new BasicNameValuePair("sortorder", "desc"));
			params.add(new BasicNameValuePair("page", "1"));
			params.add(new BasicNameValuePair("rp", "100"));
			params.add(new BasicNameValuePair("qtype", "key"));
			params.add(new BasicNameValuePair("format", "json_pretty"));

			HttpGet request = new HttpGet(
					"https://taginfo.openstreetmap.org/api/4/keys/all" + "?" + URLEncodedUtils.format(params, "utf-8"));

			request.addHeader("content-type", "application/json");

			HttpResponse result = httpClient.execute(request);

			String json = EntityUtils.toString(result.getEntity(), "UTF-8");

			try {
				JSONParser parser = new JSONParser();
				Object resultObject = parser.parse(json);

				JSONObject obj = (JSONObject) resultObject;
				JSONArray jsa = (JSONArray) obj.get("data");
				for (int i = 0; i < jsa.size(); i++) {
					JSONObject jso = (JSONObject) jsa.get(i);
					items.add(jso.get("key").toString());
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (IOException ex) {
		}

	}

	public void TagInfoValues(List<String> items, String key) {

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("key", key));
			params.add(new BasicNameValuePair("filter", "all"));
			params.add(new BasicNameValuePair("sortname", "count"));
			params.add(new BasicNameValuePair("sortorder", "desc"));
			params.add(new BasicNameValuePair("page", "1"));
			params.add(new BasicNameValuePair("rp", "100"));
			params.add(new BasicNameValuePair("qtype", "value"));
			params.add(new BasicNameValuePair("format", "json_pretty"));

			HttpGet request = new HttpGet("https://taginfo.openstreetmap.org/api/4/key/values" + "?"
					+ URLEncodedUtils.format(params, "utf-8"));

			request.addHeader("content-type", "application/json");

			HttpResponse result = httpClient.execute(request);

			String json = EntityUtils.toString(result.getEntity(), "UTF-8");

			try {
				JSONParser parser = new JSONParser();
				Object resultObject = parser.parse(json);

				JSONObject obj = (JSONObject) resultObject;
				JSONArray jsa = (JSONArray) obj.get("data");
				for (int i = 0; i < jsa.size(); i++) {
					JSONObject jso = (JSONObject) jsa.get(i);
					items.add(jso.get("value").toString());
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (IOException ex) {
		}

	}

	public void TagInfoCombinations(List<String> items, String key) {

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("key", key));
			params.add(new BasicNameValuePair("filter", "all"));
			params.add(new BasicNameValuePair("sortname", "to_count"));
			params.add(new BasicNameValuePair("sortorder", "desc"));
			params.add(new BasicNameValuePair("page", "1"));
			params.add(new BasicNameValuePair("rp", "100"));
			params.add(new BasicNameValuePair("qtype", "other_key"));
			params.add(new BasicNameValuePair("format", "json_pretty"));

			HttpGet request = new HttpGet("https://taginfo.openstreetmap.org/api/4/key/combinations" + "?"
					+ URLEncodedUtils.format(params, "utf-8"));

			request.addHeader("content-type", "application/json");

			HttpResponse result = httpClient.execute(request);

			String json = EntityUtils.toString(result.getEntity(), "UTF-8");

			try {
				JSONParser parser = new JSONParser();
				Object resultObject = parser.parse(json);

				JSONObject obj = (JSONObject) resultObject;
				JSONArray jsa = (JSONArray) obj.get("data");
				for (int i = 0; i < jsa.size(); i++) {
					JSONObject jso = (JSONObject) jsa.get(i);
					items.add(jso.get("other_key").toString());
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (IOException ex) {
		}

	}

	public NodeList xPath(String expression, String XPath) {
		NodeList nodeList = null;
		try {
			 

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			Document xmlDocument = builder.parse(new InputSource(new StringReader(expression)));

			XPath xPath = XPathFactory.newInstance().newXPath();

			nodeList = (NodeList) xPath.compile(XPath).evaluate(xmlDocument, XPathConstants.NODESET);

		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
			e.printStackTrace();
		}
		return nodeList;
	}

	@WebServlet(urlPatterns = "/*", name = "mainQXOSMServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = mainQXOSM.class, productionMode = false)
	public static class mainQXOSMServlet extends VaadinServlet {
	}
}
