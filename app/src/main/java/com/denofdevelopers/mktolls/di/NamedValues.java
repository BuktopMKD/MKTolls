package com.denofdevelopers.mktolls.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface NamedValues {
    public static final String tolls = "tolls";
    public static final String tollList = "toll.list";

}
