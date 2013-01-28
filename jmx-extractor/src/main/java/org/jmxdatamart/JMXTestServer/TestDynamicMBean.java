/*
 * Copyright (c) 2013, Tripwire, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  o Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * TestDynamicMBean.java
 *
 * Created on January 27, 2013, 1:10 PM
 */
package org.jmxdatamart.JMXTestServer;

import java.util.Random;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

/**
 * Interface TestBeanMBean
 *
 * @author Tesca Fitzgerald <tesca@pdx.edu>
 */
public class TestDynamicMBean implements DynamicMBean {
    
    private Integer a;
    private Random prng;

    private MBeanAttributeInfo[] beanAttributes = new MBeanAttributeInfo[2];
    private MBeanOperationInfo[] beanOperations = new MBeanOperationInfo[1];
    private MBeanInfo beanInfo = null;
    
    public TestDynamicMBean() {

    	//Define the bean attribute
        beanAttributes[0] = new MBeanAttributeInfo(
        		"Value",               
        		"java.lang.Integer",     
        		"Value: expected value (integer).", 
        		true,    //isReadable                   
        		true,    //isWritable
        		false);  //isIs                 

        //Define the bean operation
        beanOperations[0] = new MBeanOperationInfo(
        		"getA",                      
        		"Get the 'A' integer value", 
        		null,        // parameter types
        		"int",       // return type
        		MBeanOperationInfo.ACTION);

        //Create the overall bean info object
        beanInfo = new MBeanInfo(this.getClass().getName(),
        		"Dynamic MBean Implementation",
        		beanAttributes,
        		null,
              	beanOperations,
              	new MBeanNotificationInfo[0]);
    }

    public MBeanInfo getMBeanInfo() {
        return beanInfo;
    }
    
    //Get a list of attributes
	@Override
	public AttributeList getAttributes(String[] attributes) {
	    AttributeList attributesList = new AttributeList();	        
	    for (int i = 0; i < attributes.length; i++) {
			try {
				attributesList.add(new Attribute(attributes[i], getAttribute((String) attributes[i])));
			} catch (AttributeNotFoundException e) {
				e.printStackTrace();
			}
	    }
	    
	    return attributesList;
	}
	
	//Get a single attribute
    public Object getAttribute(String attributeName) throws AttributeNotFoundException {
    	if (attributeName.equals("A"))
    		return getA();
    	//Add other if-statement blocks here if multiple attributes exist for the mbean

    	throw(new AttributeNotFoundException("Attribute '" + attributeName + "' not found"));
    }
    
    //Private getter for bean's attribute
    private Object getA() {
        return a;
    }
 

    //Set a list of attributes
	@Override
	public AttributeList setAttributes(AttributeList attributes) {
	    AttributeList attributesList = new AttributeList();

	    if (attributes.isEmpty())
	        return attributesList;

	    for (int i = 0; i < attributes.size(); i++) {
	        try {
	            String name = ((Attribute) attributes.get(i)).getName();
	            Object value = getAttribute(name); 
	            attributesList.add(new Attribute(name, value));
	        } catch(Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return attributesList;
	}

	//Set a single attribute
	@Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException {
    	String name = attribute.getName();
    	Object value = attribute.getValue();
    	
    	if (name.equals("A")) {
    		try {
    			if ((Class.forName("java.lang.Integer")).isAssignableFrom(value.getClass()))
    				setA((Integer) value);
    			else 
    				throw(new InvalidAttributeValueException("Cannot set attribute due to incorrect data type"));
    			return;
    		} catch (ClassNotFoundException e) {
    			e.printStackTrace();
    		}
    	}
    	//Add additional if-statement blocks here for additional mbean attributes
    	
    	throw(new AttributeNotFoundException("Attribute not found"));
    }

	//Private setter for mbean attribute
    private void setA(Object obj) {
        if (obj instanceof Integer) {
            a = (Integer)obj;
        }
    }

	//Invoke a given operation
	@Override
	public Object invoke(String operationName, Object params[], String signature[]) throws MBeanException, ReflectionException {
	    if (operationName.equals("toString"))
	        return this.valueToString();
	    if (operationName.equals("randomize")) {
	    	this.randomize();
	    	return null;
	    }
	    //Add additional if-statement blocks here for other mbean operations
	    
	    throw new ReflectionException(new NoSuchMethodException(operationName), "Operation not found");
	}

    private void randomize() {
        a = prng.nextInt(100);
    }
    
    private String valueToString() {
        return "A = " + a.toString();
    }
}
