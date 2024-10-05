package ar.edu.utn.frc.tup.lciii.service;

import ar.edu.utn.frc.tup.lciii.Client.RestClient;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.entities.CountryEntity;
import ar.edu.utn.frc.tup.lciii.model.Country;
import ar.edu.utn.frc.tup.lciii.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

        private final CountryRepository countryRepository;

        private final RestTemplate restTemplate;
        @Autowired
        private RestClient restClient;

        public List<Country> getAllCountries() {
                String url = "https://restcountries.com/v3.1/all";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
                return response.stream().map(this::mapToCountry).collect(Collectors.toList());
        }

        /**
         * Agregar mapeo de campo cca3 (String)
         * Agregar mapeo campos borders ((List<String>))
         */
        private Country mapToCountry(Map<String, Object> countryData) {
                Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
                List<String> borders = (List<String>) countryData.get("borders");
                return Country.builder()
                        .name((String) nameData.get("common"))
                        .population(((Number) countryData.get("population")).longValue())
                        .area(((Number) countryData.get("area")).doubleValue())
                        .region((String) countryData.get("region"))
                        .languages((Map<String, String>) countryData.get("languages"))
                        .borders(borders != null ? borders : new ArrayList<>())
                        .code((String) countryData.get("cca3"))
                        .build();
        }



        private CountryDTO mapToDTO(Country country) {
                return new CountryDTO(country.getCode(), country.getName());
        }

        public List<CountryDTO> getCountry(){
                List<Country> countries = getAllCountries();
                List<CountryDTO> countryDTOS = new ArrayList<>();

                for (Country c:countries){
                        CountryDTO countryDTO = new CountryDTO();
                        countryDTO.setCode(c.getCode());
                        countryDTO.setName(c.getName());
                        countryDTOS.add(countryDTO);
                }
                return countryDTOS;
        }

        public List<CountryDTO> getCountryByName(String name){
                List<Country> countries = getAllCountries();
                List<CountryDTO> countryDTOS = new ArrayList<>();

                for (Country c:countries){
                        if (c.getName().equals(name)){
                                CountryDTO countryDTO = new CountryDTO();
                                countryDTO.setCode(c.getCode());
                                countryDTO.setName(c.getName());
                                countryDTOS.add(countryDTO);
                        }
                }
                return countryDTOS;
        }

        public List<CountryDTO> getCountryByCode(String code){
                List<Country> countries = getAllCountries();
                List<CountryDTO> countryDTOS = new ArrayList<>();

                for (Country c:countries){
                        if (c.getCode().equals(code)){
                                CountryDTO countryDTO = new CountryDTO();
                                countryDTO.setCode(c.getCode());
                                countryDTO.setName(c.getName());
                                countryDTOS.add(countryDTO);
                        }
                }
                return countryDTOS;
        }

        public List<CountryDTO> getCountryByContinent(String continent){
                List<Country> countries = getAllCountries();
                List<CountryDTO> countryDTOS = new ArrayList<>();

                if (!esContinenteValido(continent)){
                        throw new RuntimeException("No existe el continente que agrego");
                }

                for (Country c:countries){
                        if (c.getRegion().equals(continent)){
                                CountryDTO countryDTO = new CountryDTO();
                                countryDTO.setCode(c.getCode());
                                countryDTO.setName(c.getName());
                                countryDTOS.add(countryDTO);
                        }
                }
                return countryDTOS;
        }

        public List<CountryDTO> getCountriesByLanguage(String language) {
                List<Country> countries = getAllCountries();
                List<CountryDTO> countryDTOS = new ArrayList<>();

                for (Country country : countries) {
                        if (country.getLanguages() != null && country.getLanguages().containsValue(language)) {
                                CountryDTO countryDTO = convertToDTO(country);
                                countryDTOS.add(countryDTO);
                        }
                }

                return countryDTOS;
        }


        private CountryDTO convertToDTO(Country country) {
                return CountryDTO.builder()
                        .code(country.getCode())
                        .name(country.getName())
                        .build();
        }

        public boolean esContinenteValido(String continent) {
                boolean esValido = false;
                if (continent.equals("Africa") || continent.equals("Americas") || continent.equals("Asia") || continent.equals("Europe") || continent.equals("Oceania") || continent.equals("Antartic")){
                        esValido = true;
                }
                return esValido;
        }

        public CountryDTO getCountryWithMostBorders() {
                List<Country> countries = getAllCountries();

                if (countries == null || countries.isEmpty()) {
                        return null;
                }

                Country countryWithMostBorders = null;
                int maxBorders = 0;

                for (Country country : countries) {
                        if (country.getBorders() != null && country.getBorders().size() > maxBorders) {
                                maxBorders = country.getBorders().size();
                                countryWithMostBorders = country;
                        }
                }

                return countryWithMostBorders != null ? convertToDTO(countryWithMostBorders) : null;
        }

        private List<Map<String, Object>> getAllCountriesData() {
                String url = "https://restcountries.com/v3.1/all";
                return restTemplate.getForObject(url, List.class);
        }

        public List<CountryDTO> saveRandomCountries(int amount) {
                List<Map<String, Object>> allCountriesData = getAllCountriesData();

                if (amount > 10) {
                        throw new IllegalArgumentException("La cantidad de países a guardar no debe ser mayor a 10.");
                }

                Collections.shuffle(allCountriesData);
                List<Map<String, Object>> selectedCountriesData = allCountriesData.subList(0, amount);

                List<CountryDTO> savedCountries = new ArrayList<>();
                for (Map<String, Object> countryData : selectedCountriesData) {
                        CountryEntity countryEntity = new CountryEntity();
                        countryEntity.setCode((String) countryData.get("cca3"));
                        countryEntity.setName((String) ((Map<String, Object>) countryData.get("name")).get("common"));
                        countryEntity.setPopulation(((Number) countryData.get("population")).longValue());
                        countryEntity.setArea(((Number) countryData.get("area")).doubleValue());

                        countryRepository.save(countryEntity);

                        savedCountries.add(convertToDTO(countryEntity));
                }

                return savedCountries;
        }

        private CountryDTO convertToDTO(CountryEntity countryEntity) {
                return CountryDTO.builder()
                        .code(countryEntity.getCode())
                        .name(countryEntity.getName())
                        .build();
        }
}