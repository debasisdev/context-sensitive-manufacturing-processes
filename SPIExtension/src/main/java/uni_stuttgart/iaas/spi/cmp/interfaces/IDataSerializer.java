package uni_stuttgart.iaas.spi.cmp.interfaces;

import org.apache.camel.Exchange;

/**
 * A generic interface for serialization of TOSCA [CMP/IPSM] definitions such that 
 * it can be used over RabbitMQ-Camel routes.
 * @author Debasis Kar
 */

public interface IDataSerializer {
	/**
	 * Any custom {@link Processor} that needs to exchange message over RabbitMQ must implement 
	 * the following method that will retrieve {@link Exchange} object from the Apache {@link CamelContext} 
	 * and will serialize the required output in a array of bytes which will be forwarded to the 
	 * messaging system.
	 * @author Debasis Kar
	 * @param Exchange
	 * @return byte[]
	 */
	public byte[] getSerializedOutput(Exchange exchange);
}
