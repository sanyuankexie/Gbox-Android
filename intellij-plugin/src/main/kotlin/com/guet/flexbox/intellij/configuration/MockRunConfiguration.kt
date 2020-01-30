package com.guet.flexbox.intellij.configuration

import com.guet.flexbox.intellij.BinaryLoader
import com.guet.flexbox.intellij.configuration.options.MockOptions
import com.guet.flexbox.intellij.runJar
import com.guet.flexbox.intellij.ui.MockSettingForm
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class MockRunConfiguration(project: Project, factory: ConfigurationFactory) :
        LocatableConfigurationBase<MockOptions>(project, factory, "Mock this package") {

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = MockSettingForm()

    override fun getState(
            executor: Executor,
            environment: ExecutionEnvironment
    ): RunProfileState? {
        val port = state!!.port
        val focus = state!!.packageJson!!
        return runJar(
                project,
                environment,
                BinaryLoader.mockJarPath,
                "--server.port=$port --package.focus=$focus"
        )
    }

    override fun getOptionsClass(): Class<out RunConfigurationOptions>? {
        return MockOptions::class.java
    }


}