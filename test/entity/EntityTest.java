package entity;

import org.junit.Test;

import com.ikalagaming.entity.Entity;

/**
 * Tests the functionality of the entity class.
 *
 * @author Ches Burks
 *
 */
public class EntityTest {

	/**
	 * Creates several entities with the same name. Then some with different
	 * names. Then calls the garbage collection and waits for 5 seconds (should
	 * be long enough) for the old items to be collected, freeing up their
	 * names. Creates a new object with the old name that should have an id
	 * starting at 0 again.
	 */
	@Test
	public void testEntityNaming() {
		String name = "test-Entity";
		for (int i = 0; i < 10; ++i) {
			Entity tmp = new Entity(name);
			System.out.println(tmp.getName());
		}
		Entity tmp2 = new Entity("temp2");
		System.out.println(tmp2.getName());
		Entity tmp3 = new Entity("temp");
		System.out.println(tmp3.getName());
		Entity tmp4 = new Entity("temp");
		System.out.println(tmp4.getName());

		System.gc();
		try {
			Thread.sleep(5000L);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		Entity tmp5 = new Entity(name);
		System.out.println(tmp5.getName());
	}
}
