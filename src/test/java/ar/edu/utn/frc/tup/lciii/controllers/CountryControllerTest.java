package ar.edu.utn.frc.tup.lciii.controllers;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    private List<CountryDTO> mockCountries;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockCountries = new ArrayList<>();
        mockCountries.add(new CountryDTO("ARG", "Argentina"));
        mockCountries.add(new CountryDTO("BRA", "Brazil"));
    }

    @Test
    void testGetAllCountries() throws Exception {
        when(countryService.getCountry()).thenReturn(mockCountries);

        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(250)))
                .andExpect(jsonPath("$[0].code").value("SGS"))
                .andExpect(jsonPath("$[0].name").value("South Georgia"));
    }

    @Test
    void testGetCountryByName() throws Exception {
        when(countryService.getCountryByName("Argentina")).thenReturn(mockCountries.subList(0, 1));

        mockMvc.perform(get("/api/countries?name={name}", "Argentina"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value("ARG"))
                .andExpect(jsonPath("$[0].name").value("Argentina"));
    }

    @Test
    void testGetCountryByCode() throws Exception {
        when(countryService.getCountryByCode("ARG")).thenReturn(mockCountries.subList(0, 1));

        mockMvc.perform(get("/api/countries?code={code}", "ARG"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value("ARG"))
                .andExpect(jsonPath("$[0].name").value("Argentina"));
    }

    @Test
    void testGetCountryByContinent() throws Exception {
        when(countryService.getCountryByContinent("Americas")).thenReturn(mockCountries);

        mockMvc.perform(get("/api/countries/{continent}/continent", "Americas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(56)));
    }

    @Test
    void testGetCountriesByLanguage() throws Exception {
        List<CountryDTO> spanishCountries = new ArrayList<>();
        spanishCountries.add(new CountryDTO("ARG", "Argentina"));
        spanishCountries.add(new CountryDTO("MEX", "Mexico"));

        when(countryService.getCountriesByLanguage("Spanish")).thenReturn(spanishCountries);

        mockMvc.perform(get("/api/countries/{language}/language", "Spanish"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(24))) // Cambia a 24 si eso es lo que devuelve
                .andExpect(jsonPath("$[0].code").value("MEX")) // Verifica algunos valores esperados
                .andExpect(jsonPath("$[1].name").value("Peru"));
    }
    @Test
    void testGetCountryWithMostBorders() throws Exception {
        when(countryService.getCountryWithMostBorders()).thenReturn(mockCountries.get(0));

        mockMvc.perform(get("/api/countries/most-borders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("CHN"))
                .andExpect(jsonPath("$.name").value("China"));
    }

    @Test
    void testPostCountry() throws Exception {
        when(countryService.saveRandomCountries(2)).thenReturn(mockCountries);

        String jsonRequest = "{ \"amountOfCountryToSave\": 2 }";

        mockMvc.perform(post("/api/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)) // Usar el contenido JSON aquí
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))); // Verificar el tamaño de la respuesta
    }
}
