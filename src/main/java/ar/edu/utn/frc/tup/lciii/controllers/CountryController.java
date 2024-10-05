package ar.edu.utn.frc.tup.lciii.controllers;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryDTO;
import ar.edu.utn.frc.tup.lciii.dtos.common.CountryRequestDTO;
import ar.edu.utn.frc.tup.lciii.service.CountryService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    // 1. Endpoint que expone todos los países
    @GetMapping("")
    public ResponseEntity<List<CountryDTO>> getAllCountries(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code) {
        List<CountryDTO> countries = new ArrayList<>();
        if (name != null){
             countries = countryService.getCountryByName(name);
        } else if (code != null) {
             countries = countryService.getCountryByCode(code);
        }else {
             countries = countryService.getCountry();
        }
        return ResponseEntity.ok(countries);
    }

    // 3. Endpoint que expone países por continente
    @GetMapping("/{continent}/continent")
    public ResponseEntity<List<CountryDTO>> getCountryByContinent(@PathVariable String continent) {
        List<CountryDTO> countries = countryService.getCountryByContinent(continent);
        return ResponseEntity.ok(countries);
    }

    // 4. Endpoint que expone países por idioma
    @GetMapping("/{language}/language")
    public ResponseEntity<List<CountryDTO>> getCountriesByLanguage(@PathVariable String language) {
        List<CountryDTO> countries = countryService.getCountriesByLanguage(language);
        return ResponseEntity.ok(countries);
    }

    // 6. Endpoint que trae el nombre del país con más fronteras
    @GetMapping("/most-borders")
    public ResponseEntity<CountryDTO> getCountryWithMostBorders() {
        CountryDTO country = countryService.getCountryWithMostBorders();
        return ResponseEntity.ok(country);
    }
    @PostMapping
    public ResponseEntity<List<CountryDTO>> postCountry(@RequestBody CountryRequestDTO request) {
        List<CountryDTO> countries = countryService.saveRandomCountries(request.getAmountOfCountryToSave());
        return ResponseEntity.ok(countries);
    }
}