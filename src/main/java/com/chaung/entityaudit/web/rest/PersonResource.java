package com.chaung.entityaudit.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.chaung.entityaudit.service.PersonService;
import com.chaung.entityaudit.web.rest.errors.BadRequestAlertException;
import com.chaung.entityaudit.web.rest.util.HeaderUtil;
import com.chaung.entityaudit.web.rest.util.PaginationUtil;
import com.chaung.entityaudit.service.dto.PersonDTO;
import com.chaung.entityaudit.service.dto.PersonCriteria;
import com.chaung.entityaudit.service.PersonQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Person.
 */
@RestController
@RequestMapping("/api")
public class PersonResource {

    private final Logger log = LoggerFactory.getLogger(PersonResource.class);

    private static final String ENTITY_NAME = "person";

    private final PersonService personService;

    private final PersonQueryService personQueryService;

    public PersonResource(PersonService personService, PersonQueryService personQueryService) {
        this.personService = personService;
        this.personQueryService = personQueryService;
    }

    /**
     * POST  /people : Create a new person.
     *
     * @param personDTO the personDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new personDTO, or with status 400 (Bad Request) if the person has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/people")
    @Timed
    public ResponseEntity<PersonDTO> createPerson(@RequestBody PersonDTO personDTO) throws URISyntaxException {
        log.debug("REST request to save Person : {}", personDTO);
        if (personDTO.getId() != null) {
            throw new BadRequestAlertException("A new person cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PersonDTO result = personService.save(personDTO);
        return ResponseEntity.created(new URI("/api/people/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /people : Updates an existing person.
     *
     * @param personDTO the personDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated personDTO,
     * or with status 400 (Bad Request) if the personDTO is not valid,
     * or with status 500 (Internal Server Error) if the personDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/people")
    @Timed
    public ResponseEntity<PersonDTO> updatePerson(@RequestBody PersonDTO personDTO) throws URISyntaxException {
        log.debug("REST request to update Person : {}", personDTO);
        if (personDTO.getId() == null) {
            return createPerson(personDTO);
        }
        PersonDTO result = personService.updatePerson(personDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, personDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /people : get all the people.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of people in body
     */
    @GetMapping("/people")
    @Timed
    public ResponseEntity<List<PersonDTO>> getAllPeople(PersonCriteria criteria, Pageable pageable) {
        log.debug("REST request to get People by criteria: {}", criteria);
        Page<PersonDTO> page = personQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/people");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /people/:id : get the "id" person.
     *
     * @param id the id of the personDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the personDTO, or with status 404 (Not Found)
     */
    @GetMapping("/people/{id}")
    @Timed
    public ResponseEntity<PersonDTO> getPerson(@PathVariable Long id) {
        log.debug("REST request to get Person : {}", id);
        PersonDTO personDTO = personService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(personDTO));
    }

    /**
     * DELETE  /people/:id : delete the "id" person.
     *
     * @param id the id of the personDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/people/{id}")
    @Timed
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        log.debug("REST request to delete Person : {}", id);
        personService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
