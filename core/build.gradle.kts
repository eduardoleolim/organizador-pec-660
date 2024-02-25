plugins {
    id("org.eduardoleolim.organizadorpec660.kotlin-library-conventions")
}

dependencies {
    api("org.ktorm:ktorm-core:${properties["ktorm.version"]}")
    api("org.ktorm:ktorm-support-sqlite:${properties["ktorm.version"]}")
    api("io.github.willena:sqlite-jdbc:${properties["sqlite.version"]}")
    api("org.slf4j:slf4j-simple:${properties["slf4j.version"]}")
    api("io.insert-koin:koin-core:${properties["koin.version"]}")
}
