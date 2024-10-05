package ar.edu.utn.frc.tup.lciii.Client;
import ar.edu.utn.frc.tup.lciii.model.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RestClient {

    @Autowired
    RestTemplate restTemplate;

    private static final String URL = "https://restcountries.com/v3.1/all\n";

    public ResponseEntity<List<Country>> getAllCountry(){
        return restTemplate.exchange(URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Country>>() {});
    }

}
