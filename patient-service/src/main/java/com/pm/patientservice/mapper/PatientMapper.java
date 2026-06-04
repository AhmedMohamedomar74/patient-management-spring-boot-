package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.model.patient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientMapper {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");

    public static PatientResponseDto toDto (patient patient)
    {
        PatientResponseDto patientResponseDto = new PatientResponseDto();
        patientResponseDto.setAddress(patient.getAddress());
        patientResponseDto.setEmail(patient.getEmail());
        patientResponseDto.setId(patient.getId().toString());
        patientResponseDto.setName(patient.getName());
        return patientResponseDto;
    }
    public static patient toModel(PatientRequestDto patientRequestDto)
    {
        patient patient = new patient();
        patient.setAddress(patientRequestDto.getAddress());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setName(patientRequestDto.getName());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth(), DATE_FORMATTER));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDto.getRegisteredDate(), DATE_FORMATTER));
        return patient;
    }
}
