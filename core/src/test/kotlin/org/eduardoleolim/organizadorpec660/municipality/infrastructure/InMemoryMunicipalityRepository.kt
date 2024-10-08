package org.eduardoleolim.organizadorpec660.municipality.infrastructure

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

class InMemoryMunicipalityRepository : MunicipalityRepository {
    val municipalities: MutableMap<String, Municipality> = mutableMapOf()
    val federalEntities: MutableMap<String, FederalEntity> = mutableMapOf()

    override fun matching(criteria: Criteria): List<Municipality> {
        return InMemoryMunicipalityCriteriaParser.run {
            municipalities.values.toMutableList()
                .run { applyFilters(this, federalEntities.values.toList(), criteria) }
                .run { applyOrders(this, federalEntities.values.toList(), criteria) }
                .run { applyPagination(this, criteria.limit, criteria.offset) }
        }
    }

    override fun count(criteria: Criteria): Int {
        return InMemoryMunicipalityCriteriaParser.run {
            municipalities.values.toMutableList()
                .run { applyFilters(this, federalEntities.values.toList(), criteria) }
                .run { applyOrders(this, federalEntities.values.toList(), criteria) }
                .run { applyPagination(this, criteria.limit, criteria.offset) }.size
        }
    }

    override fun save(municipality: Municipality) {
        municipalities[municipality.id().toString()] = municipality
    }

    override fun delete(municipalityId: String) {
        municipalities.remove(municipalityId)
    }
}
