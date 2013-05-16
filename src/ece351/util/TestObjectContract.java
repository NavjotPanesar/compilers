package ece351.util;


import static org.junit.Assert.*;

import org.junit.Test;

public final class TestObjectContract extends TestObjectContractBase {

	/**
	 * Construct an object who's equals method always returns false.
	 */
	@Override
	public Object constructAlwaysFalse() {
		// this is an anonymous inner class that is a subclass of Object
		return new Object() {
			public boolean equals(Object ob) { 
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
			}
		};
	}
	
	/**
	 * Construct an object who's equals method always returns true.
	 */
	@Override
	public Object constructAlwaysTrue() {
		// this is an anonymous inner class that is a subclass of Object
		return new Object() {
			public boolean equals(Object ob) { 
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
			}
		};
	}
	
	/**
	 * Construct an object who's equals method alternates between returning true and false.
	 */
	@Override
	public Object constructToggler() {
		// this is an anonymous inner class that is a subclass of Object
		return new Object() {
			private boolean flag = true;
			public boolean equals(Object ob) { 
// TODO: 2 lines snipped
throw new ece351.util.Todo351Exception();
			}
		};
	}
	
	/**
	 * Returns false if obj.equals(null) is true.
	 * @param obj
	 */
	@Override
	boolean checkNotEqualsNull(final Object obj) {
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
	}

	/**
	 * Returns true if obj.equals(obj) is true.
	 * @param obj
	 */
	@Override
	boolean checkEqualsIsReflexive(final Object obj) {
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
	}

	/**
	 * Returns true if both are equals, or if both are not equals.
	 * Returns false if one equals the other but not vice versa.
	 * @param o1
	 * @param o2
	 * @return
	 */
	@Override
	boolean checkEqualsIsSymmetric(final Object o1, final Object o2) {
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
	}

	/**
	 * Returns true if all three objects are equals to each other.
	 * This isn't the most complete test of transitivity, but it will do.
	 * @param o1
	 * @param o2
	 * @param o3
	 */
	@Override
	boolean checkEqualsIsTransitive(final Object o1, final Object o2, final Object o3) {
// TODO: 1 lines snipped
throw new ece351.util.Todo351Exception();
	}



	/**
	 * Construct three Integer objects by hand and see if the 
	 * object contract checks pass.
	 */
	@Test
	public void testBasic() {
		fail("Not yet implemented");
	}

	/**
	 * Write a loop to randomly construct 10,000 Integer objects and
	 * check the object contract properties on them.
	 */
	@Test
	public void testRandom() {
		fail("Not yet implemented");
	}


}
