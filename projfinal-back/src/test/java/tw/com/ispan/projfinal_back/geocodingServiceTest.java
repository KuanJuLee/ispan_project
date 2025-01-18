package tw.com.ispan.projfinal_back;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import tw.com.ispan.service.pet.GeocodingService;
import tw.com.ispan.util.LatLng;

@SpringBootTest
public class geocodingServiceTest {

	@Autowired
	private GeocodingService geocodingService;

	@Test
	public void testGetCoordinatesFromAddress() throws JsonMappingException, JsonProcessingException {

		String address = "新北市蘆洲區長安街99巷";

		String adresscode = "https://maps.googleapis.com/maps/api/geocode/json?address=%E6%96%B0%E5%8C%97%E5%B8%82%E8%98%86%E6%B4%B2%E5%8D%80%E9%95%B7%E5%AE%89%E8%A1%97&key=AIzaSyBYgeaSJSbLteuiSPtVAgdIV-yPLM4Av-c";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		headers.set("Content-Type", "application/json");
		headers.set("Referer", "http://localhost:8080");
		headers.add("Accept-Language", "zh-TW");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(adresscode,
				HttpMethod.GET, entity, String.class);
		// ResponseEntity<String> response = restTemplate.getForEntity(,
		// String.class);
		System.out.println("Response: " + response.getBody());
		System.out.println(response.getBody());

		// System.out.println("-----------------------------------------");
		// geocodingService.getCoordinatesFromAddress(address);
		// System.out.println(latLng.toString());
		;

		// 直接用browser測試某地址https://maps.googleapis.com/maps/api/geocode/json?address=新北市蘆洲區長安街&key=AIzaSyBYgeaSJSbLteuiSPtVAgdIV-yPLM4Av-c
	}
}