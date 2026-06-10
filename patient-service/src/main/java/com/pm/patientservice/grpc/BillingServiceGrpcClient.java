package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponce;
import billing.BillingServiceGrpc;
import billing.BillingServiceGrpc.BillingServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(@Value("${billing.service.address:localhost}") String serveraddress,
                                    @Value("${billing.service.grpc.port}") int serverPort) {
        log.info("Connecting to GRPC server at {}:{}", serveraddress, serverPort);
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(serveraddress, serverPort).usePlaintext().build();
        blockingStub = BillingServiceGrpc.newBlockingStub(managedChannel);
    }

    public BillingResponce CreateAccount(String id, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder()
                .setEmail(email)
                .setName(name)
                .setPatientId(id)
                .build();
        BillingResponce responce = blockingStub.createBillingAccount(request);
        log.info("Recieved response from server {}", responce);
        return responce;
    }
}
