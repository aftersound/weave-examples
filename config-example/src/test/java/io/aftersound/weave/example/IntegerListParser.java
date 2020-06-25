package io.aftersound.weave.example;

import io.aftersound.weave.common.parser.FirstRawKeyValueParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Customized parser for list of integer values in form of delimited string
 */
public class IntegerListParser extends FirstRawKeyValueParser<List<Integer>> {

    private final Pattern pattern;

    public IntegerListParser(String delimiter) {
        this.pattern = Pattern.compile(delimiter);
    }

    @Override
    protected List<Integer> _parse(String rawValue) {
        String[] values = pattern.split(rawValue);
        List<Integer> ints = new ArrayList<>();
        for (String value : values) {
            ints.add(Integer.parseInt(value));
        }
        return ints;
    }

}
