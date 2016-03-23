package uni_stuttgart.iaas.spi.cmp.interfaces;

import org.apache.camel.Exchange;

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
	 * @param exchange
	 * @return byte[]
	 */
	public byte[] getSerializedOutput(Exchange exchange);
}
