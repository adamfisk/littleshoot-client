package org.lastbamboo.common.rest;

import static org.junit.Assert.*;

import org.junit.Test;


public class SafeFilterTest
    {

    @Test public void testSafety() throws Exception
        {
        assertFalse(SafeFilter.isSafe("pussy  yes.mpg"));
        assertFalse(SafeFilter.isSafe("girl fuck - great body tits"));
        assertFalse(SafeFilter.isSafe("porn big brother 7 casting couch 4 tony james milf mature sexy fuck pussy big tits massive tits clit fake boobs ass porno breasts mom mother 34 year old blonde 1st time xxx slut whore bitch cum blow.mpeg"));
        assertFalse(SafeFilter.isSafe("Group sex, Awesome, The Best scene, foursome, FFFM, pussy, tits, blowjob, anal, cum, facial, porn, xxx, sex, fuck, tit, a.mpg"));
        assertFalse(SafeFilter.isSafe("tit.mpg"));
        assertFalse(SafeFilter.isSafe("hot chicks.mpg"));
        assertFalse(SafeFilter.isSafe("big dick.mpg"));
        }
    }
