package io.pivotal.lars.credhub.pcfcredhub;

import java.util.List;

import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialSummary;

/**
 * CredHubController
 */
@RestController
public class CredHubController {

    private final CredHubService credHubService;

    public CredHubController(CredHubService credHubService) {
        this.credHubService = credHubService;
    }

    @GetMapping("/certificates/rotate")
    public CredentialDetails<CertificateCredential> rotateCertificate(@RequestParam String name)   {
        return credHubService.rotateCertificateByName(name);
    }

    @GetMapping("/passwords/generate")
    public String generatePassword()    {
        return credHubService.generatePassword();
    }

    @GetMapping("/passwords/rotate")
    public String generatePassword(@RequestParam String name)    {
        return credHubService.rotatePassword(name);
    }

    @GetMapping("/certificates")
    public CredentialDetails<CertificateCredential> getCertificate(@RequestParam String name)  {
        return credHubService.getCertificate(name);
    }

    @GetMapping("/certificates/list")
    public List<CertificateSummary> getCertificates()  {
        return credHubService.getCertificates();        
    }

    @GetMapping("/credentials/list")
    public List<CredentialSummary> getCredentials() {
        return credHubService.getCredentials();
    }

    @GetMapping("/certificates/generate/ca")
    public CredentialDetails<CertificateCredential> generateRoot()  {
        return credHubService.generateCaCertificate();
    }

    @GetMapping("/certificates/generate/byca")
    public CredentialDetails<CertificateCredential> generateCertificateByCa()  {
        return credHubService.generateCertificateSignedByCa();
    }

}