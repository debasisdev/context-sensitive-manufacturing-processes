package unistuttgart.iaas.spi.cmprocess.test;

import unistuttgart.iaas.spi.cmprocess.arch.ContextAnalyzer;
import unistuttgart.iaas.spi.cmprocess.arch.IntentionAnalyzer;
import unistuttgart.iaas.spi.cmprocess.arch.QueryManager;
import unistuttgart.iaas.spi.cmprocess.cmp.TIntention;
import unistuttgart.iaas.spi.cmprocess.cmp.TIntentions;

public class MyTester {

	public static void main(String[] args) throws Exception {
		TIntention mainIntention = new TIntention();
		mainIntention.setDocumentation("The primary goal/intention is to seal and sort the packets as per the business rule.");
		mainIntention.setName("SealAndSortPackets");
		mainIntention.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v1/packaging");
		TIntention subIntention1 = new TIntention();
		subIntention1.setDocumentation("Work must be automated more to gain higher productivity.");
		subIntention1.setName("highAutomation");
		subIntention1.setTargetNamespace("http://www.uni-stuttgart.de/iaas/cmp/v1/packaging");
		TIntention subIntention2 = new TIntention();
		subIntention2.setDocumentation("Work must be done in such a way that no employees sit idle in the premises of company.");
		subIntention2.setName("highThroughput");
		subIntention2.setTargetNamespace("Throughput time must be increased to remain competitive in the market.");
		TIntentions subIntentions = new TIntentions();
		subIntentions.getIntention().add(subIntention1);
		subIntentions.getIntention().add(subIntention2);
		mainIntention.setSubIntentions(subIntentions);
		
		QueryManager qum = new QueryManager("HowToSealAndSort",mainIntention);
		ContextAnalyzer can = new ContextAnalyzer(qum);
		IntentionAnalyzer ian = new IntentionAnalyzer(can,qum);
		System.out.println("Process Selected: "+ ian.getIntentionAnalysisProcessList());
	}
}
