/*
 * Creado el 27-feb-2008
 */
package org.pclg.dictionary;

/**
 * @author Un autor en busca de personajes.
 */
final class DictionaryEntry {
	private final String word;  
	private final String definition;  

	/**
	 * 
	 */
	DictionaryEntry(final String word, final String definition) {
		this.word = word.trim();
		this.definition = definition.trim();
	}

	/**
	 * @return Devuelve definition.
	 */
	public String getDefinition() {
		return definition;
	}
	/**
	 * @return Devuelve word.
	 */
	public String getWord() {
		return word;
	}
	/* (sin Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return word + "->" + definition;
	}

	/* (sin Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		final DictionaryEntry entry = (DictionaryEntry) obj;
		return entry.word.equals(word) && entry.definition.equals(definition);
	}

	/* (sin Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return word.hashCode() ^ definition.hashCode();
	}
}
