/*
 * InvalidFieldTypeException.java
 * Created on 24-nov-2004
 * Last modified: $Date: 2018/02/02 16:10:16 $
 *
 * Copyright 2000-2004 by mCentric.
 * c\ Gabriel Garcia Marquez 4, 28230 Las Rozas - Madrid, Spain
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of mCentric. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with mCentric.
 */
package org.pclg.migrator.clarion;

/**
 * Thrown when a field is not of the expected type.
 * @author El Coyote Cojo
 * @version $Revision: 1.3 $
 * @since 24-nov-2004
 */
final class InvalidFieldTypeException extends RuntimeException {
    private static final long serialVersionUID = -603989543469978831L;

    /**
     * Constructs a new InvalidFieldTypeException  with the specified detail message.
     * @param reason the detail message. The detail message is saved for later
     * retrieval by the Throwable.getMessage() method.
     */
    public InvalidFieldTypeException(final String reason) {
        super(reason);
    }
}
