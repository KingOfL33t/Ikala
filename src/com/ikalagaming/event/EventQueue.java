package com.ikalagaming.event;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * The queue that events are placed in pending dispatch.
 * @author Ches Burks
 *
 */
public class EventQueue implements Queue<Event>{

	/**Contains all of the data used in the queue**/
	private Event[] array;
	/**How many events are in the queue**/
	private int size = 0;
	/**Index of the next item that will be fetched**/
	private int head = 0;
	/**Index of the last item added**/
	private int tail = 0;
	/**
	 * The percentage, represented from 0 to 100 as an int,
	 * that should be unused in the array before the contents are
	 * shifted to the beginning instead of resizing the array upon
	 * adding an element.
	 **/
	private int shiftPercentageUnused = 50;
	/**The maximum number of slots that can be allocated*/
	private final int maxArraySize = 8192;

	/**
	 * Doubles the size of the array and copies the new values over
	 * to the new array. If the array has a size of zero, it will set the
	 * size to one before doubling (resulting in a size of 2).
	 *
	 * @return true if resizing was successful, false if there was an error
	 */
	private synchronized boolean doubleSize(){
		try {
			if (size == 0){
				++size;
			}

			Event[] tmp = new Event[size*2];

			/* Copy from array (old data) starting at the old head
			 * position to the new array tmp, beginning at the index 0 of tmp, of
			 * length (tail-head).
			 */
			System.arraycopy(array, head, tmp, 0, tail - head);

			//set array to the new array
			array = tmp;
			tmp = null;

			size *= 2;
			//shift the head and tail over to the start
			tail = tail - head;
			head = 0;
			return true;
		}
		catch (Exception e){
			return false;
		}
	}

	/**
	 * Takes the head and tail and shifts the data to the
	 * beginning of the array.
	 *
	 * @return true on success, false for failure
	 */
	private synchronized boolean shiftToStart(){
		try{
			/* Copy from array starting at the old head
			 * position to itself beginning at the 0 index,
			 * total length equaling (tail-head)
			 */
			System.arraycopy(array, head, array, 0, tail - head);

			//set head and tail to their new values
			tail -= head;
			head = 0;
			return true;
		}
		catch (Exception e){
			return false;
		}
	}

	/**
	 * Returns true if there is enough free space
	 * left to shift the array to the start instead
	 * of doubling size. Returns false if there is not
	 * enough space to add all elements. Only shifts if
	 * a certain percent of the array is free space.
	 * <p>
	 * This should be called before adding elements.
	 *
	 *@param elementsToAdd the number of elements to be added
	 * @return true if the array needs to be shifted, false otherwise
	 */
	private boolean shouldShift(int elementsToAdd){
		//not enough room
		if (getTotalFreeElements() < elementsToAdd){
			return false;
		}
		if ((size-(tail - head))/size >= shiftPercentageUnused){
			return true;
		}
		return false;
	}

	/**
	 * Returns the number of free elements in
	 * the array. These can be before or after
	 * the block of elements currently being used.
	 *
	 * @return total free spaces available
	 */
	private int getTotalFreeElements(){
		return getFreeElementsAtBeginning()+getFreeElementsAtEnd();
	}

	/**
	 * Returns the number of free elements between
	 * the tail and the end of the array.
	 *
	 * @return free spaces available after tail
	 */
	private int getFreeElementsAtEnd(){
		return size-tail;
	}

	/**
	 * Returns the number of free elements between
	 * the beginning of the array and the head element.
	 *
	 * @return free spaces available before head
	 */
	private int getFreeElementsAtBeginning(){
		return (head-1);
	}

	/**
	 * Adds all of the events in the given Collection to the
	 * end of the queue.
	 * Elements are added in the order they appear in the supplied
	 * collection.
	 *
	 * @param events A list of events to add to the queue.
	 * @return true on success, false in the event of a failure
	 */
	@Override
	public boolean addAll(Collection<? extends Event> events) {
		//we don't need to do anything if the collections empty
		if (events.isEmpty()){
			return true;
		}
		if (events.size() > maxArraySize){
			return false;
		}
		else if (events.size() == maxArraySize && !isEmpty()){
			return false;
		}
		//shift the array and resize if necessary
		if (shouldShift(events.size())){
			shiftToStart();
		}
		while (getFreeElementsAtEnd() < events.size()){
			doubleSize();
		}

		//copy elements over
		try {
			Object[] eventArray = events.toArray();
			for (int i = 0; i < eventArray.length; i++){
				array[i+head] = (Event) eventArray[i];
			}
			return true;
		}
		catch (Exception e){
			return false;//it failed copying
		}
	}

	/**
	 * Removes all of the elements from this collection (optional operation).
	 * The collection will be empty after this method returns.
	 * This does not actually clear out the data, only resets the head and tail
	 * positions.
	 */
	@Override
	public void clear() {
		head = 0;
		tail = 0;
	}

	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Returns true if there are no more events
	 * left to be processed. Note that this will
	 * return true if there is data stored in the
	 * queue which has already been fetched and
	 * is not going to be used again, even though
	 * the data structure is not actually empty.
	 *
	 * @return false if there are unprocessed events,
	 * true otherwise
	 */
	@Override
	public synchronized boolean isEmpty() {
		//last event has been used
		if (head>tail){
			return true;
		}
		//not a valid index for head
		if (head <= -1){
			return true;
		}
		//edge case, this should not happen
		if (tail <= -1){
			return true;
		}
		return false;
	}

	@Override
	public Iterator<Event> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Returns an array containing only valid
	 * events yet to be sent.
	 *
	 * @return the events that have yet to be dispatched
	 */
	@Override
	public Object[] toArray() {
		//TODO finish
		Object[] toReturn = new Object[tail-head];
		for (int i = head; i <= tail; i++){
			toReturn[i-head] = array[i];
		}
		return toReturn;

	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Event arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Event element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean offer(Event arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Event peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Event poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Event remove() {
		// TODO Auto-generated method stub
		return null;
	}

}
