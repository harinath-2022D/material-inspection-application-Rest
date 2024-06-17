package com.zettamine.mi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zettamine.mi.entities.Employee;
import com.zettamine.mi.mapper.EmployeeMapper;
import com.zettamine.mi.repositories.EmployeeRepository;
import com.zettamine.mi.responsedtos.EmployeeDTO;

@RestController
@RequestMapping("/emp")
public class EmpController {
	
	
	private EmployeeRepository employeeRepository;
	
	
//	private EmployeeMapper empMapper ;
	
	public EmpController(EmployeeRepository empRepo) {
		this.employeeRepository = empRepo;
	}
	
	@GetMapping("/find")
	public EmployeeDTO getEmployee(@RequestParam Integer id) {
		
		return new EmployeeDTO();
	}
	
	@PostMapping("/save")
	public String saveEmployee(@RequestBody EmployeeDTO dto) {
		
		Employee emp = EmployeeMapper.INSTANCE.convertEmployeeDTOToEntity(dto);
		
		employeeRepository.save(emp);
		
		return "saved";
	}

}
