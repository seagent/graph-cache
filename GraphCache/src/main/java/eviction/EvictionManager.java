package eviction;

public class EvictionManager {
	private Evicter evicter;

	public EvictionManager(Evicter evicter) {
		super();
		this.evicter = evicter;
	}

	public void evict() {
		Thread evitionThread = new Thread(evicter);
		evitionThread.start();
	}

}
