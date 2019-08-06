package com.github.thorbenkuck.builder.test;

import com.github.thorbenkuck.builder.annotations.InstantiationBuilder;
import com.github.thorbenkuck.builder.annotations.IgnoreField;
import com.github.thorbenkuck.builder.annotations.SetterName;

@InstantiationBuilder
public class ToBuild {

    private String superDuperName;
    @SetterName("withExternalName")
    private String externalLameName;
    @IgnoreField
    private String shouldNotBeFilledLikeEver;

}
