package io.pivotal.lars.credhub.pcfcredhub;

import java.util.List;

import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.credhub.support.CredentialPath;

/**
 * CredHubController
 */
@RestController
public class CredHubController {

    private final CredHubService credHubService;

    public CredHubController(CredHubService credHubService) {
        this.credHubService = credHubService;
    }

    @GetMapping
    public CertificateCredential generateCertificate()  {
        return credHubService.generateCertificate();
    }

    @GetMapping("/password")
    public String generatePassword()    {
        return credHubService.generatePassword();
    }

    @GetMapping("/certificates")
    public CertificateCredential getCertificate(@RequestParam String name)  {
        return credHubService.getCertificate(name);
    }

    @GetMapping("/credentials")
    public List<CredentialPath> getCredentials()    {
        return credHubService.getCredentials();
    }

    @GetMapping("/certificateslist")
    public List<CertificateSummary> getCertificates()  {
        return credHubService.getCertificates();
    }

}