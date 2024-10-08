package org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.bus

import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchById.SearchFederalEntityByIdQuery
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchById.SearchFederalEntityByIdQueryHandler
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQuery
import org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm.SearchFederalEntitiesByTermQueryHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormFederalEntityQueryHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Query>, QueryHandler<out Query, out Response>> = mapOf(
        SearchFederalEntityByIdQuery::class to searchByIdQueryHandler(),
        SearchFederalEntitiesByTermQuery::class to searchByTermQueryHandler()
    )

    private fun searchByIdQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val searcher: FederalEntitySearcher by inject()
        val queryHandler = SearchFederalEntityByIdQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByTermQueryHandler(): KtormQueryHandlerDecorator<out Query, out Response> {
        val searcher: FederalEntitySearcher by inject()
        val queryHandler = SearchFederalEntitiesByTermQueryHandler(searcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
