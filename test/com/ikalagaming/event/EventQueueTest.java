package com.ikalagaming.event;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;
/**
 * Used for testing the Game class.
 * @author Ches Burks
 *
 */

import com.ikalagaming.core.IQueue;
/**
 * Tests the IQueue using events
 * @author Ches Burks
 *
 */
@SuiteClasses({IQueue.class})
public class EventQueueTest {

	/**
	 * Ensures the game can be set up properly
	 */
	@Test
	public void testInit(){
		IQueue<Event> queue = new IQueue<Event>();

		assertNotNull("queue is null", queue);
		assertNotNull("no iterator exists", queue.iterator());
	}

	/**
	 * Preforms a variety of tests adding and removing an event. One event
	 * is in the queue at a time.
	 */
	@Test
	public void testOneElement(){
		IQueue<Event> queue = new IQueue<Event>();
		Event event = new TestEvent("Testing");
		Event[] tmpArray = new Event[0];

		try {
			queue.peek();
		} catch (Exception e) {
			org.junit.Assert.fail("Error peeking in an empty queue");
		}
		try {
			queue.poll();
		} catch (Exception e) {
			org.junit.Assert.fail("Error polling in an empty queue");
		}

		try {
			queue.add(event);
		} catch (Exception e) {
			org.junit.Assert.fail("Error adding event to queue");
		}
		assertNotNull("array is null", queue.toArray());
		assertNotNull("tmparray is null", queue.toArray(tmpArray));
		assertNotNull("peek element is null", queue.peek());

		Event popped = queue.poll();
		assertNotNull("poll element is null", popped);

		try {
			queue.add(event);
		} catch (Exception e) {
			org.junit.Assert.fail("Error adding event to queue");
		}

		popped = null;

		try {
			popped = queue.remove();
		} catch (Exception e) {
			org.junit.Assert.fail("Error adding event to queue");
		}
		assertNotNull("remove element is null", popped);

	}

	/**
	 * Preforms a variety of tests adding and removing an event. One event
	 * is in the queue at a time.
	 */
	@Test
	public void testMultipleElements(){
		IQueue<Event> queue = new IQueue<Event>();
		Event event = new TestEvent("Testing");
		Event event2 = new TestEvent("Testing");
		Event[] tmpArray = new Event[0];
		try {
			queue.add(event);
			queue.add(event2);
			queue.add(event);
			queue.add(event);
			queue.add(event);
			queue.add(event2);
		} catch (Exception e) {
			org.junit.Assert.fail("Error adding event to queue");
		}
		assertNotNull("array is null", queue.toArray());
		assertNotNull("tmparray is null", queue.toArray(tmpArray));
		assertNotNull("peek element is null", queue.peek());

		Event popped = queue.poll();
		assertNotNull("poll element is null", popped);

		popped = null;
		try {
			popped = queue.remove();
		} catch (Exception e) {
			org.junit.Assert.fail("Error adding event to queue");
		}
		assertNotNull("remove element is null", popped);

		while(!queue.isEmpty()){
			assertNotNull("poll element is null", queue.poll());
		}
	}

	/**
	 * Tries to max out the array.
	 */
	@Test
	public void testMaxingOutArray(){
		IQueue<Event> queue = new IQueue<Event>();
		int maxArraySize = 8192;
		int i;
		TestEvent tmp;
		try{
			for (i = 0; i < maxArraySize; ++i){
				queue.add(new TestEvent("evt"+i));//this should be valid
			}
		} catch (Exception e) {
			org.junit.Assert.fail("Error adding max amount of items");
		}
		while(!queue.isEmpty()){
			tmp = (TestEvent) queue.poll();//empty queue
			assertNotNull("poll element is null", tmp);
		}

		try{
			for (i = 0; i <= maxArraySize + 20; ++i){
				if (queue.offer(new TestEvent("evt"+i))){
					if (i > maxArraySize){
						org.junit.Assert.fail("offer " + i + "/" + maxArraySize
								+ " should have failed");
					}
				}
				else{
					if (i < maxArraySize){
						org.junit.Assert.fail("offer " + i + "/" + maxArraySize
								+ " should not have failed");
					}
				}
			}
		} catch (Exception e) {
			org.junit.Assert.fail("Error adding max amount of items");
		}
		int count = 0;
		while(!queue.isEmpty()){
			tmp = (TestEvent) queue.poll();//empty queue
			++count;
			assertNotNull("poll element is null", tmp);
		}
		org.junit.Assert.assertEquals("Count incorrect size ("
		+ count + ")",
				count, maxArraySize);


	}

}
