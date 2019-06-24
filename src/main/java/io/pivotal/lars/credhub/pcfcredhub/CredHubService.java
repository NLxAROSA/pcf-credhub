package io.pivotal.lars.credhub.pcfcredhub;

import java.util.List;
import java.util.UUID;

import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.permissionV2.CredHubPermissionV2Operations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateParameters;
import org.springframework.credhub.support.certificate.CertificateParametersRequest;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.certificate.CertificateParameters.CertificateParametersBuilder;
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
    private final CredHubPermissionV2Operations permissionV2Operations;

    public CredHubService(CredHubOperations credHubOperations) {
        this.credHubOperations = credHubOperations;
        this.permissionV2Operations = credHubOperations.permissionsV2();
    }

    public CredentialPermission getPermissions(String id)   {
        log.info("Getting permissions with id {}", id);
        return permissionV2Operations.getPermissions(id);
    }

    public String generatePassword() {

        SimpleCredentialName credentialName = new SimpleCredentialName("mypasswords","UUID.randomUUID().toString()");

        log.warn("Generating credential with name {}", credentialName.getName());

        PasswordParameters parameters = PasswordParameters.builder().length(12).excludeLower(false).excludeUpper(false)
                .excludeNumber(false).includeSpecial(true).build();

        CredentialDetails<PasswordCredential> password = credHubOperations.credentials()
                .generate(PasswordParametersRequest.builder().name(credentialName).parameters(parameters).build());

        return "Generated password " + password.getValue().getPassword() + " with name " + password.getName();
    }

    public String rotatePassword(String name) {
        SimpleCredentialName credentialName = new SimpleCredentialName(name);

        CredentialDetails<PasswordCredential> oldPassword = credHubOperations.credentials().getByName(credentialName,
                PasswordCredential.class);

        CredentialDetails<PasswordCredential> newPassword = credHubOperations.credentials().regenerate(credentialName,
                PasswordCredential.class);

        return "Changed password with name " + credentialName + " from " + oldPassword.getValue().getPassword() + " to "
                + newPassword.getValue().getPassword();
    }

    public CredentialDetails<CertificateCredential> generateCaCertificate() {
        CredentialName credentialName = new SimpleCredentialName("myroot", "mycacertificate");
        log.warn("Generating certificate with name {}", credentialName);
        return generateCertificate(credentialName, null);
    }

    public CredentialDetails<CertificateCredential> generateCertificateSignedByCa() {
        CredentialName name = new SimpleCredentialName("myroot", UUID.randomUUID().toString(), "certificate");
        CredentialName caName = new SimpleCredentialName("myroot", "mycacertificate");

        return generateCertificate(name, caName);
    }

    public CredentialDetails<CertificateCredential> generateCertificate(CredentialName name, CredentialName caName) {

        CertificateParametersBuilder builder = CertificateParameters.builder();

        builder.commonName("commonname").organization("myorg").organizationUnit("myorgunit").duration(90);

        if (caName != null) {
            builder.certificateAuthorityCredential(caName);
        } else {
            builder.certificateAuthority(true);
            builder.selfSign(true);
        }

        CertificateParameters certificateParameters = builder.build();

        try {
            CredentialDetails<CertificateCredential> certificate = credHubOperations.credentials().generate(
                    CertificateParametersRequest.builder().name(name).parameters(certificateParameters).build());
            log.info("Generated certificate with id {}, name {}", certificate.getId(), certificate.getName());
            return certificate;
        } catch (Exception e) {
            log.error("An error occurred " + e.getMessage());
            log.error("Failed to generate certificate with name {}", name.getName());
            throw new RuntimeException(e);
        }
    }

    public CredentialDetails<CertificateCredential> rotateCertificateByName(String name) {
        CredentialDetails<CertificateCredential> cert = credHubOperations.credentials()
                .regenerate(new SimpleCredentialName(name), CertificateCredential.class);
        log.info("Regenerated certificate with id {}, name {}", cert.getId(), cert.getName());
        return cert;
    }

    public List<CertificateSummary> getCertificates() {
        return credHubOperations.certificates().getAll();
    }

    public CredentialDetails<CertificateCredential> getCertificate(String name) {
        return getCredentialDetails(name);
    }

    public List<CredentialSummary> getCredentials() {
        return credHubOperations.credentials().findByName(new SimpleCredentialName("/"));
    }

    private CredentialDetails<CertificateCredential> getCredentialDetails(String name) {
        CredentialName credentialName = new SimpleCredentialName(name);
        log.info("Getting certificate by name {}", name);
        CredentialDetails<CertificateCredential> creds = credHubOperations.credentials().getByName(credentialName,
                CertificateCredential.class);
        return creds;
    }
}