package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CountryServiceTest {
    @Mock
    private CountryRepository countryRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCountries() {
        List<Map<String, Object>> mockResponse = Arrays.asList(
                Map.of("name", Map.of("common", "Argentina"), "population", 45000000, "area", 2780400.0, "region", "Americas", "languages", Map.of("spa", "Spanish"), "borders", List.of("BRA", "CHL", "PRY"), "cca3", "ARG"),
                Map.of("name", Map.of("common", "Brazil"), "population", 211000000, "area", 8515767.0, "region", "Americas", "languages", Map.of("por", "Portuguese"), "borders", List.of("ARG"), "cca3", "BRA")
        );

        when(restTemplate.getForObject("https://restcountries.com/v3.1/all", List.class)).thenReturn(mockResponse);

        List<Country> countries = countryService.getAllCountries();

        assertEquals(2, countries.size());
        assertEquals("Argentina", countries.get(0).getName());
        assertEquals("BRA", countries.get(0).getBorders().get(0));
    }

    @Test
    void testGetCountryByName() {
        when(restTemplate.getForObject("https://restcountries.com/v3.1/all", List.class)).thenReturn(createMockCountries());

        List<CountryDTO> countryDTOS = countryService.getCountryByName("Argentina");

        assertEquals(1, countryDTOS.size());
        assertEquals("ARG", countryDTOS.get(0).getCode());
        assertEquals("Argentina", countryDTOS.get(0).getName());
    }

    @Test
    void testGetCountryByCode() {
        when(restTemplate.getForObject("https://restcountries.com/v3.1/all", List.class)).thenReturn(createMockCountries());

        List<CountryDTO> countryDTOS = countryService.getCountryByCode("BRA");

        assertEquals(1, countryDTOS.size());
        assertEquals("BRA", countryDTOS.get(0).getCode());
        assertEquals("Brazil", countryDTOS.get(0).getName());
    }

    @Test
    void testSaveRandomCountries() {
        List<Map<String, Object>> mockResponse = createMockCountryDataOnlyArg();
        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(mockResponse);

        List<CountryDTO> savedCountries = countryService.saveRandomCountries(1);

        assertEquals(1, savedCountries.size());

        ArgumentCaptor<CountryEntity> countryEntityCaptor = ArgumentCaptor.forClass(CountryEntity.class);
        verify(countryRepository, times(1)).save(countryEntityCaptor.capture());

        List<CountryEntity> capturedCountries = countryEntityCaptor.getAllValues();
        assertEquals("ARG", capturedCountries.get(0).getCode());
        assertEquals("Argentina", capturedCountries.get(0).getName());
    }

    private List<Map<String, Object>> createMockCountryData() {
        return Arrays.asList(
                Map.of("cca3", "ARG", "name", Map.of("common", "Argentina"), "population", 45000000, "area", 2780400.0),
                Map.of("cca3", "BRA", "name", Map.of("common", "Brazil"), "population", 211000000, "area", 8515767.0)
        );
    }
    private List<Map<String, Object>> createMockCountryDataOnlyArg() {
        return Arrays.asList(
                Map.of("cca3", "ARG", "name", Map.of("common", "Argentina"), "population", 45000000, "area", 2780400.0)
        );
    }

    private List<Map<String, Object>> createMockCountries() {
        return Arrays.asList(
                Map.of("name", Map.of("common", "Argentina"), "population", 45000000, "area", 2780400.0, "region", "Americas", "languages", Map.of("spa", "Spanish"), "borders", List.of("BRA"), "cca3", "ARG"),
                Map.of("name", Map.of("common", "Brazil"), "population", 211000000, "area", 8515767.0, "region", "Americas", "languages", Map.of("por", "Portuguese"), "borders", List.of("ARG"), "cca3", "BRA")
        );
    }
}
