package com.pm.patientservice.repostiry;

import com.pm.patientservice.model.patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PatientRepositry extends JpaRepository<patient, UUID> {

}
