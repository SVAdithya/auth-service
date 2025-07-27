package com.app.auth.controller;

import com.app.user.api.MfaManagementApi;
import com.app.user.model.EnableMfaRequest;
import com.app.user.model.MfaSetupResponse;
import com.app.user.model.VerifyMfaRequest;
import com.app.user.model.VerifyMfaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
public class MfaManagementController implements MfaManagementApi {

    @Override
    public ResponseEntity<Void> disableMfa(String id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<MfaSetupResponse> enableMfa(String id, EnableMfaRequest enableMfaRequest) {
        MfaSetupResponse mfaSetupResponse = new MfaSetupResponse()
                .qrCode(Base64.getEncoder().encodeToString("sample-qr-code".getBytes()))
                .secret("SAMPLE-SECRET-KEY");

        return ResponseEntity.ok(mfaSetupResponse);
    }

    @Override
    public ResponseEntity<Void> requestMfaReset(String id) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<VerifyMfaResponse> verifyMfa(String id, VerifyMfaRequest verifyMfaRequest) {
        VerifyMfaResponse verifyMfaResponse = new VerifyMfaResponse()
                .verified(true);

        return ResponseEntity.ok(verifyMfaResponse);
    }
}