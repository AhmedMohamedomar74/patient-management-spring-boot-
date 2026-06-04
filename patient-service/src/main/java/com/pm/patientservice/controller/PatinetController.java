package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
