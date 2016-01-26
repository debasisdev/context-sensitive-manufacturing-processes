package org.demo.cmp.exec;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

/**
 * A Dummy SOAP Client to ease Other Classes in the Package to Send SOAP Request and get SOAP Response.
 * @author Debasis Kar
 */

public class DummySoapClient {
	
	/**Local Log Writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = Logger.getLogger(DummySoapClient.class.getName());
	
	/**This method facilitates calling the Web Service with a SOAP Message, thus it parses through the
	 * SOAP Response it receives and Returns it as a plain text.
	 * @author Debasis Kar
	 * @param String
	 * @return String
	 * */
	public static String SOAPWebServiceCall(String param) {
    	SOAPBody sb = null;
		try {
	        //Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			//Send SOAP Message to SOAP Server - SEAL/PREV/REPAIR/INSTALL/MAINTAIN/OPTIMIZE/COMPLETE
	        String url = "http://localhost:8080/automatedWebService/services/automatedWebServiceMainPort";
	        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(param), url);
	        soapResponse.writeTo(System.err);
	        System.err.println();
	        //Retrieve SOAP Response Body
	        sb = soapResponse.getSOAPBody();
	        soapConnection.close();
		} catch (SOAPException e) {
			log.severe("DSCL03: SOAPException has Occurred.");
		} catch (IOException e) {
			log.severe("DSCL02: IOException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("DSCL01: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("DSCL00: Unknown Exception has Occurred - " + e);
		}
		//Retrieve the Response
		if (sb.getFirstChild().getFirstChild().getTextContent().trim().length()>0)
			return sb.getFirstChild().getFirstChild().getTextContent();
		else
			return null;
    }
	
	/**This method will create a SOAP Message along with its envelope and header details for the endpoint.
	 * @author Debasis Kar
	 * @param String
	 * @return SOAPMessage
	 * */
	private static SOAPMessage createSOAPRequest(String param) {
    	SOAPMessage soapMessage = null;
		try {
			//Creating SOAP Envelope, Message
			MessageFactory messageFactory = MessageFactory.newInstance();
			soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();
	        String serverURI = "http://ws.debasis.org/";
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("ws", serverURI);
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement soapBodyElem = soapBody.addChildElement("automate", "ws");
	        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("arg0");
	        //Parameter Setting of SOAP Header
	        soapBodyElem1.addTextNode(param);
	        MimeHeaders headers = soapMessage.getMimeHeaders();
	        headers.addHeader("SOAPAction", "automate");
	        soapMessage.saveChanges();
	        soapMessage.writeTo(System.err);
	        System.err.println();
		} catch (SOAPException e) {
			log.severe("DSCL13: SOAPException has Occurred.");
		} catch (IOException e) {
			log.severe("DSCL12: IOException has Occurred.");
		} catch (NullPointerException e) {
			log.severe("DSCL11: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.severe("DSCL10: Unknown Exception has Occurred - " + e);
		}
		return soapMessage;   
    }
}