package com.pm.billingservice.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import billing.BillingResponce;

@GrpcService
public class BillingServiceGrpc extends billing.BillingServiceGrpc.BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpc.class);

    @Override
    public void createBillingAccount(billing.BillingRequest request, StreamObserver<BillingResponce> response) {
        log.info("create Billing Account requested recieved {}",request.toString());
        BillingResponce billingResponse = BillingResponce.newBuilder()
                .setAccountId("1234")
                .setStatus("active")
                .build();
        response.onNext(billingResponse);
        response.onCompleted();
    }

}
