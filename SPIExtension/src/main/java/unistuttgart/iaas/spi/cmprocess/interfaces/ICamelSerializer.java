package unistuttgart.iaas.spi.cmprocess.interfaces;

import org.apache.camel.Exchange;

/**
 * A Generic Interface for Serialization of TOSCA Definitions such that It can be used over RabbitMQ-Camel Routes.
 * @author Debasis Kar
 */

public interface ICamelSerializer {
	/**
	 * Any Custom Analyzer must implement the following method that will retrieve the TProcessDefinitions as
	 * Exchange object from the Apache CamelContext and will serialize the required output in a array of bytes 
	 * that will be forwarded to the Messaging System.
	 * @author Debasis Kar
	 * @param Exchange
	 * @return byte[]
	 */
	public byte[] getSerializedOutput(Exchange exchange);
}
