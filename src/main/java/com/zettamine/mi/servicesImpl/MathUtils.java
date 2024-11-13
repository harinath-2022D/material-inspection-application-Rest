package com.zettamine.mi.servicesImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zettamine.mi.exceptions.InvalidElementException;

public class MathUtils {

	public int add(int a, int b) {
		return a + b;
	}

	public List<Integer> getListOfEvenIntegers(List<Integer> list) throws InvalidElementException {
		
		if(list.contains(-1)) {
			throw new InvalidElementException("list contains -1 ");
		}
	
		List<Integer> resp = list.stream().filter(n -> n % 2 == 0).toList();
		return resp;
	}
	
	
	public Map<Boolean, List<Integer>> separateEvenAndOdd(List<Integer> list){
		
		Map<Boolean, List<Integer>> resp = list.stream().collect(Collectors.partitioningBy(n -> n % 2 == 0));
		
		return resp;
	}
	
	
	public boolean isOdd(int num) {
		return num % 2 == 1;
	}
	
	public int divide(int a, int b) {
//		throw new RuntimeException();
		return a / b;
	}
	
	public int multiply(int a, int b) {
		return a * b;
	}
	
	public void bookTicket(int id) {
		System.out.println("ticket booked with id : " + id);
	}
}
