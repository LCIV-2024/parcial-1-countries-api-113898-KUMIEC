package ar.edu.utn.frc.tup.lciii.client;

import ar.edu.utn.frc.tup.lciii.Client.RestClient;
import ar.edu.utn.frc.tup.lciii.model.Country;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RestClientTest {

    @InjectMocks
    private RestClient restClient;

    @Mock
    private RestTemplate restTemplate;

    public RestClientTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCountry() {
        Country c = new Country();
        c.setName("Argentina");

        Country c2 = new Country();
        c2.setName("Brazil");
        List<Country> expectedCountries = Arrays.asList(c, c2);
        ResponseEntity<List<Country>> responseEntity = new ResponseEntity<>(expectedCountries, HttpStatus.OK);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<Country>> actualResponse = restClient.getAllCountry();

        assertEquals(expectedCountries, actualResponse.getBody());
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    }
}
