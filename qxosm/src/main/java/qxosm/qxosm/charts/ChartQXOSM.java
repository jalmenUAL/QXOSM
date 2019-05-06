package qxosm.qxosm.charts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vaadin.addon.charts.Chart;
//import com.vaadin.addon.charts.examples.AbstractVaadinChartExample;
//import com.vaadin.addon.charts.examples.ExampleUtil;
import com.vaadin.addon.charts.model.AbstractSeries;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataLabels;
import com.vaadin.addon.charts.model.DataProviderSeries;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.LayoutDirection;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import qxosm.qxosm.DefaultParser;

public class ChartQXOSM extends AbstractVaadinChartExample {

	@Override
	public String getDescription() {
		return "ChartQXOSM";

	}

	@Override
	public Component getChart() {
		return null;
	}

	 
	public Component getChartSimple(String file, ChartType c, String title, String subtitle, String unit, String tip, String XaxisName, String YaxisName) {
		HorizontalLayout lo = new HorizontalLayout();
		lo.setSpacing(true);

		Chart chart = new Chart(c);

		Configuration configuration = chart.getConfiguration();

		configuration.setTitle(title);
		configuration.setSubTitle(subtitle);
		configuration.getxAxis().setTitle(XaxisName);
		configuration.getyAxis().setTitle(YaxisName);
		
		configuration.getLegend().setEnabled(false);
		
		
		PlotOptionsColumn column = new PlotOptionsColumn();
        column.setCursor(Cursor.POINTER);
        column.setDataLabels(new DataLabels(true));
        column.getDataLabels().setFormatter("this.y+' "+ unit +"'");
        configuration.setPlotOptions(column);

        
        
		chart.setSizeFull();

		 
		
		NodeList nl = xPath(file,"/result/row/x/text()");
		NodeList nl2 = xPath(file,"/result/row/y/text()");
		
		
		String[] categories = new String[nl.getLength()]; 

		DataSeries series = new DataSeries(tip);
		series.setId(tip);
		
		
		for (int i = 0; i < nl.getLength(); i++) {
			series.add(new DataSeriesItem(nl.item(i).getNodeValue(), Float.parseFloat(nl2.item(i).getNodeValue())));
			categories[i] = nl.item(i).getNodeValue();
		}

		configuration.getxAxis().setCategories(categories);
		configuration.addSeries(series);
		chart.setSizeFull();

		lo.addComponent(chart);

		lo.setWidth("100%");
		lo.setHeight("100%");
		lo.addComponent(chart);
		lo.setExpandRatio(chart, 0.4f);

		return lo;
	}
	
