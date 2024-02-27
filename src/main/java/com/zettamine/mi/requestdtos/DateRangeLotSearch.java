package com.zettamine.mi.requestdtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DateRangeLotSearch {
	
	@NotNull(message = "please provide valid date format of yyyy-mm-dd")
	private LocalDate fromDate;
	
	@NotNull(message = "please provide valid date format of yyyy-mm-dd")
	private LocalDate toDate;
	
	private String materialId;
	
	private int vendorId;
	
	private String plantId;
	
	private String status;
}
