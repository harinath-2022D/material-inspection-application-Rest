package com.zettamine.mi.servicesImpl;

import static org.junit.Assume.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.zettamine.mi.exceptions.InvalidElementException;

@TestInstance(TestInstance.Lifecycle.PER_METHOD) // Default
@DisplayName("When running MathUtils.Class")
class MathUtilsTest {
	
	private MathUtils obj;
	
	private List<Integer> list;
	
	private TestInfo testInfo;
	private TestReporter testReporter;  // used for getting the current executing test method and other details about test method
	
	@BeforeEach
	private void setUp(TestInfo testInfo, TestReporter testReporter) throws Exception {
		obj = new MathUtils();
		list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, -1);
		this.testInfo = testInfo;
		this.testReporter = testReporter;
		
		testReporter.publishEntry("Running " + testInfo.getDisplayName() + " Method Name : " + testInfo.getTestMethod());
	}

	@AfterEach
	private void tearDown() throws Exception {
		obj = null;
	}
	
	@BeforeAll
	static void beforeAllSetUp() {  // static because these are runs before initializing instance of mathutilstest class
		System.out.println("Executing before all");
	}
	
	@AfterAll
	static void afterAllSetUp() {
		System.out.println("executing after all");
	}

	@Test
	void test() {
		int expected = 2;
		int actual = obj.add(1, 1);
		assertEquals(expected, actual, "The add method should add two numbers");
	}
	
	@Test
	void test_GetEvenIntegers() {
		List<Integer> expected = List.of(2, 4, 6);
		List<Integer> actual;
		try {
			
			actual = obj.getListOfEvenIntegers(list);
			assertEquals(expected, actual, "Expecting only even numbers");
			
		} catch (Exception e) {
			
			assertTrue(e instanceof InvalidElementException);
		}
		
	}
	
	
	@Test
	@Order(3)
	@DisplayName("NEGATIVE_TEST_EXCEPTION")
	void test_GetEvenIntegers_Case2() {	
		assertThrows(InvalidElementException.class, () -> obj.getListOfEvenIntegers(list));
	}
	
	@Test
	@RepeatedTest(value = 2, name = "{displayName} - repetition {currentRepetition} / {totalRepetitions}")
	@DisplayName("REPEATED_TEST")
	@Order(-1)
	void test_SeperateEvenAndOdd() {
		Map<Boolean, List<Integer>> expected = Map.of(false, List.of(1, 3, 5, 7, -1), true, List.of(2, 4, 6));
		Map<Boolean, List<Integer>> actual = obj.separateEvenAndOdd(list);
		
		assertEquals(expected, actual);
		
	}
	
	@ParameterizedTest
	@ValueSource(ints = {1, 5, 3})
	void test_IsOdd_ParameteredTest(int num) {
		assertTrue(obj.isOdd(num));
	}
	
	@Test
	@DisplayName("ASSUME_TEST")
	void test_Divide() {
		int expected = 2;
		int actual = obj.divide(4, 2);
		
		boolean isServerUp = true;
		assumeTrue(isServerUp); // conditional assumptions if assumption fail junit ignores this test
		
		assertEquals(expected, actual);
	}
	
	@Test
	@DisplayName("multiply method")
	void test_multiply() {
		assertAll(                     // runs multiple test cases 
				() -> assertEquals(4, obj.multiply(2, 2)),
				() -> assertEquals(0, obj.multiply(2, 0))
				);
	}
	
	
	@Nested
	@DisplayName("Testing for add method")
	@Tag("Sum")
	class AddTwoNumbers{
		
		@Test
		@DisplayName("when adding two positive numbers")
		void test_add_case_1() {
//			System.out.println("Running " + testInfo.getDisplayName() + " Method Name : " + testInfo.getTestMethod());
			assertEquals(4, obj.add(2, 2), "should return right sum");
		}
		
		@Test
		@DisplayName("when adding two negative numbers")
		void test_add_case_2() {
			int expected = 0;
			int actual = obj.add(2, 1);
			assertEquals(expected, actual, () -> "should return sum " + expected+ " but actual is " + actual);
		}
		
		
		
	}
	
	
	@Test
	void testBookTicketReturnTypeIsVoid() {
		MathUtils math = mock(MathUtils.class);
		
		math.bookTicket(2);
		verify(math).bookTicket(2);
	}
	
	
	
	
	


}
