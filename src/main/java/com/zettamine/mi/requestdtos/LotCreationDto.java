package com.zettamine.mi.requestdtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LotCreationDto {
	@NotBlank(message = "matId should not be empty")
	private String matId;
	
	@NotBlank(message = "matId should not be empty")
	private String plantId;
	
	@NotNull(message = "invalid vendor id")
	private int vendorId;
	
	@NotNull(message = "please provide valid date format of yyyy-mm-dd")
	private LocalDate stDt;
	
	@NotNull(message = "please provide valid date format of yyyy-mm-dd")
	private LocalDate crDt;
}
