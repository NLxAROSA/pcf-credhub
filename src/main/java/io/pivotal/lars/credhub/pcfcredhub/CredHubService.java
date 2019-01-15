package io.pivotal.lars.credhub.pcfcredhub;

import java.util.List;
import java.util.UUID;

import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialPath;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.credhub.support.certificate.CertificateCredentialRequest;
import org.springframework.credhub.support.certificate.CertificateParameters;
import org.springframework.credhub.support.certificate.CertificateParametersRequest;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.password.PasswordCredential;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.credhub.support.password.PasswordParametersRequest;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * CredHubService
 */
@Service
@Slf4j
public class CredHubService {

    private final CredHubOperations credHubOperations;

    public CredHubService(CredHubOperations credHubOperations) {
        this.credHubOperations = credHubOperations;
    }

    public String generatePassword() {

        SimpleCredentialName credentialName = new SimpleCredentialName(UUID.randomUUID().toString(), "password");

        PasswordParameters parameters = PasswordParameters.builder().length(12).excludeLower(false).excludeUpper(false)
                .excludeNumber(false).includeSpecial(true).build();

        CredentialDetails<PasswordCredential> password = credHubOperations.credentials()
                .generate(PasswordParametersRequest.builder().name(credentialName).parameters(parameters).build());

        return password.getValue().getPassword();
    }

    public CertificateCredential generateCertificate() {
        CredentialName credentialName = new SimpleCredentialName(UUID.randomUUID().toString(), "certificate");

        CertificateParameters certificateParameters = CertificateParameters.builder().commonName("commonname")
                .organization("myorg").organizationUnit("myorgunit").duration(90).selfSign(true).build();

        CredentialDetails<CertificateCredential> certificate = credHubOperations.credentials().generate(
                CertificateParametersRequest.builder().name(credentialName).parameters(certificateParameters).build());
        log.info("Generated certificate with id {}, name {}", certificate.getId(), certificate.getName());
        return certificate.getValue();
    }

    public List<CertificateSummary> getCertificates() {
        return credHubOperations.certificates().getAll();
    }

    public CertificateCredential getCertificate(String name) {
        CredentialName credentialName = new SimpleCredentialName(name);
        log.info("Getting certificate by name {}", name);
        CredentialDetails<CertificateCredential> creds = credHubOperations.credentials().getByName(credentialName,
                CertificateCredential.class);
        return creds.getValue();
    }

}