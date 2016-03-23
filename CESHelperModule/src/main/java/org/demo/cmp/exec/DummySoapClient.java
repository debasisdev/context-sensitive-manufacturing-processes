package org.demo.cmp.exec;

import java.io.IOException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Copyright 2016 Debasis Kar
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**/

/**
 * A dummy SOAP client to ease other classes in the package to send SOAP request and get SOAP response.
 * @author Debasis Kar
 */

public class DummySoapClient {
	
	/**Local log writer
	 * @author Debasis Kar
	 * */
	private static final Logger log = LoggerFactory.getLogger(DummySoapClient.class);
	
	/**This method facilitates calling the Web Service with a SOAP message, thus it parses through the
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
	        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(param), Settings.AUTOSERVICE_URI);
	        soapResponse.writeTo(System.err);
	        System.err.println();
	        //Retrieve SOAP Response Body
	        sb = soapResponse.getSOAPBody();
	        soapConnection.close();
		} catch (SOAPException e) {
			log.error("DSCL03: SOAPException has Occurred.");
		} catch (IOException e) {
			log.error("DSCL02: IOException has Occurred.");
		} catch (NullPointerException e) {
			log.error("DSCL01: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.error("DSCL00: Unknown Exception has Occurred - " + e);
		}
		//Retrieve the Response
		if (sb.getFirstChild().getFirstChild().getTextContent().trim().length()>0)
			return sb.getFirstChild().getFirstChild().getTextContent();
		else
			return null;
    }
	
	/**This method will create a SOAP message along with its envelope and header details for the end-point.
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
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration(Settings.SOAP_FIELD_SER, Settings.SERVICE_NAMESPACE);
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement soapBodyElem = soapBody.addChildElement(Settings.SOAP_FIELD_AUTOMATE, Settings.SOAP_FIELD_SER);
	        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(Settings.SOAP_FIELD_TASK);
	        //Parameter Setting of SOAP Header
	        soapBodyElem1.addTextNode(param);
	        soapMessage.saveChanges();
	        soapMessage.writeTo(System.err);
	        System.err.println();
		} catch (SOAPException e) {
			log.error("DSCL13: SOAPException has Occurred.");
		} catch (IOException e) {
			log.error("DSCL12: IOException has Occurred.");
		} catch (NullPointerException e) {
			log.error("DSCL11: NullPointerException has Occurred.");
		} catch (Exception e) {
			log.error("DSCL10: Unknown Exception has Occurred - " + e);
		}
		return soapMessage;   
    }
}