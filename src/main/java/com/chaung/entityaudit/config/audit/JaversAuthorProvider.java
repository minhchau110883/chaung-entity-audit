package com.chaung.entityaudit.config.audit;

import com.chaung.entityaudit.config.Constants;
import com.chaung.entityaudit.security.SecurityUtils;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.stereotype.Component;

@Component
public class JaversAuthorProvider implements AuthorProvider {

   @Override
   public String provide() {
       String userName = SecurityUtils.getCurrentUserLogin().toString();
       return (userName != null ? userName : Constants.SYSTEM_ACCOUNT);
   }
}
