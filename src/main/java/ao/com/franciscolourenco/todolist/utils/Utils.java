package ao.com.franciscolourenco.todolist.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {

    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames(Object source){

        final BeanWrapper src = new BeanWrapperImpl(source);

        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> propertiesNull = Arrays.stream(pds)
                .filter(propertie -> src.getPropertyValue(propertie.getName()) == null)
                .map(PropertyDescriptor::getName)
                .collect(Collectors.toSet());

        return propertiesNull.toArray(new String[propertiesNull.size()]);

    }


}
