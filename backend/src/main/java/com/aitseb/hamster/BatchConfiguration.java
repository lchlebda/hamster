package com.aitseb.hamster;

import com.aitseb.hamster.dao.Activity;
import com.aitseb.hamster.utils.JpaActivityWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Map.entry;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final Entry<String, String>[] fields = new Entry[]{
            entry("strava_id", "stravaId"),
            entry("date", "date"),
            entry("sport", "sport"),
            entry("description", "description"),
            entry("time", "time"),
            entry("rege_time", "regeTime"),
            entry("hr", "hr"),
            entry("hr_max", "hrMax"),
            entry("cadence", "cadence"),
            entry("power", "power"),
            entry("ef", "ef"),
            entry("tss", "tss"),
            entry("effort", "effort"),
            entry("elevation", "elevation"),
            entry("speed", "speed"),
            entry("distance", "distance"),
            entry("notes", "notes")};

    private static final LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<>();

    static {
        Arrays.stream(fields).forEach(field -> fieldsMap.put(field.getKey(), field.getValue()));
    }

    @Autowired public JobBuilderFactory jobBuilderFactory;
    @Autowired public StepBuilderFactory stepBuilderFactory;
    @Autowired private JpaActivityWriter jpaActivityWriter;

    @Value("${file.input}")
    private String fileInput;

    private interface StringToLocalDateConverter extends Converter<String, LocalDate>{ }

    @SuppressWarnings("ConstantConditions")
    @Bean
    public FlatFileItemReader<Activity> reader() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter((StringToLocalDateConverter) s -> LocalDate.parse(s, ISO_LOCAL_DATE));

        return new FlatFileItemReaderBuilder<Activity>()
                .name("activityItemReader")
                .resource(new ClassPathResource(fileInput))
                .delimited()
                .delimiter("|")
                .names(fieldsMap.values().toArray(new String[0]))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Activity.class);
                    setConversionService(conversionService);
                }})
                .build();
    }

    @Bean
    public Job importActivityJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importActivityJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Activity, Activity> chunk(10)
                .reader(reader())
                .writer(jpaActivityWriter)
                .build();
    }
}
