package com.alura.cursos.foroHub.domain.response;

import com.alura.cursos.foroHub.domain.topic.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    Page<Response> findByActiveTrue(Pageable paged);
}
