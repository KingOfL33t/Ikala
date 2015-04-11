package crypto;

import org.junit.Test;

import com.ikalagaming.crypto.Hasher;

/**
 * Contains tests for the hashing algorithm.
 *
 * @author Ches Burks
 *
 */
public class HasherTest {
	/**
	 * Creates a hash for passwords and tests collisions and matching.
	 */
	@Test
	public void testPasswordHashing() {
		try {
			// Print out 10 hashes
			for (int i = 0; i < 10; i++) {
				System.out.println(Hasher.createHash("p\r\nassw0Rd!"));
			}

			// Test password validation
			boolean failure = false;
			System.out.println("Running tests...");
			for (int i = 0; i < 500; ++i) {
				String password = "" + i;
				String hash = Hasher.createHash(password);
				String secondHash = Hasher.createHash(password);
				if (hash.equals(secondHash)) {
					System.out.println("FAILURE: TWO HASHES ARE EQUAL!");
					failure = true;
				}
				String wrongPassword = "" + (i + 1);
				if (Hasher.validatePassword(wrongPassword, hash)) {
					System.out.println("FAILURE: WRONG PASSWORD ACCEPTED!");
					failure = true;
				}
				if (!Hasher.validatePassword(password, hash)) {
					System.out.println("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
					failure = true;
				}
			}
			if (failure) {
				System.out.println("TESTS FAILED!");
			}
			else {
				System.out.println("TESTS PASSED!");
			}
		}
		catch (Exception ex) {
			System.out.println("ERROR: " + ex);
		}
	}

}
