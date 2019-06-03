package com.senacor.schnaufr

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.impl.launcher.VertxCommandLauncher
import io.vertx.core.impl.launcher.VertxLifecycleHooks
import io.vertx.core.impl.launcher.commands.ExecUtils
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.SLF4JLogDelegateFactory
import java.io.File

class Starter : VertxCommandLauncher(), VertxLifecycleHooks {

    companion object {
        private const val CONFIG_FILE_PROPERTY = "logback.configurationFile"

        @JvmStatic
        fun main(vararg args: String) {
            Starter().dispatch(args)
        }
    }

    override fun beforeStartingVertx(options: VertxOptions?) {
        val logbackFile = File("logback.xml")
        if (logbackFile.exists()) {
            System.err.println("logback.xml does not exist.")
        } else {
            println("configuring logging to use slf4j + logback, using config file $logbackFile")
        }
        System.setProperty(CONFIG_FILE_PROPERTY, logbackFile.absolutePath)
        System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory::class.java.name)
        LoggerFactory.initialise()
    }


    override fun beforeStoppingVertx(vertx: Vertx?) {
    }

    override fun afterStoppingVertx() {
        System.clearProperty(CONFIG_FILE_PROPERTY)
        System.clearProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME)
    }

    override fun handleDeployFailed(vertx: Vertx, mainVerticle: String?, deploymentOptions: DeploymentOptions?, cause: Throwable?) {
        val logger = LoggerFactory.getLogger(Starter::class.java)
        logger.error("deployment of main verticle failed. deployment options: " + deploymentOptions!!.toJson().encodePrettily(), cause)
        vertx.close()
        ExecUtils.exitBecauseOfVertxDeploymentIssue()
    }

    override fun afterConfigParsed(config: JsonObject?) {
    }

    override fun afterStartingVertx(vertx: Vertx?) {
    }


    override fun beforeDeployingVerticle(deploymentOptions: DeploymentOptions?) {
    }
}
