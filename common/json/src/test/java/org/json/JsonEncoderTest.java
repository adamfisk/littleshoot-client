package org.json;

import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.littleshoot.util.BeanUtils;


public class JsonEncoderTest
    {

    @Test public void testEncode() throws Exception
        {
        final Map<String, String> map = BeanUtils.mapBean(new TestBean());
        final JSONObject json = new JSONObject();
        json.append("map", map);
        System.out.println(json.toString());
        }
    
    public static final class TestBean
        {
        public String getJiggy()
            {
            return "jiggy";
            }
        public String getSomething()
            {
            return "thing";
            }
        
        public Date getDate()
            {
            return new Date();
            }
        
        public int getInt()
            {
            return 10;
            }
        
        public boolean getBoolean()
            {
            return false;
            }
        }
    
    }
