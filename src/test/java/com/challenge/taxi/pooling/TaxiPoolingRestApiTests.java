package com.challenge.taxi.pooling;

import com.challenge.taxi.pooling.repository.TaxiRepository;
import com.challenge.taxi.pooling.service.TaxiPoolingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
		classes = TaxiPoolingApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class TaxiPoolingRestApiTests {
	private final static String BASE_ENDPOINT_URL = "/api/";
	private final static String STATUS_ENDPOINT_URL = "/status";
	private final static String TAXIS_ENDPOINT_URL = "/taxis";
	private final static int SERVER_PORT = 8081;
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
	public void givenServiceStarted_whenGetStatusEndpoint_ThenStatus200OK() throws Exception {
		mockMvc.perform(get(BASE_ENDPOINT_URL + STATUS_ENDPOINT_URL)).andExpect(status().isOk());
	}

	@Test
	public void whenPutTaxisEndpointWithNoTaxi_ThenGetTaxisEndpointThrowsException() throws Exception {
		final int TAXI_LIST_SIZE = 0;

		Exception exception = assertThrows(NestedServletException.class, () -> {
			verifyPutAndGetTaxiEndpoints(TAXI_LIST_SIZE);
		});
	}

	@Test
	public void whenPutTaxisEndpointWithMoreThanOneTaxi_ThenGetTaxisEndpointThrowsException() throws Exception {
		final int TAXI_LIST_SIZE = 2;

		verifyPutAndGetTaxiEndpoints(TAXI_LIST_SIZE);
	}

	@Test
	public void whenPutTaxisEndpointWithOneTaxi_ThenGetTaxisEndpointThrowsException() throws Exception {
		final int TAXI_LIST_SIZE = 1;

		verifyPutAndGetTaxiEndpoints(TAXI_LIST_SIZE);
	}

	@Test
	public void whenPutTaxisEndpointWithMultipleTaxi_ThenGetTaxisEndpointThrowsException() throws Exception {
		final int TAXI_LIST_SIZE = 1000;

		verifyPutAndGetTaxiEndpoints(TAXI_LIST_SIZE);
	}

	private void verifyPutAndGetTaxiEndpoints(int taxisNumber) throws Exception {
		String taxiListJson = TestUtilities.generateTaxiListJsonWithNElements(taxisNumber);

		mockMvc.perform(put(BASE_ENDPOINT_URL + TAXIS_ENDPOINT_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content( taxiListJson))
				.andExpect(status().isOk());

		mockMvc.perform(get(BASE_ENDPOINT_URL + TAXIS_ENDPOINT_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(taxisNumber)));
	}

}
