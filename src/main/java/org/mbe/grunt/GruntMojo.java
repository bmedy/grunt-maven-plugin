package org.mbe.grunt;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo( name = "build", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class GruntMojo extends AbstractMojo {
    @Parameter( defaultValue = "static", required = true )
    File gruntProjectDirectory;
    @Parameter( defaultValue = "${os.name}", readonly = true)
    String osName;
    @Parameter( defaultValue = "install", required = true )
    String npmInstallArgs;
    @Parameter( defaultValue = "install --no-color", required = true )
    String bowerInstallArgs;
    @Parameter( defaultValue = "dist --no-color", required = true )
    String gruntInstallArgs;

    public void execute() throws MojoExecutionException {
        getLog().debug("Test logger");
        npmInstall();
        bowerInstall();
        grunt();
    }

    void npmInstall() throws MojoExecutionException {
        logToolVersion("node");
        logToolVersion("npm");
        logAndExecuteCommand("npm "+ npmInstallArgs);
    }

    void bowerInstall() throws MojoExecutionException {
        logToolVersion("bower");
        logAndExecuteCommand("bower " + bowerInstallArgs);
    }
    void grunt() throws MojoExecutionException {
        logToolVersion("grunt");
        logAndExecuteCommand("grunt " + gruntInstallArgs);
    }

    void logToolVersion(final String toolName) throws MojoExecutionException {
        getLog().info(toolName + " version :");
        executeCommand(toolName + " --version");
    }

    void logAndExecuteCommand(String command) throws MojoExecutionException {
        logCommand(command);
        executeCommand(command);
    }

    void logCommand(String command) {
        getLog().info("--------------------------------------");
        getLog().info("         " + command.toUpperCase());
        getLog().info("--------------------------------------");
    }

    void executeCommand(String command) throws MojoExecutionException {
        try {
            if (isWindows()) {
                command = "cmd /c " + command;
            }
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(gruntProjectDirectory);
            executor.execute(cmdLine);
        } catch (IOException e) {
            throw new MojoExecutionException("Error during : " + command, e);
        }
    }


    private boolean isWindows() {
        return osName.startsWith("Windows");
    }
}
