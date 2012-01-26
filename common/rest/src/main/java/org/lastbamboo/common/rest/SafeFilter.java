package org.lastbamboo.common.rest;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.littleshoot.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeFilter
    {

    private static final Logger LOG = LoggerFactory.getLogger(SafeFilter.class);
    
    private static final Set<String> s_unsafeWords = new HashSet<String>();
    
    private static final Set<String> s_unsafePhrases = new HashSet<String>();
    
    private static final Pattern WORD_SEPARATOR = 
        Pattern.compile("\\s+|(?=[\\p{Punct}&&[^']](?:\\s|$))");
    
    static 
        {
        s_unsafeWords.add("tit");
        s_unsafeWords.add("tits");
        s_unsafeWords.add("cum");
        s_unsafeWords.add("pussy");
        s_unsafeWords.add("penis");
        s_unsafeWords.add("cock");
        s_unsafeWords.add("blowjob");
        s_unsafeWords.add("blowjobs");
        s_unsafeWords.add("orgasm");
        s_unsafeWords.add("orgasms");
        s_unsafeWords.add("porn");
        s_unsafeWords.add("porno");
        s_unsafeWords.add("lesbian");
        s_unsafeWords.add("lesbians");
        s_unsafeWords.add("ejaculation");
        s_unsafeWords.add("orgy");
        s_unsafeWords.add("masturbating");
        s_unsafeWords.add("incest");
        s_unsafeWords.add("underage");
        s_unsafeWords.add("lolita");
        s_unsafeWords.add("foursome");
        s_unsafeWords.add("threesome");
        s_unsafeWords.add("threesomes");
        }
    
    static 
        {
        s_unsafePhrases.add("hot chick");
        s_unsafePhrases.add("big ass");
        s_unsafePhrases.add("big dick");
        s_unsafePhrases.add("teen sex");
        s_unsafePhrases.add("chicks fucking");
        }
    
    
    public static boolean isSafe(final String keywords)
        {
        final String toFilter;
        if (FileUtils.hasFileExtension(keywords))
            {
            toFilter = keywords.substring(0, keywords.length()-4).toLowerCase();
            }
        else
            {
            toFilter = keywords.toLowerCase();
            }
        
        for (final String phrase : s_unsafePhrases)
            {
            if (toFilter.contains(phrase))
                {
                LOG.debug("Found unsage phrase: {}", phrase);
                return false;
                }
            }
        
        final Scanner sc = new Scanner(toFilter);
        sc.useDelimiter(WORD_SEPARATOR);
        
        while (sc.hasNext())
            {
            final String current = sc.next();
            if (s_unsafeWords.contains(current)) 
                {
                LOG.debug("Found unsafe word: {}", current);
                return false;
                }
            }
        return true;
        }
    }
