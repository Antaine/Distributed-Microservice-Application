package com.tus.characters.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // This is the username or service name that will be stored in createdBy/updatedBy
        return Optional.of("CHARACTERS_MS");
    }
}