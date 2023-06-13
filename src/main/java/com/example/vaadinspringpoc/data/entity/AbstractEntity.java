package com.example.vaadinspringpoc.data.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Pretty generic base entity with id generated via sequence, and a version column.
 *
 * Source: https://vaadin.com/docs/latest/tutorial/project-setup
 * https://start.vaadin.com/dl?preset=flow-crm-tutorial&preset=partial-latest
 */
@MappedSuperclass
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = {"id"})
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgenerator")
    @SequenceGenerator(name = "idgenerator", initialValue = 1000)
    @Setter
    private Long id;

    @Version
    private int version;
}
