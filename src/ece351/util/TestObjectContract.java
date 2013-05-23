package ece351.util;
import java.util.Random;

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
				return false;
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
				return true;
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
				flag = !flag;
				return flag;
			}
		};
	}
	
	/**
	 * Returns false if obj.equals(null) is true.
	 * @param obj
	 */
	@Override
	boolean checkNotEqualsNull(final Object obj) {
		return !obj.equals(null);
	}

	/**
	 * Returns true if obj.equals(obj) is true.
	 * @param obj
	 */
	@Override
	boolean checkEqualsIsReflexive(final Object obj) {
		return obj.equals(obj);
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
		return (o1.equals(o2) && o2.equals(o1))||(!o1.equals(o2) && !o2.equals(o1));
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
		return (o1.equals(o2) && o2.equals(o3) && o3.equals(o1));
	}



	/**
	 * Construct three Integer objects by hand and see if the 
	 * object contract checks pass.
	 */
	@Test
	public void testBasic() {
		TestObjectContract t = new TestObjectContract();
		assertTrue(t.checkEqualsIsSymmetric(1,1));
		assertTrue(t.checkEqualsIsTransitive(2,2,2));
		assertTrue(t.checkEqualsIsReflexive(1));
		assertFalse(t.checkEqualsIsSymmetric(0,1));
		assertFalse(t.checkEqualsIsTransitive(0,1,2));
		
	}

	/**
	 * Write a loop to randomly construct 10,000 Integer objects and
	 * check the object contract properties on them.
	 */
	@Test
	public void testRandom() {
		int[] ints = new int[10000];
		for(int i = 0; i < 10000;i++){
			ints[i] = 1;
		}
		TestObjectContract t = new TestObjectContract();
		Random rgen = new Random( 10101010 );
		int count = 0;
		while(count++ < 10000){
			int index1 = rgen.nextInt()%10000;
			int index2 = rgen.nextInt()%10000;
			int index3 = rgen.nextInt()%10000;
			if(index1 < 0) index1 *= -1;
			if(index2 < 0) index2 *= -1;
			if(index3 < 0) index3 *= -1;
			assertTrue(t.checkEqualsIsSymmetric(ints[index1],ints[index2]));
			assertTrue(t.checkEqualsIsTransitive(ints[index1],ints[index2],ints[index3]));
			assertTrue(t.checkEqualsIsReflexive(ints[index1]));
		}
	}


}
