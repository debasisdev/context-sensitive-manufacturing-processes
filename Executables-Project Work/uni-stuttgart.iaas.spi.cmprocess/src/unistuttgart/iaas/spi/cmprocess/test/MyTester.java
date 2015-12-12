package unistuttgart.iaas.spi.cmprocess.test;

import unistuttgart.iaas.spi.cmprocess.arch.CESExecutor;

public class MyTester {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		CESExecutor cesProcess = new CESExecutor("SealAndSortPackets, highAutomation, highThroughput",
				"shockDetectorStatus, infraredSensorStatus, unitsOrdered, availableWorkers");
	}
}
