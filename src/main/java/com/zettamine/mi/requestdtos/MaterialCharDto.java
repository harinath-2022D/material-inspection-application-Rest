package com.zettamine.mi.requestdtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaterialCharDto {
	@Size(min = 5, max = 256, message = "characteristic description should be greater than 5 char and less than 256 char")
	private String charDesc;
	
	@NotNull(message = "invalid upper tolerance limit")
	private double utl;
	
	@NotNull(message = "invalid lower tolerance limit")
	private double ltl;
	
	@NotBlank(message = "unit of measurement should not be empty")
	private String uom;
	
	@NotBlank(message = "invalid material id")
	private String matId;
}
