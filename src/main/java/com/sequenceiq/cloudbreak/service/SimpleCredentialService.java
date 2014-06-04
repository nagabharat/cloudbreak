package com.sequenceiq.cloudbreak.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UnknownFormatConversionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.controller.NotFoundException;
import com.sequenceiq.cloudbreak.controller.json.CredentialJson;
import com.sequenceiq.cloudbreak.controller.json.IdJson;
import com.sequenceiq.cloudbreak.converter.AwsCredentialConverter;
import com.sequenceiq.cloudbreak.converter.AzureCredentialConverter;
import com.sequenceiq.cloudbreak.domain.AwsCredential;
import com.sequenceiq.cloudbreak.domain.AzureCredential;
import com.sequenceiq.cloudbreak.domain.Credential;
import com.sequenceiq.cloudbreak.domain.User;
import com.sequenceiq.cloudbreak.repository.AwsCredentialRepository;
import com.sequenceiq.cloudbreak.repository.AzureCredentialRepository;
import com.sequenceiq.cloudbreak.repository.CredentialRepository;
import com.sequenceiq.cloudbreak.service.azure.AzureCredentialService;

@Service
public class SimpleCredentialService implements CredentialService {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private AwsCredentialConverter awsCredentialConverter;

    @Autowired
    private AzureCredentialConverter azureCredentialConverter;

    @Autowired
    private AzureCredentialRepository azureCredentialRepository;

    @Autowired
    private AwsCredentialRepository awsCredentialRepository;

    @Autowired
    private AzureCredentialService azureCredentialService;

    public Set<CredentialJson> getAll(User user) {
        Set<CredentialJson> result = new HashSet<>();
        result.addAll(awsCredentialConverter.convertAllEntityToJson(user.getAwsCredentials()));
        result.addAll(azureCredentialConverter.convertAllEntityToJson(user.getAzureCredentials()));
        return result;
    }

    public CredentialJson get(Long id) {
        Credential credential = credentialRepository.findOne(id);
        if (credential == null) {
            throw new NotFoundException(String.format("Template '%s' not found.", id));
        } else {
            switch (credential.cloudPlatform()) {
            case AWS:
                return awsCredentialConverter.convert((AwsCredential) credential);
            case AZURE:
                return azureCredentialConverter.convert((AzureCredential) credential);
            default:
                throw new UnknownFormatConversionException(String.format("The cloudPlatform '%s' is not supported.", credential.cloudPlatform()));
            }
        }
    }

    public IdJson save(User user, CredentialJson credentialJson) {
        switch (credentialJson.getCloudPlatform()) {
        case AWS:
            AwsCredential awsCredential = awsCredentialConverter.convert(credentialJson);
            awsCredential.setAwsCredentialOwner(user);
            awsCredentialRepository.save(awsCredential);
            return new IdJson(awsCredential.getId());
        case AZURE:
            AzureCredential azureCredential = azureCredentialConverter.convert(credentialJson);
            azureCredential.setAzureCredentialOwner(user);
            azureCredentialRepository.save(azureCredential);
            azureCredentialService.generateCertificate(azureCredential, user);
            return new IdJson(azureCredential.getId());
        default:
            throw new UnknownFormatConversionException(String.format("The cloudPlatform '%s' is not supported.", credentialJson.getCloudPlatform()));
        }
    }

    public void delete(Long id) {
        Credential credential = credentialRepository.findOne(id);
        if (credential == null) {
            throw new NotFoundException(String.format("Credential '%s' not found.", id));
        }
        credentialRepository.delete(credential);
    }

}