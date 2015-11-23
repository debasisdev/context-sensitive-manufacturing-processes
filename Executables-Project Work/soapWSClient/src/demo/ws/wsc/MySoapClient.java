package demo.ws.wsc;

import javax.xml.soap.*;

public class MySoapClient {

    public static void main(String args[]) throws Exception {
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        // Send SOAP Message to SOAP Server - SEAL/PREV/REPAIR/INSTALL/MAINTAIN/OPTIMIZE/COMPLETE
        String url = "http://localhost:8080/automatedWebService/services/automatedWebServiceMainPort";
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest("complete"), url);
        // System.out.println("Response SOAP Message:");
        // soapResponse.writeTo(System.out);
        SOAPBody sb = soapResponse.getSOAPBody();
        System.out.println(sb.getFirstChild().getFirstChild().getTextContent());
        soapConnection.close();
    }

    private static SOAPMessage createSOAPRequest(String param) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "http://ws.debasis.org/";
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("ws", serverURI);
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("automate", "ws");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("arg0");
        
        /*
          Constructed SOAP Request Message:
	        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
	        xmlns:ws="http://ws.debasis.org/">
	   		<soapenv:Header/>
	   		<soapenv:Body>
	      		<ws:automate>
	         		<arg0>seal</arg0>
	      		</ws:automate>
	   		</soapenv:Body>
			</soapenv:Envelope>
         */
        
        //Parameter
        soapBodyElem1.addTextNode(param);
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "automate");
        soapMessage.saveChanges();

        // System.out.println("Request SOAP Message:");
        // soapMessage.writeTo(System.out);
        return soapMessage;
    }

}