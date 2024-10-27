package com.sondertara.common;

import com.sondertara.common.text.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/7/24 14:59
 */
class StringUtilsTest {


    @Test
    void testAppendIfMissing() {
        String result = StringUtils.appendIfMissing("abcdxyz", "ddd", "xyz");
        Assertions.assertEquals("abcdxyz", result);
    }

    @Test
    void underlineToCamel() {
        String result = StringUtils.underlineToCamel("ID",true);
        System.out.println(result);
    }


    @Test
    void testAppendIfMissing1() {
        {
            String result = StringUtils.appendIfMissing("abcdxyz", "ddd", "mod");
            Assertions.assertEquals("abcdxyzddd", result);
        }
        {
            String result1 = StringUtils.appendIfMissing("abcdmod", "xyz", "mod");
            Assertions.assertEquals("abcdmod", result1);
        }
        {
            String result1 = StringUtils.appendIfMissing("abcdxyz", "xyz", "mod");
            Assertions.assertEquals("abcdxyz", result1);
        }
    }

    @Test
    void testAppendIfMissing2() {
        String result = StringUtils.appendIfMissing("abcdxyz", "xyz", "mod");
        Assertions.assertEquals("abcdxyz", result);
    }


    @Test
    void testCapitalize() {
        String result = StringUtils.capitalize("abc");
        Assertions.assertEquals("Abc", result);
    }

    @Test
    void testCenter() {
        String result = StringUtils.center("abcd", 2);
        Assertions.assertEquals("abcd", result);
    }

    @Test
    void testCenter2() {
        String result = StringUtils.center("abcd", 8, '*');
        Assertions.assertEquals("**abcd**", result);
    }


    @Test
    void testChomp() {
        String result = StringUtils.chomp("abc \r");
        Assertions.assertEquals("abc ", result);
    }


    @Test
    void testChop() {
        String result = StringUtils.chop("abcd");
        Assertions.assertEquals("abc", result);
    }

    @Test
    void testCompare() {
        int result = StringUtils.compare("abc", "adc");
        Assertions.assertTrue(result<0);
    }

    @Test
    void testCompare2() {
        int result = StringUtils.compare("abc", null, true);
        Assertions.assertEquals(1, result);
    }

    @Test
    void testCompareIgnoreCase() {
        int result = StringUtils.compareIgnoreCase("Abc", "abc");
        Assertions.assertEquals(0, result);
    }


    @Test
    void testContains() {
        boolean result = StringUtils.contains("abcde", "bc");
        Assertions.assertEquals(true, result);
    }

    @Test
    void testContains2() {
        boolean result = StringUtils.contains("abcd", "d");
        Assertions.assertEquals(true, result);
    }

    @Test
    void testContainsAny() {
        boolean result = StringUtils.containsAny("abcd", 'a', 'e');
        Assertions.assertEquals(true, result);
    }


    @Test
    void testContainsIgnoreCase() {
        boolean result = StringUtils.containsIgnoreCase("AbcdE", "cde");
        Assertions.assertEquals(true, result);
    }

    @Test
    void testContainsNone() {
        boolean result = StringUtils.containsNone("abdc", 'b', 'd');
        Assertions.assertEquals(false, result);
    }


