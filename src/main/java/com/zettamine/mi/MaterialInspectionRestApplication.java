package com.zettamine.mi;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/*
 * developed by : Harinath Akkati ZM-284
 */
@SpringBootApplication
@EnableTransactionManagement
@OpenAPIDefinition(info = @Info(title = "Material Inspection Service", 
                                description = "Capturing Inspection Actuals details.",
                                contact = @Contact(name = "Harinath Akkati", 
                                email = "harinath.a@zettamine.in")))
public class MaterialInspectionRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaterialInspectionRestApplication.class, args);
		
		printMaterialDTOMapper();
	}

	private static void printMaterialDTOMapper() {
		
		
	}
	
	static {
		int  abc = 10;
	}

}
