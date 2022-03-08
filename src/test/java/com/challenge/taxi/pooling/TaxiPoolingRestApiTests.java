package com.challenge.taxi.pooling;

import com.challenge.taxi.pooling.model.Taxi;
import com.challenge.taxi.pooling.service.TaxiPoolingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

	@BeforeEach
	public void setUp() {
		taxiPoolingService.clearRepositoriesAndQueue();
	}

	@Test
	public void sanityChecks() {
		assertThat(port).isEqualTo(SERVER_PORT);
	}

	@Test
	public void givenServiceStarted_whenGetStatusEndpoint_ThenStatus200OK() throws Exception {
		mockMvc.perform(get(BASE_ENDPOINT_URL + STATUS_ENDPOINT_URL)).andExpect(status().isOk());
	}

	@Test
	public void whenPostTaxisEndpointWithNoTaxi_ThenGetTaxisEndpointThrowsException() throws Exception {
		final int TAXI_LIST_SIZE = 0;

		Exception exception = assertThrows(NestedServletException.class, () -> {
			verifyPostAndGetTaxiEndpoints(TAXI_LIST_SIZE);
		});
	}

	@Test
	public void whenPostTaxisEndpointWithMoreThanOneTaxi_ThenGetTaxisEndpointThrowsException() throws Exception {
		final int TAXI_LIST_SIZE = 2;

		verifyPostAndGetTaxiEndpoints(TAXI_LIST_SIZE);
	}

	@Test
	public void whenPostTaxisEndpointWithOneTaxi_ThenGetTaxisEndpointThrowsException() throws Exception {
		final int TAXI_LIST_SIZE = 1;

		verifyPostAndGetTaxiEndpoints(TAXI_LIST_SIZE);
	}

	@Test
	public void whenDeleteTaxisRepositoryThatIsEmpty_Then404NotFoundIsReturned() throws Exception {
		mockMvc.perform(delete(BASE_ENDPOINT_URL + TAXIS_ENDPOINT_URL))
				.andExpect(status().isNoContent());
	}

	@Test
	public void whenDeleteTaxisRepositoryThatIsNotEmpty_Then204NoContentIsReturned() throws Exception {
		final int TAXIS_NUMBER = 20;

		insertTaxisToRepository(TAXIS_NUMBER);

		mockMvc.perform(delete(BASE_ENDPOINT_URL + TAXIS_ENDPOINT_URL))
				.andExpect(status().isNoContent());
	}

	@Test
	public void whenInsertATaxiThatAlreadyExists_ThenTheTaxiIsUpdated() throws Exception {
		String taxiJson = TestUtilities.generateTaxiListJsonWithNElements(1);

		mockMvc.perform(put(BASE_ENDPOINT_URL + TAXIS_ENDPOINT_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(taxiJson))
				.andExpect(status().isOk());

		taxiJson = TestUtilities.generateTaxiListJsonWithNElements(1);

		mockMvc.perform(put(BASE_ENDPOINT_URL + TAXIS_ENDPOINT_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(taxiJson))
				.andExpect(status().isOk());
	}

	@Test
	public void whenPostTaxisEndpointWithMultipleTaxi_ThenGetTaxisEndpointThrowsException() throws Exception {
		final int TAXI_LIST_SIZE = 1000;

		verifyPostAndGetTaxiEndpoints(TAXI_LIST_SIZE);
	}

	private void verifyPostAndGetTaxiEndpoints(int taxisNumber) throws Exception {
		String taxiListJson = TestUtilities.generateTaxiListJsonWithNElements(taxisNumber);

		mockMvc.perform(post(BASE_ENDPOINT_URL + TAXIS_ENDPOINT_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content( taxiListJson))
				.andExpect(status().isOk());

		mockMvc.perform(get(BASE_ENDPOINT_URL + TAXIS_ENDPOINT_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(taxisNumber)));
	}

	private void insertTaxisToRepository(int taxisNumber) {
		List<Taxi> taxiList = TestUtilities.generateTaxiListWithNElements(taxisNumber);

		taxiPoolingService.saveTaxisToRepository(taxiList);
	}

}
