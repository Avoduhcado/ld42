package com.avogine.ld42.system;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AvoEventQueue {

	private static Set<Runnable> laterEvents = new HashSet<>();
		
	public static void doLater(Runnable run) {
		laterEvents.add(run);
	}
	
	public static void processEvents() {
		for(Iterator<Runnable> e = laterEvents.iterator(); e.hasNext();) {
			Runnable r = e.next();
			r.run();
			e.remove();
		}
	}
	
}
