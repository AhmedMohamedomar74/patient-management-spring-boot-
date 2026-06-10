package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDto;
import com.pm.patientservice.dto.PatientResponseDto;
import com.pm.patientservice.dto.PatientUpdateRequestDto;
import com.pm.patientservice.exception.EmailAreadyExistException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.patient;
import com.pm.patientservice.repostiry.PatientRepositry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private PatientRepositry patientRepositry;
    private BillingServiceGrpcClient BillingServiceGrpcClient;
    public PatientService(PatientRepositry patientRepositry , BillingServiceGrpcClient BillingServiceGrpcClient) {
        this.patientRepositry = patientRepositry;
        this.BillingServiceGrpcClient = BillingServiceGrpcClient;
    }

    public List<PatientResponseDto> listAllPatients() {
        List<patient> patients = patientRepositry.findAll();

        return patients.stream().map(PatientMapper::toDto
        ).toList();
    }

    public PatientResponseDto addPatient(PatientRequestDto requestDto) {
        if (patientRepositry.existsByEmail(requestDto.getEmail())) {
            throw new EmailAreadyExistException("Email is already exist " + requestDto.getEmail());
        }
        PatientResponseDto newPatientResponse = PatientMapper.toDto(patientRepositry.save(PatientMapper.toModel(requestDto)));
        BillingServiceGrpcClient.CreateAccount(newPatientResponse.getId() , newPatientResponse.getName(),newPatientResponse.getEmail());
        return newPatientResponse;
    }

    public PatientResponseDto updatePatient(PatientUpdateRequestDto requestDto, UUID id) {
        patient patient = patientRepositry.findById(id).orElseThrow(() -> new PatientNotFoundException("patient not found with id " + id));

        if (requestDto.getEmail() != null && !patient.getEmail().equals(requestDto.getEmail())
                && patientRepositry.existsByEmail(requestDto.getEmail())) {
            throw new EmailAreadyExistException("Email is already exist " + requestDto.getEmail());
        }

        PatientMapper.updatePatient(patient, requestDto);
        patientRepositry.save(patient);

        return PatientMapper.toDto(patient);
    }

    public PatientResponseDto getPatient(UUID id)
    {
        patient patient = patientRepositry.findById(id).orElseThrow(() -> new PatientNotFoundException("patient not found with id " + id));
        return  PatientMapper.toDto(patient);
    }

    public void deletePatient(UUID id)
    {
        patientRepositry.deleteById(id);
    }
}
