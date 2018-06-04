package de.webtwob.agd.project.service.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import de.webtwob.agd.project.service.api.GraphUpdateEvent;
import de.webtwob.agd.project.service.api.IGraphUpdateEventHandler;
import de.webtwob.agd.project.service.api.IGraphUpdateEventQueue;

public class GraphUpdateEventQueue implements IGraphUpdateEventQueue {

	public Set<IGraphUpdateEventHandler> subscriberList = new HashSet<>();

	public Queue<GraphUpdateEvent> eventQueue = new LinkedList<>();
	
	Thread eventThread;

	public GraphUpdateEventQueue() {
		eventThread = new Thread(this::runHandler);
		eventThread.setDaemon(true);
		eventThread.setName("GraphUpdateEventThread");
		eventThread.start();
	}

	@Override
	public void subscribeToEventQueue(IGraphUpdateEventHandler igueh) {
		synchronized (subscriberList) {
			subscriberList.add(igueh);
		}
	}

	@Override
	public void unsubscribeFromEventQueue(IGraphUpdateEventHandler igueh) {
		synchronized (subscriberList) {
			subscriberList.remove(igueh);
		}
	}

	@Override
	public void publishEvent(GraphUpdateEvent gue) {
		synchronized (eventQueue) {
			if(eventQueue.isEmpty()) {
				//wake up the eventThread if there where no events in the queue
				eventThread.interrupt();
			}
			eventQueue.add(gue);
			
		}
	}

	private void runHandler() {
		while (true) {
			if (eventQueue.isEmpty()) {
				synchronized (eventQueue) {
					while (eventQueue.isEmpty()) {
						try {
							eventQueue.wait();
						} catch (InterruptedException ignore) {

						}
					}
				}
			}
			final GraphUpdateEvent event;

			synchronized (eventQueue) {
				event = eventQueue.poll();
			}

			synchronized (subscriberList) {
				subscriberList.forEach(s -> s.graphUpdate(event));
			}
		}
	}

}
