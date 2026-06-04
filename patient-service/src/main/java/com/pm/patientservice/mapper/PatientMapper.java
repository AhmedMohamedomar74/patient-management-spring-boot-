package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.model.patient;

public class PatientMapper {
    public static PatientResponseDto toDto (patient patient)
    {
        PatientResponseDto patientResponseDto = new PatientResponseDto();
        patientResponseDto.setAddress(patient.getAddress());
        patientResponseDto.setEmail(patient.getEmail());
        patientResponseDto.setId(patient.getId().toString());
        patientResponseDto.setName(patient.getName());
        return patientResponseDto;
    }
}
