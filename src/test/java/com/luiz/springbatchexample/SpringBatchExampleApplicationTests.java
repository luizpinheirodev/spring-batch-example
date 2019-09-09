package com.luiz.springbatchexample;

import com.luiz.springbatchexample.batch.BatchConfig;
import com.luiz.springbatchexample.batch.CapitalizeNamesJobConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBatchExampleApplicationTests.class})
class SpringBatchExampleApplicationTests {

    private static Path csvFilesPath, testInputsPath;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeClass
    public static void copyFiles()
            throws URISyntaxException, IOException {
        csvFilesPath = Paths.get(new ClassPathResource("csv").getURI());
        testInputsPath = Paths.get("target/test-inputs");
        try {
            Files.createDirectory(testInputsPath);
        } catch (Exception e) {
            // if directory exists do nothing
        }

        FileSystemUtils.copyRecursively(csvFilesPath, testInputsPath);
    }


    @Test
    public void testHelloWorldJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getExitStatus().getExitCode())
                .isEqualTo("COMPLETED");
    }

    @Configuration
    @Import({BatchConfig.class, CapitalizeNamesJobConfig.class})
    static class BatchTestConfig {

        @Autowired
        private Job capitalizedNamesJob;

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils()
                throws NoSuchJobException {
            JobLauncherTestUtils jobLauncherTestUtils =
                    new JobLauncherTestUtils();
            jobLauncherTestUtils.setJob(capitalizedNamesJob);

            return jobLauncherTestUtils;
        }
    }
}
