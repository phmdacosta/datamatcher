package com.phmc.datamanager.test;

import com.phmc.datamatcher.builder.ImplementationClass;
import com.phmc.datamatcher.builder.MatcherBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatcherBuilderTest {
//    private WrapperObj wrapperObj;
//    private PrimitiveObj primitiveObj;

    @DisplayName("Test MatcherBuilder::build -> Success for wrapper property")
    @Test
    void testImplementationMatcherDefaultInterfaceCreationWrapper() throws Exception {
        WrapperObj obj = new WrapperObj(56, (short) 1, 98762168598L, 15.5f, 12564.568, Boolean.TRUE, 'c', "byte".getBytes()[0], new Date());
        MatcherBuilder builder = new MatcherBuilder(obj.getClass());
        ImplementationClass implClass = builder.build();
        assertEquals("WrapperObjMatcherImpl", implClass.getClassSimpleName());
        assertEquals(obj.getClass().getPackage().getName(), implClass.getPackageName());
        assertEquals(getWrapperObjBody(), implClass.beautify());
    }

    private String getWrapperObjBody() {
        return "package com.phmc.datamanager.test;\n" +
                "import com.phmc.datamatcher.builder.IMatcher;\n" +
                "import java.util.Objects;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "public class WrapperObjMatcherImpl implements IMatcher<WrapperObj> {\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean matches(WrapperObj o1, WrapperObj o2) {\n" +
                "        if (o1 == null && o2 == null) {\n" +
                "            return true;\n" +
                "        }\n" +
                "        if (o1 == null) {\n" +
                "            return false;\n" +
                "        }\n" +
                "        if (o2 == null) {\n" +
                "            return false;\n" +
                "        }\n" +
                "        return Objects.equals(o1.getIntProp(), o2.getIntProp()) \n" +
                "                && Objects.equals(o1.getShortProp(), o2.getShortProp()) \n" +
                "                && Objects.equals(o1.getLongProp(), o2.getLongProp()) \n" +
                "                && Objects.equals(o1.getFloatProp(), o2.getFloatProp()) \n" +
                "                && Objects.equals(o1.getDoubleProp(), o2.getDoubleProp()) \n" +
                "                && Objects.equals(o1.getBooleanProp(), o2.getBooleanProp()) \n" +
                "                && Objects.equals(o1.getCharProp(), o2.getCharProp()) \n" +
                "                && Objects.equals(o1.getByteProp(), o2.getByteProp()) \n" +
                "                && Objects.equals(o1.getDateProp(), o2.getDateProp());\n" +
                "    }\n" +
                "}";
    }
}
