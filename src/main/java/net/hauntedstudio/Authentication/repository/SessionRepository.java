package net.hauntedstudio.Authentication.repository;

import net.hauntedstudio.Authentication.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
}
