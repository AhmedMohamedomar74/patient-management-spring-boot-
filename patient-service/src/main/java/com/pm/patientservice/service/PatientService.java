package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.patient;
import com.pm.patientservice.repostiry.PatientRepositry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private PatientRepositry patientRepositry;

    public PatientService(PatientRepositry patientRepositry) {
        this.patientRepositry = patientRepositry;
    }

    public List<PatientResponseDto> ListAllPatient()
    {
        List<patient> patients = patientRepositry.findAll();

        return patients.stream().map(PatientMapper::toDto
        ).toList();
    }

    public PatientResponseDto addPatient(PatientRequestDto PatientRequestDto)
    {
        return PatientMapper.toDto(patientRepositry.save(PatientMapper.toModel(PatientRequestDto)));
    }
}
