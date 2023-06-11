package com.example.vaadinspringpoc.data.repository;

import com.example.vaadinspringpoc.data.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("""
            select p from Player p
            where lower(p.firstName) like lower(concat('%', :searchTerm, '%'))
            or lower(p.lastName) like lower(concat('%', :searchTerm, '%'))
            order by lastName
            """)
    List<Player> search(@Param("searchTerm") String searchTerm);

}
