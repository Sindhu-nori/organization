package com.dtt.organization.asserts;

import com.dtt.organization.exception.RAServiceException;

public class ServiceAsserts {
		
	/**
	 * Not nullor empty.
	 *
	 * @param orgumentName
	 *            the orgument name
	 * @param orgumentName1
	 *            the orgument name 1
	 * @throws RAServiceException
	 *             the RA service exception
	 */
	public static void notNullorEmpty(String orgumentName, String orgumentName1) throws RAServiceException {
		if (orgumentName == null || orgumentName.isEmpty()) {
			throw new RAServiceException(orgumentName1);
		}
	}

	/**
	 * Not nullor empty.
	 *
	 * @param size
	 *            the size
	 * @param orgumentName
	 *            the orgument name
	 * @throws RAServiceException
	 *             the RA service exception
	 */
	public static void notNullorEmpty(int size, String orgumentName) throws RAServiceException {
		if (size == 0) {
			throw new RAServiceException(orgumentName);
		}
	}

	/**
	 * Not nullor empty.
	 *
	 * @param orgumentName
	 *            the orgument name
	 * @param message
	 *            the message
	 * @throws RAServiceException
	 *             the RA service exception
	 */
	public static void notNullorEmpty(Object orgumentName, String message) throws RAServiceException {
		if (orgumentName == null) {
			throw new RAServiceException(message);
		}
	}
}
