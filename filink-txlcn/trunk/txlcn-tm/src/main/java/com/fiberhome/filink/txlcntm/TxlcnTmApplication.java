package com.fiberhome.filink.txlcntm;

import com.fiberhome.filink.txlcntm.support.TxLcnManagerBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TxlcnTmApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(TxlcnTmApplication.class);
        springApplication.setBanner(new TxLcnManagerBanner());
        springApplication.run(args);
    }

}
