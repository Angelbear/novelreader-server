package cn.com.sae.utils;

/*
 Copyright (c) 2003 eInnovation Inc. All rights reserved

 This library is free software; you can redistribute it and/or modify it under the terms
 of the GNU Lesser General Public License as published by the Free Software Foundation;
 either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Lesser General Public License for more details.
 */

/*--

 Copyright (C) 2001-2002 Anthony Eden.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions, and the disclaimer that follows
 these conditions in the documentation and/or other materials
 provided with the distribution.

 3. The name "JPublish" must not be used to endorse or promote products
 derived from this software without prior written permission.  For
 written permission, please contact me@anthonyeden.com.

 4. Products derived from this software may not be called "JPublish", nor
 may "JPublish" appear in their name, without prior written permission
 from Anthony Eden (me@anthonyeden.com).

 In addition, I request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
 "This product includes software developed by
 Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 For more information on JPublish, please see <http://www.jpublish.org/>.

 */

/**
 * Utility class for working with request paths.
 * 
 * @author Anthony Eden
 */
public final class PathUtilities {
	private static final String WILDCARD = "*";

	/**
	 * resolve a relative URL string against an absolute URL string.
	 * 
	 * This method was adapted from the CalCom library at http://www.calcom.de
	 * 
	 * <p>
	 * the absolute URL string is the start point for the relative path.
	 * </p>
	 * 
	 * <p>
	 * <b>Example:</b>
	 * </p>
	 * 
	 * <pre>
	 *   relative path:  ../images/test.jpg
	 *   absolute path:  file:/d:/eigene dateien/eigene bilder/
	 *   result:         file:/d:/eigene dateien/images/test.jpg
	 * </pre>
	 * 
	 * @param relPath
	 *            The relative URL string to resolve. Unlike the Calcom version,
	 *            this may be an absolute path, if it starts with "/".
	 * @param absPath
	 *            The absolute URL string to start at. Unlike the CalCom
	 *            version, this may be a filename rather than just a path.
	 * 
	 * @return the absolute URL string resulting from resolving relPath against
	 *         absPath
	 * 
	 * @author Ulrich Hilger
	 * @author CalCom
	 * @author <a href="http://www.calcom.de">http://www.calcom.de</a>
	 * @author <a href="mailto:info@calcom.de">info@calcom.de</a>
	 * @author Dennis Brown (eInnovation)
	 */
	public static String resolveRelativePath(String relPath, String absPath) {
		// if relative path is really absolute, then ignore absPath (eInnovation
		// change)
		if (relPath.startsWith("/")) {
			absPath = "";
		}

		String newAbsPath = absPath;
		String newRelPath = relPath;
		if (relPath.startsWith("$")) {
			return relPath;
		} else if (absPath.endsWith("/")) {
			newAbsPath = absPath.substring(0, absPath.length() - 1);
		} else {
			// absPath ends with a filename, remove it (eInnovation change)
			int lastSlashIndex = absPath.lastIndexOf('/');
			if (lastSlashIndex >= 0) {
				newAbsPath = absPath.substring(0, lastSlashIndex);
			} else {
				newAbsPath = "";
			}
		}

		int relPos = newRelPath.indexOf("../");
		while (relPos > -1) {
			newRelPath = newRelPath.substring(relPos + 3);
			int lastSlashInAbsPath = newAbsPath.lastIndexOf("/");
			if (lastSlashInAbsPath >= 0) {
				newAbsPath = newAbsPath.substring(0,
						newAbsPath.lastIndexOf("/"));
			} else {
				// eInnovation change: fix potential exception
				newAbsPath = "";
			}
			relPos = newRelPath.indexOf("../");
		}
		String returnedPath;
		if (newRelPath.startsWith("/")) {
			returnedPath = newAbsPath + newRelPath;
		} else {
			returnedPath = newAbsPath + "/" + newRelPath;
		}

		// remove any "." references to current directory (eInnovation change)
		// For example:
		// "./junk" becomes "junk"
		// "/./junk" becomes "/junk"
		// "junk/." becomes "junk"
		while (returnedPath.endsWith("/.")) {
			returnedPath = returnedPath.substring(0, returnedPath.length() - 2);
		}
		do {
			int dotSlashIndex = returnedPath.lastIndexOf("./");
			if (dotSlashIndex < 0) {
				break;
			} else if (dotSlashIndex == 0
					|| returnedPath.charAt(dotSlashIndex - 1) != '.') {
				String firstSubstring;
				if (dotSlashIndex > 0) {
					firstSubstring = returnedPath.substring(0, dotSlashIndex);
				} else {
					firstSubstring = "";
				}
				String secondSubstring;
				if (dotSlashIndex + 2 < returnedPath.length()) {
					secondSubstring = returnedPath.substring(dotSlashIndex + 2,
							returnedPath.length());
				} else {
					secondSubstring = "";
				}
				returnedPath = firstSubstring + secondSubstring;
			}
		} while (true);

		return returnedPath;
	}

}
