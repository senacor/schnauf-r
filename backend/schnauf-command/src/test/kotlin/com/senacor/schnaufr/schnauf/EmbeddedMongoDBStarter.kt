package com.senacor.schnaufr.schnauf

import com.mongodb.ConnectionString
import com.mongodb.reactivestreams.client.MongoClient
import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.*
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.extract.UserTempNaming
import de.flapdoodle.embed.process.runtime.Network
import org.litote.kmongo.reactivestreams.KMongo
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.lifecycle.MemoizedValue
import org.spekframework.spek2.style.specification.Suite


fun Suite.mongoDB(port: Int = 27017): MemoizedValue<MongoClient> {

    val mongoDBStarter = EmbeddedMongoDBStarter()
    return memoized(
        mode = CachingMode.SCOPE,
        factory = {
            mongoDBStarter.start(port)
            KMongo.createClient(ConnectionString("mongodb://localhost:$port"))
        },
        destructor = { mongoDBStarter.stop() }
    )
}

class EmbeddedMongoDBStarter {

    private lateinit var mongodExecutable: MongodExecutable
    private lateinit var mongod: MongodProcess

    fun start(port: Int = 27017) {
        val command = Command.MongoD

        val runtimeConfig = RuntimeConfigBuilder()
            .defaults(command)
            .artifactStore(ExtractedArtifactStoreBuilder()
                .defaults(command)
                .download(DownloadConfigBuilder()
                    .defaultsForCommand(command).build())
                .executableNaming(UserTempNaming()))
            .build()

        val mongodConfig = MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(Net(port, Network.localhostIsIPv6()))
            .build()

        val runtime = MongodStarter.getInstance(runtimeConfig)


        mongodExecutable = runtime.prepare(mongodConfig)
        mongod = mongodExecutable.start()
    }

    fun stop() {
        mongodExecutable.stop()
        mongod.stop()
    }
}
