package storm.starter.step3.topwords;

import static storm.starter.ClusterHelper.localClusterWith;
import storm.starter.TopologyBuilder;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;

public class RollingTopWords {
	private static final int TOP_N = 3;
	private static final int MILLISECONDS_OFFEST = 2000;
	private static final String SPOUT_FIELD = "word";

	public static void main(final String[] args) throws Exception {
		final TopologyBuilder topology = new TopologyBuilder();

		final IRichSpout testWordSpout = new TestWordSpout();
		final IRichBolt rollingCountObjects = new RollingCountObjects(60, 10);
		final IBasicBolt rankObjects = new RankObjects(TOP_N, MILLISECONDS_OFFEST);
		final IBasicBolt mergeObjects = new MergeObjects(TOP_N, MILLISECONDS_OFFEST);

		topology.setSpout(testWordSpout, 5);
		topology.setBolt(rollingCountObjects, 4).fieldsGrouping(testWordSpout, SPOUT_FIELD);
		topology.setBolt(rankObjects, 4).fieldsGrouping(rollingCountObjects, RollingCountObjects.OBJ_FIELD);
		topology.setBolt(mergeObjects).globalGrouping(rankObjects);

		localClusterWith(topology).run();
	}
}
