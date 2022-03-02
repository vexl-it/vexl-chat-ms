package com.cleevio.vexl.common;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class BaseControllerTest {

    @Autowired
    protected MockMvc mvc;

    @BeforeEach
    @SneakyThrows
    public void setup() {


    }
}
