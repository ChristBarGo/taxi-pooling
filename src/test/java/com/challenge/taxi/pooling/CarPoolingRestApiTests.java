package com.challenge.taxi.pooling;

import com.challenge.taxi.pooling.service.TaxiPoolingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
		classes = TaxiPoolingApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class taxiPoolingRestApiTests {
	private final static String STATUS_ENDPOINT_URL = "/status";
	private final static String TAXIS_ENDPOINT_URL = "/ taxis";
	private final static int SERVER_PORT = 9091;
	@LocalServerPort
	private int port;
	@Autowired
	private TaxiPoolingService taxiPoolingService;
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void sanityChecks() {
		assertThat(port).isEqualTo(SERVER_PORT);
	}

	@Test
	public void testGetStatusEndpointWhenServiceStarted() throws Exception {
		mockMvc.perform(get(STATUS_ENDPOINT_URL)).andExpect(status().isOk());
	}

	@Test
	public void TestPutTaxisEndpointMoreThanOneTaxi() throws Exception {
		final int TAXI_LIST_SIZE = 2;
		String taxiListJson = TestUtilities.generateTaxiListJsonWithNElements( TAXI_LIST_SIZE);

		mockMvc.perform(put(TAXIS_ENDPOINT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content( taxiListJson))
				.andExpect(status().isOk());

		mockMvc.perform(get(TAXIS_ENDPOINT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize( TAXI_LIST_SIZE)));
	}

}
