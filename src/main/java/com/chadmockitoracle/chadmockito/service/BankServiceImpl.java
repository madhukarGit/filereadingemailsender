package com.chadmockitoracle.chadmockito.service;

import com.chadmockitoracle.chadmockito.constants.AccountStatus;
import com.chadmockitoracle.chadmockito.constants.Region;
import com.chadmockitoracle.chadmockito.entity.BankCustomer;
import com.chadmockitoracle.chadmockito.repository.BankCustomerRepository;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Service
@Slf4j
public class BankServiceImpl {
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private Integer batchSize ;
    Faker faker = new Faker(new Locale("en-US"));

    private final BankCustomerRepository bankCustomerRepository;

    public BankServiceImpl(BankCustomerRepository bankCustomerRepository) {
        this.bankCustomerRepository = bankCustomerRepository;
    }

    public void saveLargeData(){
        long start = System.currentTimeMillis();
        List<BankCustomer> bankCustomers = new ArrayList<>();
        IntStream.range(0,1_00_000_00)
                .forEach(customer->{
                    bankCustomers.add(bankCustomer());
                });

        log.info("bank customers {}",bankCustomers.size());
        for (int i = 0; i < bankCustomers.size(); i = i + batchSize) {
            if( i+ batchSize > bankCustomers.size()){
                List<BankCustomer> bankCustomerList = bankCustomers.subList(i, bankCustomers.size() - 1);
                bankCustomerRepository.saveAll(bankCustomerList);
                break;
            }
            List<BankCustomer> bankCustomerList = bankCustomers.subList(i, i + batchSize);
            bankCustomerRepository.saveAll(bankCustomerList);
        }
        long end = System.currentTimeMillis();
        float msec = end - start;
        // converting it into seconds
        float sec= msec/1000F;
        // converting it into minutes
        float minutes=sec/60F;
        System.out.println(minutes + " minutes");
    }

    private BankCustomer bankCustomer(){
        BankCustomer bankCustomer =
                new BankCustomer();
        bankCustomer.setName(faker.funnyName().name());
        bankCustomer.setBranch(faker.country().capital());
        bankCustomer.setBalance((double)Math.round((Math.random()*1000000)*100)/100);
        bankCustomer.setRegisteredDate(LocalDate.now().minusMonths(4));
        bankCustomer.setStatus(fetchStatus());
        bankCustomer.setRegion(fetchRegion());
        return bankCustomer;
    }

    private String fetchRegion(){
        Random random = new Random();
        List<String> list =  List.of("AMEA","EMEA","NA");
        int value = random.nextInt(2);
        //log.info("value is "+list.get(value));
        return list.get(value);
    }
//    public String date() {
//        int hundredYears = 2;
//        LocalDate localDate = LocalDate.ofEpochDay(ThreadLocalRandom
//                .current().nextInt(hundredYears, hundredYears));
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
//                .ofPattern("dd-MM-yyyy");
//        return dateTimeFormatter.format(localDate);
//    }

    public LocalDate between(LocalDate startInclusive, LocalDate endExclusive) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endExclusive.toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("dd-MM-yyyy");
        String data = dateTimeFormatter.format(LocalDate.ofEpochDay(randomDay));
        return LocalDate.parse(data,dateTimeFormatter);
    }
    private String fetchStatus(){
        Random random = new Random();
        List<String> status =
                List.of("ACTIVE", "DEPRECATED", "INACTIVE", "LEAN");
         int value = random.nextInt(3);
        // log.info("value is "+status.get(value));
         return status.get(value);
    }


}
