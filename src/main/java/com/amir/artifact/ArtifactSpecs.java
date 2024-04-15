package com.amir.artifact;

import org.springframework.data.jpa.domain.Specification;

public class ArtifactSpecs {
    public static Specification<Artifact> hasId(String providedId) {
        // root -> think of root as FROM
        // query -> Ordering, Distinct and Grouping By
        // criteriaBuilder -> construct criteria (WHERE Clause)
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), providedId);
    }

    public static Specification<Artifact> containsName(String providedName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + providedName.toLowerCase() + "%");
    }

    public static Specification<Artifact> containsDescription(String providedDescription) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + providedDescription.toLowerCase() + "%");
    }

    public static Specification<Artifact> hasOwnerName(String providedOwnerName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("owner").get("name")), providedOwnerName.toLowerCase());
    }
}