package test.mock;

import core.MemoryGraph;
import core.Querier;

public class QuerierWithQuerySizeAndListener extends Querier {

	public QuerierWithQuerySizeAndListener() {
		this.memoryGraph = new MemoryGraphWithQuerySize();
	}

	public MemoryGraph getMemoryGraph() {
		return this.memoryGraph;
	}

}
