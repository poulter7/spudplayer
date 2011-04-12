/*
 * JOCL - Java bindings for OpenCL
 *
 * Copyright (c) 2009 Marco Hutter - http://www.jocl.org
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jocl;

/**
 * An exception that may be thrown due to a OpenCL error. <br />
 * <br />
 * Exceptions may be enabled or disabled using
 * {@link org.jocl.CL#setExceptionsEnabled(boolean) CL#setExceptionsEnabled(boolean)}.
 * If exceptions are enabled, the JOCL methods will throw a 
 * CLException if the OpenCL function did not return CL_SUCCESS.<br />
 */
public class CLException extends RuntimeException
{
    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 1587809813906124159L;

    /**
     * Creates a new CLException with the given error message.
     * 
     * @param message The error message for this CLException
     */
    public CLException(String message)
    {
        super(message);
    }

    /**
     * Creates a new CLException with the given error message.
     * 
     * @param message The error message for this CLException
     * @param cause The throwable that caused this exception
     */
    public CLException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
} 
