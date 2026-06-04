package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatinetController {
    private PatientService PatientService ;
    public PatinetController(PatientService patientService) {
        PatientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getPateints()
    {
        List<PatientResponseDto> patients= PatientService.ListAllPatient();
        return ResponseEntity.ok(patients);
    }

    @PostMapping
    public ResponseEntity<PatientResponseDto> addPatient(@Valid  @RequestBody PatientRequestDto PatientRequestDto)
    {
        PatientResponseDto PatientResponseDto = PatientService.addPatient(PatientRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(PatientResponseDto);
    }
}
