package com.example.vaadinspringpoc.data.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

/**
 * Pretty generic base entity with id generated via sequence, and a version column.
 *
 * Source: https://vaadin.com/docs/latest/tutorial/project-setup
 * https://start.vaadin.com/dl?preset=flow-crm-tutorial&preset=partial-latest
 */
@MappedSuperclass
@Getter
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgenerator")
    @SequenceGenerator(name = "idgenerator", initialValue = 1000)
    @Setter
    private Long id;

    @Version
    private int version;

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity that)) {
            return false;
        }
        if (getId() != null) {
            return getId().equals(that.getId());
        }
        return super.equals(that);
    }
}
