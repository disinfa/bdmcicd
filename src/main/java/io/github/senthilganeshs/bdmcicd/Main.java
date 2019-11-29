package io.github.senthilganeshs.bdmcicd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.github.senthilganeshs.bdmcicd.client.DISClient;
import io.github.senthilganeshs.bdmcicd.client.actions.Action;
import io.github.senthilganeshs.bdmcicd.client.query.Query;


public final class Main {

    public static void main(final String[] args) {
        
        final String propsFile = System.getProperty("dis.rest.config.props", 
            FileSystems.getDefault().getPath(
                "src", 
                    "main", 
                        "resources", 
                            "dis.rest.config.properties")
            .toFile().getAbsolutePath());
        
        final Properties disProperties = new Properties();

        try (final InputStream is = Files.newInputStream(
            FileSystems.getDefault().getPath(propsFile))) {
            disProperties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        final Options options = new Options();
        
        final String hostName = disProperties.getProperty("host.name");
        final int    disHttpPort = Integer.parseInt(disProperties.getProperty("dis.http.port"));
        final String userName = disProperties.getProperty("dis.username");
        final String password = disProperties.getProperty("dis.password");
        final String securityDomain = disProperties.getProperty("security.domain");
        final String repositoryName = disProperties.getProperty("repository.name");
        final String repositoryUser = disProperties.getProperty("repository.user");
        final String repositoryPass = disProperties.getProperty("repository.pass");
        
        try(final DISClient disHttpClient = DISClient.create(
            hostName, 
            disHttpPort,
            userName, 
            password, 
            securityDomain, 
            repositoryName, 
            repositoryUser,
            repositoryPass)) {
            
            options.addRequiredOption("o", "oauth", true, "OAuth token generated for the repository where MRS contents are saved.");
            options.addRequiredOption("r", "repo", true, "Repository path. Written as user-name/repository-name.");
            options.addRequiredOption("ch", "commitHash", true, "Commit hash returned by GitHub event.");
            
            //FIXME: Create Options group for DIS and GitHub Repository options.
            options.addOption("q", "query", false, "Query the objects checked-in.");
            options.addOption("e", "export", true, "Export the objects checked-in into file.");
            options.addOption("a", "app", true, "Application name, required param in-case of export/deploy options.");
            options.addOption("pn", "patch-name", true, "Patch name to be associated with the export payload.");
            options.addOption("pd", "patch-desc", true, "Path description to be associated with the export payload.");
            options.addOption("d", "deploy", true, "Deploy the piar file into Data Integration Service.");
            options.addOption("x", "run", false, "Run Executable objects such as Mappings/Workflows which are checked-in.");
            options.addOption("re", "redirect", true, "Redirect output to a file.");
            
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);
            
            Action action = null;
            
            if (line.hasOption("oauth") && line.hasOption("repo") && line.hasOption("commitHash")) {
                action = Query.createGitHubQuery(            
                    line.getOptionValue("oauth"), //oauth  "bfaf4a907d0a2d25f014b820adf7ee662dd2c204" 
                    line.getOptionValue("repo"), //repo path "senthilganeshs/mrs-repo"
                    line.getOptionValue("commitHash"), //commit hash "305b1bd068636e01ea92716428c6cd7bb586a6d1"
                    disHttpClient).execute();
                
                if (line.hasOption("query")) {
                    if (line.hasOption("redirect")) {
                        OutputStream os = Files.newOutputStream(FileSystems.getDefault().getPath(line.getOptionValue("redirect")));
                        action.report().render(os);
                        os.close();
                        return;
                    } else {
                        action.report().render(System.out);
                        return;
                    }
                } else if (line.hasOption("export")) {
                    if (line.hasOption("app") && line.hasOption("patch-name") && line.hasOption("patch-desc")) {
                        if (line.hasOption("redirect")) {
                            OutputStream os = Files.newOutputStream(FileSystems.getDefault().getPath(line.getOptionValue("redirect")));
                            action.export(
                                line.getOptionValue("app"), 
                                line.getOptionValue("export"), 
                                line.getOptionValue("patch-name"),
                                line.getOptionValue("patch-desc")).report().render(os);
                            os.close();
                            return;
                        } else {
                            action.export(
                                line.getOptionValue("app"), 
                                line.getOptionValue("export"), 
                                line.getOptionValue("patch-name"),
                                line.getOptionValue("patch-desc")).report().render(System.out);
                            return;
                        }
                    }                    
                } else if (line.hasOption("deploy")) {
                    //TBD
                    return;
                } else if (line.hasOption("run")) {
                    //TBD
                    return;
                }
            }
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("bdmcicd", "DIS REST GitHub Client", options, 
                "***WARNING*** This is not production ready code.\nThe purpose of this sample is only to serve as an example.");
            throw new RuntimeException("Please check your input arguments and try again");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("bdmcicd", "DIS REST GitHub Client", options, 
                "***WARNING*** This is not production ready code.\nThe purpose of this tool is to only serve as an example.");
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        } 
    }
}