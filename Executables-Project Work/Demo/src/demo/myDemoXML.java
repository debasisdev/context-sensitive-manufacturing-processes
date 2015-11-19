package demo;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class myDemoXML {
	public static void main(String[] args){
		try {
		    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		    Document doc = docBuilder.parse(new File("conDef.xml"));
		    doc.getDocumentElement().normalize();
		    XPathFactory xPathfactory = XPathFactory.newInstance();
		    XPath xpath = xPathfactory.newXPath();
		    String workerQuery1 = "//Context[@name='availableWorkers']/ContextDefinition/DefinitionContent[SenseValue>=10]/SenseValue/text()";
		    XPathExpression expr = xpath.compile(workerQuery1);
		    NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		    NodeList nlx;
		    if(nl.getLength() > 0) {
		    	String unitQuery1 = "//Context[@name='unitsOrdered']/ContextDefinition/DefinitionContent[SenseValue<=1000]/SenseValue/text()";
		    	expr = xpath.compile(unitQuery1);
		    	nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		    	if(nl.getLength() > 0)
		    		System.out.println("Manual Process Will Run");
		    	else {
		    		String unitQuery2 = "//Context[@name='unitsOrdered']/ContextDefinition/DefinitionContent[SenseValue>1000]/SenseValue/text()";
			    	expr = xpath.compile(unitQuery2);
			    	nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			    	if(nl.getLength() > 0) {
			    		String sensorQuery1 = "//Context[@name='infraredSensorStatus']/ContextDefinition/DefinitionContent[contains(SenseValue,'OKAY')]/SenseValue/text()";
			    		String sensorQuery2 = "//Context[@name='shockDetectorStatus']/ContextDefinition/DefinitionContent[contains(SenseValue,'OKAY')]/SenseValue/text()";
				    	expr = xpath.compile(sensorQuery1);
				    	nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				    	expr = xpath.compile(sensorQuery2);
				    	nlx = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				    	if(nl.getLength() > 0 && nlx.getLength() > 0)
				    		System.out.println("Automatic Process without Repair Will Run");
				    	else
				    		System.out.println("Automatic Process with Repair Will Run");
			    	}
			    	else
			    		System.out.println("Context Not Available...");
		    	}
		    }
		    else {
		    	String workerQuery2 = "//Context[@name='availableWorkers']/ContextDefinition/DefinitionContent[SenseValue<10]/SenseValue/text()";
		    	expr = xpath.compile(workerQuery2);
		    	nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		    	if(nl.getLength() > 0) {
		    		String sensorQuery1 = "//Context[@name='infraredSensorStatus']/ContextDefinition/DefinitionContent[contains(SenseValue,'OKAY')]/SenseValue/text()";
		    		String sensorQuery2 = "//Context[@name='shockDetectorStatus']/ContextDefinition/DefinitionContent[contains(SenseValue,'OKAY')]/SenseValue/text()";
		    		expr = xpath.compile(sensorQuery1);
		    		nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		    		expr = xpath.compile(sensorQuery2);
		    		nlx = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		    		if(nl.getLength() > 0 && nlx.getLength() > 0)
		    			System.out.println("Automatic Process without Repair Will Run");
		    		else
		    			System.out.println("Automatic Process with Repair Will Run");
		    	}
		    	else
		    		System.out.println("Context Not Available");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}          
	}
}
