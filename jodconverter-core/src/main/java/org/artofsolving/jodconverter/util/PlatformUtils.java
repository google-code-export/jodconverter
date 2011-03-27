//
// JODConverter - Java OpenDocument Converter
// Copyright 2009 Art of Solving Ltd
// Copyright 2004-2009 Mirko Nasato
//
// JODConverter is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// JODConverter is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General
// Public License along with JODConverter.  If not, see
// <http://www.gnu.org/licenses/>.
//
package org.artofsolving.jodconverter.util;

public class PlatformUtils {

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    
    private PlatformUtils() {
        throw new AssertionError("utility class must not be instantiated");
    }

    public static boolean isLinux() {
        return OS_NAME.startsWith("linux");
    }

    public static boolean isMac() {
        return OS_NAME.startsWith("mac");
    }

    public static boolean isWindows() {
        return OS_NAME.startsWith("windows");
    }
    
    /**
     * This method will escape Comma (,) to Period (.)
     * Because the PTQL cannot escape those correctly, so we will use '.' in regular expression.
     * We also have to remove \Q and \E because they are not correctly interpreted as literal characters
     * 
     * @param s - The string you want to espace
     * 
     * NB: Note that you should only espace the value of the PTQL, not the query it self
     * ie: State.Name.ct=pipe,name=office1 should be converted to State.Name.ct=pipe.name.office1
     * @return - The escaped string
     */
    public static String escapePTQLForRegex(String s) {
    	return s.replaceAll(",", ".").replaceAll("\\\\Q|\\\\E", "");
    }
}