    @Test
    void testContainsWhitespace() {
        boolean result = StringUtils.containsWhitespace("afb ffg g");
        Assertions.assertEquals(true, result);
    }
//
//    @Test
//    void testCountMatches() {
//        int result = StringUtils.countMatches(str, ch);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testCountMatches2() {
//        int result = StringUtils.countMatches(str, sub);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testDefaultIfBlank() {
//        T result = StringUtils.defaultIfBlank(str, defaultStr);
//        Assertions.assertEquals(null, result);
//    }
//
//    @Test
//    void testDefaultIfEmpty() {
//        T result = StringUtils.defaultIfEmpty(str, defaultStr);
//        Assertions.assertEquals(null, result);
//    }
//
//    @Test
//    void testDefaultString() {
//        String result = StringUtils.defaultString(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testDefaultString2() {
//        String result = StringUtils.defaultString(str, nullDefault);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testDeleteWhitespace() {
//        String result = StringUtils.deleteWhitespace(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testDifference() {
//        String result = StringUtils.difference(str1, str2);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testEndsWith() {
//        boolean result = StringUtils.endsWith(str, suffix);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEndsWith2() {
//        boolean result = StringUtils.endsWith(str, suffix, ignoreCase);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEndsWithAny() {
//        boolean result = StringUtils.endsWithAny(sequence, searchStrings);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEndsWithIgnoreCase() {
//        boolean result = StringUtils.endsWithIgnoreCase(str, suffix);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEquals() {
//        boolean result = StringUtils.equals(cs1, cs2);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEqualsAny() {
//        boolean result = StringUtils.equalsAny(string, searchStrings);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEqualsAnyIgnoreCase() {
//        boolean result = StringUtils.equalsAnyIgnoreCase(string, searchStrings);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEqualsIgnoreCase() {
//        boolean result = StringUtils.equalsIgnoreCase(cs1, cs2);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testFirstNonBlank() {
//        T result = StringUtils.firstNonBlank(values);
//        Assertions.assertEquals(null, result);
//    }
//
//    @Test
//    void testFirstNonEmpty() {
//        T result = StringUtils.firstNonEmpty(values);
//        Assertions.assertEquals(null, result);
//    }
//
//    @Test
//    void testGetBytes() {
//        byte[] result = StringUtils.getBytes(string, charset);
//        Assertions.assertArrayEquals(new byte[]{(byte) 0}, result);
//    }
//
//    @Test
//    void testGetBytes2() {
//        byte[] result = StringUtils.getBytes(string, charset);
//        Assertions.assertArrayEquals(new byte[]{(byte) 0}, result);
//    }
//
//    @Test
//    void testGetCommonPrefix() {
//        String result = StringUtils.getCommonPrefix(strs);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGetDigits() {
//        String result = StringUtils.getDigits(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGetFuzzyDistance() {
//        int result = StringUtils.getFuzzyDistance(term, query, locale);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testGetIfBlank() {
//        T result = StringUtils.getIfBlank(str, defaultSupplier);
//        Assertions.assertEquals(null, result);
//    }
//
//    @Test
//    void testGetIfEmpty() {
//        T result = StringUtils.getIfEmpty(str, defaultSupplier);
//        Assertions.assertEquals(null, result);
//    }
//
//    @Test
//    void testGetJaroWinklerDistance() {
//        double result = StringUtils.getJaroWinklerDistance(first, second);
//        Assertions.assertEquals(0d, result);
//    }
//
//    @Test
//    void testGetLevenshteinDistance() {
//        int result = StringUtils.getLevenshteinDistance(s, t);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testGetLevenshteinDistance2() {
//        int result = StringUtils.getLevenshteinDistance(s, t, threshold);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOf() {
//        int result = StringUtils.indexOf(seq, searchSeq);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOf2() {
//        int result = StringUtils.indexOf(seq, searchSeq, startPos);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOf3() {
//        int result = StringUtils.indexOf(seq, searchChar);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOf4() {
//        int result = StringUtils.indexOf(seq, searchChar, startPos);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOfAny() {
//        int result = StringUtils.indexOfAny(cs, searchChars);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOfAny2() {
//        int result = StringUtils.indexOfAny(str, searchStrs);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOfAny3() {
//        int result = StringUtils.indexOfAny(cs, searchChars);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOfAnyBut() {
//        int result = StringUtils.indexOfAnyBut(cs, searchChars);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOfAnyBut2() {
//        int result = StringUtils.indexOfAnyBut(seq, searchChars);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOfDifference() {
//        int result = StringUtils.indexOfDifference(css);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOfDifference2() {
//        int result = StringUtils.indexOfDifference(cs1, cs2);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOfIgnoreCase() {
//        int result = StringUtils.indexOfIgnoreCase(str, searchStr);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIndexOfIgnoreCase2() {
//        int result = StringUtils.indexOfIgnoreCase(str, searchStr, startPos);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testIsAllBlank() {
//        boolean result = StringUtils.isAllBlank(css);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAllEmpty() {
//        boolean result = StringUtils.isAllEmpty(css);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAllLowerCase() {
//        boolean result = StringUtils.isAllLowerCase(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAllUpperCase() {
//        boolean result = StringUtils.isAllUpperCase(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAlpha() {
//        boolean result = StringUtils.isAlpha(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAlphanumeric() {
//        boolean result = StringUtils.isAlphanumeric(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAlphanumericSpace() {
//        boolean result = StringUtils.isAlphanumericSpace(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAlphaSpace() {
//        boolean result = StringUtils.isAlphaSpace(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAnyBlank() {
//        boolean result = StringUtils.isAnyBlank(css);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAnyEmpty() {
//        boolean result = StringUtils.isAnyEmpty(css);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsAsciiPrintable() {
//        boolean result = StringUtils.isAsciiPrintable(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsBlank() {
//        boolean result = StringUtils.isBlank(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsEmpty() {
//        boolean result = StringUtils.isEmpty(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsMixedCase() {
//        boolean result = StringUtils.isMixedCase(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsNoneBlank() {
//        boolean result = StringUtils.isNoneBlank(css);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsNoneEmpty() {
//        boolean result = StringUtils.isNoneEmpty(css);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsNotBlank() {
//        boolean result = StringUtils.isNotBlank(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsNotEmpty() {
//        boolean result = StringUtils.isNotEmpty(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsNumeric() {
//        boolean result = StringUtils.isNumeric(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsNumericSpace() {
//        boolean result = StringUtils.isNumericSpace(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsWhitespace() {
//        boolean result = StringUtils.isWhitespace(cs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testJoin() {
//        String result = StringUtils.join(array, delimiter);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin2() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin3() {
//        String result = StringUtils.join(array, delimiter);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin4() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin5() {
//        String result = StringUtils.join(array, delimiter);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin6() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin7() {
//        String result = StringUtils.join(array, delimiter);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin8() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin9() {
//        String result = StringUtils.join(array, delimiter);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin10() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin11() {
//        String result = StringUtils.join(array, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin12() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin13() {
//        String result = StringUtils.join(iterable, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin14() {
//        String result = StringUtils.join(iterable, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin15() {
//        String result = StringUtils.join(iterator, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin16() {
//        String result = StringUtils.join(iterator, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin17() {
//        String result = StringUtils.join(list, separator, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin18() {
//        String result = StringUtils.join(list, separator, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin19() {
//        String result = StringUtils.join(array, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin20() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin21() {
//        String result = StringUtils.join(array, delimiter);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin22() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin23() {
//        String result = StringUtils.join(array, delimiter);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin24() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin25() {
//        String result = StringUtils.join(array, delimiter);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin26() {
//        String result = StringUtils.join(array, delimiter, startIndex, endIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin27() {
//        String result = StringUtils.join(elements);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoinWith() {
//        String result = StringUtils.joinWith(delimiter, array);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testLastIndexOf() {
//        int result = StringUtils.lastIndexOf(seq, searchSeq);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testLastIndexOf2() {
//        int result = StringUtils.lastIndexOf(seq, searchSeq, startPos);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testLastIndexOf3() {
//        int result = StringUtils.lastIndexOf(seq, searchChar);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testLastIndexOf4() {
//        int result = StringUtils.lastIndexOf(seq, searchChar, startPos);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testLastIndexOfAny() {
//        int result = StringUtils.lastIndexOfAny(str, searchStrs);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testLastIndexOfIgnoreCase() {
//        int result = StringUtils.lastIndexOfIgnoreCase(str, searchStr);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testLastIndexOfIgnoreCase2() {
//        int result = StringUtils.lastIndexOfIgnoreCase(str, searchStr, startPos);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testLastOrdinalIndexOf() {
//        int result = StringUtils.lastOrdinalIndexOf(str, searchStr, ordinal);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testLeft() {
//        String result = StringUtils.left(str, len);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testLeftPad() {
//        String result = StringUtils.leftPad(str, size);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testLeftPad2() {
//        String result = StringUtils.leftPad(str, size, padChar);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testLeftPad3() {
//        String result = StringUtils.leftPad(str, size, padStr);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testLength() {
//        int result = StringUtils.length(cs);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testLowerCase() {
//        String result = StringUtils.lowerCase(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testLowerCase2() {
//        String result = StringUtils.lowerCase(str, locale);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testMid() {
//        String result = StringUtils.mid(str, pos, len);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testNormalizeSpace() {
//        String result = StringUtils.normalizeSpace(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testOrdinalIndexOf() {
//        int result = StringUtils.ordinalIndexOf(str, searchStr, ordinal);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testOverlay() {
//        String result = StringUtils.overlay(str, overlay, start, end);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testPrependIfMissing() {
//        String result = StringUtils.prependIfMissing(str, prefix, prefixes);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testPrependIfMissingIgnoreCase() {
//        String result = StringUtils.prependIfMissingIgnoreCase(str, prefix, prefixes);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemove() {
//        String result = StringUtils.remove(str, remove);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemove2() {
//        String result = StringUtils.remove(str, remove);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveAll() {
//        String result = StringUtils.removeAll(text, regex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveEnd() {
//        String result = StringUtils.removeEnd(str, remove);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveEndIgnoreCase() {
//        String result = StringUtils.removeEndIgnoreCase(str, remove);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveFirst() {
//        String result = StringUtils.removeFirst(text, regex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveIgnoreCase() {
//        String result = StringUtils.removeIgnoreCase(str, remove);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemovePattern() {
//        String result = StringUtils.removePattern(source, regex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveStart() {
//        String result = StringUtils.removeStart(str, remove);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveStart2() {
//        String result = StringUtils.removeStart(str, remove);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveStartIgnoreCase() {
//        String result = StringUtils.removeStartIgnoreCase(str, remove);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRepeat() {
//        String result = StringUtils.repeat(ch, repeat);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRepeat2() {
//        String result = StringUtils.repeat(str, repeat);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRepeat3() {
//        String result = StringUtils.repeat(str, separator, repeat);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplace() {
//        String result = StringUtils.replace(text, searchString, replacement);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplace2() {
//        String result = StringUtils.replace(text, searchString, replacement, max);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceAll() {
//        String result = StringUtils.replaceAll(text, regex, replacement);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceChars() {
//        String result = StringUtils.replaceChars(str, searchChar, replaceChar);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceChars2() {
//        String result = StringUtils.replaceChars(str, searchChars, replaceChars);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceEach() {
//        String result = StringUtils.replaceEach(text, searchList, replacementList);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceEachRepeatedly() {
//        String result = StringUtils.replaceEachRepeatedly(text, searchList, replacementList);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceFirst() {
//        String result = StringUtils.replaceFirst(text, regex, replacement);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceIgnoreCase() {
//        String result = StringUtils.replaceIgnoreCase(text, searchString, replacement);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceIgnoreCase2() {
//        String result = StringUtils.replaceIgnoreCase(text, searchString, replacement, max);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceOnce() {
//        String result = StringUtils.replaceOnce(text, searchString, replacement);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceOnceIgnoreCase() {
//        String result = StringUtils.replaceOnceIgnoreCase(text, searchString, replacement);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplacePattern() {
//        String result = StringUtils.replacePattern(source, regex, replacement);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReverse() {
//        String result = StringUtils.reverse(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReverseDelimited() {
//        String result = StringUtils.reverseDelimited(str, separatorChar);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRight() {
//        String result = StringUtils.right(str, len);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRightPad() {
//        String result = StringUtils.rightPad(str, size);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRightPad2() {
//        String result = StringUtils.rightPad(str, size, padChar);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRightPad3() {
//        String result = StringUtils.rightPad(str, size, padStr);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRotate() {
//        String result = StringUtils.rotate(str, shift);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSplit() {
//        String[] result = StringUtils.split(str);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplit2() {
//        String[] result = StringUtils.split(str, separatorChar);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplit3() {
//        String[] result = StringUtils.split(str, separatorChars);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplit4() {
//        String[] result = StringUtils.split(str, separatorChars, max);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitByCharacterType() {
//        String[] result = StringUtils.splitByCharacterType(str);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitByCharacterTypeCamelCase() {
//        String[] result = StringUtils.splitByCharacterTypeCamelCase(str);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitByWholeSeparator() {
//        String[] result = StringUtils.splitByWholeSeparator(str, separator);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitByWholeSeparator2() {
//        String[] result = StringUtils.splitByWholeSeparator(str, separator, max);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitByWholeSeparatorPreserveAllTokens() {
//        String[] result = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitByWholeSeparatorPreserveAllTokens2() {
//        String[] result = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator, max);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitPreserveAllTokens() {
//        String[] result = StringUtils.splitPreserveAllTokens(str);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitPreserveAllTokens2() {
//        String[] result = StringUtils.splitPreserveAllTokens(str, separatorChar);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitPreserveAllTokens3() {
//        String[] result = StringUtils.splitPreserveAllTokens(str, separatorChars);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSplitPreserveAllTokens4() {
//        String[] result = StringUtils.splitPreserveAllTokens(str, separatorChars, max);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testStartsWith() {
//        boolean result = StringUtils.startsWith(str, prefix);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testStartsWith2() {
//        boolean result = StringUtils.startsWith(str, prefix, ignoreCase);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testStartsWithAny() {
//        boolean result = StringUtils.startsWithAny(sequence, searchStrings);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testStartsWithIgnoreCase() {
//        boolean result = StringUtils.startsWithIgnoreCase(str, prefix);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testStrip() {
//        String result = StringUtils.strip(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStrip2() {
//        String result = StringUtils.strip(str, stripChars);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStripAccents() {
//        String result = StringUtils.stripAccents(input);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStripAll() {
//        String[] result = StringUtils.stripAll(strs);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testStripAll2() {
//        String[] result = StringUtils.stripAll(strs, stripChars);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testStripEnd() {
//        String result = StringUtils.stripEnd(str, stripChars);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStripStart() {
//        String result = StringUtils.stripStart(str, stripChars);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStripToEmpty() {
//        String result = StringUtils.stripToEmpty(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStripToNull() {
//        String result = StringUtils.stripToNull(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstring() {
//        String result = StringUtils.substring(str, start);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstring2() {
//        String result = StringUtils.substring(str, start, end);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringAfter() {
//        String result = StringUtils.substringAfter(str, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringAfter2() {
//        String result = StringUtils.substringAfter(str, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringAfterLast() {
//        String result = StringUtils.substringAfterLast(str, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringAfterLast2() {
//        String result = StringUtils.substringAfterLast(str, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringBefore() {
//        String result = StringUtils.substringBefore(str, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringBefore2() {
//        String result = StringUtils.substringBefore(str, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringBeforeLast() {
//        String result = StringUtils.substringBeforeLast(str, separator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringBetween() {
//        String result = StringUtils.substringBetween(str, tag);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringBetween2() {
//        String result = StringUtils.substringBetween(str, open, close);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubstringsBetween() {
//        String[] result = StringUtils.substringsBetween(str, open, close);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testSwapCase() {
//        String result = StringUtils.swapCase(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testToCodePoints() {
//        int[] result = StringUtils.toCodePoints(cs);
//        Assertions.assertArrayEquals(new int[]{0}, result);
//    }
//
//    @Test
//    void testToEncodedString() {
//        String result = StringUtils.toEncodedString(bytes, charset);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testToRootLowerCase() {
//        String result = StringUtils.toRootLowerCase(source);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testToRootUpperCase() {
//        String result = StringUtils.toRootUpperCase(source);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testToString() {
//        String result = StringUtils.toString(bytes, charsetName);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTruncate() {
//        String result = StringUtils.truncate(str, maxWidth);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTruncate2() {
//        String result = StringUtils.truncate(str, offset, maxWidth);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUncapitalize() {
//        String result = StringUtils.uncapitalize(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUnwrap() {
//        String result = StringUtils.unwrap(str, wrapChar);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUnwrap2() {
//        String result = StringUtils.unwrap(str, wrapToken);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUpperCase() {
//        String result = StringUtils.upperCase(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUpperCase2() {
//        String result = StringUtils.upperCase(str, locale);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testValueOf() {
//        String result = StringUtils.valueOf(value);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testWrap() {
//        String result = StringUtils.wrap(str, wrapWith);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testWrap2() {
//        String result = StringUtils.wrap(str, wrapWith);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testWrapIfMissing() {
//        String result = StringUtils.wrapIfMissing(str, wrapWith);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testWrapIfMissing2() {
//        String result = StringUtils.wrapIfMissing(str, wrapWith);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testIsTextLineBreak() {
//        boolean result = StringUtils.isTextLineBreak(c);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testCharToString() {
//        String result = StringUtils.charToString(c);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testIsWhitespace2() {
//        boolean result = StringUtils.isWhitespace(str);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testUseValueIfNull() {
//        String result = StringUtils.useValueIfNull(str, defaultValue);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUseValueIfBlank() {
//        String result = StringUtils.useValueIfBlank(str, defaultValue);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUseValueIfEmpty() {
//        String result = StringUtils.useValueIfEmpty(str, defaultValue);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGetEmptyIfNull() {
//        String result = StringUtils.getEmptyIfNull(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGetEmptyIfBlank() {
//        String result = StringUtils.getEmptyIfBlank(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGetNullIfEmpty() {
//        String result = StringUtils.getNullIfEmpty(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGetNullIfBlank() {
//        String result = StringUtils.getNullIfBlank(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTrimOrEmpty() {
//        String result = StringUtils.trimOrEmpty(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTrimOrNull() {
//        String result = StringUtils.trimOrNull(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin28() {
//        String result = StringUtils.join(separator, objects);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin29() {
//        String result = StringUtils.join(separator, prefix, suffix, filterNull, objects);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin30() {
//        String result = StringUtils.join(separator, prefix, suffix, objects, mapper, predicate);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin31() {
//        String result = StringUtils.join(separator, objects, startIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testJoin32() {
//        String result = StringUtils.join(separator, objects, startIndex, length);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testIterateJoin() {
//        String result = StringUtils.iterateJoin(separator, itemPrefix, itemSuffix, objs);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testIterateJoin2() {
//        String result = StringUtils.iterateJoin(separator, itemPrefix, itemSuffix, objs);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testInsert() {
//        String result = StringUtils.insert(string, slotIndexes, insertment);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSlice() {
//        String[] result = StringUtils.slice(str, substringLength);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testDecodeHexNibble() {
//        int result = StringUtils.decodeHexNibble(c);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testSubstringMatch() {
//        boolean result = StringUtils.substringMatch(str, index, substring);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testGetBytesUtf8() {
//        byte[] result = StringUtils.getBytesUtf8(string);
//        Assertions.assertArrayEquals(new byte[]{(byte) 0}, result);
//    }
//
//    @Test
//    void testNewStringUtf8() {
//        String result = StringUtils.newStringUtf8(bytes);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testNewString() {
//        String result = StringUtils.newString(bytes, charset);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testNewStringUsAscii() {
//        String result = StringUtils.newStringUsAscii(bytes);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveDuplicateWhitespace() {
//        String result = StringUtils.removeDuplicateWhitespace(s);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUnifyLineSeparators() {
//        String result = StringUtils.unifyLineSeparators(s);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUnifyLineSeparators2() {
//        String result = StringUtils.unifyLineSeparators(s, lineSeparator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testContains3() {
//        boolean result = StringUtils.contains(cs, searchChars, ignoreCase);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testContainsRegexp() {
//        boolean result = StringUtils.containsRegexp(text, regexp);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testCountOccurrencesOf() {
//        int result = StringUtils.countOccurrencesOf(str, token);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testUpperCase3() {
//        String result = StringUtils.upperCase(str, offset, limit);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUpperCaseFirstLetter() {
//        String result = StringUtils.upperCaseFirstLetter(name);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTransformFirstChar() {
//        String result = StringUtils.transformFirstChar(str, transformer);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testLowerCaseFirstChar() {
//        String result = StringUtils.lowerCaseFirstChar(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUpperCaseFirstChar() {
//        String result = StringUtils.upperCaseFirstChar(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveFirstChar() {
//        String result = StringUtils.removeFirstChar(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testReplaceFirstChar() {
//        String result = StringUtils.replaceFirstChar(str, replacement);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testLowerCase3() {
//        String result = StringUtils.lowerCase(str, offset, limit);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testCompletingLength() {
//        String result = StringUtils.completingLength(str, expectedLength, c, left);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testConvertTo() {
//        T result = StringUtils.convertTo(str, targetClass);
//        Assertions.assertEquals(null, result);
//    }
//
//    @Test
//    void testIsVowelLetter() {
//        boolean result = StringUtils.isVowelLetter(c);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testStartsWithVowelLetter() {
//        boolean result = StringUtils.startsWithVowelLetter(string);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIndexOf5() {
//        int result = StringUtils.indexOf(seq, searchSeq, ignoreCase);
//        Assertions.assertEquals(0, result);
//    }
//
//    @Test
//    void testSubSequence() {
//        CharSequence result = StringUtils.subSequence(cs, start);
//        Assertions.assertEquals(null, result);
//    }
//
//    @Test
//    void testToCharArray() {
//        char[] result = StringUtils.toCharArray(cs);
//        Assertions.assertArrayEquals(new char[]{'a'}, result);
//    }
//
//    @Test
//    void testRegionMatches() {
//        boolean result = StringUtils.regionMatches(cs, ignoreCase, thisStart, substring, start, length);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testToStringArray() {
//        String[] result = StringUtils.toStringArray(strings);
//        Assertions.assertArrayEquals(new String[]{"replaceMeWithExpectedResult"}, result);
//    }
//
//    @Test
//    void testUnderlineToCamel() {
//        String result = StringUtils.underlineToCamel(string, firstLetterToLower);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSeparatorToCamel() {
//        String result = StringUtils.separatorToCamel(string, separator, firstLetterToLower);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testShortenTextWithEllipsis() {
//        String result = StringUtils.shortenTextWithEllipsis(text, maxLength, suffixLength);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testShortenTextWithEllipsis2() {
//        String result = StringUtils.shortenTextWithEllipsis(text, maxLength, suffixLength, useEllipsisSymbol);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testShortenTextWithEllipsis3() {
//        String result = StringUtils.shortenTextWithEllipsis(text, maxLength, suffixLength, symbol);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testFindBom() {
//        BOM result = StringUtils.findBom(str);
//        Assertions.assertEquals(null, result);
//    }
//
//    @Test
//    void testChars() {
//        char[] result = StringUtils.chars(sequence);
//        Assertions.assertArrayEquals(new char[]{'a'}, result);
//    }
//
//    @Test
//    void testChars2() {
//        char[] result = StringUtils.chars(sequence, begin);
//        Assertions.assertArrayEquals(new char[]{'a'}, result);
//    }
//
//    @Test
//    void testChars3() {
//        char[] result = StringUtils.chars(sequence, begin, end);
//        Assertions.assertArrayEquals(new char[]{'a'}, result);
//    }
//
//    @Test
//    void testIsBlankIfStr() {
//        boolean result = StringUtils.isBlankIfStr(obj);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testHasBlank() {
//        boolean result = StringUtils.hasBlank(strs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsEmptyIfStr() {
//        boolean result = StringUtils.isEmptyIfStr(obj);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testNullToEmpty() {
//        String result = StringUtils.nullToEmpty(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testNullToDefault() {
//        String result = StringUtils.nullToDefault(str, defaultStr);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testEmptyToDefault() {
//        String result = StringUtils.emptyToDefault(str, defaultStr);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testBlankToDefault() {
//        String result = StringUtils.blankToDefault(str, defaultStr);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testEmptyToNull() {
//        String result = StringUtils.emptyToNull(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testHasEmpty() {
//        boolean result = StringUtils.hasEmpty(strs);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsNullOrUndefined() {
//        boolean result = StringUtils.isNullOrUndefined(str);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsEmptyOrUndefined() {
//        boolean result = StringUtils.isEmptyOrUndefined(str);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testIsBlankOrUndefined() {
//        boolean result = StringUtils.isBlankOrUndefined(str);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testTrim() {
//        String result = StringUtils.trim(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTrim2() {
//        StringUtils.trim(strs);
//    }
//
//    @Test
//    void testTrimToEmpty() {
//        String result = StringUtils.trimToEmpty(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTrimToNull() {
//        String result = StringUtils.trimToNull(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTrimStart() {
//        String result = StringUtils.trimStart(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTrimEnd() {
//        String result = StringUtils.trimEnd(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testTrim3() {
//        String result = StringUtils.trim(str, mode);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStartWith() {
//        boolean result = StringUtils.startWith(str, c);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testStartWith2() {
//        boolean result = StringUtils.startWith(str, prefix, isIgnoreCase);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testStartWith3() {
//        boolean result = StringUtils.startWith(str, prefix);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testStartWithIgnoreCase() {
//        boolean result = StringUtils.startWithIgnoreCase(str, prefix);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testStartWithAny() {
//        boolean result = StringUtils.startWithAny(str, prefixes);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEndWith() {
//        boolean result = StringUtils.endWith(str, c);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEndWith2() {
//        boolean result = StringUtils.endWith(str, suffix, isIgnoreCase);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEndWith3() {
//        boolean result = StringUtils.endWith(str, suffix);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEndWithIgnoreCase() {
//        boolean result = StringUtils.endWithIgnoreCase(str, suffix);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testEndWithAny() {
//        boolean result = StringUtils.endWithAny(str, suffixes);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testContains4() {
//        boolean result = StringUtils.contains(str, searchChar);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testContainsBlank() {
//        boolean result = StringUtils.containsBlank(str);
//        Assertions.assertEquals(true, result);
//    }
//
//    @Test
//    void testGetContainsStr() {
//        String result = StringUtils.getContainsStr(str, testStrs);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGetContainsStrIgnoreCase() {
//        String result = StringUtils.getContainsStrIgnoreCase(str, testStrs);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGetGeneralField() {
//        String result = StringUtils.getGeneralField(getOrSetMethodName);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGenSetter() {
//        String result = StringUtils.genSetter(fieldName);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testGenGetter() {
//        String result = StringUtils.genGetter(fieldName);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveAll2() {
//        String result = StringUtils.removeAll(str, strToRemove);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveAll3() {
//        String result = StringUtils.removeAll(str, chars);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveAllLineBreaks() {
//        String result = StringUtils.removeAllLineBreaks(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemovePreAndLowerFirst() {
//        String result = StringUtils.removePreAndLowerFirst(str, preLength);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemovePreAndLowerFirst2() {
//        String result = StringUtils.removePreAndLowerFirst(str, prefix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUpperFirstAndAddPre() {
//        String result = StringUtils.upperFirstAndAddPre(str, preString);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testUpperFirst() {
//        String result = StringUtils.upperFirst(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testLowerFirst() {
//        String result = StringUtils.lowerFirst(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemovePrefix() {
//        String result = StringUtils.removePrefix(str, prefix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemovePrefixIgnoreCase() {
//        String result = StringUtils.removePrefixIgnoreCase(str, prefix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveSuffix() {
//        String result = StringUtils.removeSuffix(str, suffix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveSufAndLowerFirst() {
//        String result = StringUtils.removeSufAndLowerFirst(str, suffix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testRemoveSuffixIgnoreCase() {
//        String result = StringUtils.removeSuffixIgnoreCase(str, suffix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStrip3() {
//        String result = StringUtils.strip(str, prefixOrSuffix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStrip4() {
//        String result = StringUtils.strip(str, prefix, suffix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStripIgnoreCase() {
//        String result = StringUtils.stripIgnoreCase(str, prefixOrSuffix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testStripIgnoreCase2() {
//        String result = StringUtils.stripIgnoreCase(str, prefix, suffix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testAddPrefixIfNot() {
//        String result = StringUtils.addPrefixIfNot(str, prefix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testAddSuffixIfNot() {
//        String result = StringUtils.addSuffixIfNot(str, suffix);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testCleanBlank() {
//        String result = StringUtils.cleanBlank(str);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSub() {
//        String result = StringUtils.sub(str, fromIndex, toIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testMaxLength() {
//        String result = StringUtils.maxLength(string, length);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubPre() {
//        String result = StringUtils.subPre(string, toIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubSuf() {
//        String result = StringUtils.subSuf(string, fromIndex);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubSufByLength() {
//        String result = StringUtils.subSufByLength(string, length);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubWithLength() {
//        String result = StringUtils.subWithLength(input, fromIndex, length);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubBefore() {
//        String result = StringUtils.subBefore(string, separator, isLastSeparator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubBefore2() {
//        String result = StringUtils.subBefore(string, separator, isLastSeparator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubAfter() {
//        String result = StringUtils.subAfter(string, separator, isLastSeparator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubAfter2() {
//        String result = StringUtils.subAfter(string, separator, isLastSeparator);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    void testSubBetween() {
//        String result = StringUtils.subBetween(str, before, after);
//        Assertions.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
    @Test
    void testSubBetween2() {
        String result = StringUtils.subBetween("abcdefghi", "bc","gh");
        Assertions.assertEquals("def", result);
    }

    @Test
    void testIsSurround() {
        boolean result = StringUtils.isSurround("abfdd", "ab", "d");
        Assertions.assertEquals(true, result);
    }

}