	public Component getChartSimplePercentage(String file, ChartType c, String title, String subtitle, String unit,  String tip, String XaxisName, String YaxisName) {
		HorizontalLayout lo = new HorizontalLayout();
		lo.setSpacing(true);

		Chart chart = new Chart(c);

		Configuration configuration = chart.getConfiguration();

		configuration.setTitle(title);
		configuration.setSubTitle(subtitle);
		
		
		YAxis y = new YAxis();
        y.setMin(0);
        y.setMax(100);
        AxisTitle titleY = new AxisTitle(YaxisName);
        titleY.setAlign(VerticalAlign.MIDDLE);
        y.setTitle(titleY);
        configuration.addyAxis(y);
		
        
        configuration.getxAxis().setTitle(XaxisName);
        
		configuration.getLegend().setEnabled(false);
		
		
		PlotOptionsColumn column = new PlotOptionsColumn();
        column.setCursor(Cursor.POINTER);
        column.setDataLabels(new DataLabels(true));
        column.getDataLabels().setFormatter("this.y+' "+unit+"'");
        configuration.setPlotOptions(column);

		chart.setSizeFull();

		 
		
		NodeList ntotal = xPath(file,"/result/total/text()");
		
		Float total = Float.parseFloat(ntotal.item(0).getNodeValue());
		
		
		 
		
		NodeList nl = xPath(file,"/result/row/x/text()");
		NodeList nl2 = xPath(file,"/result/row/y/text()");
		
		String[] categories = new String[nl.getLength()]; 

		DataSeries series = new DataSeries(tip);
		series.setId(tip);

		for (int i = 0; i < nl.getLength(); i++) {
			series.add(new DataSeriesItem(nl.item(i).getNodeValue(), Float.parseFloat(nl2.item(i).getNodeValue())*100/total));
			categories[i] = nl.item(i).getNodeValue();
		}

		configuration.getxAxis().setCategories(categories);
		configuration.addSeries(series);
		chart.setSizeFull();

		lo.addComponent(chart);

		lo.setWidth("100%");
		lo.setHeight("100%");
		lo.addComponent(chart);
		lo.setExpandRatio(chart, 0.4f);

		return lo;
	}


	 

	
	public Component getChartListPercentage(String file, ChartType c, String title, String subtitle, String unit,
			String XaxisName, String YaxisName,String[] categories) {
		HorizontalLayout lo = new HorizontalLayout();
		lo.setSpacing(true);

		Chart chart = new Chart(c);
		
		Configuration configuration = chart.getConfiguration();
		configuration.setTitle(title);
		configuration.setSubTitle(subtitle);
		
		chart.setSizeFull();
		
		
		PlotOptionsColumn column = new PlotOptionsColumn();
        column.setCursor(Cursor.POINTER);
        column.setDataLabels(new DataLabels(true));
        column.getDataLabels().setFormatter("this.y+ '"+unit+"'");
        configuration.setPlotOptions(column);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ this.y + '"+unit+"'");
        configuration.setTooltip(tooltip);
        
        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setAlign(HorizontalAlign.RIGHT);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(-100);
        legend.setY(100);
        legend.setFloating(true);
        legend.setBorderWidth(1);
        legend.setBackgroundColor(new SolidColor("#FFFFFF"));
        legend.setShadow(true);
        configuration.setLegend(legend);

         

		 
        
        NodeList nl = xPath(file,"/result/row/x/text()");
        
		
		YAxis y = new YAxis();
        y.setMin(0);
        y.setMax(100);
        AxisTitle titleY = new AxisTitle(YaxisName);
        titleY.setAlign(VerticalAlign.MIDDLE);
        y.setTitle(titleY);
        configuration.addyAxis(y);
	       
        configuration.getxAxis().setTitle(XaxisName);
	    
		 
		List<Series> series = new ArrayList<Series>();
		for (int i=0;i<nl.getLength();i++)
		{
			
			int p = i + 1;
			 
			 
			
			NodeList nl4 = xPath(file,"(/result/row/result)"+"["+p+"]/row/y/text()");
			NodeList ntotal = xPath(file,"(/result/row/result)"+"["+p+"]/total/text()");
			
			Float total = Float.parseFloat(ntotal.item(0).getNodeValue());
			Number[] af = new Number[nl4.getLength()];
			 
			
			for (int j=0;j<nl4.getLength();j++)
			{
				af[j]=Float.parseFloat(nl4.item(j).getNodeValue()) * 100 / total;
				
				
			}
			  
			series.add(new ListSeries(nl.item(i).getNodeValue(),af));
		}
		
		configuration.getxAxis().setCategories(categories); 
		configuration.setSeries(series);
		
		 

		lo.setWidth("100%");
		lo.setHeight("100%");
		lo.addComponent(chart);
		lo.setExpandRatio(chart, 0.4f);

		return lo;
	}
	
	public Component getChartList(String file, ChartType c, String title, String subtitle, String unit,
			String XaxisName, String YaxisName,String[] categories) {
		HorizontalLayout lo = new HorizontalLayout();
		lo.setSpacing(true);

		Chart chart = new Chart(c);
		
		Configuration configuration = chart.getConfiguration();
		configuration.setTitle(title);
		configuration.setSubTitle(subtitle);
		
		chart.setSizeFull();
		
		
		PlotOptionsColumn column = new PlotOptionsColumn();
        column.setCursor(Cursor.POINTER);
        column.setDataLabels(new DataLabels(true));
        column.getDataLabels().setFormatter("this.y+ '"+unit+"'");
        configuration.setPlotOptions(column);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name +': '+ this.y + '"+unit+"'");
        configuration.setTooltip(tooltip);
        
        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setAlign(HorizontalAlign.RIGHT);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(-100);
        legend.setY(100);
        legend.setFloating(true);
        legend.setBorderWidth(1);
        legend.setBackgroundColor(new SolidColor("#FFFFFF"));
        legend.setShadow(true);
        configuration.setLegend(legend);

         

		 
        
        NodeList nl = xPath(file,"/result/row/x/text()");
        
		
		YAxis y = new YAxis();
         
        AxisTitle titleY = new AxisTitle(YaxisName);
        titleY.setAlign(VerticalAlign.MIDDLE);
        y.setTitle(titleY);
        configuration.addyAxis(y);
	       
        configuration.getxAxis().setTitle(XaxisName);
	    
		 
		List<Series> series = new ArrayList<Series>();
		for (int i=0;i<nl.getLength();i++)
		{
			
			int p = i + 1;
			 
			
			NodeList nl4 = xPath(file,"(/result/row/result)"+"["+p+"]/row/y/text()");
			 
			Number[] af = new Number[nl4.getLength()];
			 
			
			for (int j=0;j<nl4.getLength();j++)
			{
				af[j]=Float.parseFloat(nl4.item(j).getNodeValue()) ;
				
				
			}
			  
			series.add(new ListSeries(nl.item(i).getNodeValue(),af));
		}
		
		configuration.getxAxis().setCategories(categories); 
		configuration.setSeries(series);
		
		 

		lo.setWidth("100%");
		lo.setHeight("100%");
		lo.addComponent(chart);
		lo.setExpandRatio(chart, 0.4f);

		return lo;
	}
	
	public NodeList xPath(String expression,String XPath) {
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
	 

}
