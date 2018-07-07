package com.chaung.entityaudit.web.rest;

import com.chaung.entityaudit.domain.EntityAuditEvent;
import com.chaung.entityaudit.domain.Person;
import com.chaung.entityaudit.web.rest.util.PaginationUtil;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import com.chaung.entityaudit.security.AuthoritiesConstants;
import org.javers.shadow.Shadow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.annotation.Secured;
import com.codahale.metrics.annotation.Timed;

import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * REST controller for getting the audit events for entity
 */
@RestController
@RequestMapping("/api")
public class JaversEntityAuditResource {

    private final Logger log = LoggerFactory.getLogger(JaversEntityAuditResource.class);

    private final Javers javers;

    public JaversEntityAuditResource(Javers javers) {
        this.javers = javers;
    }

    /**
     * fetches all the audited entity types
     *
     * @return
     */
    @RequestMapping(value = "/audits/entity/all",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public List<String> getAuditedEntities() {

      return Arrays.asList("Person");
    }

    /**
     * fetches the last 100 change list for an entity class, if limit is passed fetches that many changes
     *
     * @return
     */
    @RequestMapping(value = "/audits/entity/changes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<EntityAuditEvent>> getChanges(@RequestParam(value = "entityType") String entityType,
                                                             @RequestParam(value = "limit") int limit)
        throws URISyntaxException, ClassNotFoundException {
        log.debug("REST request to get a page of EntityAuditEvents");
        Pageable pageRequest = createPageRequest(limit);

        Class entityTypeToFetch = Class.forName("com.chaung.entityaudit.domain." + entityType);
        QueryBuilder jqlQuery = QueryBuilder.byClass(entityTypeToFetch)
                                            .limit(limit)
                                            .withNewObjectChanges(true);

        List<CdoSnapshot> snapshots =  javers.findSnapshots(jqlQuery.build());

        List<EntityAuditEvent> auditEvents = new ArrayList<>();

        snapshots.forEach(snapshot -> {
           EntityAuditEvent event = EntityAuditEvent.fromJaversSnapshot(snapshot);
           event.setEntityType(entityType);
           auditEvents.add(event);
        });

        Page<EntityAuditEvent> page = new PageImpl<>(auditEvents);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/audits/entity/changes");

        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

    }

    /**
     * fetches a previous version for for an entity class and id
     *
     * @return
     */
    @RequestMapping(value = "/audits/entity/changes/version/previous",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<EntityAuditEvent> getPrevVersion(@RequestParam(value = "qualifiedName") String qualifiedName,
                                                           @RequestParam(value = "entityId") Long entityId,
                                                           @RequestParam(value = "commitVersion") Long commitVersion)
        throws URISyntaxException, ClassNotFoundException {

        Class entityTypeToFetch = Class.forName("com.chaung.entityaudit.domain." + qualifiedName);

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(entityId, entityTypeToFetch)
                                           .limit(1)
                                           .withVersion(commitVersion - 1)
                                           .withNewObjectChanges(true);

        EntityAuditEvent prev = EntityAuditEvent.fromJaversSnapshot(javers.findSnapshots(jqlQuery.build()).get(0));

        return new ResponseEntity<>(prev, HttpStatus.OK);

    }

    @RequestMapping(value = "/audits/entity/history",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<Diff>> getHistory(@RequestParam(value = "qualifiedName") String qualifiedName,
                                                           @RequestParam(value = "entityId") Long entityId)
        throws Exception {

        Class entityTypeToFetch = Class.forName("com.chaung.entityaudit.domain." + qualifiedName);


        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(entityId, entityTypeToFetch);
        List<Shadow<EntityAuditEvent>> history = javers.findShadows(jqlQuery.build());
        List<Diff> diffList = new ArrayList<>();
        if (history != null && history.size() > 0) {
            for(int i = 0; i < (history.size() - 1); i++) {
                Diff diff = javers.compare(history.get(i + 1).get(), history.get(i).get());
                diffList.add(diff);
            }
            EntityAuditEvent emptyObj = new EntityAuditEvent();
            //emptyObj.setId(history.get(history.size() - 1).get().getId());

            Diff diff = javers.compare(new Person(), history.get(history.size() - 1).get());
            diffList.add(diff);
        }

        diffList.forEach(diff -> System.out.println(diff));

        return new ResponseEntity<>(diffList, HttpStatus.OK);

    }

    /**
     * creates a page request object for PaginationUti
     *
     * @return
     */
    private Pageable createPageRequest(int size) {
        return new PageRequest(0, size);
    }

}
